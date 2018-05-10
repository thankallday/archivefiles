package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.IOException;

import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;

public class RevertTask implements ITask
{
  private Integer stackId;

  private Config config;

  private Document document;

  private ILog logger = null;
  
  private int exitCode = -1;
 
  public RevertTask(Integer stackId, Config config, Document document)
  {
    this.stackId = stackId;
    this.config = config;
    this.document = document;

    logger = new LogTrackerImpl(this.getClass().getName());
    ((IHeader)logger).addLocalHeader("ConfigId", config.getConfigId().toString());
    ((IHeader)logger).addLocalHeader("DocumentId", document.getDocumentId().toString());
  }

  public int getExitCode()
  {
    return exitCode;
  }
  
  @Override
  public Long getTransferTime()
  {
    return null;
  }

  @Override
  public Long getTransferSize()
  {
    return null;
  }
  public Double getTransferRate()
  {
    return null;
  }
  
  public void run()
  {
    logger.log(LogType.INFO, "Start Revert Task");

    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(stackId);

    try
    {
      String source = ConfigPropertyValues.getProperty("tier3.directory") + File.separator + document.getArchiveYear()
          + File.separator + document.getArchiveMonth() + File.separator + ConfigPropertyValues.getProperty("environment") + stackId 
          + File.separator + config.getShortName() + File.separator + document.getDocumentId();
      
      
      String destination = ConfigPropertyValues.getProperty("tier2.directory") + File.separator + 
          ConfigPropertyValues.getProperty("environment") + stackId + File.separator + 
          config.getShortName() + File.separator + 
          document.getFileStoreYear() + File.separator + 
          document.getFileStoreMonth();
      
      exitCode = fs.copy(source, destination);
    }
    catch (IOException e)
    {
      logger.log(LogType.ERROR, e.getMessage());
    }
  }


}
