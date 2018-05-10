package com.thomsonreuters.scholarone.archivefiles.audit;

import java.util.List;
import java.util.Map;

import com.scholarone.archivefiles.common.S3File;

public class DocumentAuditInfo
{
  private Integer stackId;

  private Integer documentId;

  private String configShortName;

  private Integer fileStoreYear;

  private Integer fileStoreMonth;

  private Integer archiveYear;

  private Integer archiveMonth;

  private Integer archiveStatusId;

  private boolean configKeepPDF;

  private Map<String, List<DocumentFileAuditInfo>> documentFilesMap;

  private Map<String, List<String>> preAuditTier2Files;

  private Map<String, List<String>> postAuditTier2Files;

  private Map<String, List<String>> postAuditTier3Files;

  public Integer getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(Integer documentId)
  {
    this.documentId = documentId;
  }

  public String getConfigShortName()
  {
    return configShortName;
  }

  public void setConfigShortName(String configShortName)
  {
    this.configShortName = configShortName;
  }

  public Integer getFileStoreYear()
  {
    return fileStoreYear;
  }

  public void setFileStoreYear(Integer fileStoreYear)
  {
    this.fileStoreYear = fileStoreYear;
  }

  public String getFileStoreMonth()
  {
    String month = fileStoreMonth.toString();
    if (month.length() == 1)
    {
      month = "0" + month;
    }
    
    return month;
  }

  public void setFileStoreMonth(Integer fileStoreMonth)
  {
    this.fileStoreMonth = fileStoreMonth;
  }

  public Integer getArchiveStatusId()
  {
    return archiveStatusId;
  }

  public void setArchiveStatusId(Integer archiveStatusId)
  {
    this.archiveStatusId = archiveStatusId;
  }

  public boolean isFileMoveNeeded()
  {
    if (documentId != null && documentId > 0 && (archiveStatusId == 1 || archiveStatusId == 32))
    {
      return true;
    }
    return false;
  }

  public Integer getStackId()
  {
    return stackId;
  }

  public void setStackId(Integer stackId)
  {
    this.stackId = stackId;
  }

  public boolean isConfigKeepPDF()
  {
    return configKeepPDF;
  }

  public void setConfigKeepPDF(boolean configKeepPDF)
  {
    this.configKeepPDF = configKeepPDF;
  }

  public Map<String, List<DocumentFileAuditInfo>> getDocumentFilesMap()
  {
    return documentFilesMap;
  }

  public void setDocumentFilesMap(Map<String, List<DocumentFileAuditInfo>> documentFilesMap)
  {
    this.documentFilesMap = documentFilesMap;
  }

  public Map<String, List<String>> getPreAuditTier2Files()
  {
    return preAuditTier2Files;
  }

  public void setPreAuditTier2Files(Map<String, List<String>> preAuditTier2Files)
  {
    this.preAuditTier2Files = preAuditTier2Files;
  }

  public Map<String, List<String>> getPostAuditTier2Files()
  {
    return postAuditTier2Files;
  }

  public void setPostAuditTier2Files(Map<String, List<String>> postAuditTier2Files)
  {
    this.postAuditTier2Files = postAuditTier2Files;
  }

  public Map<String, List<String>> getPostAuditTier3Files()
  {
    return postAuditTier3Files;
  }

  public void setPostAuditTier3Files(Map<String, List<String>> postAuditTier3Files)
  {
    this.postAuditTier3Files = postAuditTier3Files;
  }

  public Integer getArchiveYear()
  {
    return archiveYear;
  }

  public void setArchiveYear(Integer archiveYear)
  {
    this.archiveYear = archiveYear;
  }

  public String getArchiveMonth()
  {
    String month = archiveMonth.toString();
    if (month.length() == 1)
    {
      month = "0" + month;
    }
    
    return month;
  }

  public void setArchiveMonth(Integer archiveMonth)
  {
    this.archiveMonth = archiveMonth;
  }

  public String toString()
  {
    StringBuffer s = new StringBuffer();
    
    s.append("\nstackId=" + stackId + "\n");
    s.append("documentId=" + documentId + "\n");
    s.append("configShortName=" + configShortName + "\n");
    s.append("fileStoreYear=" + fileStoreYear + "\n");
    s.append("fileStoreMonth=" + fileStoreMonth + "\n");
    s.append("archiveYear=" + archiveYear + "\n");
    s.append("archiveMonth=" + archiveMonth + "\n");
    s.append("archiveStatusId=" + archiveStatusId + "\n");
    s.append("configKeepPDF=" + configKeepPDF + "\n\n");
    s.append("documentFilesMap=" + documentFilesMap.toString() + "\n\n");
    s.append("preAuditTier2Files=" + preAuditTier2Files.toString() + "\n\n");
    s.append("postAuditTier2Files=" + postAuditTier2Files.toString() + "\n\n");
    s.append("postAuditTier3Files=" + postAuditTier3Files.toString() + "\n\n");

    return s.toString();
  }
}
