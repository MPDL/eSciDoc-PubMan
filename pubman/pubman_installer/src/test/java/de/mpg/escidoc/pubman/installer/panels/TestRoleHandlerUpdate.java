package de.mpg.escidoc.pubman.installer.panels;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.www.services.aa.RoleHandler;
import de.mpg.escidoc.pubman.installer.util.Utils;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class TestRoleHandlerUpdate
{
    private static final String ESCIDOC_ROLE_MODERATOR = "escidoc:role-moderator";
    private static final String ESCIDOC_ROLE_DEPOSITOR = "escidoc:role-depositor";
    
    private static final String ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME = "CoNE-Open-Vocabulary-Editor";
    private static final String ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME = "CoNE-Closed-Vocabulary-Editor";
    
    /**
     * Constants for queries.
     */
    protected static final String SEARCH_RETRIEVE = "searchRetrieve";
    protected static final String QUERY = "query";
    protected static final String VERSION = "version";
    protected static final String OPERATION = "operation";
    
    private static RoleHandler roleHandler = null;
    
    static Properties p = new Properties();
    
    private Logger logger = Logger.getLogger(getClass());
    
    @BeforeClass
    public static void setUp() throws Exception
    {    
        p.setProperty("escidoc.framework_access.framework.url", "http://dev-pubman.mpdl.mpg.de");
        p.setProperty("escidoc.framework_access.login.url", "http://dev-pubman.mpdl.mpg.de");
        p.setProperty("framework.admin.username", "roland");
        p.setProperty("framework.admin.password", "3rk*Trcp*G");
        
        roleHandler = ServiceLocator.getRoleHandler(loginSystemAdministrator());
    }

    private static String loginSystemAdministrator() throws HttpException, IOException, ServiceException, URISyntaxException {
        
        return AdminHelper.loginUser(p.getProperty("framework.admin.username"), p.getProperty("framework.admin.password"));
    }

    @Test
    @Ignore
    public void retrieve()
    {
        try
        {
            String xml = roleHandler.retrieve("escidoc:role-moderator");
            assertTrue(xml != null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    @Ignore
    public void update() throws Exception
    {
        doUpdate(ESCIDOC_ROLE_CONE_OPEN_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_open_vocabulary_editor.xml");
        doUpdate(ESCIDOC_ROLE_CONE_CLOSED_VOCABULARY_EDITOR_NAME, "datasetObjects/role_cone_closed_vocabulary_editor.xml");
    }
    
    private String doUpdate(String roleId, String templateFileName) throws Exception
    {    
        logger.info("******************************************* Starting doUpdate for " + roleId);
        String lastModDate = "2012-07-09T06:24:00.000Z";
        String out = null;
        
        String newPolicy = Utils.getResourceAsXml(templateFileName);
        newPolicy = newPolicy.replaceAll("template_last_modification_date", lastModDate);
        
        out = roleHandler.create(newPolicy);
        
        String newDate = Utils.getValueFromXml("last-modification-date=\"", out);
        
        logger.info("newDate: " + newDate);
        logger.info("******************************************* Ended doUpdate for " + roleId);
        return out;
    }
    
    
}
