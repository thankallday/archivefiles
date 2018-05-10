package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;

import com.scholarone.archivefiles.common.S3File;
import com.thomsonreuters.scholarone.archivefiles.ConfigPropertyValues;

public class DocumentPostAuditTest
{
  /* LW  - These tests are currently not being run sine they rely on specific documents in the db and file system
   * Need to investigate best way to handle setup and tear down for db and file system.
   */
  private String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");
  private String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");
  private S3File sourceS3Dir = new S3File("docfiles/dev4/bmm-iop/2015/02/274746/", sourceBucket);
  private S3File destinationS3Dir = new S3File("docfiles/2015/04/dev4/bmm-iop/274746/", destinationBucket); 
  
  //@Test
  public void testFailedAudit()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746, sourceS3Dir, destinationS3Dir);  // bmm-iop, BMM-100681

    DocumentAuditInfo documentAuditInfo = preAudit.performPreAudit();

    DocumentPostAudit documentPostAudit = new DocumentPostAudit(documentAuditInfo, sourceS3Dir, destinationS3Dir);

    assertTrue(!documentPostAudit.performPostAudit());

  }

  //@Test
  public void testRevertAudit()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746, sourceS3Dir, destinationS3Dir);

    DocumentAuditInfo documentAuditInfo = preAudit.performPreAudit();

    DocumentRevertAudit documentPostRevertAudit = new DocumentRevertAudit(documentAuditInfo, sourceS3Dir, destinationS3Dir);

    assertTrue(documentPostRevertAudit.performRevertAudit());

  }

}
