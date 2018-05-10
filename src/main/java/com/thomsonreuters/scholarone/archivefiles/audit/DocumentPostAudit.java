package com.thomsonreuters.scholarone.archivefiles.audit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;
import com.thomsonreuters.scholarone.archivefiles.FileArchiveConstants;

public class DocumentPostAudit
{
  private ILog logger = null;
  
  public DocumentPostAudit(DocumentAuditInfo documentAuditInfo)
  {
    super();
    this.documentAuditInfo = documentAuditInfo;
    
    logger = new LogTrackerImpl(this.getClass().getName());
    ((IHeader)logger).clearLocalHeaders();
    
    ((IHeader)logger).addLocalHeader("ThreadId", Long.valueOf(Thread.currentThread().getId()).toString());      
    ((IHeader)logger).addLocalHeader("DocumentId", documentAuditInfo.getDocumentId().toString());
  }

  private DocumentAuditInfo documentAuditInfo;

  public boolean performPostAudit()
  {
    logger.log(LogType.INFO, "Post-Audit:  Begin");

    try
    {
      // Get snapshot of both tier2 and tier3 files systems for this document
      documentAuditInfo.setPostAuditTier2Files(FileAuditUtility.getFilesAndPathsAsMap(FileAuditUtility
          .getTier2FilePath(documentAuditInfo)));
      documentAuditInfo.setPostAuditTier3Files(FileAuditUtility.getFilesAndPathsAsMap(FileAuditUtility
          .getTier3FilePath(documentAuditInfo)));
    }
    catch (IOException e)
    {
      logger.log(LogType.ERROR, "Post-Audit:  Exception thrown trying to build post-move file structure. " + e.getMessage());
      return false;
    }

    logger.log(LogType.INFO, "Post-Audit:" + documentAuditInfo.toString());
    
    // go through all files and check to see if they should be moved or stay
    Map<String, List<DocumentFileAuditInfo>> documentFileMap = documentAuditInfo.getDocumentFilesMap();

    for (Entry<String, List<DocumentFileAuditInfo>> entry : documentFileMap.entrySet())
    {
      // Check to see if file should be moved. If there are multiple files, use the most restrictive(remain)
      boolean filesShouldBeMoved = true;
      List<DocumentFileAuditInfo> documentFileAuditInfoList = entry.getValue();
      for (DocumentFileAuditInfo documentFileAuditInfo : documentFileAuditInfoList)
      {
        if (!FileAuditUtility.shouldFileBeMoved(documentFileAuditInfo.getFileTypeId(), documentFileAuditInfo.isCoverletter()))
        {
          // files are excluded from moving by name, so if any files with the same name are to be kept, all need to
          // be kept.
          filesShouldBeMoved = false;
          break;
        }
      }

      // Time to do the file checks.
      // First, we can ignore the audit if the file wasn't found on tier2 storage
      if (!FileAuditUtility.didFileExistInTier2PriorToMove(documentAuditInfo, entry.getKey()))
      {
        logger.log(LogType.INFO, "File: " + entry.getKey() + " - Post-Audit:  File not found on pre-move tier2. Ignore audit");
      }
      else
      {
        // If the files should be moved...
        if (filesShouldBeMoved)
        {
          // Check to see if they were moved (including removal from tier2)
          if (FileAuditUtility.didFilesMove(documentAuditInfo, entry.getKey()))
          {
            logger.log(LogType.INFO, "File: " + entry.getKey()
                + " - Post-Audit:  File(s) successfully moved to tier3 storage");
          }
          else
          {
            logger.log(LogType.INFO, "File: " + entry.getKey()
                + " - Post-Audit:  Failed to move file(s) to tier3 storage. Abort");

            logger.log(LogType.INFO, "Post-Audit: Status = FAILURE");
            return false;
          }
        }
        else
        {
          // Files should have stayed on tier2. Let's make sure that's the case.
          if (FileAuditUtility.didFilesRemain(documentAuditInfo, entry.getKey()))
          {
            logger.log(LogType.INFO, "File: " + entry.getKey()
                + " - Post-Audit:  File(s) remained in tier2 storage as expected.");
          }
          else
          {
            logger.log(LogType.ERROR, "File: " + entry.getKey()
                + " - Post-Audit:  Failed to keep file(s) to tier2 storage. Abort");

            logger.log(LogType.INFO, "Post-Audit: Status = FAILURE");
            return false;
          }
        }
      }
    }

    // Now check the proof files which are not saved in the database.
    // Creating a list of file names. If the file didn't exist in tier2, they will be ignored
    List<String> proofFileNameList = new ArrayList<String>();

    // high res proof
    if (FileAuditUtility.didFileExistInTier2PriorToMove(documentAuditInfo, FileArchiveConstants.PDF_PROOF_HI_FILE_NAME))
    {
      proofFileNameList.add(FileArchiveConstants.PDF_PROOF_HI_FILE_NAME);
    }
    else
    {
      logger.log(LogType.INFO, "File: " + FileArchiveConstants.PDF_PROOF_HI_FILE_NAME
          + " - Post-Audit:  File not found on pre-move tier2. Ignore audit");
    }

    // first look proof
    if (FileAuditUtility.didFileExistInTier2PriorToMove(documentAuditInfo,
        FileArchiveConstants.PDF_PROOF_FIRST_LOOK_FILE_NAME))
    {
      proofFileNameList.add(FileArchiveConstants.PDF_PROOF_FIRST_LOOK_FILE_NAME);
    }
    else
    {
      logger.log(LogType.INFO, "File: " + FileArchiveConstants.PDF_PROOF_FIRST_LOOK_FILE_NAME
          + " - Post-Audit:  File not found on pre-move tier2. Ignore audit");
    }

    // Now loop through list and check to see if they're in the correct place.
    for (String fileName : proofFileNameList)
    {
      // proof files should be moved
      if (!documentAuditInfo.isConfigKeepPDF())
      {
        if (FileAuditUtility.didFilesMove(documentAuditInfo, fileName))
        {
          logger.log(LogType.INFO, "File: " + fileName
              + " - Post-Audit:  File(s) successfully moved to tier3 storage");
        }
        else
        {
          logger.log(LogType.ERROR, "File: " + fileName
              + " - Post-Audit:  Failed to move file(s) to tier3 storage. Abort");

          logger.log(LogType.INFO, "Post-Audit: Status = FAILURE");
          return false;
        }
      }
      else
      {
        // Configuration says files should be kept (on tier2)
        if (FileAuditUtility.didFilesRemain(documentAuditInfo, fileName))
        {
          logger.log(LogType.INFO, "File: " + fileName
              + " - Post-Audit:  File(s) remained in tier2 storage as expected.");
        }
        else
        {
          logger.log(LogType.ERROR, "File: " + fileName
              + " - Post-Audit:  Failed to keep file(s) to tier2 storage. Abort");
          logger.log(LogType.INFO, "Post-Audit: Status = FAILURE");
          return false;
        }
      }
    }

    // default to true if it gets past all checks
    logger.log(LogType.INFO, "Post-Audit: Status = SUCCESS");
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
