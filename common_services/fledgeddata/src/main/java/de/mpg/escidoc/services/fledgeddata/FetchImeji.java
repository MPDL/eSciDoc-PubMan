package de.mpg.escidoc.services.fledgeddata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

import de.mpg.escidoc.services.fledgeddata.oai.OAIUtil;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.CannotDisseminateFormatException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.IdDoesNotExistException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.NoItemsMatchException;
import de.mpg.escidoc.services.fledgeddata.oai.exceptions.OAIInternalServerError;
import de.mpg.escidoc.services.fledgeddata.oai.valueobjects.oaiRecordFactory;




/**
 * 
 * @author kleinfe1
 *
 */
public class FetchImeji 
{

    private static SortedMap nativeMap = null;
    private HashMap resumptionResults=new HashMap();
    private static int maxListSize;
    private ArrayList sets = null;
    private boolean schemaLocationIndexed = false;
    private static oaiRecordFactory oaiRecordFactory;
	
	
	/**
     * Retrieve a list of Identifiers that satisfy the criteria parameters
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
     * identifier keys with corresponding values of "true" or null depending on
     * whether the identifier is deleted or not.
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     */
    public static Map listIdentifiers(String from, String until, String set, String metadataPrefix)
        throws NoItemsMatchException 
    {
        //purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        int count = 0;

        while (count < maxListSize && iterator.hasNext()) 
        {
        	Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            String recordDate = oaiRecordFactory.getDatestamp(nativeRecord);

            //TODO check metadataprefix muss eigentlich vorher schon passieren
            if (recordDate.compareTo(from) >= 0
                && recordDate.compareTo(until) <= 0) 
            {
            	System.out.println("--- Create header for item: " + nativeRecord.toString());
                String[] header = oaiRecordFactory.createHeader(nativeRecord);
                headers.add(header[0]);
                identifiers.add(header[1]);
                count++;
            }
        }

        if (count == 0)
        {
            throw new NoItemsMatchException();
        }

//        if (iterator.hasNext()) 
//        {        	
//	 	    listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString()));
//        }
        
        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        return listIdentifiersMap;
    }
	
	public String quickCreate(Object nativeItem, String schemaURL,
			String metadataPrefix) throws IllegalArgumentException,
			CannotDisseminateFormatException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public boolean isDeleted(Object nativeItem) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public Iterator getSetSpecs(Object nativeItem)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String getOAIIdentifier(Object nativeItem) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String getDatestamp(Object nativeItem) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Iterator getAbouts(Object nativeItem) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getRecord(String identifier, String metadataPrefix, Properties properties) 
			throws OAIInternalServerError,
				   IdDoesNotExistException,
				   CannotDisseminateFormatException
	{
		//Properties
		String nativeFormat = properties.getProperty("Repository.nativeFormat.Name");
		String oaiXslt = properties.getProperty("Repository.oai.stylesheet");
		String fetchUrl = properties.getProperty("Repository.oai.fetchURL");
		
		//Variables
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        OAIUtil util = new OAIUtil();

        try
        {
    		URL url = new URL(util.constructFetchUrl(fetchUrl, identifier));
            conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    System.out.println("Fetch xml from export");
                	
                    // Get XML
                    isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
                    bReader = new BufferedReader(isReader);
                    String line = "";
                    while ((line = bReader.readLine()) != null)
                    {
                    	resultXml += line + "\n";
                    }
                    httpConn.disconnect();  
                    System.out.println("ResultXml: " + resultXml);
                    break;
                default:
                    throw new OAIInternalServerError("An error occurred during metadata fetch from repository: "
                            + responseCode + ": " + httpConn.getResponseMessage() + ".");
            }

        }
        catch (Exception e)
        {
            throw new OAIInternalServerError(e.getMessage());
        }

        //check if result is empty
        if (!resultXml.contains("<imeji:image rdf:about=\""+identifier+"\">"))
        {
        	throw new IdDoesNotExistException(identifier);
        }
        
        if (metadataPrefix.equalsIgnoreCase(nativeFormat))
        {
        	System.out.println("Create native format record");
        	resultXml = util.craeteNativeOaiRecord(resultXml, identifier);
        }
        else 
        	if (metadataPrefix.equalsIgnoreCase("oai_dc"))
        	{        		
        		try
        		{
        			System.out.println("Create oai_dc record");
        			resultXml = util.xsltTransform(oaiXslt, resultXml);
        		}
        		catch (Exception e) 
        		{
					throw new OAIInternalServerError("An error occurred during transformation to oai_dc format. " + e.getMessage());
				}
        	}
        	else
        	{
        		throw new CannotDisseminateFormatException(metadataPrefix);
        	}
        
		
		return resultXml;
	}

}