package com.thomsonreuters.scholarone.archivefiles.audit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.scholarone.activitytracker.IHeader;
import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;

public class DocumentPreAudit
{
  private ILog logger = null;

  public DocumentPreAudit(Integer stackId, Integer documentId)
  {
    this.stackId = stackId;
    this.documentId = documentId;

    logger = new LogTrackerImpl(this.getClass().getName());
    ((IHeader)logger).clearLocalHeaders();
    
    ((IHeader)logger).addLocalHeader("ThreadId", Long.valueOf(Thread.currentThread().getId()).toString());      
    ((IHeader)logger).addLocalHeader("DocumentId", documentId.toString());
  }

  private Integer stackId;

  private Integer documentId;

  public Integer getStackId()
  {
    return stackId;
  }

  public void setStackId(Integer stackId)
  {
    this.stackId = stackId;
  }

  public int getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(Integer documentId)
  {
    this.documentId = documentId;
  }

  public DocumentAuditInfo performPreAudit()
  {
    logger.log(LogType.INFO, "Pre-Audit:  Begin");

    boolean success = true;

    IAuditFileMoveDAO db = new AuditFileMoveDAOImpl();
    db.openConnection(stackId);

    DocumentAuditInfo documentAuditInfo = null;
    try
    {
      logger.log(LogType.INFO, "Pre-Audit:  Retrieving document information");
      // Get document/config info
      documentAuditInfo = db.getDocumentAuditInfo(documentId);
      if (documentAuditInfo == null)
      {
        return null;
      }
      // Set stackID, which is needed later for post auditing
      documentAuditInfo.setStackId(stackId);
    }
    catch (SQLException e)
    {
      logger.log(LogType.ERROR, "Pre-Audit:  Exception thrown while retrieving document information.  " + e.getMessage());
      success = false;
    }

    // Double check to make sure the document is ready to have it's files moved
    if (documentAuditInfo.isFileMoveNeeded())
    {
      try
      {
        logger.log(LogType.INFO, "Pre-Audit:  Retrieving file information");
        // Get file info
        Map<String, List<DocumentFileAuditInfo>> documentFileAuditInfoList = db.getDocumentFileAuditInfo(documentId);
        if (documentFileAuditInfoList != null)
        {
          documentAuditInfo.setDocumentFilesMap(documentFileAuditInfoList);
          // Get snapshot of tier2 file system for the document
          documentAuditInfo.setPreAuditTier2Files(FileAuditUtility.getFilesAndPathsAsMap(FileAuditUtility
              .getTier2FilePath(documentAuditInfo)));
        }
        else
        {
          logger.log(LogType.INFO, "Pre-Audit:  No files found");
        }
      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, "Pre-Audit:  Exceptoin thrown while retrieving file information. " + e.getMessage());
        success = false;
      }
      catch (IOException e)
      {
        logger.log(LogType.ERROR, "Pre-Audit:  Exception thrown while building file structure. " + e.getMessage());
        success = false;
      }
    }
    else
    {
      logger.log(LogType.ERROR, "Pre-Audit:  Document not ready for archival. Abort");
      success = false;
    }

    db.closeConnection();

    // if no errors, return the audit object. Otherwise return null
    if (success)
    {
      return documentAuditInfo;
    }
    return null;
  }

}
