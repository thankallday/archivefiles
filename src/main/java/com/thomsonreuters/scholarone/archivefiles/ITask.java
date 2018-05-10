package com.thomsonreuters.scholarone.archivefiles;

public interface ITask extends Runnable
{
  public static final int NO_AUDIT = 0;
  
  public static final int AUDIT = 1;
  
  public static final int AUDIT_REVERT = 2;
  
  public int getExitCode();
  
  public Double getTransferRate();
  
  public Long getTransferTime();

  public Long getTransferSize();

  public void run();
}
