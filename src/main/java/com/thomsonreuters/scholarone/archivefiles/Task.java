package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.s3.model.StorageClass;
import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.IMonitor;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;
import com.scholarone.activitytracker.ref.MonitorTrackerImpl;
import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;
import com.scholarone.monitoring.common.IMetricSubTypeConstants;
import com.scholarone.monitoring.common.Environment;
import com.scholarone.monitoring.common.MetricProduct;
import com.scholarone.monitoring.common.PublishMetrics;
import com.scholarone.monitoring.common.ServiceComponent;
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
  
  private S3File sourceS3Dir;

  private S3File destinationS3Dir;
  
  private String archiveCacheDir;

  private ILock lockObject;

  private int auditLevel;
  
  private int exitCode = -1;
  
  private Long transferTime;
  
  private Long transferSize;
  
  private Double transferRate;

  private String prefixDirectory;
  
  private Environment envType;
  
  private String envName;

  public Task(Integer stackId, Config config, Document document, Long runId, int level, String environment, String archiveCacheDir, 
      String sourceBucketName, String destinationBucketName, String prefixDirectory, Environment envType, String envName) throws IOException
  {
    this.environment = environment;
    this.stackId = stackId;
    this.runId = runId;
    this.config = config;
    this.document = document;
    this.auditLevel = level;
    this.archiveCacheDir = archiveCacheDir;
    this.prefixDirectory = prefixDirectory;
    this.envType = envType;
    this.envName = envName;

    logger = new LogTrackerImpl(this.getClass().getName());
    ((IHeader)logger).addLocalHeader("ConfigId", config.getConfigId().toString());
    ((IHeader)logger).addLocalHeader("DocumentId", document.getDocumentId().toString());
    
    monitor = MonitorTrackerImpl.getInstance();
    
    sourceS3Dir = new S3File(prefixDirectory + File.separator
        + environment + stackId + File.separator + config.getShortName()
        + File.separator + document.getFileStoreYear() + File.separator + document.getFileStoreMonth() + File.separator
        + document.getDocumentId() + File.separator,
        sourceBucketName);

    destinationS3Dir = new S3File(prefixDirectory + File.separator + document.getArchiveYear()
        + File.separator + document.getArchiveMonth() + File.separator + environment + stackId 
        + File.separator + config.getShortName() + File.separator + document.getDocumentId() + File.separator, 
        destinationBucketName);

    lockObject = new TaskLock(this, archiveCacheDir);
  }

  public Config getConfig()
  {
    return config;
  }

  public Document getDocument()
  {
    return document;
  }

  public S3File getSourceS3Dir()
  {
    return sourceS3Dir;
  }

  public S3File getDestinationS3File()
  {
    return destinationS3Dir;
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
    logger.log(LogType.INFO, "START Task document. | " + sourceS3Dir.getKey() + " | " + destinationS3Dir.getKey());

    PublishMetrics.incrementTotalCount(MetricProduct.S1M, ServiceComponent.ARCHIVE_S1SVC.getComponent(), IMetricSubTypeConstants.ARCHIVE_DOCUMEMNT, envType, envName);

    StatInfo stat = new StatInfo();
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
    
    int returnCode = -1;
    try
    {
      if (!S3FileUtil.isDirectory(sourceS3Dir))
      {
        exitCode = 0;
        logger.log(LogType.INFO, getSourceS3Dir() + " directory does not exists, aborting. " + sourceS3Dir.getKey());
        return;
      }
      
      if (lockObject.lock())
      {
        logger.log(LogType.INFO, sourceS3Dir.getKey() + " locked to move to " + destinationS3Dir.getKey());

        // preAudit
        DocumentAuditInfo documentAuditInfo = null;
        if (auditLevel != ITask.NO_AUDIT)
        {
          DocumentPreAudit preAudit = new DocumentPreAudit(stackId, document.getDocumentId(), sourceS3Dir, destinationS3Dir);
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
          returnCode = S3FileUtil.createExclusionsFile(sourceS3Dir, exclusionList, archiveCacheDir);
          
          if (returnCode != 0)
          {
            logger.log(LogType.ERROR, sourceS3Dir.getKey() + S3FileUtil.EXCLUDED_FILE + " failed to create.");
            
          }
          else
          {
            returnCode = S3FileUtil.copyS3Dir(sourceS3Dir, destinationS3Dir, exclusionList, StorageClass.StandardInfrequentAccess, stat);
            
            if (returnCode != 0)
            {
              logger.log(LogType.ERROR, sourceS3Dir.getKey() + " failed to copy directory to " + destinationS3Dir.getKey());
            }
            else
            {
              transferSize = stat.getTransferSize();
              transferTime = stat.getTransferTime();
              transferRate = stat.getTransferRate();

              returnCode = S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir, exclusionList);
              
              if (returnCode != 0)
              {
                logger.log(LogType.ERROR, sourceS3Dir.getKey() + " failed to delete directory");
              }
              else
              {
                // postAudit
                if (auditLevel != ITask.NO_AUDIT)
                {
                  DocumentPostAudit documentPostAudit = new DocumentPostAudit(documentAuditInfo, sourceS3Dir, destinationS3Dir);
                  if (documentAuditInfo == null || documentPostAudit.performPostAudit())
                  {
                    exitCode = 0;
                  }
                  else
                  {
                    logger.log(LogType.ERROR, sourceS3Dir.getKey() + " failed to audit with " + destinationS3Dir.getKey());
                    
                    if (auditLevel == ITask.AUDIT_REVERT)
                    {
                      // revert
                      RevertTask revertTask = new RevertTask(stackId, config, document, getSourceS3Dir(), getDestinationS3File());
                      revertTask.run();
                    }
                  }
                }
                else
                {
                  exitCode = 0;
                }
              }
            }
          }
        }
        else
        {
          //no files
          exitCode = 0;
        }
      }
      else
      {
        //lock error
        logger.log(LogType.ERROR, "Failed to get lock. " + sourceS3Dir.getKey() + "tier3move.lock exists");
      } // end of if (lockObject.lock())
    }
    catch (Exception e)
    {
      logger.log(LogType.ERROR, sourceS3Dir.getKey() + " occurred exception " + e.getMessage());
    }
    finally
    {
      if ( exitCode == 0 )
      {
        stat.incrementSuccessCount(); 
        updateDB(true);
        PublishMetrics.incrementSuccessCount(MetricProduct.S1M, ServiceComponent.ARCHIVE_S1SVC.getComponent(), IMetricSubTypeConstants.ARCHIVE_DOCUMEMNT, envType, envName);
        PublishMetrics.logRate(MetricProduct.S1M, stat.getTransferRate(), ServiceComponent.ARCHIVE_S1SVC.getComponent(), IMetricSubTypeConstants.ARCHIVE_DOCUMEMNT, envType, envName);
      }
      else
      {
        stat.incrementFailureCount();
        document.incrementRetryCount();
        updateDB(false);
        PublishMetrics.incrementFailureCount(MetricProduct.S1M, ServiceComponent.ARCHIVE_S1SVC.getComponent(), IMetricSubTypeConstants.ARCHIVE_DOCUMEMNT, envType, envName);
      }
      
      lockObject.unlock();
      logger.log(LogType.INFO, sourceS3Dir.getKey() + " unlocked");
      
      stat.markEndTime();

      DecimalFormat formatter = new DecimalFormat("#0");
      StringBuilder sb = new StringBuilder();
      sb.append("name=" + stat.getName())
            .append(", type=" + stat.getType())
            .append(", message=" + stat.getMessage())
            .append(", environment=" + stat.getEnvironment())
            .append(", stackId=" + stackId)
            .append(", groupId=" + stat.getGroupId())     
            .append(", documentId=" + document.getDocumentId().longValue())
            .append(", transferSize=" + stat.getTransferSize() + " bytes")
            .append(", transferTime=" + stat.getTransferTime() + " ms")
            .append(", transferRate=" + (stat.getTransferRate() == null ? "" : formatter.format(stat.getTransferRate()) + " bytes/sec"))
            .append(", totalCount=" + stat.getTotalCount())
            .append(", successCount=" + stat.getSuccessCount())
            .append(", failureCount=" + stat.getFailureCount())
            .append(", numOfFilesTotal=" + stat.getNumOfFilesTotal())
            .append(", numberOfFilesSuccess=" + stat.getNumOfFilesSuccess())
            .append(", numberOfFilesFailure=" + stat.getNumOfFilesFailure())
            .append(", startTime=" + stat.getStartTime())
            .append(", endTime=" + stat.getEndTime())
            .append(", elapsedTime=" + (stat.getEndTime().getTime() - stat.getStartTime().getTime()) + " ms");
      
      logger.log(LogType.INFO, sb.toString());
      if (exitCode >= 0)
        logger.log(LogType.INFO, "END Task document. SUCCESS. | " + sourceS3Dir.getKey() + " | " + destinationS3Dir.getKey());
      else
        logger.log(LogType.ERROR, "END Task document. FAILURE. | " + sourceS3Dir.getKey() + " | " + destinationS3Dir.getKey());
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
