package com.thomsonreuters.scholarone.archivefiles;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.scholarone.activitytracker.ref.LogType;

public class ArchiveFilesDAOImpl extends BaseDAO implements IArchiveFilesDAO
{
  /*
   * (non-Javadoc)
   * 
   * @see com.thomsonreuters.scholarone.cand.IArchivesFilesDAO#getConfigs()
   */
  @Override
  public List<Config> getConfigs() throws SQLException
  {
    List<Config> configs = new ArrayList<Config>();
    HashMap<Integer, Config> configMap = new HashMap<Integer, Config>();

    PreparedStatement statement = null;
    if (connection != null)
    {
      try
      {
        String query = "select c.config_id, o.short_name, coalesce(v.value, 'N') as value from organization o, config c left outer join "
            + "(select config_id, value from  parameter_value pv, parameter p where p.parameter_id = pv.parameter_id and "
            + "p.name = 'ARCHIVE_KEEP_PDF_PROOF_FL' and p.datetime_deleted is null and pv.datetime_deleted is null) as v "
            + "on c.config_id = v.config_id where c.organization_id = o.organization_id for fetch only";

        statement = connection.prepareStatement(query);

        ResultSet rs = statement.executeQuery();
        while (rs.next())
        {
          Config config = new Config();
          config.setConfigId(rs.getInt("CONFIG_ID"));
          config.setShortName(rs.getString("SHORT_NAME"));
          config.setKeepPDF(rs.getString("VALUE").equalsIgnoreCase("Y"));

          configs.add(config);
          configMap.put(config.getConfigId(), config);
        }

      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, e.getMessage()); 
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
    else
    {
      throw new SQLException("There is no database connection");
    }

    return configs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.thomsonreuters.scholarone.cand.IArchivesFilesDAO#getDocuments(java.lang.Integer)
   */
  @Override
  public List<Document> getDocuments(Integer configId, Integer rows) throws SQLException
  {
    List<Document> documents = new ArrayList<Document>();

    PreparedStatement statement = null;
    if (connection != null)
    {
      try
      {
        String query = "select t.DOCUMENT_ID, t.move_retry_count, t.archive_date, t.file_store_year, t.file_store_month "
            + "from ( "
            + "select "
            + "    ROWNUMBER() OVER() rownum, "
            + "    d.DOCUMENT_ID,"
            + "    d.move_retry_count,"
            + "    d.archive_date, "
            + "    d.file_store_year, "
            + "    d.file_store_month "
            + "from document d "
            + "where "
            + "    d.config_id = ? and "
            + "    d.ARCHIVE_DATE is not null and "
            + "    d.archive_date < current timestamp and "
            + "    d.archive_status_id in (1,32) and "
            + "    d.datetime_moved_tier3 is null and "
            + "    (d.move_retry_count is null or d.move_retry_count < 5) "
            + ") t where t.rownum <= ? ";

        statement = connection.prepareStatement(query);

        statement.setInt(1, configId);
        statement.setInt(2, rows);

        ResultSet rs = statement.executeQuery();

        while (rs.next())
        {
          Document document = new Document();
          document.setDocumentId(rs.getInt("DOCUMENT_ID"));
          document.setRetryCount((int) rs.getShort("MOVE_RETRY_COUNT"));
          document.setFileStoreMonth(rs.getInt("FILE_STORE_MONTH"));
          document.setFileStoreYear(rs.getInt("FILE_STORE_YEAR"));

          Timestamp archiveDate = rs.getTimestamp("ARCHIVE_DATE");
          Calendar cal = Calendar.getInstance();
          cal.setTimeInMillis(archiveDate.getTime());
          document.setArchiveMonth(cal.get(Calendar.MONTH) + 1);
          document.setArchiveYear(cal.get(Calendar.YEAR));
          documents.add(document);
          
          logger.log(LogType.INFO, "Include config [" + configId + "], document [" + document.getDocumentId() + "] for processing");
        }
      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, "ConfigId: " + configId + " - " + e.getMessage());
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
            logger.log(LogType.ERROR, "ConfigId: " + configId + " - " + e.getMessage());
          }
        }
      }
    }
    else
    {
      throw new SQLException("There is no database connection");
    }

    return documents;
  }

  @Override
  public List<DocumentFile> getFiles(Integer documentId) throws SQLException
  {
    List<DocumentFile> files = new ArrayList<DocumentFile>();

    PreparedStatement statement = null;
    if (connection != null)
    {
      try
      {
        String query = "select f.name, f.file_type_id, f2d.part_of_comments_fl from file f, mtm_file2doc f2d "
            + "where f2d.document_id = ? and f2d.file_id = f.file_id "
            + "union "
            + "select f.name, f.file_type_id, 0 from file f, mtm_docnote2file d2f, document_notes dn "
            + "where dn.document_id = ? and dn.document_notes_id = d2f.document_notes_id and d2f.file_id = f.file_id";

        statement = connection.prepareStatement(query);
        statement.setInt(1, documentId);
        statement.setInt(2, documentId);
        
        ResultSet rs = statement.executeQuery();

        while (rs.next())
        {
          DocumentFile file = new DocumentFile();
          file.setName(rs.getString("NAME"));
          file.setType(rs.getInt("FILE_TYPE_ID"));
          file.setCoverletter(rs.getBoolean("PART_OF_COMMENTS_FL"));
          files.add(file);
        }
      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, "DocumentId: " + documentId + " - " + e.getMessage());
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
            logger.log(LogType.ERROR, "DocumentId: " + documentId + " - " + e.getMessage());
          }
        }
      }
    }
    else
    {
      throw new SQLException("There is no database connection");
    }

    return files;
  }

  public boolean updateDocument(Integer documentId, Timestamp movedDate, Integer retryCount) throws SQLException
  {
    boolean success = false;

    PreparedStatement statement = null;
    if (connection != null)
    {
      try
      {
        String sql = "UPDATE DOCUMENT "
            + "SET DATETIME_MOVED_TIER3 = ?, "
            + "MOVE_RETRY_COUNT = ?, "
            + "UNARCHIVE_MOVE_RETRY_COUNT = 0, " //reset for unarchiving again later
            + "MODIFIED_BY = ? "
            + "WHERE DOCUMENT_ID = ?";
        
        statement = connection.prepareStatement(sql);
        statement.setTimestamp(1, movedDate);
        if ( retryCount != null )
          statement.setShort(2, retryCount.shortValue());
        else
          statement.setShort(2, (short) 0);
        statement.setInt(3, 0);
        statement.setInt(4, documentId);
        
        statement.executeUpdate();
        success = true;
      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, "DocumentId: " + documentId + " - " + e.getMessage());
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
            logger.log(LogType.ERROR, "DocumentId: " + documentId + " - " + e.getMessage());
          }
        }
      }
    }
    else
    {
      throw new SQLException("There is no database connection");
    }

    return success;
  }
}
