package de.mpg.escidoc.services.pidcache.gwdg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pidcache.json.EpicPid;
import de.mpg.escidoc.services.pidcache.json.FullPid;

/**
 * Class handling GWDG PID service interface
 * 
 * @author saquet
 *
 */
public class GwdgPidService 
{
    public static String GWDG_PIDSERVICE = null;
    
	public static String GWDG_PIDSERVICE_CREATE = null;
    public static String GWDG_PIDSERVICE_VIEW = null;
    public static String GWDG_PIDSERVICE_FIND = null;
	public static String GWDG_PIDSERVICE_EDIT = null;
	public static String GWDG_PIDSERVICE_DELETE = null;
	
	public static int GWDG_SERVICE_TIMEOUT = 3;
	
	private static final String APPLICATION_JSON = "application/json";
	
	private static final Logger logger = Logger.getLogger(GwdgPidService.class);
	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public GwdgPidService() throws Exception
	{
		GWDG_PIDSERVICE = PropertyReader.getProperty("escidoc.pid.gwdg.service.url").concat(PropertyReader.getProperty("escidoc.pid.gwdg.service.suffix"));
    	GWDG_PIDSERVICE_CREATE = PropertyReader.getProperty("escidoc.pid.service.create.path");
    	GWDG_PIDSERVICE_VIEW = PropertyReader.getProperty("escidoc.pid.service.view.path");
    	GWDG_PIDSERVICE_FIND = PropertyReader.getProperty("escidoc.pid.service.search.path");
    	GWDG_PIDSERVICE_EDIT = PropertyReader.getProperty("escidoc.pid.service.update.path");
    	GWDG_PIDSERVICE_DELETE = PropertyReader.getProperty("escidoc.pid.service.delete.path");
    	
    	GWDG_SERVICE_TIMEOUT = Integer.parseInt(PropertyReader.getProperty("escidoc.pid.gwdg.timeout"));
    	//todo
    	if (logger.isInfoEnabled()) {
    	    logger.info("GWDG_PIDSERVICE <"  + GWDG_PIDSERVICE + ">");
    	    logger.info("GWDG_SERVICE_TIMEOUT <"  + GWDG_SERVICE_TIMEOUT + ">");
    	}
	}
	
    /**
     * True if GWDG PID service is available.
     * False if not.
     * @return
     */
    public boolean available()
    {     
        HttpClient client = getHttpClient();
        
        GetMethod get = null;
        
        try
        {  
            get = createGetMethod();
            client.executeMethod(get);
        } 
        catch (Exception e) 
        {
            return false;
        }
        if (get.getStatusCode() == 200) 
        {
            return true;
        }
        return false;
    }
		
	/**
     * Calls GWDG PID manager REST interface: Creates a new pid assigned to the given url
     * 
     *  - http://vm04.pid.gwdg.de:8081/handles/<suffix>
     * 
     * @param url 
     * @return pid
     */
	public String create(String url) throws Exception
	{

	    HttpClient client = getHttpClient();
         
	    PostMethod create = createPostMethod(url);
	    client.executeMethod(create);
        
        int status = create.getStatusCode();
        logger.info("Create request returned with status <" + status + ">");
    	
        String jsonPid = create.getResponseBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        
        EpicPid epicPid = mapper.readValue(jsonPid, EpicPid.class);
        
        logger.info("Create request returning pid  <" + epicPid.getEpicPid() + ">");
        
        String purePid = epicPid.getEpicPid().replace(PropertyReader.getProperty("escidoc.pid.gwdg.service.suffix").concat("/"), "");
        
     	return purePid;
	}
	
	 /**
     * Calls GWDG PID manager REST interface:
     * 
     * Update the given pid by its id with the given url
     * 
     * @param id
     * @param url
     * @return
     * @throws Exception 
     */
    public int update(String id, String url) throws Exception
    {
        
        HttpClient client = getHttpClient();
        
        PutMethod update = createPutMethod(id, url);
        client.executeMethod(update);
        logger.info("Pid <" + id + "> updated.");
        
        int status = update.getStatusCode();
        logger.info("Update request returned with status <" + status + ">");
        
        return status; 
    }
    
    /**
     * Calls GWDG PID manager Rest interface:
     * 
     * Retrieves information for the given pid
     *  
     * @param 
     * 
     * @return
     */
    public String retrieve(String id) throws Exception
    {
        
        HttpClient client = getHttpClient();
        
        GetMethod retrieve = createGetMethod(id);
        client.executeMethod(retrieve);
        
        int status = retrieve.getStatusCode();
        logger.info("Get request returned with status <" + status + ">");
        
        String jsonFullPid = retrieve.getResponseBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        
        FullPid fullPid = mapper.readValue(jsonFullPid, FullPid.class);
        return fullPid.getData();
    }

	private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(GWDG_SERVICE_TIMEOUT * 1000);        
        client.getHttpConnectionManager().getParams().setSoTimeout(GWDG_SERVICE_TIMEOUT * 1000);
        
        return client;
    }
	
	private String getAuthHeader() throws IOException, URISyntaxException {
        StringBuffer authBuffer = new StringBuffer(1024);
        authBuffer.append(PropertyReader.getProperty("escidoc.pid.gwdg.user.login")).append(":")
                .append(PropertyReader.getProperty("escidoc.pid.gwdg.user.password"));

        String authHeader =
                "Basic " + Base64.getEncoder().encodeToString(authBuffer.toString().getBytes());
        return authHeader;
    }
	
    private PostMethod createPostMethod(String url) throws IOException, URISyntaxException {
        
        String authHeader = getAuthHeader();
        
        StringRequestEntity requestEntity =
                new StringRequestEntity("[{\"type\":\"URL\",\"parsed_data\":\"" + url + "\"}]",
                        APPLICATION_JSON, "UTF-8");

        PostMethod create = new PostMethod(GWDG_PIDSERVICE);
        create.setRequestHeader("Accept", APPLICATION_JSON);
        create.setRequestHeader("Content-type", APPLICATION_JSON);
        create.setRequestHeader("Authorization", authHeader);
        create.setRequestEntity(requestEntity);
        return create;
    }
    
    //
    // helper methods
    //
    
    private GetMethod createGetMethod() throws IOException, URISyntaxException {
        
        String authHeader = getAuthHeader();

        GetMethod get = new GetMethod(GWDG_PIDSERVICE);
        get.setRequestHeader("Accept", APPLICATION_JSON);
        get.setRequestHeader("Content-type", APPLICATION_JSON);
        get.setRequestHeader("Authorization", authHeader);

        return get;
    }
    
    private GetMethod createGetMethod(String id) throws IOException, URISyntaxException {
        
        String authHeader = getAuthHeader();

        GetMethod get = new GetMethod(GWDG_PIDSERVICE.concat("/" + id));
        get.setRequestHeader("Accept", APPLICATION_JSON);
        get.setRequestHeader("Content-type", APPLICATION_JSON);
        get.setRequestHeader("Authorization", authHeader);

        return get;
    }
    
    private PutMethod createPutMethod(String id, String url) throws IOException, URISyntaxException {
        
        String authHeader = getAuthHeader();
        StringRequestEntity requestEntity =
                new StringRequestEntity("[{\"type\":\"URL\",\"parsed_data\":\"" + url + "\"}]",
                        APPLICATION_JSON, "UTF-8");

        PutMethod update = new PutMethod(GWDG_PIDSERVICE.concat("/" + id));
        update.setRequestHeader("Accept", APPLICATION_JSON);
        update.setRequestHeader("Content-type", APPLICATION_JSON);
        update.setRequestHeader("Authorization", authHeader);
        update.setRequestEntity(requestEntity);
        return update;
    }
}
