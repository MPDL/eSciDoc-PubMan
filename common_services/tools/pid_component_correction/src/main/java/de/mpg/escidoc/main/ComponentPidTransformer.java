package de.mpg.escidoc.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import de.mpg.escidoc.handler.ItemHandler;
import de.mpg.escidoc.handler.ItemHandler.Type;
import de.mpg.escidoc.handler.PIDProviderException;
import de.mpg.escidoc.util.TransformationReport;
import de.mpg.escidoc.util.Util;


public class ComponentPidTransformer
{
    private static Logger logger = Logger.getLogger(ComponentPidTransformer.class);   
    
    private static TransformationReport report = new TransformationReport();
    
    private PIDProviderIf pidProvider;
    private Properties properties = new Properties();
    
    private String actualFileName = "";
    
    private String locationFile = null;
    
    public static String propFileName = "pidProvider.properties";
    
    public ComponentPidTransformer() 
    {
    	try
		{
			init();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void init() throws IOException
    {
    	logger.debug("init starting");
        
        InputStream s = getClass().getClassLoader().getResourceAsStream(propFileName);
		
		if (s != null)
		{
			properties.load(s);
			logger.info(properties.toString());
		}
		else 
		{
			throw new FileNotFoundException("Not found " + propFileName);
		}
		
		locationFile = properties.getProperty("transformer.locationFile");
    	
    }
    
    public void transform(File rootDir) throws Exception
    {
        if (rootDir != null && rootDir.isFile())
        {
            transformFile(rootDir);
            return;
        }
        
        File[] files = rootDir.listFiles();
        
        Collections.sort(Arrays.asList(files));
        
        for (File file : files)
        {
            if (file.getName().endsWith(".svn"))
                continue;
            
            if (file.isDirectory() && !file.getName().startsWith(".svn"))
            {
                transform(file);
            }
            else
            {
                transformFile(file);
            }
        }
    }
    
	/**
	 * Gather information from the FOXMLs and write it into the given file.
	 * 
	 * @param dir the directory to start from
	 * 
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public void storeLocation(File dir) throws Exception
	{
		logger.info("Starting create location file");
		
		FileOutputStream fileOutputStream = new FileOutputStream(new File(locationFile));
		fileOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
		fileOutputStream.write("<index>\n".getBytes("UTF-8"));
		checkDir(dir, fileOutputStream);
		fileOutputStream.write("</index>\n".getBytes("UTF-8"));
		fileOutputStream.close();
		
		logger.info("Creating index database finished");
	}
	

	/**
	 * read the contents of a directory and write it to location file
	 * 
	 * @param dir Current directory or file.
	 * @param fileOutputStream The stream pointing to the index db.
	 * @throws Exception Any exception.
	 */
	private void checkDir(File dir, FileOutputStream fileOutputStream) throws Exception
	{
		if (dir.isFile())
		{
			fileOutputStream.write(("<object name=\"" + dir.getName().replace("_", ":") + "\" path=\"" + dir.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			return;
		}
		
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				checkDir(file, fileOutputStream);
			}
			else
			{
				fileOutputStream.write(("<object name=\"" + file.getName().replace("_", ":") + "\" path=\"" + file.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			}
		}
	}

    
    public TransformationReport getReport()
    {
        return report;
    }

    private void transformFile(File file) throws Exception
    {   
        logger.info("****************** Start transforming " + file.getName());
        
        report.incrementFilesTotal();
        
        if ((report.getFilesTotal() % 1000) == 0 )
        {
            logger.info("***********************************************************");
            logger.info("****************** Number of files already done " + report.getFilesTotal());
            logger.info("***********************************************************");
        }
        
        actualFileName = file.getName();
        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        ItemHandler itemHandler = new ItemHandler();
        
        parser.parse(file, itemHandler);
        
        // loop over items - component data are fetched 
        if ((itemHandler.getObjectType()== null) || !itemHandler.getObjectType().equals(Type.ITEM))
        {
            logger.info("Nothing to do for file <" + file.getName() + "> because of type <" + itemHandler.getObjectType() + ">");
            logger.info("****************** End   transforming " + file.getName());
            report.incrementFilesNotItem();
            return;
        }
        
        Class<?> pidProviderClass = Class.forName(properties.getProperty("pidprovider.class"));
        
        pidProvider = (PIDProviderIf)pidProviderClass.newInstance();                
        pidProvider.init();
      
        for (String relsExtId : itemHandler.getGlobalElementMap().keySet())
    	{
        	Map<String, Set<String>>elementMapForRELSEXT = itemHandler.getGlobalElementMap().get(relsExtId);

        	String itemId = itemHandler.getEscidocId();
        	Set<String> componentIds = elementMapForRELSEXT.get("srel:component");
        	Set<String> versionNumber = elementMapForRELSEXT.get("version:number");
        	
        	for (String cid : componentIds)
        	{
        		Map<String, String> componentMap = itemHandler.getComponentMap(cid);
        		pidProvider.updateComponentPid(itemId, versionNumber.iterator().next(), cid, componentMap.get("prop:pid"), componentMap.get("dc:title"));
        	}
        	
    	}
        
        logger.info("****************** End   transforming " + file.getName());
    }
    
    public void onError(PIDProviderException e)
    {
        logger.warn("Error getting PID " + e.getMessage(), e);
        report.incrementFilesErrorOccured();
        report.addToErrorList(actualFileName);
        
        // continue processing for deprecated components - otherwise stop
        if (e.getMessage().contains("No item was found"))
            return;
        
        logger.info("FilesTotal                      " + report.getFilesTotal());
        logger.info("FilesMigratedNotReleased        " + report.getFilesNotReleased());
        logger.info("FilesMigratedNotItemOrComponent " + report.getFilesNotItem());
        logger.info("FilesErrorOccured               " + report.getFilesErrorOccured());
        logger.info("FilesMigrationDone              " + report.getFilesMigrationDone());
        logger.info("TotalNumberOfPidsRequested      " + report.getTotalNumberOfPidsUpdated());
        
        long s = report.getTimeUsed();
        logger.info("TimeUsed                        " + String.format("%d:%02d:%02d", s/3600, (s%3600)/60, (s%60)));
        
        System.exit(1);
    }
    
    static private void printUsage(String message)
    {
        System.out.print("***** " + message + " *****");
        System.out.print("Usage: ");
        System.out.println("java "  + " [-storeLocation|-update] rootDir");
        System.out.println("  -storeLocation\t\tStore the location of all foxmls in root directory in a file.");
        System.out.println("  -update\t\tUpdate the existing component pids");
        
        System.out.println("  <rootDir>\tThe root directory to start updating component pids from");

        System.exit(-1);
    }
    
    public static void main(String[] args) throws Exception
    {  
        String mode = args[0];
        
        if (mode == null || (!mode.contains("storeLocation") && !mode.contains("update")))
                printUsage("Invalid mode parameter.");
        
        String rootDirName = args[1];
        if (rootDirName == null)
            printUsage("Invalid root directory parameter.");
        
        File rootDir = new File(rootDirName);
        if (!rootDir.exists())
            printUsage("Directory or file does not exists: " + rootDir.getAbsolutePath() + "\n");
        
        int totalNumberOfFiles = Util.countFilesInDirectory(rootDir);
        logger.info("Total number of files to migrate <" + totalNumberOfFiles + "> for directory <"  + rootDir.getName() + ">");
        
        TransformationReport report = new TransformationReport(); 
        
        if (mode.contains("transform"))
        {
            ComponentPidTransformer pidMigr = new ComponentPidTransformer();
            pidMigr.transform(rootDir);
            
            report = pidMigr.getReport();
            logger.info("FilesTotal                      " + report.getFilesTotal());
            logger.info("FilesMigratedNotReleased        " + report.getFilesNotReleased());
            logger.info("FilesMigratedNotItemOrComponent " + report.getFilesNotItem());      
            logger.info("FilesErrorOccured               " + report.getFilesErrorOccured());
            logger.info("FilesMigrationDone              " + report.getFilesMigrationDone());
            logger.info("TotalNumberOfPidsRequested      " + report.getTotalNumberOfPidsUpdated());
            
            long s = report.getTimeUsed();
            
            logger.info("TimeUsed                        " + String.format("%d:%02d:%02d", s/3600, (s%3600)/60, (s%60)));
            logger.info("ErrorList                       " + report.getErrorList());
        }
        
    }

}
