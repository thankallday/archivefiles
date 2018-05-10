package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.scholarone.archivefiles.common.S3File;
import com.thomsonreuters.scholarone.archivefiles.ConfigPropertyValues;

public class DocumentPreAuditTest
{

  /* LW  - These tests are currently not being run sine they rely on specific documents in the db and file system
   * Need to investigate best way to handle setup and tear down for db and file system.
   */
  private String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");
  private String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");
  private S3File sourceS3Dir = new S3File("docfiles/dev4/bmm-iop/2015/02/274746/", sourceBucket);
  private S3File destinationS3Dir = new S3File("docfiles/2015/04/dev4/bmm-iop/274746/", destinationBucket); 
  
  @Test
  public void testInvalidDocumentId()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, -274746, sourceS3Dir, destinationS3Dir );  // bmm-iop, BMM-100681
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    assertTrue(documentAudit == null);
  }

  @Test
  public void testFindDocumentAuditInfo()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746, sourceS3Dir, destinationS3Dir );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    assertTrue(documentAudit != null && documentAudit.getDocumentId() > 0);
  }
  
  @Test
  public void testDocumentShouldBeArchived()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746, sourceS3Dir, destinationS3Dir );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    
    assertTrue(documentAudit != null && (documentAudit.getArchiveStatusId() == 1 || documentAudit.getArchiveStatusId() == 32));
  }
  
  @Test
  public void testDocumentShouldNotBeArchived()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 276176, sourceS3Dir, destinationS3Dir );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    
    assertTrue(documentAudit == null);
  }

  @Test
  public void testDocumentHasFileInfo()
  {
    DocumentPreAudit preAudit = new DocumentPreAudit(4, 274746, sourceS3Dir, destinationS3Dir );
    
    DocumentAuditInfo documentAudit = preAudit.performPreAudit();
    
    assertTrue(documentAudit != null && !documentAudit.getDocumentFilesMap().isEmpty());
  }

}
