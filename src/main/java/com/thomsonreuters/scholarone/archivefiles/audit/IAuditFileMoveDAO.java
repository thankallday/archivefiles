package com.thomsonreuters.scholarone.archivefiles.audit;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IAuditFileMoveDAO
{
  public abstract boolean openConnection(Integer stackId);

  public abstract void closeConnection();

  public abstract DocumentAuditInfo getDocumentAuditInfo(Integer documentId) throws SQLException;

  public abstract Map<String, List<DocumentFileAuditInfo>> getDocumentFileAuditInfo(Integer documentId)
      throws SQLException;

}