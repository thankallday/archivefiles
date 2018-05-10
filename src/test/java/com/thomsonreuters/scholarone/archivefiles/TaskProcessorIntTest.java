package com.thomsonreuters.scholarone.archivefiles;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContextTest.xml")
public class TaskProcessorIntTest
{  
  String source = "/shared/gus/docfiles/dev4/fse/2015/12/282294";

  String destination = "/shared/gus_archive/docfiles/2015/12/dev4/fse/282294";

  @Before
  public void setup() throws IOException
  {
    File zipFile = new File("FSETest.zip");
    UnZip unzip = new UnZip();
    unzip.extract(zipFile.getPath(), source);
    
    ArchiveFilesDAOTestImpl db = new ArchiveFilesDAOTestImpl();
    db.openConnection(4);
    
    db.deleteDocument();
    db.addDocument();
    
    db.closeConnection();
  }
  
  @After
  public void teardown() throws IOException
  {
    ArchiveFilesDAOTestImpl db = new ArchiveFilesDAOTestImpl();
    db.openConnection(4);    
    db.deleteDocument();
    db.closeConnection();
    
    File sourceFile = new File(source);
    File destFile = new File(destination);
    
    FileUtils.deleteDirectory(sourceFile);    
    FileUtils.deleteDirectory(destFile);
  }
  
  @Test
  public void testProcess() throws IOException
  {
    List<ITask> tasks = new ArrayList<ITask>();

    Integer stackId = Integer.valueOf(4);
    Config config = new Config();
    config.setConfigId(963);
    config.setShortName("fse");
    Document document = new Document();
    document.setDocumentId(282294);
    document.setFileStoreMonth(12);
    document.setFileStoreYear(2015);
    document.setArchiveMonth(12);
    document.setArchiveYear(2015);
    document.setRetryCount(0);
    
    DocumentFile file1 = new DocumentFile();
    file1.setName("282294_File000000_4675773.doc");
    file1.setType(1);
    DocumentFile file2 = new DocumentFile();
    file2.setName("282294_File000004_4675785.jpg");
    file2.setType(4);
    DocumentFile file3 = new DocumentFile();
    file3.setName("282294_File000002_4675779.xlsx");
    file3.setType(37);
    
    ArrayList<DocumentFile> files = new ArrayList<DocumentFile>();
    files.add(file1);
    files.add(file2);
    files.add(file3);
    document.setFiles(files);
    
    Long runId = UUID.randomUUID().getLeastSignificantBits();
    ITask task = new Task(stackId, config, document, runId, ITask.NO_AUDIT);
    tasks.add(task);
    
    TaskProcessor p = new TaskProcessor(stackId, runId, new TaskFactoryImpl(stackId, runId));
    p.setTasks(tasks);
    p.start();

    try
    {
      while (p.isAlive())
      {
        Thread.sleep(5000);
      }
    }
    catch (InterruptedException ie)
    {
      fail(ie.getMessage());
    }
    
    Assert.assertTrue(1 == p.getCompletedCount());
    
    File test1 = new File(source + "/docfiles/original-files/", "282294_File000000_4675773.doc");
    Assert.assertFalse(test1.exists());
    
    File test2 = new File(source + "/docfiles/original-files/", "282294_File000004_4675785.jpg");
    Assert.assertTrue(test2.exists());
    
    File test3 = new File(source + "/docfiles/original-files/", "282294_File000002_4675779.xlsx");
    Assert.assertTrue(test3.exists());
  }
  
  @Test
  public void testRevertProcess() throws IOException
  {    
    List<ITask> tasks = new ArrayList<ITask>();

    Integer stackId = Integer.valueOf(4);
    Config config = new Config();
    config.setConfigId(963);
    config.setShortName("fse");
    Document document = new Document();
    document.setDocumentId(282294);
    document.setFileStoreMonth(12);
    document.setFileStoreYear(2015);
    document.setArchiveMonth(12);
    document.setArchiveYear(2015);
    
    ITask task = new RevertTask(stackId, config, document);
    tasks.add(task);
    
    Long runId = UUID.randomUUID().getLeastSignificantBits();
    
    TaskProcessor p = new TaskProcessor(stackId, runId, new TaskFactoryImpl(stackId, runId));
    p.setTasks(tasks);
    p.start();

    try
    {
      while (p.isAlive())
      {
        Thread.sleep(300);
      }
    }
    catch (InterruptedException ie)
    {
      fail(ie.getMessage());
    }
    
    Assert.assertTrue(1 == p.getCompletedCount());
  }
}
