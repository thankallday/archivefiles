package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.util.ArrayList;

import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;
import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;
import com.thomsonreuters.scholarone.archivefiles.ConfigPropertyValues;

public class RevertTask implements ITask
{
  private Integer stackId;

  private Config config;

  private Document document;

  private ILog logger = null;
  
  private int exitCode = -1;
  
  private S3File sourceS3Dir;
  
  private S3File destinationS3Dir;
 
  public RevertTask(Integer stackId, Config config, Document document, S3File sourceS3Dir, S3File destinationS3Dir)
  {
    this.stackId = stackId;
    this.config = config;
    this.document = document;
    this.sourceS3Dir = sourceS3Dir;
    this.destinationS3Dir = destinationS3Dir;

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
    logger.log(LogType.INFO, "Start Revert Task." + sourceS3Dir.getKey() + " " + destinationS3Dir.getKey());

    sourceS3Dir = new S3File(ConfigPropertyValues.getProperty("prefix.directory") + File.separator
        + ConfigPropertyValues.getProperty("environment") + stackId + File.separator + config.getShortName()
        + File.separator + document.getFileStoreYear() + File.separator + document.getFileStoreMonth() + File.separator
        + document.getDocumentId() + File.separator,
        sourceS3Dir.getBucketName());

    destinationS3Dir = new S3File(ConfigPropertyValues.getProperty("prefix.directory") + File.separator + document.getArchiveYear()
        + File.separator + document.getArchiveMonth() + File.separator + ConfigPropertyValues.getProperty("environment") + stackId 
        + File.separator + config.getShortName() + File.separator + document.getDocumentId() + File.separator, 
        destinationS3Dir.getBucketName());
    
    exitCode = S3FileUtil.copyS3Dir(sourceS3Dir, destinationS3Dir, new ArrayList<String>(), new StatInfo());
    
    logger.log(LogType.INFO, "END Revert Task.");

  }
}
