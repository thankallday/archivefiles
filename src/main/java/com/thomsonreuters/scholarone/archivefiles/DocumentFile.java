package com.thomsonreuters.scholarone.archivefiles;

public class DocumentFile
{
  
  private String name;
  
  private Integer type;

  private boolean coverletter;
  
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Integer getType()
  {
    return type;
  }

  public void setType(Integer type)
  {
    this.type = type;
  }
  
  public boolean isCoverletter()
  {
    return coverletter;
  }

  public void setCoverletter(boolean coverletter)
  {
    this.coverletter = coverletter;
  }
  
  public boolean isExcluded()
  {
    boolean rv = true;
    
    if ( type.equals(FileArchiveConstants.FOR_REVIEW) ||
        (type.equals(FileArchiveConstants.SUPPL_NOT_FOR_REVIEW) && !coverletter) ||
        type.equals(FileArchiveConstants.OFFLINE_UPLOAD) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_PDF) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_HTML) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_300DPI) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_150DPI) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_72DPI) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_THUMB) ||
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_600DPI) ||        
        type.equals(FileArchiveConstants.SUPPL_FOR_REVIEW) ||   
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_XML) ||   
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_NLM_XML) ||   
        type.equals(FileArchiveConstants.LTE_RTF_SUBMISSION_TEMPLATE) || 
        type.equals(FileArchiveConstants.SYSTEM_GENERATED_LETTER_TO_EDITOR_HTML) ||        
        type.equals(FileArchiveConstants.THIRD_PART_PROOF))
    {
      rv = false;
    }
        
    return rv;
  }
}
