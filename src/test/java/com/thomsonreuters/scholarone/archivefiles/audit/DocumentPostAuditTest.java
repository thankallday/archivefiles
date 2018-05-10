package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;

public class DocumentPostAuditTest
{
  /* LW  - These tests are currently not being run sine they rely on specific documents in the db and file system
   * Need to investigate best way to handle setup and tear down for db and file system.
   */
  
  //@Test
  public void testFailedAudit()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746);

    DocumentAuditInfo documentAuditInfo = preAudit.performPreAudit();

    DocumentPostAudit documentPostAudit = new DocumentPostAudit(documentAuditInfo);

    assertTrue(!documentPostAudit.performPostAudit());

  }

  //@Test
  public void testRevertAudit()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746);

    DocumentAuditInfo documentAuditInfo = preAudit.performPreAudit();

    DocumentRevertAudit documentPostRevertAudit = new DocumentRevertAudit(documentAuditInfo);

    assertTrue(documentPostRevertAudit.performRevertAudit());

  }

}
