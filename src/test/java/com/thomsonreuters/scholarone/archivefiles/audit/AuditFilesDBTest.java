package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AuditFilesDBTest
{

  @Test
  public void testGetDocumentAuditInfo()
  {
    IAuditFileMoveDAO db = new AuditFileMoveDAOImpl();

    db.openConnection(4);
    DocumentAuditInfo documentAudit = null;
    try
    {
      documentAudit = db.getDocumentAuditInfo(274746);
    }
    catch (SQLException e)
    {
      fail(e.getMessage());
    }
    db.closeConnection();

    assertTrue(documentAudit != null && documentAudit.getDocumentId() > 0);
  }

  @Test
  public void testGetDocumentFileAuditInfo()
  {
    IAuditFileMoveDAO db = new AuditFileMoveDAOImpl();

    db.openConnection(4);
    Map<String, List<DocumentFileAuditInfo>> documentFileMap = null;
    
    try
    {
      documentFileMap = db.getDocumentFileAuditInfo(274746);
    }
    catch (SQLException e)
    {
      fail(e.getMessage());
    }
    db.closeConnection();

    assertTrue(documentFileMap != null && !documentFileMap.isEmpty());
  }

}
