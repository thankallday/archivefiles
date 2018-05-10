package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;

public class DocumentPreAuditTest
{

  /* LW  - These tests are currently not being run sine they rely on specific documents in the db and file system
   * Need to investigate best way to handle setup and tear down for db and file system.
   */
  //@Test
  public void testInvalidDocumentId()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, -274746 );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    assertTrue(documentAudit == null);
  }

  //@Test
  public void testFindDocumentAuditInfo()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746 );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    assertTrue(documentAudit != null && documentAudit.getDocumentId() > 0);
  }
  
  //@Test
  public void testDocumentShouldBeArchived()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746 );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    
    assertTrue(documentAudit != null && (documentAudit.getArchiveStatusId() == 1 || documentAudit.getArchiveStatusId() == 32));
  }
  
  //@Test
  public void testDocumentShouldNotBeArchived()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 276176 );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    
    assertTrue(documentAudit == null);
  }

  //@Test
  public void testDocumentHasFileInfo()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746 );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    
    assertTrue(documentAudit != null && !documentAudit.getDocumentFilesMap().isEmpty());
  }

}
