package de.mpg.escidoc.services.extraction;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestLargePdfExtraction {
    
    private static ExtractionChain extractor;     
    private static Properties p = new Properties();
    
    private static Logger logger = Logger.getLogger(TestLargePdfExtraction.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        FileUtils.deleteQuietly(new File("src/test/resources/Heath_HumburiSenni_2015.txt"));
        
        extractor = new ExtractionChain();
        
        p.setProperty("extract.pdftotext.path", "C:/xpdfbin-win-3.04/xpdfbin-win-3.04/bin64/pdftotext.exe");
        p.setProperty("extract.pdfbox-app-jar.path", 
                "C:/Users/sieders.MUCAM/.m2/repository/org/apache/pdfbox/pdfbox-app/1.8.6/pdfbox-app-1.8.6.jar");
        
        extractor.setProperties(p, logger);
    }

    @Test
    public void test() {
        extractor.doExtract("src/test/resources/Heath_HumburiSenni_2015.pdf", "src/test/resources/Heath_HumburiSenni_2015.txt");
        
        assertTrue(new File("src/test/resources/Heath_HumburiSenni_2015.txt").exists());
    }

}
