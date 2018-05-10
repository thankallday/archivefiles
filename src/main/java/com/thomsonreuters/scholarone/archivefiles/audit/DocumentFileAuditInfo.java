package com.thomsonreuters.scholarone.archivefiles.audit;

public class DocumentFileAuditInfo
{
  private Integer fileId;

  private Integer fileTypeId;

  private String name;

  private boolean coverletter;
  
  public Integer getFileId()
  {
    return fileId;
  }

  public void setFileId(Integer fileId)
  {
    this.fileId = fileId;
  }

  public Integer getFileTypeId()
  {
    return fileTypeId;
  }

  public void setFileTypeId(Integer fileTypeId)
  {
    this.fileTypeId = fileTypeId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
  
  public boolean isCoverletter()
  {
    return coverletter;
  }

  public void setCoverletter(boolean coverletter)
  {
    this.coverletter = coverletter;
  }
  
  public String toString()
  {
    StringBuffer s = new StringBuffer();
    
    s.append("\nfileId=" + fileId + "\n");
    s.append("fileTypeId=" + fileTypeId + "\n");
    s.append("name=" + name + "\n");
    s.append("coverleter=" + Boolean.valueOf(coverletter).toString());
    
    return s.toString();
  }
}
