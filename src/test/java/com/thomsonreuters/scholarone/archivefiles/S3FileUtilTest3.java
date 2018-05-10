package com.thomsonreuters.scholarone.archivefiles;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class S3FileUtilTest3
{
  String sourceBucketName = ConfigPropertyValues.getProperty("source.bucket.name");
  String deistinationBucketName = ConfigPropertyValues.getProperty("destination.bucket.name");
  
  S3File directory = new S3File("docfiles/dev5/prod5-qared/2018/04/305223/", sourceBucketName);
  S3File file = new S3File("docfiles/dev5/prod5-qared/2018/04/305223/_system_appendPDF_proof_hi.pdf", deistinationBucketName);

  @Test
  public void testAIsS3DirectoryFormat()
  {
    Assert.assertTrue(S3FileUtil.isS3DirectoryFormat(directory));
    Assert.assertFalse(S3FileUtil.isS3DirectoryFormat(file));
  }

  @Test
  public void testBGetFileName()
  {
    Assert.assertTrue(S3FileUtil.getFileName(file).equals("_system_appendPDF_proof_hi.pdf"));
  }
  

  @Test
  public void testCGetParentPath()
  {
    Assert.assertTrue(S3FileUtil.getParentPath(file).equals("docfiles/dev5/prod5-qared/2018/04/305223/"));
  }
}
