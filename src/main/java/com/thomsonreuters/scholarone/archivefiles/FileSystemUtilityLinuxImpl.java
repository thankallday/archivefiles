package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.scholarone.activitytracker.ILog;
import com.scholarone.activitytracker.TrackingInfo;
import com.scholarone.activitytracker.ref.LogTrackerImpl;
import com.scholarone.activitytracker.ref.LogType;

public class FileSystemUtilityLinuxImpl implements IFileSystemUtility
{
  private static ILog logger = null;

  private Integer stackId;
  
  public FileSystemUtilityLinuxImpl(Integer stackId)
  {
    if (logger == null)
    {
      logger = new LogTrackerImpl(this.getClass().getName());
    }
    
    this.stackId = stackId;
  }

  public int copy(String source, String destination) throws IOException
  {
    return copy(source, destination, new ArrayList<String>());
  }

  public int copy(String source, String destination, List<String> exclusions) throws IOException
  {
    return copy(source, destination, exclusions, null);
  }

  public int copy(String source, String destination, List<String> exclusions, TrackingInfo stat)
      throws IOException
  {
    File destFile = new File(destination);
    if (!destFile.exists())
    {
      destFile.mkdirs();
    }

    List<String> commands = new ArrayList<String>();
    commands.add("rsync");
    commands.add("-arvP");    
    commands.add("-stats");

    String excludeFile = source + "/exclude_from.txt";
    try
    {
      FileWriter writer = new FileWriter(excludeFile, true);
      for (String filename : exclusions)
      {
        filename = filename.replace(" ", "*");
        filename = filename.replace("#", "*");
        
        int n =  filename.lastIndexOf('.');
        String extension = filename.substring(n + 1);
        if ( n > -1 && ( extension.indexOf('[') != -1 
            || extension.indexOf(']') != -1 ))
        {
          String name = filename.substring(0,  n);
          name = name.replace("[", "\\[");
          name = name.replace("]", "\\]");
          extension = extension.replace("[", "*");
          extension = extension.replace("]", "*");
          
          filename = name + "." + extension;
        }
        else
        {
          filename = filename.replace("[", "\\[");
          filename = filename.replace("]", "\\]");
        }
                        
        writer.write(filename + "\r\n");
      }
      writer.close();
      
      commands.add("--exclude-from=" + excludeFile);
    }
    catch (Exception e)
    {
      logger.log(LogType.ERROR, "Failed to write exclusions list - " + excludeFile + " - " + e.getMessage());
    }
    
    commands.add(source);
    commands.add(destination);

    ProcessBuilder pb = new ProcessBuilder().command(commands);
    pb.redirectOutput();
    Process iostat = pb.start();

    if (stat != null) ParseRSYNC.parse(iostat.getInputStream(), stat, stackId);

    int exitCode = 0;

    try
    {
      exitCode = iostat.waitFor();
      if ( exitCode != 0 )
      {
        logger.log(LogType.ERROR, "Failed to copy. Exit code: " + exitCode + ", Command=" + commands );
      }
    }
    catch (InterruptedException e)
    {
      logger.log(LogType.ERROR, e.getMessage());
    }

    File file = new File(excludeFile);
    file.delete();

    return exitCode;
  }

  public int delete(String source, boolean recurse) throws IOException
  {
    String[] command = { "rm", (recurse ? "-rf" : "-f"), source };

    Process iostat = new ProcessBuilder().command(command).inheritIO().start();

    int exitCode = 0;

    try
    {
      exitCode = iostat.waitFor();
      if ( exitCode != 0 )
      {
        logger.log(LogType.ERROR, "Failed to delete. Exit code: " + exitCode + ", Command=" + command );
      }
    }
    catch (InterruptedException e)
    {
      logger.log(LogType.ERROR, e.getMessage());
    }

    return exitCode;
  }

  public int delete(String source, boolean recurse, List<String> exclusions) throws IOException
  {
    if (exclusions == null || exclusions.size() == 0) return delete(source, recurse);

    ArrayList<String> command1 = new ArrayList<String>();
    command1.add("bash");
    command1.add("-c");

    String c1 = "find . -depth ";
    for (String filename : exclusions)
    {
      filename = filename.replace("[", "\\[");
      filename = filename.replace("]", "\\]");
      filename = filename.replace("\"", "\\\"");
      filename = filename.replace("`", "\\`");
      
      c1 += ("! -name \"" + filename + "\" ");
    }
    c1 += ("-type f -exec rm -rf {} \\;");

    command1.add(c1);

    ArrayList<String> command2 = new ArrayList<String>();
    command2.add("bash");
    command2.add("-c");
    command2.add(new String("find . -depth -type d -empty -exec rmdir {} \\;"));

    int exitCode = 0;

    ProcessBuilder pb = new ProcessBuilder().command(command1).inheritIO();
    pb.directory(new File(source));
    Process iostat = pb.start();

    try
    {
      exitCode = iostat.waitFor();
    }
    catch (InterruptedException e)
    {
      logger.log(LogType.ERROR, e.getMessage());
    }

    if (exitCode == 0)
    {
      pb = new ProcessBuilder().command(command2).inheritIO();
      pb.directory(new File(source));
      iostat = pb.start();

      try
      {
        exitCode = iostat.waitFor();
      }
      catch (InterruptedException e)
      {
        logger.log(LogType.ERROR, e.getMessage());
      }
      
      if ( exitCode != 0 )
      {
        logger.log(LogType.ERROR, "Failed to delete with exclusions. Exit code: " + exitCode + ", Command2=" + command2 );
      }
    }
    else
    {
      logger.log(LogType.ERROR, "Failed to delete with exculsions. Exit code: " + exitCode + ", Command1=" + command1 );
    }

    return exitCode;
  }
}
