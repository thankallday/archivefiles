package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;
import com.scholarone.monitoring.common.Environment;
import com.thomsonreuters.scholarone.archivefiles.ConfigPropertyValues;

public class TaskFactoryImpl implements ITaskFactory
{
  private static ILog logger = null;

  private Integer stackId = null;
  
  private Long runId;

  public TaskFactoryImpl(Integer stackId, Long runId)
  {
    this.stackId = stackId;
    
    this.runId = runId;

    if (logger == null)
    {
      logger = new LogTrackerImpl(this.getClass().getName());
    }
  }

  @Override
  public List<ITask> getTasks()
  {
    List<ITask> tasks = new ArrayList<ITask>();

    Integer documentCount = Integer.valueOf(ConfigPropertyValues.getProperty("documents.per.config." + stackId));

    String environment = ConfigPropertyValues.getProperty("environment");;
    
    String configIdStr = ConfigPropertyValues.getProperty("configs." + stackId);

    Integer auditLevel = Integer.valueOf(ConfigPropertyValues.getProperty("audit.level." + stackId));
    
    String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");
    
    String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");

    String prefixDirectory = ConfigPropertyValues.getProperty("prefix.directory");

    String archiveCacheDir = ConfigPropertyValues.getProperty("archive.cache.dir"); //use for creating local file to be uploaded to s3 bucket.
    
    Environment envType = Environment.getEnvironmentType("DEV");

    if (archiveCacheDir == null || archiveCacheDir.trim().length() == 0)
    {
      return tasks;
    }
    else
    {
      File archiveCache = new File(archiveCacheDir);
      if (archiveCache.exists())
      {
        archiveCache.delete();
        archiveCache.mkdir();
      }
    }
    
    IArchiveFilesDAO db = new ArchiveFilesDAOImpl();

    try
    {
      ArrayList<Integer> configIds = new ArrayList<Integer>();
      if ( configIdStr != null ) 
      {
        StringTokenizer st = new StringTokenizer(configIdStr, ",");
        while (st.hasMoreElements())
        {
          configIds.add(Integer.valueOf((String) st.nextElement()));
        }
      }
      
      if (db.openConnection(stackId))
      {
        List<Config> configs = db.getConfigs();
        for (Config config : configs)
        {
          if (configIds.isEmpty() || configIds.contains(config.getConfigId()))
          {
            List<Document> documents = db.getDocuments(config.getConfigId(), documentCount);
            if (documents.size() > 0)
            {
              for (Document document : documents)
              {
                try
                {
                  List<DocumentFile> files = db.getFiles(document.getDocumentId());
                  document.setFiles(files);
                  tasks.add(new Task(stackId, config, document, runId, auditLevel.intValue(), environment, archiveCacheDir, sourceBucket, destinationBucket, prefixDirectory, envType, environment));
                }
                catch (Exception e)
                {
                  logger.log(LogType.ERROR, "ConfigId: " + config.getConfigId() + " - DocumentId: " + document.getDocumentId() + " - " + e.getMessage());
                }
              }
            }
          }
        }
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

    return tasks;
  }
}
