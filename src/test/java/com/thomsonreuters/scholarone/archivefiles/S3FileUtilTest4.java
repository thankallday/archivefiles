package com.thomsonreuters.scholarone.archivefiles;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;

public class S3FileUtilTest4
{

  String sourceBucketName = ConfigPropertyValues.getProperty("source.bucket.name");
  
  S3File directory = new S3File("docfiles/dev5/ss11/2018/05/305409/", sourceBucketName);
  
  @Test
  public void testDeleteS3DirectoryRecursive()
  {
    int exitCode = S3FileUtil.deleteS3AllVersionsRecursive(directory);
    assertTrue(exitCode == 0);
  }
 
  
}
