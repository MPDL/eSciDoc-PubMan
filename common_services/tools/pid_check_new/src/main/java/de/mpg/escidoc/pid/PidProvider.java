package de.mpg.escidoc.pid;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.util.HandleUpdateStatistic;
import de.mpg.escidoc.util.LocatorCheckStatistic;



public class PidProvider extends AbstractPidProvider
{
    private static Logger logger = Logger.getLogger(PidProvider.class);  
    
    private String location;
    private String user;
    private String password;
    private String server;
    
    private HttpClient httpClient;

    public PidProvider() throws Exception
    {
        this.init();
    }
    
    public void init() throws Exception
    {
        logger.debug("init starting");
        
        super.init();
        
        location = PropertyReader.getProperty("escidoc.pidcache.service.url");
        user = PropertyReader.getProperty("escidoc.pidcache.user.name");
        password = PropertyReader.getProperty("escidoc.pidcache.user.password");
        
        server = PropertyReader.getProperty("escidoc.pidcache.server");
        
        httpClient = getHttpClient();
        httpClient.getParams().setAuthenticationPreemptive(true);

        logger.debug("init finished");
    }
    
    public static HttpClient getHttpClient()
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        return httpClient;
    }

    public int updatePid(String pid, String irItemId, HandleUpdateStatistic statistic)
    {
        logger.debug("updatePid starting");
        
        if ("".equals(irItemId))
        {
            statistic.incrementHandlesNotFound();
            successMap.put(pid, "NOT USED");
            return 0;
        }
        
        int code = HttpStatus.SC_OK;
        String newUrl = "";
        String pidCacheUrl = location + "/write/modify";
        
        PostMethod method = null;
        method = new PostMethod(pidCacheUrl.concat("?pid=").concat(pid));
        
        try
        {
            newUrl = getRegisterUrl(irItemId);
            logger.info("Register Url for <" + pid + "> " + " <"+ irItemId + ">");
        }
        catch (Exception e)
        {
            logger.warn("Error occured when getting Url for <" + irItemId + ">");
        }
        
        method.setParameter("url", newUrl);
        
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setCredentials(new AuthScope(server, 8080),
                    new UsernamePasswordCredentials(user, password));
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {
                logger.warn("Problem updating a pid <" + pid + ">" + "with newUrl <" + newUrl + ">");
                failureMap.put(pid, newUrl);
                statistic.incrementHandlesUpdateError();
            }
            else
            {
                successMap.put(pid, newUrl);
                statistic.incrementHandlesUpdated();
            }
   
            logger.info("pid update returning code <" + code + ">" + method.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + irItemId + ">" );
            statistic.incrementHandlesUpdateError();
        }
        
        long end = System.currentTimeMillis();
        
        logger.info("Time used for updating pid <" + (end - start) + ">ms");
        
        return code;
    }
    
    public int checkToResolvePid(String pid, HandleUpdateStatistic statistic)
    {
        logger.debug("checkToResolvePid startingfor <" + pid + ">");

        StringBuffer b = new StringBuffer("http://hdl.handle.net/");
        b.append(pid);

        int code = HttpStatus.SC_OK;
        
        GetMethod method =  new GetMethod(b.toString());
        method.setFollowRedirects(true);
        
        long start = System.currentTimeMillis();
        try
        {
        	httpClient.getState().setAuthenticationPreemptive(true);
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {               
                failureMap.put(pid, "http code " + code);
                logger.warn("Problem when resolving <" + pid + "> http code " + code);
                statistic.incrementHandlesNotFound();
            }
            else
            {
                successMap.put(pid, "http code " + code);
                statistic.incrementHandlesUpdated();
            }              
        }
        catch (Exception e)
        {
        	statistic.incrementHandlesUpdateError();
            logger.warn("Error occured when resolving Url for <" + pid + ">" );
        }   
        finally
        {
        	method.releaseConnection();
        	
        }
        long end = System.currentTimeMillis();        
        logger.info("Time used for resolving pid <" + (end - start) + ">ms");
        
        return code;   
    }
    
    public int checkToResolveLocator(String locatorUrl, LocatorCheckStatistic statistic)
    {
        logger.debug("checkToResolveLocator startingfor <" + locatorUrl + ">");

        int code = HttpStatus.SC_OK;
        
        statistic.incrementTotal();
        
        GetMethod method =  new GetMethod(locatorUrl);
        method.setFollowRedirects(true);
        
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setAuthenticationPreemptive(true);
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {               
                failureMap.put(locatorUrl, "http code " + code);
                logger.warn("Problem when resolving <" + locatorUrl + "> http code " + code);
                statistic.incrementLocatorsNotResolved();
            }
            else
            {
                successMap.put(locatorUrl, "http code " + code);
                statistic.incrementLocatorsResolved();
            }              
        }
        catch (Exception e)
        {
            statistic.incrementLocatorsNotResolved();
            logger.warn("Error occured when resolving Url for <" + locatorUrl + ">" );
        }   
        finally
        {
            method.releaseConnection();
            
        }
        long end = System.currentTimeMillis();        
        logger.info("Time used for resolving pid <" + (end - start) + ">ms");
        
        return code;   
    }

    private String getRegisterUrl(String itemId) throws Exception
    {
        String registerUrl =  PropertyReader.getProperty("escidoc.pubman.instance.url") +
                PropertyReader.getProperty("escidoc.pubman.instance.context.path") + itemId;
                
        return registerUrl;
    }

    public void addToFailure(String pid, String string)
    {
        this.failureMap.put(pid, "");      
    }
}
