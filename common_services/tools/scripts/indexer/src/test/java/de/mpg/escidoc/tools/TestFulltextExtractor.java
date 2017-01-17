package de.mpg.escidoc.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFulltextExtractor
{
	
	private static final int EXTRACTION_DONE = 27;
    private static final int FILES_TOTAL = 28;
    static FullTextExtractor extractor; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		extractor = new FullTextExtractor();
	}
	
	
	@Before
	public void setUp() throws IOException
	{
		extractor.getStatistic().clear();	
		
		// first clean fulltext directory
		for (File f : new File(extractor.getFulltextPath()).listFiles())
		{
			try
			{
				FileUtils.forceDelete(f);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testFilesOk() throws Exception
	{	
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_20017+content+content.0"));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_20017+content+content.0"));
		extractor.finalizeExtraction();
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
		
		// only with iText successful 
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_28177+content+content.0"));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_28177+content+content.0"));
		extractor.finalizeExtraction();
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_28177+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 2);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}

	@Test
	public void testFilesOk1() throws Exception
	{	
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_2110752+content+content.0"));
		extractor.finalizeExtraction();
		Thread.sleep(200);
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_2110752+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}

	
	@Test
	public void testDirOk() throws Exception
	{
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
		extractor.finalizeExtraction();
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_28177+content+content.0.txt")).exists());
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_2110752+content+content.0.txt")).exists());
		
		assertTrue("Expected " + FILES_TOTAL + " Found " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == FILES_TOTAL);
		assertTrue("Expected 1 Found " + extractor.getStatistic().getFilesErrorOccured(), extractor.getStatistic().getFilesErrorOccured() == 1);
		assertTrue("Is " + extractor.getStatistic().getFilesExtractionDone(), extractor.getStatistic().getFilesExtractionDone() == EXTRACTION_DONE);
		assertTrue("Is " + extractor.getStatistic().getFilesSkipped(), extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue("Is " + extractor.getStatistic().getErrorList().size(), extractor.getStatistic().getErrorList().size() == 1);
	}
	
	@Test
	public void testDirWithHtmlFile() throws Exception
	{
		
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_html+content+content.0"));
		extractor.finalizeExtraction();
		
		assertTrue(!(new File(extractor.getFulltextPath(), "escidoc_1587192+content+content.0.txt")).exists());
		
		assertTrue("Expected 27 Found " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == FILES_TOTAL);
		assertTrue("expected <0> got <" + extractor.getStatistic().getFilesErrorOccured() + ">", extractor.getStatistic().getFilesErrorOccured() == 0) ;
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);
	}
	
	/**
	 * Test ms excel - should be done by Tika now
	 * @throws Exception
	 */
    @Test
    public void testDirWithExcelFile() throws Exception
    {
        
        extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
        extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_733621+content+content.0"));
        extractor.finalizeExtraction();
        
        assertTrue((new File(extractor.getFulltextPath(), "escidoc_733621+content+content.0.txt")).exists());
        
        assertTrue("Is " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == FILES_TOTAL);
        assertTrue("expected <0> got <" + extractor.getStatistic().getFilesErrorOccured() + ">", extractor.getStatistic().getFilesErrorOccured() == 0) ;
        assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
        assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
        assertTrue(extractor.getStatistic().getErrorList().size() == 0);
    }
    
    /**
     * Test doc - should be done by Tika now, but is corrupt
     * @throws Exception
     */
    @Test
    public void testDirWithCorruptDocFile() throws Exception
    {
        
        extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
        extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_1454595+content+content.0"));
        extractor.finalizeExtraction();
        
        assertFalse((new File(extractor.getFulltextPath(), "escidoc_1454595+content+content.0.txt")).exists());
        
        assertTrue("Is " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == FILES_TOTAL);
        assertTrue("expected <0> got <" + extractor.getStatistic().getFilesErrorOccured() + ">", extractor.getStatistic().getFilesErrorOccured() == 1) ;
        assertTrue(extractor.getStatistic().getFilesExtractionDone() == 0);
        assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
        assertTrue(extractor.getStatistic().getErrorList().size() == 1);
    }
    
    /**
     * Test pdf with problems 
     * @throws Exception
     */
    @Test
    public void testDirWithProblemPdfFile() throws Exception
    {
        
        extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
        extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_1956237+content+content.0"));
        extractor.finalizeExtraction();
        
        assertTrue((new File(extractor.getFulltextPath(), "escidoc_1956237+content+content.0.txt")).exists());
        
        assertTrue("Is " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == FILES_TOTAL);
        assertTrue("expected <0> got <" + extractor.getStatistic().getFilesErrorOccured() + ">", extractor.getStatistic().getFilesErrorOccured() == 0) ;
        assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
        assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
        assertTrue(extractor.getStatistic().getErrorList().size() == 0);
    }
    
    /**
     * Test pdf with problems 
     * @throws Exception
     */
    @Test
    public void testDirWithProblemCsvFile() throws Exception
    {
        
        extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
        extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_441261+content+content.0"));
        extractor.finalizeExtraction();
        
        assertTrue((new File(extractor.getFulltextPath(), "escidoc_441261+content+content.0.txt")).exists());
        
        assertTrue("Is " + extractor.getStatistic().getFilesTotal(), extractor.getStatistic().getFilesTotal() == FILES_TOTAL);
        assertTrue("expected <0> got <" + extractor.getStatistic().getFilesErrorOccured() + ">", extractor.getStatistic().getFilesErrorOccured() == 0) ;
        assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
        assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
        assertTrue(extractor.getStatistic().getErrorList().size() == 0);
    }
	
	@Test
	public void testLastModifiedFile1() throws Exception
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		
		// extract all modified since 2015-03-12. The testfiles have lastModificationDate 2015-03-14.
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_20017+content+content.0"));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_20017+content+content.0"), f.parse("2015-03-12").getTime());
		extractor.finalizeExtraction();
		
		assertTrue((new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 1);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 0);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);

	}
	
	@Test
	public void testLastModifiedFile2() throws Exception
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		
		extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_20017+content+content.0"));
		extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS + "escidoc_20017+content+content.0"), f.parse("2016-03-12").getTime());
		extractor.finalizeExtraction();
		
		assertTrue(!(new File(extractor.getFulltextPath(), "escidoc_20017+content+content.0.txt")).exists());
		assertTrue(extractor.getStatistic().getFilesTotal() == 1);
		assertTrue(extractor.getStatistic().getFilesErrorOccured() == 0);
		assertTrue(extractor.getStatistic().getFilesExtractionDone() == 0);
		assertTrue(extractor.getStatistic().getFilesSkipped() == 1);
		assertTrue(extractor.getStatistic().getErrorList().size() == 0);

	}
	
	
}
