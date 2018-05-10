package com.thomsonreuters.scholarone.archivefiles;

import java.sql.SQLException;
import java.sql.Statement;

import com.scholarone.activitytracker.ref.LogType;

public class ArchiveFilesDAOTestImpl extends ArchiveFilesDAOImpl
{

  public boolean addDocument()
  {
    boolean success = false;

    String insertSQL = "insert into document (document_id, datetime_added, added_by) values (0, current timestamp, -3141593)";
    
    Statement statement = null;
    if (connection != null)
    {
      try
      {
        statement = connection.createStatement();
        int r = statement.executeUpdate(insertSQL);
        if ( r == 1 )
          success = true;
      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, e.getMessage());
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

    return success;
  }
  
  public boolean deleteDocument()
  {
    boolean success = false;

    String deleteSQL = "delete from document where document_id = 0";
    
    Statement statement = null;
    if (connection != null)
    {
      try
      {
        statement = connection.createStatement();
        int r = statement.executeUpdate(deleteSQL);
        if ( r == 1 )
          success = true;
      }
      catch (SQLException e)
      {
        logger.log(LogType.ERROR, e.getMessage());
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

    return success;
  }
}
