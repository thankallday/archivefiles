package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.scholarone.activitytracker.TrackingInfo;
import com.scholarone.archivefiles.common.FileUtility;
import com.scholarone.archivefiles.common.S3File;
import com.scholarone.archivefiles.common.S3FileUtil;

public class S3FileUtilTest2
{

  final String archiveCacheDir = ConfigPropertyValues.getProperty("archive.cache.dir");
  
  final File sourceDir = new File (archiveCacheDir + File.separator + "docfiles/dev4/bmm-iop/2015/02/274746");

  final String sourceBucket = ConfigPropertyValues.getProperty("source.bucket.name");

  final String destinationBucket = ConfigPropertyValues.getProperty("destination.bucket.name");

  final S3File sourceS3Dir = new S3File("docfiles/dev4/bmm-iop/2015/02/274746/", sourceBucket);

  final S3File destinationS3Dir = new S3File("docfiles/2015/04/dev4/bmm-iop/274746/", destinationBucket); 


  @Test
  public void testCompleteMove() throws Exception
  {
    if (sourceDir.exists())
      FileUtils.deleteDirectory(sourceDir);
    
    int exitCode;
    
    if (S3FileUtil.isDirectory(sourceS3Dir))
    {
      exitCode = S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
      Assert.assertTrue(exitCode == 0);
    }
    
    if (S3FileUtil.isDirectory(destinationS3Dir))
    {
      exitCode = S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
      Assert.assertTrue(exitCode == 0);
    }
    
    
    File tempDir = new File(archiveCacheDir);
    if (!tempDir.exists())
      tempDir.mkdirs();
    
    ArrayList<File> lists = new ArrayList<File>();
    File zipFile = new File("FSETest.zip");
    UnZip unzip = new UnZip();
    unzip.extract(zipFile.getPath(), sourceDir.getPath());

    FileUtility.listDir(sourceDir, lists);
    for(File f : lists)
    {
      if(f.isDirectory())
        continue;
      S3FileUtil.putFile(S3FileUtil.trimKey(f.getPath()), f, sourceS3Dir.getBucketName());
    }
    
    ArrayList<String> inclusions = new ArrayList<String>();
    inclusions.add("_system_appendPDF_cover-forpdf.htm");
    inclusions.add("_system_appendPDF_cover-forpdf.pdf");
    inclusions.add("_system_appendPDF_params.txt");
    inclusions.add("_system_appendPDF_stamp.txt");
    inclusions.add("_system_appendPDF_params_test_only.txt");
    inclusions.add("282294_File000001_4675776_sheet001.html-withlinks.htm");
    inclusions.add("282294_File000002_4675779.html");
    inclusions.add("282294_File000002_4675779_sheet001.html");
    inclusions.add("282294_File000002_4675779.html-withlinks.htm");
    inclusions.add("282294_File000003_4675782_colorschememapping.xml");
    inclusions.add("282294_File000001_4675776.pdf");
    inclusions.add("282294_File000000_4675773.html-withlinks.htm");
    inclusions.add("282294_File000001_4675776_tabstrip.html-withlinks.htm");
    inclusions.add("282294_File000000_4675773_colorschememapping.xml");
    inclusions.add("282294_File000001_4675776_tabstrip.html");
    inclusions.add("282294_File000002_4675779_tabstrip.html-withlinks.htm");
    inclusions.add("282294_File000004_4675785.jpg-hi.forpdf.htm");
    inclusions.add("282294_File000003_4675782.html-withlinks.htm");
    inclusions.add("282294_File000003_4675782_filelist.xml");
    inclusions.add("282294_File000002_4675779.pdf");
    inclusions.add("282294_File000001_4675776_sheet002.html-withlinks.htm");
    inclusions.add("282294_File000000_4675773_filelist.xml");
    inclusions.add("282294_File000001_4675776_filelist.xml");
    inclusions.add("282294_File000001_4675776_sheet001.html");
    inclusions.add("282294_File000001_4675776_image001.png");
    inclusions.add("282294_File000000_4675773_image002.jpg");
    inclusions.add("282294_File000000_4675773_image001.jpg");
    inclusions.add("282294_File000001_4675776_image002.gif");
    inclusions.add("282294_File000001_4675776_sheet002.html");
    inclusions.add("282294_File000002_4675779_filelist.xml");
    inclusions.add("282294_File000002_4675779_tabstrip.html");
    inclusions.add("282294_File000005_4675788.jpg.htm");
    inclusions.add("282294_File000002_4675779_stylesheet.css");
    inclusions.add("282294_File000001_4675776.html-withlinks.htm");
    inclusions.add("282294_File000002_4675779_sheet003.html");
    inclusions.add("282294_File000002_4675779_stylesheet.css-withlinks.htm");
    inclusions.add("282294_File000002_4675779_sheet002.html");
    inclusions.add("282294_File000003_4675782_themedata.thmx");
    inclusions.add("282294_File000000_4675773_themedata.thmx");
    inclusions.add("282294_File000001_4675776_stylesheet.css-withlinks.htm");
    inclusions.add("282294_File000004_4675785-thumb.jpg");
    inclusions.add("282294_File000005_4675788.jpg");
    inclusions.add("282294_File000004_4675785.jpg");
    inclusions.add("282294_File000005_4675788-thumb.jpg");
    inclusions.add("282294_File000001_4675776.html");
    inclusions.add("282294_File000002_4675779_sheet002.html-withlinks.htm");
    inclusions.add("282294_File000005_4675788.jpg");
    inclusions.add("282294_File000003_4675782.docx");
    inclusions.add("282294_File000002_4675779.xlsx");
    inclusions.add("282294_File000000_4675773.doc");
    inclusions.add("282294_File000004_4675785.jpg");
    inclusions.add("282294_File000001_4675776.xlsx");
    inclusions.add("282294_File000002_4675779_sheet001.html-withlinks.htm");
    inclusions.add("nf.bst");
    inclusions.add("282294_File000003_4675782.html");
    inclusions.add("282294_File000002_4675779_sheet003.html-withlinks.htm");
    inclusions.add("282294_File000000_4675773.pdf");
    inclusions.add("282294_File000001_4675776_sheet003.html");
    inclusions.add("282294_File000004_4675785.jpg.htm");
    inclusions.add("282294_File000001_4675776_stylesheet.css");
    inclusions.add("282294_File000003_4675782.pdf");
    inclusions.add("282294_File000000_4675773.html");
    inclusions.add("282294_File000001_4675776_sheet003.html-withlinks.htm");
    inclusions.add("282294_File000005_4675788.jpg-hi.forpdf.pdf");
    inclusions.add("282294_File000004_4675785.jpg-hi.forpdf.pdf");
    inclusions.add("282294_File000005_4675788.jpg-hi.forpdf.htm");
    
    S3File sourceIncludedS3File0   = new S3File(sourceS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(0 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File1   = new S3File(sourceS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(1 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File2   = new S3File(sourceS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(2 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File3   = new S3File(sourceS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(3 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File4   = new S3File(sourceS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(4 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File5   = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(5 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File6   = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(6 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File7   = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(7 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File8   = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(8 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File9   = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(9 ), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File10  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(10), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File11  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(11), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File12  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(12), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File13  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(13), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File14  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(14), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File15  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(15), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File16  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(16), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File17  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(17), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File18  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(18), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File19  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(19), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File20  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(20), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File21  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(21), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File22  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(22), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File23  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(23), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File24  = new S3File(sourceS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(24), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File25  = new S3File(sourceS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(25), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File26  = new S3File(sourceS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(26), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File27  = new S3File(sourceS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(27), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File28  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(28), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File29  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(29), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File30  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(30), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File31  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(31), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File32  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(32), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File33  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(33), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File34  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(34), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File35  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(35), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File36  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(36), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File37  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(37), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File38  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(38), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File39  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(39), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File40  = new S3File(sourceS3Dir.getKey() + "docfiles/images/"         + inclusions.get(40), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File41  = new S3File(sourceS3Dir.getKey() + "docfiles/images/"         + inclusions.get(41), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File42  = new S3File(sourceS3Dir.getKey() + "docfiles/images/"         + inclusions.get(42), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File43  = new S3File(sourceS3Dir.getKey() + "docfiles/images/"         + inclusions.get(43), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File44  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(44), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File45  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(45), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File46  = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(46), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File47  = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(47), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File48  = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(48), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File49  = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(49), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File50  = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(50), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File51  = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(51), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File52  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(52), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File53  = new S3File(sourceS3Dir.getKey() + "docfiles/tex/home/rbb/sources/LaTeX/bibtex/bst/" + inclusions.get(53), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File54  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(54), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File55  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(55), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File56  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(56), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File57  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(57), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File58  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(58), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File59  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(59), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File60  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(60), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File61  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(61), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File62  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(62), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File63  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(63), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File64  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(64), sourceS3Dir.getBucketName());
    S3File sourceIncludedS3File65  = new S3File(sourceS3Dir.getKey() + "docfiles/"                + inclusions.get(65), sourceS3Dir.getBucketName());
   
    S3File destinationIncludedS3File0   = new S3File(destinationS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(0 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File1   = new S3File(destinationS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(1 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File2   = new S3File(destinationS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(2 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File3   = new S3File(destinationS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(3 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File4   = new S3File(destinationS3Dir.getKey() + "docfiles/temp/"           + inclusions.get(4 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File5   = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(5 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File6   = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(6 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File7   = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(7 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File8   = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(8 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File9   = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(9 ), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File10  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(10), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File11  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(11), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File12  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(12), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File13  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(13), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File14  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(14), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File15  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(15), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File16  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(16), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File17  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(17), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File18  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(18), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File19  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(19), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File20  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(20), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File21  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(21), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File22  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(22), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File23  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(23), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File24  = new S3File(destinationS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(24), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File25  = new S3File(destinationS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(25), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File26  = new S3File(destinationS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(26), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File27  = new S3File(destinationS3Dir.getKey() + "docfiles/inline-images/"  + inclusions.get(27), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File28  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(28), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File29  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(29), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File30  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(30), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File31  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(31), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File32  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(32), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File33  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(33), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File34  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(34), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File35  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(35), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File36  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(36), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File37  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(37), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File38  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(38), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File39  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(39), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File40  = new S3File(destinationS3Dir.getKey() + "docfiles/images/"         + inclusions.get(40), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File41  = new S3File(destinationS3Dir.getKey() + "docfiles/images/"         + inclusions.get(41), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File42  = new S3File(destinationS3Dir.getKey() + "docfiles/images/"         + inclusions.get(42), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File43  = new S3File(destinationS3Dir.getKey() + "docfiles/images/"         + inclusions.get(43), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File44  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(44), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File45  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(45), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File46  = new S3File(destinationS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(46), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File47  = new S3File(destinationS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(47), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File48  = new S3File(destinationS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(48), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File49  = new S3File(destinationS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(49), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File50  = new S3File(destinationS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(50), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File51  = new S3File(destinationS3Dir.getKey() + "docfiles/original-files/" + inclusions.get(51), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File52  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(52), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File53  = new S3File(destinationS3Dir.getKey() + "docfiles/tex/home/rbb/sources/LaTeX/bibtex/bst/" + inclusions.get(53), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File54  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(54), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File55  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(55), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File56  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(56), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File57  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(57), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File58  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(58), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File59  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(59), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File60  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(60), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File61  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(61), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File62  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(62), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File63  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(63), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File64  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(64), destinationS3Dir.getBucketName());
    S3File destinationIncludedS3File65  = new S3File(destinationS3Dir.getKey() + "docfiles/"                + inclusions.get(65), destinationS3Dir.getBucketName());
    
    ArrayList<String> exclusions = new ArrayList<String>();
    exclusions.add("_system_appendPDF_proof_hi.pdf");
    exclusions.add("_system_appendPDF_proof_output_test_only.pdf");
    exclusions.add("1-s2.0-S0144861713011223-main[paper1].pdf");
    exclusions.add("FINAL-tropical- after second modification 15-7-2012[1].doc");
    exclusions.add("[Linear Algebra re 10,4. Welles. 7,1. 7,1. 2015.] (Figures).pdf");
    exclusions.add("- nl-test.pdf");
    exclusions.add("502676_File000003_4095174.0]");
    exclusions.add("#2_Second response to reviewers(JSLee).doc");
    exclusions.add("jz201001661U-Reviewer#4.pdf");
    exclusions.add("#7bis.tex");
    exclusions.add("\" jm-2012-01013n.R1.pdf");
    exclusions.add("$7M to spend 8-18[1]-Bradley.docx");
    exclusions.add("$7M to spend 8-18[1]-Bradley.docx");
    exclusions.add("PBC-`13-1140.R1.pdf");
    
    S3File sourceExcludeds3File0   = new S3File(sourceS3Dir.getKey() + "docfiles/"               + exclusions.get(0 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File1   = new S3File(sourceS3Dir.getKey() + "docfiles/"               + exclusions.get(1 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File2   = new S3File(sourceS3Dir.getKey() + "docfiles/"               + exclusions.get(2 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File3   = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(3 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File4   = new S3File(sourceS3Dir.getKey() + "docfiles/notes/345206/"  + exclusions.get(4 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File5   = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(5 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File6   = new S3File(sourceS3Dir.getKey() + "docfiles/original-files/" + exclusions.get(6 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File7   = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(7 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File8   = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(8 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File9   = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(9 ), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File10  = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(10), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File11  = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(11), sourceS3Dir.getBucketName());
    S3File sourceExcludeds3File12  = new S3File(sourceS3Dir.getKey() + "docfiles/notes/5455/"    + exclusions.get(12), sourceS3Dir.getBucketName());
    
    S3File destinationExcludeds3File0   = new S3File(sourceExcludeds3File0.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File1   = new S3File(sourceExcludeds3File1.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File2   = new S3File(sourceExcludeds3File2.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File3   = new S3File(sourceExcludeds3File3.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File4   = new S3File(sourceExcludeds3File4.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File5   = new S3File(sourceExcludeds3File5.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File6   = new S3File(sourceExcludeds3File6.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File7   = new S3File(sourceExcludeds3File7.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File8   = new S3File(sourceExcludeds3File8.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File9   = new S3File(sourceExcludeds3File9.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File10  = new S3File(sourceExcludeds3File10.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File11  = new S3File(sourceExcludeds3File11.getKey(), destinationS3Dir.getBucketName());
    S3File destinationExcludeds3File12  = new S3File(sourceExcludeds3File12.getKey(), destinationS3Dir.getBucketName());
    
    S3File sourceS3LockFile = new S3File(sourceS3Dir.getKey() + "tier3move.lock", sourceS3Dir.getBucketName());
    S3File destination3LockFile = new S3File(destinationS3Dir.getKey() + "tier3move.lock", destinationS3Dir.getBucketName());
    
    S3File sourceS3ExcludedFile = new S3File(sourceS3Dir.getKey() + "exclude_from.txt", sourceS3Dir.getBucketName());
    S3File destinationS3ExcludedFile = new S3File(destinationS3Dir.getKey() + "exclude_from.txt", destinationS3Dir.getBucketName());
    
    S3FileUtil.createExclusionsFile(sourceS3Dir, exclusions, archiveCacheDir);
    
    PrintWriter out = null;
    File dir = new File(tempDir + File.separator);
    if (!dir.exists()) dir.mkdirs();
    File lockFile = new File(dir, "tier3move.lock");
    out = new PrintWriter(lockFile);
    out.println(Runtime.getRuntime().toString());
    out.println(new Date().getTime());
    out.close(); 
    S3FileUtil.putFile(sourceS3Dir.getKey() + "tier3move.lock", lockFile, sourceS3Dir.getBucketName());
    lockFile.delete();
    
    StatInfo stat = new StatInfo();
    exitCode = S3FileUtil.copyS3Dir(sourceS3Dir, destinationS3Dir, exclusions, stat);
    Assert.assertTrue(exitCode == 0);
    
    exitCode = S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir, exclusions);
    Assert.assertTrue(exitCode == 0);
    
    Assert.assertFalse(S3FileUtil.exists(sourceS3LockFile));
    Assert.assertTrue(S3FileUtil.exists(destination3LockFile));
    
    Assert.assertFalse(S3FileUtil.exists(sourceS3ExcludedFile));
    Assert.assertTrue(S3FileUtil.exists(destinationS3ExcludedFile));
    
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File0 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File1 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File2 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File3 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File4 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File5 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File6 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File7 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File8 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File9 ));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File10));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File11));
    Assert.assertTrue(S3FileUtil.exists(sourceExcludeds3File12));
    
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File0 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File1 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File2 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File3 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File4 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File5 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File6 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File7 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File8 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File9 ));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File10));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File11));
    Assert.assertFalse(S3FileUtil.exists(destinationExcludeds3File12));
    
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File0 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File1 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File2 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File3 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File4 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File5 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File6 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File7 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File8 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File9 ));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File10));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File11));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File12));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File13));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File14));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File15));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File16));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File17));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File18));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File19));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File20));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File21));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File22));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File23));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File24));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File25));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File26));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File27));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File28));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File29));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File30));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File31));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File32));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File33));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File34));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File35));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File36));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File37));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File38));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File39));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File40));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File41));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File42));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File43));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File44));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File45));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File46));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File47));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File48));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File49));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File50));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File51));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File52));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File53));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File54));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File55));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File56));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File57));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File58));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File59));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File60));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File61));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File62));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File63));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File64));
    Assert.assertFalse(S3FileUtil.exists(sourceIncludedS3File65));
    
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File0 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File1 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File2 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File3 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File4 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File5 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File6 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File7 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File8 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File9 ));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File10));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File11));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File12));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File13));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File14));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File15));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File16));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File17));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File18));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File19));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File20));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File21));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File22));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File23));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File24));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File25));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File26));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File27));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File28));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File29));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File30));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File31));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File32));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File33));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File34));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File35));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File36));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File37));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File38));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File39));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File40));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File41));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File42));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File43));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File44));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File45));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File46));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File47));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File48));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File49));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File50));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File51));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File52));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File53));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File54));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File55));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File56));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File57));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File58));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File59));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File60));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File61));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File62));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File63));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File64));
    Assert.assertTrue(S3FileUtil.exists(destinationIncludedS3File65));
    
    S3FileUtil.deleteS3AllVersionsRecursive(sourceS3Dir);
    Assert.assertFalse(S3FileUtil.exists(sourceS3Dir));
    
    S3FileUtil.deleteS3AllVersionsRecursive(destinationS3Dir);
    Assert.assertFalse(S3FileUtil.exists(destinationS3Dir));
    
    FileUtils.deleteDirectory(sourceDir);
    Assert.assertFalse(sourceDir.exists());
  }
}
