package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileAuditUtilityTest
{
  /* LW  - These tests are currently not being run sine they rely on specific documents in the db and file system
   * Need to investigate best way to handle setup and tear down for db and file system.
   */
  //@Test
  public void testFileStructure()
  {
    DocumentAuditInfo documentAuditInfo = new DocumentAuditInfo();
    documentAuditInfo.setDocumentId(274746);
    documentAuditInfo.setFileStoreYear(2015);
    documentAuditInfo.setFileStoreMonth(10);
    documentAuditInfo.setConfigShortName("dir");
    documentAuditInfo.setStackId(4);

    Map<String, List<String>> fileMap = null;
    try
    {
      fileMap = FileAuditUtility.getFilesAndPathsAsMap(FileAuditUtility.getTier2FilePath(documentAuditInfo));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    assertTrue(fileMap != null && fileMap.size() > 0);

  }
}
