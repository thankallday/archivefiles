package com.thomsonreuters.scholarone.archivefiles;

public interface ILock
{
  public boolean lock();
  
  public void unlock();
}
