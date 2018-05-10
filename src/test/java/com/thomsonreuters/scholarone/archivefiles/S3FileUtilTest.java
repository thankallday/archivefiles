package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scholarone.activitytracker.TrackingInfo;
import com.scholarone.archivefiles.common.FileUtility;
import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class S3FileUtilTest
{
  
  String tempDir = ConfigPropertyValues.getProperty("archive.cache.dir");
  
  File sourceDir = new File (tempDir + File.separator + "docfiles/dev4/bmm-iop/2015/02/274746");

  String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");

  String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");

  S3File sourceS3Dir = new S3File("docfiles/dev4/bmm-iop/2015/02/274746/", sourceBucket);

  S3File destinationS3Dir = new S3File("docfiles/2015/04/dev4/bmm-iop/274746/", destinationBucket); 

  @Test
  public void testAInit() throws IOException
  {
    if (sourceDir.exists())
      FileUtils.deleteDirectory(sourceDir);
    
    S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
    S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
    
    Assert.assertFalse(S3FileUtil.exists(sourceS3Dir));
    Assert.assertFalse(S3FileUtil.exists(destinationS3Dir));
    
    File archiveCacheDir = new File(tempDir);
    if (!archiveCacheDir.exists())
      archiveCacheDir.mkdirs();
  }
  
  @Test
  public void testBUploadZipFileToS3() throws Exception
  {
    ArrayList<File> lists = new ArrayList<File>();
    File zipFile = new File("FSETest.zip");
    UnZip unzip = new UnZip();
    unzip.extract(zipFile.getPath(), sourceDir.getPath());

    FileUtility.listDir(sourceDir, lists);
    for(File f : lists)
    {
      if(f.isDirectory())
        continue;
      String key = S3FileUtil.trimKey(f.getPath());
      S3FileUtil.putFile(key, f, sourceS3Dir.getBucketName());
    }
  }

  @Test
  public void testCCreateS3File() throws Exception
  {
    PrintWriter out = null;
    File dir = new File(tempDir + File.separator);
    if (!dir.exists()) dir.mkdirs();
    File lockFile = new File(dir, "tier3move.lock");
    out = new PrintWriter(lockFile);
    out.println(Runtime.getRuntime().toString());
    out.println(new Date().getTime());
    out.close(); 
    S3FileUtil.putFile(sourceS3Dir.getKey() + "tier3move.lock", lockFile, sourceS3Dir.getBucketName());
    lockFile.delete();
    Assert.assertTrue(S3FileUtil.exists(sourceS3Dir.getKey() + "tier3move.lock", sourceS3Dir.getBucketName()));
  }
  
  @Test
  public void testDCopy()
  {
    StatInfo stat = new StatInfo();
    ArrayList<String> exclusions = new ArrayList<String>();
    int exitCode = S3FileUtil.copyS3Dir(sourceS3Dir, destinationS3Dir, exclusions, stat);
    Assert.assertTrue(exitCode == 0);
  
    boolean exists = S3FileUtil.exists(destinationS3Dir.getKey() + "docfiles/tex/home/rbb/sources/LaTeX/bibtex/bst/nf.bst", destinationS3Dir.getBucketName());
    Assert.assertTrue(exists);
    
    exitCode = S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
    Assert.assertTrue(exitCode == 0);
  }

  
  @Test
  public void testECopyMissingDirectory() throws IOException
  {
    S3File missingS3Dir = new S3File("docfiles/dev4/missing_directory/", sourceS3Dir.getBucketName());
    S3File targetS3Dir = new S3File("docfiles/2015/04/dev4/missing_directory/", destinationS3Dir.getBucketName());
    ArrayList<String> exclusions = new ArrayList<String>();
    StatInfo stat = new StatInfo();
    int exitCode = S3FileUtil.copyS3Dir(missingS3Dir, targetS3Dir, exclusions, stat);
    
    Assert.assertTrue(exitCode != 0);
  }
  
  @Test
  public void testFDelete() throws IOException
  {

    int exitCode = S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
    Assert.assertTrue(exitCode == 0);
  }
  
  @Test
  public void testGDeleteMissingDirectory() throws IOException
  {
    S3File missingS3Dir = new S3File("docfiles/dev4/missing_directory2/", sourceS3Dir.getBucketName());
    int exitCode = S3FileUtil.deleteS3AllVersionsRecursive(missingS3Dir);
    Assert.assertTrue(exitCode != 0);
  }
  
  //@Test
  public void testHDeleteWithExclusions() throws Exception
  {
   // See Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File0 )); in S3FileUtilTest2
  }

  @Test
  public void testIsDirectory()
  {
    S3File emptyDirectoryEndingWithSlash = new S3File("docfiles/dev4/prod4-qared/2001/", sourceS3Dir.getBucketName()); //empty directory
    boolean result = S3FileUtil.isDirectory(emptyDirectoryEndingWithSlash);
    Assert.assertTrue(result);
    
    S3File emptyDirectoryEndingWithoutSlash = new S3File("docfiles/dev4/prod4-qared/2001", sourceS3Dir.getBucketName()); //empty directory
    result = S3FileUtil.isDirectory(emptyDirectoryEndingWithoutSlash);
    Assert.assertTrue(result);
    
    S3File nonEmptyDirectoryEndingWithSlash = new S3File("docfiles/dev4/prod4-qared/data-feed-export/", sourceS3Dir.getBucketName()); //non empty directory
    result = S3FileUtil.isDirectory(nonEmptyDirectoryEndingWithSlash);
    Assert.assertTrue(result);
    
    S3File nonEmptyDirectoryEndingWithoutSlash = new S3File("docfiles/dev4/prod4-qared/data-feed-export", sourceS3Dir.getBucketName()); //non empty directory
    result = S3FileUtil.isDirectory(nonEmptyDirectoryEndingWithoutSlash);
    Assert.assertTrue(result);
    
    S3File existfile = new S3File("docfiles/dev4/prod4-qared/data-feed-export/prod4-qared_dataFeed_10-08-2017-22-00.zip", sourceS3Dir.getBucketName()); //existent file
    result = S3FileUtil.isDirectory(existfile);
    Assert.assertFalse(result);
    
    S3File notExistDirectory = new S3File("docfile/dev/prod4-qared/1900/", sourceS3Dir.getBucketName()); //non existent directory
    result = S3FileUtil.isDirectory(notExistDirectory);
    Assert.assertFalse(result);
    
    S3File notExistFile = new S3File("docfiles/dev4/prod4-qared/data-feed-export/abc.zip", sourceS3Dir.getBucketName()); //non existent file
    result = S3FileUtil.isDirectory(notExistFile);
    Assert.assertFalse(result);
  }
  
  //@Test - DO NOT RUN this
  public void testITearDown() throws IOException
  {    
    FileUtils.deleteDirectory(sourceDir);
    S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
    //S3FileUtil.shutdownS3Daemons();
  }
  
}
