package com.thomsonreuters.scholarone.archivefiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.scholarone.activitytracker.TrackingInfo;

public class FileSystemUtilityLinuxImplTest
{
  String source = "/shared/gus/docfiles/dev4/fse/2015/12/282294";

  String destination = "/shared/gus_archive/docfiles/2015/12/dev4/fse";

  @Before
  public void setup() throws IOException
  {
    File zipFile = new File("FSETest.zip");
    UnZip unzip = new UnZip();
    unzip.extract(zipFile.getPath(), source);
  }

  @After
  public void teardown() throws IOException
  {
    File sourceFile = new File(source);
    File destFile = new File(destination);
    
    FileUtils.deleteDirectory(sourceFile);    
    FileUtils.deleteDirectory(destFile);
  }

  @Test
  public void testCopy() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);
    TrackingInfo stat = new TrackingInfo();
    ArrayList<String> exclusions = new ArrayList<String>();
    int exitCode = fs.copy(source, destination, exclusions, stat);

    Assert.assertTrue(exitCode == 0);
    File destFile = new File(destination + "/282294/docfiles/tex/home/rbb/sources/LaTeX/bibtex/bst/", "nf.bst");
    Assert.assertTrue(destFile.exists());
  }

  @Test 
  public void testCopyWithExclusions() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);
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
    exclusions.add("untitled-[2]");
    exclusions.add("$7M to spend 8-18[1]-Bradley.docx");
    
    TrackingInfo stat = new TrackingInfo();
    fs.copy(source, destination, exclusions, stat);
    
    File sourceFile1 = new File(source + "/docfiles/", "_system_appendPDF_proof_hi.pdf");
    File sourceFile2 = new File(source + "/docfiles/", "_system_appendPDF_proof_output_test_only.pdf");
    File sourceFile3 = new File(source + "/docfiles/", "1-s2.0-S0144861713011223-main[paper1].pdf");
    File sourceFile4 = new File(source + "/docfiles/notes/5455", "FINAL-tropical- after second modification 15-7-2012[1].doc");
    File sourceFile5 = new File(source + "/docfiles/notes/345206", "[Linear Algebra re 10,4. Welles. 7,1. 7,1. 2015.] (Figures).pdf");
    File sourceFile6 = new File(source + "/docfiles/notes/5455", "- nl-test.pdf");
    File sourceFile7 = new File(source + "/docfiles/original-files", "502676_File000003_4095174.0]");
    File sourceFile8 = new File(source + "/docfiles/notes/5455", "#2_Second response to reviewers(JSLee).doc");
    File sourceFile9 = new File(source + "/docfiles/notes/5455", "jz201001661U-Reviewer#4.pdf");
    File sourceFile10 = new File(source + "/docfiles/notes/5455", "#7bis.tex");
    File sourceFile11 = new File(source + "/docfiles/notes/5455", "\" jm-2012-01013n.R1.pdf");
    File sourceFile12 = new File(source + "/docfiles/notes/5455", "$7M to spend 8-18[1]-Bradley.docx");
    
    File destFile1 = new File(destination + "/282294/docfiles/", "_system_appendPDF_proof_hi.pdf");
    File destFile2 = new File(destination + "/282294/docfiles/", "_system_appendPDF_proof_output_test_only.pdf");
    File destFile3 = new File(destination + "/282294/docfiles/", "1-s2.0-S0144861713011223-main[paper1].pdf");
    File destFile4 = new File(destination + "/282294/docfiles/notes/5455", "FINAL-tropical- after second modification 15-7-2012[1].doc");
    File destFile5 = new File(destination + "/282294/docfiles/notes/345206", "[Linear Algebra re 10,4. Welles. 7,1. 7,1. 2015.] (Figures).pdf");
    File destFile6 = new File(destination + "/282294/docfiles/notes/5455", "- nl-test.pdf");
    File destFile7 = new File(destination + "/282294/docfiles/original-files", "502676_File000003_4095174.0]");
    File destFile8 = new File(destination + "/282294/docfiles/notes/5455", "#2_Second response to reviewers(JSLee).doc");
    File destFile9 = new File(destination + "/282294/docfiles/notes/5455", "jz201001661U-Reviewer#4.pdf");
    File destFile10 = new File(destination + "/282294/docfiles/notes/5455", "#7bis.tex");
    File destFile11 = new File(destination + "/282294/docfiles/notes/5455", "\" jm-2012-01013n.R1.pdf");
    File destFile12 = new File(destination + "/282294/docfiles/notes/5455", "$7M to spend 8-18[1]-Bradley.docx");
    
    Assert.assertTrue(sourceFile1.exists());
    Assert.assertTrue(sourceFile2.exists());
    Assert.assertTrue(sourceFile3.exists());
    Assert.assertTrue(sourceFile4.exists());
    Assert.assertTrue(sourceFile5.exists());
    Assert.assertTrue(sourceFile6.exists());
    Assert.assertTrue(sourceFile7.exists());
    Assert.assertTrue(sourceFile8.exists());
    Assert.assertTrue(sourceFile9.exists());
    Assert.assertTrue(sourceFile10.exists());
    Assert.assertTrue(sourceFile11.exists());
    Assert.assertTrue(sourceFile12.exists());
    
    Assert.assertFalse(destFile1.exists());
    Assert.assertFalse(destFile2.exists());
    Assert.assertFalse(destFile3.exists());
    Assert.assertFalse(destFile4.exists());
    Assert.assertFalse(destFile5.exists());
    Assert.assertFalse(destFile6.exists());
    Assert.assertFalse(destFile7.exists());
    Assert.assertFalse(destFile9.exists());
    Assert.assertFalse(destFile10.exists());
    Assert.assertFalse(destFile11.exists());
    Assert.assertFalse(destFile12.exists());
  }
  
  @Test
  public void testCopyMissingDirectory() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);
    int exitCode = fs.copy("/shared/gus/docfiles/dev4/missing_directory", destination);

    Assert.assertTrue(exitCode == 23);
  }
  
  @Test
  public void testDelete() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);
    int exitCode = fs.delete(source, true);

    Assert.assertTrue(exitCode == 0);
  }
  
  @Test
  public void testDeleteMissingDirectory() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);
    int exitCode = fs.delete("/shared/gus/docfiles/dev4/missing_directory", true);

    Assert.assertTrue(exitCode == 0);
  }
  
  @Test
  public void testDeleteWithExclusions() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);
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
    exclusions.add("PBC-`13-1140.R1.pdf");
    
    int exitCode = fs.delete(source, true, exclusions);

    File sourceFile1 = new File(source + "/docfiles/", "_system_appendPDF_proof_hi.pdf");
    File sourceFile2 = new File(source + "/docfiles/", "_system_appendPDF_proof_output_test_only.pdf");
    File sourceFile3 = new File(source + "/docfiles/", "1-s2.0-S0144861713011223-main[paper1].pdf");
    File sourceFile4 = new File(source + "/docfiles/notes/5455", "FINAL-tropical- after second modification 15-7-2012[1].doc");
    File sourceFile5 = new File(source + "/docfiles/notes/345206", "[Linear Algebra re 10,4. Welles. 7,1. 7,1. 2015.] (Figures).pdf");
    File sourceFile6 = new File(source + "/docfiles/notes/5455", "- nl-test.pdf");
    File sourceFile7 = new File(source + "/docfiles/original-files", "502676_File000003_4095174.0]");
    File sourceFile8 = new File(source + "/docfiles/notes/5455", "#2_Second response to reviewers(JSLee).doc");
    File sourceFile9 = new File(source + "/docfiles/notes/5455", "jz201001661U-Reviewer#4.pdf");
    File sourceFile10 = new File(source + "/docfiles/notes/5455", "#7bis.tex");
    File sourceFile11 = new File(source + "/docfiles/notes/5455", "\" jm-2012-01013n.R1.pdf");
    File sourceFile12 = new File(source + "/docfiles/notes/5455", "PBC-`13-1140.R1.pdf");
    
    Assert.assertTrue(sourceFile1.exists());
    Assert.assertTrue(sourceFile2.exists());
    Assert.assertTrue(sourceFile3.exists());
    Assert.assertTrue(sourceFile4.exists());
    Assert.assertTrue(sourceFile5.exists());
    Assert.assertTrue(sourceFile6.exists());
    Assert.assertTrue(sourceFile7.exists());
    Assert.assertTrue(sourceFile8.exists());
    Assert.assertTrue(sourceFile9.exists());
    Assert.assertTrue(sourceFile10.exists());
    Assert.assertTrue(sourceFile11.exists());
    Assert.assertTrue(sourceFile12.exists());
    
    Assert.assertTrue(exitCode == 0);
  }

  @Test
  public void testCompleteMove() throws IOException
  {
    IFileSystemUtility fs = new FileSystemUtilityLinuxImpl(4);

    int exitCode = fs.copy(source, destination);
    Assert.assertTrue(exitCode == 0);

    exitCode = fs.delete(source, true);
    Assert.assertTrue(exitCode == 0);
  }
}
