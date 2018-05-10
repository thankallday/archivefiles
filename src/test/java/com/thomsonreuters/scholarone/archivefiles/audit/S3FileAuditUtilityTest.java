package com.thomsonreuters.scholarone.archivefiles.audit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.scholarone.archivefiles.common.FileUtility;
import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;
import com.thomsonreuters.scholarone.archivefiles.ConfigPropertyValues;
import com.thomsonreuters.scholarone.archivefiles.UnZip;

public class S3FileAuditUtilityTest
{
  /* LW  - These tests are currently not being run sine they rely on specific documents in the db and file system
   * Need to investigate best way to handle setup and tear down for db and file system.
   */
  
  final String archiveCacheDir = ConfigPropertyValues.getProperty("archive.cache.dir");
  
  final File sourceDir = new File (archiveCacheDir + File.separator + "docfiles/dev4/bmm-iop/2015/02/274746");

  final String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");

  final String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");

  final S3File sourceS3Dir = new S3File("docfiles/dev4/bmm-iop/2015/02/274746/", sourceBucket);

  final S3File destinationS3Dir = new S3File("docfiles/2015/04/dev4/bmm-iop/274746/", destinationBucket); 
  
  @Before
  public void setup() throws IOException
  {
    if (sourceDir.exists())
      FileUtils.deleteDirectory(sourceDir);
    
    int exitCode;
    
    if (S3FileUtil.isDirectory(sourceS3Dir))
    {
      exitCode = S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
      Assert.assertTrue(exitCode == 0);
    }
    
    if (S3FileUtil.isDirectory(destinationS3Dir))
    {
      exitCode = S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
      Assert.assertTrue(exitCode == 0);
    }
    
    
    File tempDir = new File(archiveCacheDir);
    if (!tempDir.exists())
      tempDir.mkdirs();
    
    ArrayList<File> lists = new ArrayList<File>();
    File zipFile = new File("FSETest.zip");
    UnZip unzip = new UnZip();
    unzip.extract(zipFile.getPath(), sourceDir.getPath());
  
    FileUtility.listDir(sourceDir, lists);
    for(File f : lists)
    {
      if(f.isDirectory())
        continue;
      S3FileUtil.putFile(S3FileUtil.trimKey(f.getPath()), f, sourceS3Dir.getBucketName());
    }
  }
  
  @After
  public void teardown() throws IOException
  {  
    S3File taskLockFile = new S3File(sourceS3Dir.getKey() + "tier3move.lock", sourceS3Dir.getBucketName());
    if (S3FileUtil.exists(taskLockFile))
      S3FileUtil.deleteS3AllVersionsRecursive(taskLockFile);
  }
  
  @Test
  public void testFileStructure()
  {
    DocumentAuditInfo documentAuditInfo = new DocumentAuditInfo();
    documentAuditInfo.setDocumentId(274746);
    documentAuditInfo.setFileStoreYear(2015);
    documentAuditInfo.setFileStoreMonth(02);
    documentAuditInfo.setConfigShortName("bmm-iop");
    documentAuditInfo.setStackId(4);

    Map<String, List<String>> fileMap = null;
    try
    {
      fileMap = S3FileAuditUtility.getFilesAndPathsAsMap(S3FileAuditUtility.getTier2FilePath(documentAuditInfo, sourceS3Dir));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    assertTrue(fileMap != null && fileMap.size() > 0);

  }
}
