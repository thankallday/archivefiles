package com.thomsonreuters.scholarone.archivefiles;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class ArchiveFilesDBTest
{

  @Test
  public void testGetConfigs()
  {
    IArchiveFilesDAO db = new ArchiveFilesDAOTestImpl();
    
    db.openConnection(4);
    List<Config> configs = new ArrayList<Config>();
    try
    {
      configs = db.getConfigs();
    }
    catch (SQLException e)
    {
      fail(e.getMessage());
    }
    db.closeConnection();
    
    assertTrue(configs.size() > 0);
  }

  @Test
  public void testGetDocuments()
  {
    IArchiveFilesDAO db = new ArchiveFilesDAOTestImpl();
    
    db.openConnection(4);
    List<Document> documents = new ArrayList<Document>();
    try
    {
      documents = db.getDocuments(49, 100);
    }
    catch (SQLException e)
    {
      fail(e.getMessage());
    }
    db.closeConnection();
    
    assertTrue(documents.size() > 0);
  }
  
  @Test
  public void testGetFiles()
  {
    IArchiveFilesDAO db = new ArchiveFilesDAOTestImpl();
    
    db.openConnection(4);
    List<DocumentFile> files = new ArrayList<DocumentFile>();
    try
    {
      files = db.getFiles(100);
    }
    catch (SQLException e)
    {
      fail(e.getMessage());
    }
    db.closeConnection();
    
    assertTrue(files.size() > 0);
  }
  
  @Test
  public void testUpdateDocument()
  {  
    ArchiveFilesDAOTestImpl db = new ArchiveFilesDAOTestImpl();
    Date currentDate = new Date();
    
    db.openConnection(4);
    
    // Setup
    db.deleteDocument();
    db.addDocument();
   
    boolean rv = false;
    try
    {
      rv = db.updateDocument(0, new Timestamp(currentDate.getTime()), 0);
    }
    catch (SQLException e)
    {
      fail(e.getMessage());
    }
    
    db.deleteDocument();
    
    db.closeConnection();
    
    assertTrue(rv);
  }
}
