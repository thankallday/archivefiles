package com.thomsonreuters.scholarone.archivefiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scholarone.archivefiles.common.FileUtility;
import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileNotFoundException;
import com.scholarone.archivefiles.common.S3FileUtil;
import com.scholarone.monitoring.common.Environment;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskLockTest
{
  Task task;

  String source = "/shared/gus/docfiles/dev4/fse/2015/12/282294";
  
  final String archiveCacheDir = ConfigPropertyValues.getProperty("archive.cache.dir");
  
  final File sourceDir = new File (archiveCacheDir + File.separator + "docfiles/dev4/fse/2015/12/282294");

  final String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");

  final String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");

  final S3File sourceS3Dir = new S3File("docfiles/dev4/fse/2015/12/282294/", sourceBucket);

  final S3File destinationS3Dir = new S3File("docfiles/2016/03/dev4/fse/282294/", destinationBucket); 
  
  final Environment envType = Environment.getEnvironmentType("DEV");
  

  @Before
  public void setup() throws IOException
  {    
    if (sourceDir.exists())
      FileUtils.deleteDirectory(sourceDir);
    
    int exitCode;
    
    if (S3FileUtil.isDirectory(sourceS3Dir))
    {
      exitCode = S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
      Assert.assertTrue(exitCode != 0);
    }
    
    if (S3FileUtil.isDirectory(destinationS3Dir))
    {
      exitCode = S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
      Assert.assertTrue(exitCode != 0);
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

    Integer stackId = Integer.valueOf(4);
    Config config = new Config();
    config.setConfigId(589);
    config.setShortName("fse");
    Document document = new Document();
    document.setDocumentId(282294);
    document.setFileStoreMonth(12);
    document.setFileStoreYear(2015);
    document.setArchiveMonth(03);
    document.setArchiveYear(2016);
    document.setRetryCount(0);

    File lockFile = new File(sourceDir.getPath() + File.separator + "tier3move.lock");
    if (lockFile.exists())
      lockFile.delete();
    S3File taskLockFile = new S3File(sourceS3Dir.getKey() + "tier3move.lock", sourceS3Dir.getBucketName());
    if (S3FileUtil.exists(taskLockFile))
        S3FileUtil.deleteS3AllVersionsRecursive(taskLockFile);
    
    Long runId = UUID.randomUUID().getLeastSignificantBits();   
  
    task = new Task(stackId, config, document, runId, ITask.NO_AUDIT, "dev", archiveCacheDir, sourceBucket, destinationBucket, "docfiles", envType, "dev");
  }

  @After
  public void teardown() throws IOException
  {    
    File lockFile = new File(sourceDir.getPath() + File.separator + "tier3move.lock");
    if (lockFile.exists())
      lockFile.delete();
    S3File taskLockFile = new S3File(sourceS3Dir.getKey() + "tier3move.lock", sourceS3Dir.getBucketName());
    if (S3FileUtil.exists(taskLockFile))
        S3FileUtil.deleteS3AllVersionsRecursive(taskLockFile);
    
    S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
    S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
  }

  @Test
  public void testABLock()
  {
    ILock taskLock = new TaskLock(task, archiveCacheDir);
    S3File sourceS3LockFile = new S3File(sourceS3Dir.getKey(), sourceS3Dir.getBucketName());
    
    boolean result = taskLock.lock();
    Assert.assertTrue(result);
    
    result = S3FileUtil.exists(sourceS3LockFile);
    Assert.assertTrue(result);
    
    result = taskLock.lock();
    Assert.assertTrue(result);
        
    taskLock.unlock();

  }

  @Test
  public void testBLockDifferentProcess() throws S3FileNotFoundException, IOException
  {
    ILock taskLock = new TaskLock(task, archiveCacheDir);

    Assert.assertTrue(taskLock.lock());

    // Change process
    BufferedReader in = null;
    PrintWriter out = null;
    File lockFile = null;
    try
    {
      lockFile = S3FileUtil.getFile(task.getSourceS3Dir().getKey() + "tier3move.lock", task.getSourceS3Dir().getBucketName());
      in = new BufferedReader(new FileReader(lockFile));
      String process = in.readLine();
      String time = in.readLine();
      in.close();

      out = new PrintWriter(lockFile);
      out.println(process + "xyz");
      out.println(time);
      out.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (in != null) in.close();
        if (out != null) out.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }

    S3FileUtil.putFile(task.getSourceS3Dir().getKey() + lockFile.getName(), lockFile, task.getSourceS3Dir().getBucketName());

    boolean result = taskLock.lock();
    Assert.assertFalse(result);
    taskLock.unlock();
  }

  @Test
  public void testCLockExpired() throws S3FileNotFoundException, IOException
  {
    ILock taskLock = new TaskLock(task, archiveCacheDir);

    Assert.assertTrue(taskLock.lock());

    // Change process
    BufferedReader in = null;
    PrintWriter out = null;
    File lockFile = null;

    try
    {
      lockFile = S3FileUtil.getFile(task.getSourceS3Dir().getKey() + "tier3move.lock", task.getSourceS3Dir().getBucketName());
      in = new BufferedReader(new FileReader(lockFile));
      String process = in.readLine();
      in.close();

      out = new PrintWriter(lockFile);
      out.println(process + "xyz");
      out.println("1");
      out.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (in != null) in.close();
        if (out != null) out.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }

    S3FileUtil.putFile(task.getSourceS3Dir().getKey() + lockFile.getName(), lockFile, task.getSourceS3Dir().getBucketName());
    
    boolean result = taskLock.lock();
    Assert.assertTrue(result);
    taskLock.unlock();
  }
}
