package com.thomsonreuters.scholarone.archivefiles.audit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;
import com.thomsonreuters.scholarone.archivefiles.ConfigPropertyValues;
import com.thomsonreuters.scholarone.archivefiles.FileArchiveConstants;

public class S3FileAuditUtility
{ 
  public static S3File getTier2FilePath(DocumentAuditInfo documentAuditInfo, S3File sourceS3Dir) throws IOException
  {
    String path = ConfigPropertyValues.getProperty("prefix.directory") + File.separator
        + ConfigPropertyValues.getProperty("environment") + documentAuditInfo.getStackId() + File.separator
        + documentAuditInfo.getConfigShortName() + File.separator + documentAuditInfo.getFileStoreYear()
        + File.separator + documentAuditInfo.getFileStoreMonth() + File.separator + documentAuditInfo.getDocumentId() + File.separator;

    S3File filetoReturn = new S3File(path, sourceS3Dir.getBucketName());
    return filetoReturn;
  }

  public static S3File getTier3FilePath(DocumentAuditInfo documentAuditInfo, S3File detinationS3Dir) throws IOException
  {
    String path = ConfigPropertyValues.getProperty("prefix.directory") + File.separator
        + documentAuditInfo.getArchiveYear() + File.separator + documentAuditInfo.getArchiveMonth() 
        + File.separator + ConfigPropertyValues.getProperty("environment") + documentAuditInfo.getStackId() + File.separator
        + documentAuditInfo.getConfigShortName() + File.separator + documentAuditInfo.getDocumentId() + File.separator;
    ;

    S3File filetoReturn = new S3File(path, detinationS3Dir.getBucketName());
    return filetoReturn;
  }

  /*
   * Returns a map, keyed by filename and a list of paths containing a file with that name. This is done to handle
   * duplicate file names
   */
  public static Map<String, List<String>> getFilesAndPathsAsMap(S3File basePath)
  {
    Map<String, List<String>> fileMap = new HashMap<String, List<String>>();

    if (S3FileUtil.isDirectory(basePath))
    {
      addFilesToMap(basePath, fileMap);
    }
    else
    {
      return fileMap;
    }

    return fileMap;
  }

  /*
   * Recursive method to traverse directories and build the map
   */
  private static void addFilesToMap(S3File path, Map<String, List<String>> fileMap)
  {
    if (!S3FileUtil.isDirectory(path)) return;

    S3File[] fileList = S3FileUtil.listFiles(path);
    if (fileList != null)
    {
      for (int i = 0; i < fileList.length; i++)
      {
        S3File f = fileList[i];
        if (!S3FileUtil.isDirectory(f))
        {
          if (!fileMap.containsKey(f.getKey()))
          {
            List<String> pathList = new ArrayList<String>();
            fileMap.put(S3FileUtil.getFileName(f), pathList);
          }
          fileMap.get(S3FileUtil.getFileName(f)).add(S3FileUtil.getParentPath(f));
        }
        else
        {
          addFilesToMap(f, fileMap);
        }
      }
    }
  }

  public static boolean shouldFileBeMoved(Integer fileTypeId, boolean coverletter)
  {
    if (fileTypeId.equals(FileArchiveConstants.FOR_REVIEW)
        || (fileTypeId.equals(FileArchiveConstants.SUPPL_NOT_FOR_REVIEW) && !coverletter)
        || fileTypeId.equals(FileArchiveConstants.OFFLINE_UPLOAD)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_PDF)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_HTML)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_300DPI)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_150DPI)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_72DPI)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_THUMB)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_IMAGE_600DPI)
        || fileTypeId.equals(FileArchiveConstants.SUPPL_FOR_REVIEW)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_XML)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_NLM_XML)
        || fileTypeId.equals(FileArchiveConstants.LTE_RTF_SUBMISSION_TEMPLATE)
        || fileTypeId.equals(FileArchiveConstants.SYSTEM_GENERATED_LETTER_TO_EDITOR_HTML)
        || fileTypeId.equals(FileArchiveConstants.THIRD_PART_PROOF))
    {
      return true;
    }
    return false;
  }

  /*
   * This method will return true if all files with the same name were moved to tier3 storage. This includes the
   * deletion of tier2 versions
   */
  public static boolean didFilesMove(DocumentAuditInfo documentAuditInfo, String fileName)
  {
    // Should we log which specific check failed?
    return (documentAuditInfo.getPostAuditTier3Files().containsKey(fileName)
        && (documentAuditInfo.getPreAuditTier2Files().get(fileName).size() == documentAuditInfo
            .getPostAuditTier3Files().get(fileName).size()) && !documentAuditInfo.getPostAuditTier2Files().containsKey(
        fileName));
  }

  /*
   * This method will return true if all files with the same name remain in tier2 storage and do not exist in tier3
   */
  public static boolean didFilesRemain(DocumentAuditInfo documentAuditInfo, String fileName)
  {
    // Should we log which specific check failed?
    return (documentAuditInfo.getPostAuditTier2Files().containsKey(fileName)
        && (documentAuditInfo.getPreAuditTier2Files().get(fileName).size() == documentAuditInfo
            .getPostAuditTier2Files().get(fileName).size()) && !documentAuditInfo.getPostAuditTier3Files().containsKey(
        fileName));
  }

  /*
   * This method will check to see if a file existed in tier2 storage. If missing, audits can be skipped
   */
  public static boolean didFileExistInTier2PriorToMove(DocumentAuditInfo documentAuditInfo, String fileName)
  {
    return documentAuditInfo.getPreAuditTier2Files().containsKey(fileName);
  }
}
