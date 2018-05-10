package com.thomsonreuters.scholarone.archivefiles;

public class Config
{
  private Integer configId;
  
  private String shortName;
  
  private boolean keepPDF;

  public Integer getConfigId()
  {
    return configId;
  }

  public void setConfigId(Integer configId)
  {
    this.configId = configId;
  }

  public String getShortName()
  {
    return shortName;
  }

  public void setShortName(String shortName)
  {
    this.shortName = shortName;
  }
  
  public boolean isKeepPDF()
  {
    return keepPDF;
  }

  public void setKeepPDF(boolean keepPDF)
  {
    this.keepPDF = keepPDF;
  }
}
