/**
 * 
 */
package test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.ResourceUtil;
import de.mpg.escidoc.services.citationmanager.XmlHelper;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles.OutFormats;

import static org.junit.Assert.*;

/**
 * @author endres 
 *
 */
public class CitationTest {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private XmlHelper xh = new XmlHelper();
	
	private final String dsFileName = "item-list-inga.xml";  
//	private final String dsFileName = "mpi-psl.xml";  
	
	private String itemList;
	
	private CitationStyleHandler pcs = new ProcessCitationStyles();
	
	/**
     * Tests CitationStyle.xml (APA by default)
     * TODO: At the moment only the Validation method is being tested, 
     * 		 Citation Style Processing will tested by ProcessCitationStyleTest later  
     * TODO endres: unittest is ignored because of com.topologi.schematron.SchtrnValidator's unusual
     *              relative path behavior. maven resource paths are not recognized.      
     * @throws IOException 
     */

    /**
     * Get test item list from XML 
     * @throws Exception
     */
    @Before
    public final void getItemList() throws Exception
    {
    	String ds = ResourceUtil.getPathToDataSources() + dsFileName; 
    	logger.info("Data Source:" + ds);
    			
        itemList = ResourceUtil.getResourceAsString(ds);

        assertNotNull("Item list xml is not found:", ds);
    }	
	
    /**
     * Validates DataSource against XML Schema  
     * @throws IOException 
     */
    @Test
    public final void testDataSourceValidation() throws IOException{
    	
		long start = 0;
		String dsName = ResourceUtil.getUriToResources() 
			+ ResourceUtil.DATASOURCES_DIRECTORY 
			+ "item-list-inga.xml";
	      
        try {
        	start = System.currentTimeMillis();
        	xh.validateDataSourceXML(dsName);
            logger.info("DataSource file:" + dsName + " is valid.");
            
        }catch (CitationStyleManagerException e){ 
            logger.info("DataSource file:" + dsName + " is not valid.\n", e);
            fail();
        }
        logger.info("Data Source Validation time : " + (System.currentTimeMillis() - start));
    }
    
    /**
     * Validates CitationStyle against XML Schema and Schematron Schema  
     * @throws IOException 
     */
    @Test
    public final void testCitationStyleValidation() throws IOException{
    	
    	long start = 0;
    	String csName = 
    		ResourceUtil.getUriToResources()
    		+ ResourceUtil.CITATIONSTYLES_DIRECTORY
    		+ "APA/CitationStyle.xml";  
    	logger.info("CitationStyle URI: " + csName);
    	try {
    		start = System.currentTimeMillis();
    		xh.validateCitationStyleXML(csName);
    		logger.info("CitationStyle XML file: " + csName + " is valid.");
    		
    	}catch (CitationStyleManagerException e){ 
    		logger.info("CitationStyle XML file: " + csName + " is not valid.\n", e);
    		fail();
    	}
    	logger.info("Data Source Validation time : " + (System.currentTimeMillis() - start));
    }


    /**
     * Test service with a item list XML.
     * All exports 
     * @throws Exception Any exception.
     */
    @Test
    public final void testCitManOutput() throws Exception {
		long start;
    	byte[] result;
		for ( OutFormats ouf : OutFormats.values() ) {
			String format = ouf.toString();
	    	start = System.currentTimeMillis();
	    	result = pcs.getOutput("APA", format, itemList);
	        logger.info("Output to " + format + ", time: " + (System.currentTimeMillis() - start));
	        logger.info(format + " length: " + result.length);
	        assertTrue(format + " output should not be empty", result.length > 0);
	        logger.info(format + " is OK");
			
		}
    }
    
    /**
     * Test service with a item list XML.
     * Snippet export
     * @throws Exception Any exception.
     */
    @Test
    @Ignore
    public final void testCitManOutputSnippet() throws Exception {
    	long start;
    	byte[] result;
    	start = System.currentTimeMillis();
    	String format = ProcessCitationStyles.OutFormats.snippet.toString();
    	result = pcs.getOutput("APA", format, itemList);
    	
    	//-------------- Test output generation
        /*FileWriter fstream = new FileWriter(ResourceUtil.getPathToDataSources() + "item-list-MPI_Psycholinguistics-OUTPUT.xml");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(new String(result));
        out.close();
        fstream.close();
        */
    	//--------------
        
    	logger.info("Output to " + format + ", time: " + (System.currentTimeMillis() - start));
    	logger.info(format + " length: " + result.length);
    	logger.info("result=" + new String(result));
    	assertTrue(format + " output should not be empty", result.length > 0);
    	logger.info(format + " is OK");
    }
    
    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
    public final void testListOfStyles() throws Exception {
    	long start;
    	start = System.currentTimeMillis();
    	for (String s : XmlHelper.getListOfStyles() )
    		logger.info("Citation Style: " + s);
    	logger.info("List of styles time: " + (System.currentTimeMillis() - start));
    }    
    
}
