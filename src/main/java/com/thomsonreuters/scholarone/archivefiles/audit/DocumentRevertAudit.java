package com.thomsonreuters.scholarone.archivefiles.audit;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;
import com.scholarone.archivefiles.common.S3File;

public class DocumentRevertAudit
{
  private ILog logger = null;

  private DocumentAuditInfo documentAuditInfo;
  private S3File sourceS3Dir;
  private S3File destinationS3Dir;

  public DocumentRevertAudit(DocumentAuditInfo documentAuditInfo, S3File sourceS3Dir, S3File destinationS3Dir)
  {
    super();
    this.documentAuditInfo = documentAuditInfo;
    this.sourceS3Dir = sourceS3Dir;
    this.destinationS3Dir = destinationS3Dir;

    logger = new LogTrackerImpl(this.getClass().getName());
    ((IHeader)logger).clearLocalHeaders();
    
    ((IHeader)logger).addLocalHeader("ThreadId", Long.valueOf(Thread.currentThread().getId()).toString());      
    ((IHeader)logger).addLocalHeader("DocumentId", documentAuditInfo.getDocumentId().toString());
  }

  public boolean performRevertAudit()
  {
    logger.log(LogType.INFO, "Revert-Audit:  Begin");

    try
    {
      //get snapshots of the tier2 and tier3 file systems
      documentAuditInfo.setPostAuditTier2Files(S3FileAuditUtility.getFilesAndPathsAsMap(S3FileAuditUtility
          .getTier2FilePath(documentAuditInfo, sourceS3Dir)));
      documentAuditInfo.setPostAuditTier3Files(S3FileAuditUtility.getFilesAndPathsAsMap(S3FileAuditUtility
          .getTier3FilePath(documentAuditInfo, destinationS3Dir)));
    }
    catch (IOException e)
    {
      logger.log(LogType.ERROR, "Revert-Audit:  Exception thrown trying to build directory structure. " + e.getMessage());

      return false;
    }

    // First, let's make sure that tier3 storage was cleaned up
    if (!documentAuditInfo.getPostAuditTier3Files().isEmpty())
    {
      logger.log(LogType.ERROR, "Revert-Audit:  Files were not removed from tier3 during revert.");

      return false;

    }

    // Now, simply compare the before and after directory snapshots
    Map<String, List<String>> tier2PreMap = documentAuditInfo.getPreAuditTier2Files();
    Map<String, List<String>> tier2PostMap = documentAuditInfo.getPostAuditTier2Files();

    for (Entry<String, List<String>> entry : tier2PreMap.entrySet())
    {
      String key = entry.getKey();
      List<String> tier2FilePathList = entry.getValue();

      // Post revert map doesn't have the key
      if (!tier2PostMap.containsKey(key))
      {
        logger.log(LogType.ERROR, "File: " + entry.getKey() + " - Revert-Audit:  Tier2 storage does not have all of the expected files");
        return false;
      }
      // Post revert doesn't have the same number of items in the list
      if (tier2PostMap.get(key).size() != tier2FilePathList.size())
      {
        logger.log(LogType.ERROR, " File: " + entry.getKey()
            + " - Revert-Audit:  Tier2 storage does not have the same # of files as expected");
        return false;
      }
      // finally compare the list
      for (String filePath : tier2FilePathList)
      {
        if (!tier2PostMap.get(key).contains(filePath))
        {
          logger.log(LogType.ERROR, "File: " + entry.getKey()
              + " - Revert-Audit:  Files are not in the expected location(s)");
          return false;
        }
      }
    }

    // default to true if it gets past all checks
    return true;
  }

  public DocumentAuditInfo getDocumentAuditInfo()
  {
    return documentAuditInfo;
  }

  public void setDocumentAuditInfo(DocumentAuditInfo documentAuditInfo)
  {
    this.documentAuditInfo = documentAuditInfo;
  }
}
