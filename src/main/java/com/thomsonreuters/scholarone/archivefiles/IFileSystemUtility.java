package com.thomsonreuters.scholarone.archivefiles;

import java.io.IOException;
import java.util.List;

import com.scholarone.activitytracker.TrackingInfo;

public interface IFileSystemUtility
{
  public int copy(String source, String destination) throws IOException;
  
  public int copy(String source, String destination, List<String> exclusions) throws IOException;
  
  public int copy(String source, String destination, List<String> exclusions, TrackingInfo stat) throws IOException;
  
  public int delete(String source, boolean recurse) throws IOException;
  
  public int delete(String source, boolean recurse, List<String> exclusions) throws IOException;
}
