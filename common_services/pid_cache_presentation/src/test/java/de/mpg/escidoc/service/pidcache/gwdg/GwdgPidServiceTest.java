package de.mpg.escidoc.service.pidcache.gwdg;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;

public class GwdgPidServiceTest {
    
    private static final Logger logger = Logger.getLogger(GwdgPidServiceTest.class);

    @Test
    public void available() throws Exception {
        GwdgPidService gwdgPidService = new GwdgPidService();
        assertTrue(gwdgPidService.available());
    }
    
    @Test
    public void create() throws Exception {
        GwdgPidService gwdgPidService = new GwdgPidService();
        String response = gwdgPidService.create("http://test.mpdl.mpg.de");
        
        logger.info("Create request returned <" + response + ">");
        assertTrue(response != null);
    }
    
    @Test
    public void update() throws Exception {
        GwdgPidService gwdgPidService = new GwdgPidService();
        int status = gwdgPidService.update("0000-0005-24B7-4", "http://test.mpdl.mpg.de/updatedNew");
        //int status = gwdgPidService.update("00-001M-0000-002E-A349-6", "http://pubman.mpdl.mpg.de/pubman/item/escidoc:2518593");
        
        logger.info("Update request returned <" + status + ">");
        
        assertTrue(status - 200 < 10);
    }
    
    @Test
    @Ignore
    public void retrieve() throws Exception {
        GwdgPidService gwdgPidService = new GwdgPidService();
        String data = gwdgPidService.retrieve("0000-0005-24B7-4");
        
        logger.info("Update request returned <" + data + ">");
        
        assertTrue(data != null);
    }
    
    @Test
    public void createAndUpdatePid() throws Exception {
        GwdgPidService gwdgPidService = new GwdgPidService();
        String response = gwdgPidService.create("http://test.mpdl.mpg.de");
        
        logger.info("Create request returned <" + response + ">");
        assertTrue(response != null);
        
        int status = gwdgPidService.update(response, "http://test.mpdl.mpg.de/updated");
        
        assertTrue(status - 200 < 10);
    }

}
