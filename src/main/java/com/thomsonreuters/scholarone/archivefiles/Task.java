package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.IMonitor;
import com.scholarone.activitytracker.TrackingInfo;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;
import com.scholarone.activitytracker.ref.MonitorTrackerImpl;
import com.thomsonreuters.scholarone.archivefiles.audit.DocumentAuditInfo;
import com.thomsonreuters.scholarone.archivefiles.audit.DocumentPostAudit;
import com.thomsonreuters.scholarone.archivefiles.audit.DocumentPreAudit;

public class Task implements ITask
{
  private String environment;
  
  private Integer stackId;
  
  private Long runId;

  private Config config;

  private Document document;

  private ILog logger = null;

  private static IMonitor monitor = null;
  
  private String source;

  private String destination;

  private ILock lockObject;

  private int auditLevel;
  
  private int exitCode = -1;
  
  private Long transferTime;
  
  private Long transferSize;
  
  private Double transferRate;

  public Task(Integer stackId, Config config, Document document, Long runId, int level) throws IOException
  {
    this.environment = ConfigPropertyValues.getProperty("environment");
    this.stackId = stackId;
    this.runId = runId;
    this.config = config;
    this.document = document;
    this.auditLevel = level;

    logger = new LogTrackerImpl(this.getClass().getName());
    ((IHeader)logger).addLocalHeader("ConfigId", config.getConfigId().toString());
    ((IHeader)logger).addLocalHeader("DocumentId", document.getDocumentId().toString());
    
    monitor = MonitorTrackerImpl.getInstance();
    
    source = ConfigPropertyValues.getProperty("tier2.directory") + File.separator
        + environment + stackId + File.separator + config.getShortName()
        + File.separator + document.getFileStoreYear() + File.separator + document.getFileStoreMonth() + File.separator
        + document.getDocumentId();

    destination = ConfigPropertyValues.getProperty("tier3.directory") + File.separator + document.getArchiveYear()
        + File.separator + document.getArchiveMonth() + File.separator + environment + stackId 
        + File.separator + config.getShortName();

    lockObject = new TaskLock(this);
  }

  public Config getConfig()
  {
    return config;
  }

  public Document getDocument()
  {
    return document;
  }

  public String getSource()
  {
    return source;
  }

  public Integer getStackId()
  {
    return stackId;
  }
  
  public int getExitCode()
  {
    return exitCode;
  }
  
  public Long getTransferTime()
  {
    return transferTime;
  }

  public Long getTransferSize()
  {
    return transferSize;
  }
  
  public Double getTransferRate()
  {
    return transferRate;
  }


  public void run()
  {
    logger.log(LogType.INFO, "Start Task");

    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(stackId);

    TrackingInfo stat = new TrackingInfo();
    stat.setType(MonitorConstants.FILE_ARCHIVE_DOCUMENT);
    stat.setStackId(stackId);
    stat.setEnvironment("s1m-" + environment + "-stack" + stackId);
    stat.setGroupId(runId);
    stat.setObjectId(document.getDocumentId().longValue());
    stat.setObjectTypeId(ObjectTypeConstants.DOCUMENT_ID);
    stat.setMessage(((IHeader)monitor).getGlobalHeaders());
    stat.setName("Document Information");
    stat.setTotalCount(1);
    stat.markStartTime();
    
    try
    {       
      File dir = new File(getSource());
      if ( !dir.exists() )
      {
        logger.log(LogType.INFO, "Directory does not exists, aborting lock - " + getSource());
        
        exitCode = 0;
        
        return;
      }
    
      if (lockObject.lock())
      {
        logger.log(LogType.INFO, "Locked");

        // preAudit
        DocumentAuditInfo documentAuditInfo = null;
        if (auditLevel != ITask.NO_AUDIT)
        {
          DocumentPreAudit preAudit = new DocumentPreAudit(stackId, document.getDocumentId());
          documentAuditInfo = preAudit.performPreAudit();
        }

        List<DocumentFile> files = document.getFiles();
        if (files != null && files.size() > 0)
        {
          ArrayList<String> exclusionList = new ArrayList<String>();
          for (DocumentFile file : document.getFiles())
          {
            if (file.isExcluded())
            {
              exclusionList.add(file.getName());
            }
          }

          if (config.isKeepPDF())
          {
            exclusionList.add(FileArchiveConstants.PDF_PROOF_FIRST_LOOK_FILE_NAME);
            exclusionList.add(FileArchiveConstants.PDF_PROOF_HI_FILE_NAME);
          }

          exitCode = fs.copy(source, destination, exclusionList, stat);
          if (exitCode == 0)
          {
            transferSize = stat.getTransferSize();
            transferTime = stat.getTransferTime();
            transferRate = stat.getTransferRate();
            
            exitCode = fs.delete(source, true, exclusionList);
          }

          if (exitCode == 0)
          {
            // postAudit
            if (auditLevel != ITask.NO_AUDIT)
            {
              DocumentPostAudit documentPostAudit = new DocumentPostAudit(documentAuditInfo);
              if (documentAuditInfo == null || documentPostAudit.performPostAudit())
              {
                stat.incrementSuccessCount();
              }
              else
              {
                exitCode = -1;
                document.incrementRetryCount();
                stat.incrementFailureCount();
                
                if (auditLevel == ITask.AUDIT_REVERT)
                {
                  // revert
                  RevertTask revertTask = new RevertTask(stackId, config, document);
                  revertTask.run();
                }
              }
            }
            else
            {
              stat.incrementSuccessCount();
            }
          }
          else
          {
            document.incrementRetryCount();
            stat.incrementFailureCount();
          }
        }
        else
        {
          exitCode = 0;
        }
      }
      else
      {
        exitCode = 1;
        document.incrementRetryCount();
        stat.incrementFailureCount();
        logger.log(LogType.INFO, "Failed to get lock");
      }
    }
    catch (Exception e)
    {
      logger.log(LogType.ERROR, e.getMessage());
      document.incrementRetryCount();
      
      stat.incrementFailureCount();
    }
    finally
    {
      if ( exitCode == 0 )
        updateDB(true);
      else
        updateDB(false);
      
      lockObject.unlock();
      logger.log(LogType.INFO, "Unlock");
      
      stat.markEndTime();
      monitor.monitor(stat);
    }
  }

  private void updateDB(boolean success)
  {
    IArchiveFilesDAO db = new ArchiveFilesDAOImpl();
    Date currentDate = new Date();

    try
    {
      if (db.openConnection(stackId))
      {
        if (success)
          db.updateDocument(document.getDocumentId(), new Timestamp(currentDate.getTime()), document.getRetryCount());
        else
          db.updateDocument(document.getDocumentId(), null, document.getRetryCount());
      }
    }
    catch (Exception e)
    {
      logger.log(LogType.ERROR, e.getMessage());
    }
    finally
    {
      db.closeConnection();
    }
  }
}
