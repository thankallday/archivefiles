package com.thomsonreuters.scholarone.archivefiles.audit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scholarone.activitytracker.ref.LogType;
import com.thomsonreuters.scholarone.archivefiles.BaseDAO;

public class AuditFileMoveDAOImpl extends BaseDAO implements IAuditFileMoveDAO
{
  /*
   * retrieve basic iformation about the document and the config to which is belongs. (non-Javadoc)
   * 
   * @see com.thomsonreuters.scholarone.archivefiles.audit.IAuditFileMoveDAO#getDocumentAuditInfo(java.lang.Integer)
   */
  @Override
  public DocumentAuditInfo getDocumentAuditInfo(Integer documentId) throws SQLException
  {
    DocumentAuditInfo documentAuditInfo = null;

    if (connection != null)
    {
      PreparedStatement statement = null;
      try
      {
        statement = connection
            .prepareStatement("select o.short_name, d.document_id, d.file_store_year, d.file_store_month, d.archive_date, d.archive_status_id, coalesce(v.value, 'N') as value from organization o, document d, config c left join (select config_id, value from parameter_value pv, parameter p where pv.parameter_id = p.parameter_id and name = 'ARCHIVE_KEEP_PDF_PROOF_FL' and p.datetime_deleted is null and pv.datetime_deleted is null) as v on c.config_id = v.config_id where o.organization_id = c.organization_id and c.config_id = d.config_id and d.document_id = ?");
        statement.setInt(1, documentId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet != null && resultSet.next())
        {
          documentAuditInfo = new DocumentAuditInfo();
          documentAuditInfo.setDocumentId(resultSet.getInt("document_id"));
          documentAuditInfo.setConfigShortName(resultSet.getString("short_name"));
          documentAuditInfo.setFileStoreYear(resultSet.getInt("file_store_year"));
          documentAuditInfo.setFileStoreMonth(resultSet.getInt("file_store_month"));
          documentAuditInfo.setArchiveStatusId(resultSet.getInt("archive_status_id"));
          if (resultSet.getString("value").equals("Y"))
          {
            documentAuditInfo.setConfigKeepPDF(true);
          }
          else
          {
            documentAuditInfo.setConfigKeepPDF(false);
          }

          Timestamp archiveDate = resultSet.getTimestamp("archive_date");
          if (archiveDate != null)
          {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(archiveDate.getTime());
            documentAuditInfo.setArchiveMonth(cal.get(Calendar.MONTH) + 1);
            documentAuditInfo.setArchiveYear(cal.get(Calendar.YEAR));
          }
        }
      }
      catch (SQLException e)
      {
        throw e;
      }
      finally
      {
        if (statement != null)
        {
          try
          {
            statement.close();
          }
          catch (SQLException e)
          {
            logger.log(LogType.ERROR, e.getMessage());
          }
        }
      }

    }
    return documentAuditInfo;

  }

  /*
   * Get a map containing all of the files that belong to this document, according to the database. (non-Javadoc)
   * 
   * @see com.thomsonreuters.scholarone.archivefiles.audit.IAuditFileMoveDAO#getDocumentFileAuditInfo(java.lang.Integer)
   */
  @Override
  public Map<String, List<DocumentFileAuditInfo>> getDocumentFileAuditInfo(Integer documentId) throws SQLException
  {
    Map<String, List<DocumentFileAuditInfo>> documentFileMap = new HashMap<String, List<DocumentFileAuditInfo>>();

    if (connection != null)
    {
      PreparedStatement statement = null;
      StringBuilder stmt = new StringBuilder();
      stmt.append("select f.file_id, file_type_id, f.name, m.part_of_comments_fl ");
      stmt.append("from file f, mtm_file2doc m  ");
      stmt.append("where f.file_id = m.file_id ");
      stmt.append("and m.document_id = ? ");
      stmt.append("union ");
      stmt.append("select f.file_id, file_type_id, f.name, 0 ");
      stmt.append("from file f, mtm_docnote2file m, document_notes d ");
      stmt.append("where f.file_id = m.file_id ");
      stmt.append("and m.document_notes_id = d.document_notes_id ");
      stmt.append("and d.document_id = ? ");

      try
      {
        statement = connection.prepareStatement(stmt.toString());
        statement.setInt(1, documentId);
        statement.setInt(2, documentId);
        ResultSet results = statement.executeQuery();

        if (results != null)
        {
          while (results.next())
          {
            DocumentFileAuditInfo documentFileAuditInfo = new DocumentFileAuditInfo();
            documentFileAuditInfo.setFileId(results.getInt("file_id"));
            documentFileAuditInfo.setFileTypeId(results.getInt("file_type_id"));
            documentFileAuditInfo.setName(results.getString("name"));
            documentFileAuditInfo.setCoverletter(results.getBoolean("part_of_comments_fl"));

            if (!documentFileMap.containsKey(documentFileAuditInfo.getName()))
            {
              List<DocumentFileAuditInfo> fileList = new ArrayList<DocumentFileAuditInfo>();
              documentFileMap.put(documentFileAuditInfo.getName(), fileList);
            }
            documentFileMap.get(documentFileAuditInfo.getName()).add(documentFileAuditInfo);
          }
        }
      }
      catch (SQLException e)
      {
        throw e;
      }
      finally
      {
        if (statement != null)
        {
          try
          {
            statement.close();
          }
          catch (SQLException e)
          {
            logger.log(LogType.ERROR, e.getMessage());
          }
        }
      }

    }
    return documentFileMap;
  }
}
