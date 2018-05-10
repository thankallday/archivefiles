package com.thomsonreuters.scholarone.archivefiles;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface IArchiveFilesDAO
{
  public abstract boolean openConnection(Integer stackId);

  public abstract void closeConnection();

  public abstract List<Config> getConfigs() throws SQLException;

  public abstract List<Document> getDocuments(Integer configId, Integer rows) throws SQLException;
  
  public abstract List<DocumentFile> getFiles(Integer documentId) throws SQLException;  
  
  public abstract boolean updateDocument(Integer documentId, Timestamp movedDate, Integer retryCount) throws SQLException;
}