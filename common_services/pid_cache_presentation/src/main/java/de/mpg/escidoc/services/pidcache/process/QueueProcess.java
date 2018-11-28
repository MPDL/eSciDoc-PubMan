/**
 * 
 */
package de.mpg.escidoc.services.pidcache.process;

import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.tables.Queue;

/**
 * 
 * Process managing the {@link Queue}
 * 	- Check whether queue has enqueued PID 
 * 	  (i.e waiting for an update at GWDG service)
 * 	- Update enqueued item at the GWDG service.
 * 
 * @author saquet
 *
 */
public class QueueProcess 
{      
	private static final Logger logger = Logger.getLogger(QueueProcess.class);
;
	private int blockSize = 1;
	
	/**
     * Default constructor
     */
    public QueueProcess() throws Exception {   
    }

    public void emptyBlock() throws Exception
    {
        Queue queue = new Queue();
        List<Pid> pids = queue.getFirstBlock(this.blockSize);
        if (logger.isDebugEnabled()) {
            logger.debug("emptyBlock got " + this.blockSize + " pids");
        }
        if (pids.size() == 0)
        {
            return;
        }
        GwdgPidService gwdgPidService = new GwdgPidService();
        if (gwdgPidService.available()) 
        {
            for (Pid pid : pids) 
            {
                int httpStatus = HttpStatus.SC_NO_CONTENT;
                try 
                {
                    httpStatus = gwdgPidService.update(pid.getIdentifier(), pid.getUrl());
                     
                    if (logger.isDebugEnabled()) {
                        logger.debug("emptyBlock updated pid <" 
                                + pid.getIdentifier() + "> url <" 
                                + pid.getUrl() + "> with status <" + httpStatus + ">");
                    }
                    if (httpStatus == HttpStatus.SC_OK || httpStatus == HttpStatus.SC_NO_CONTENT)
                    {
                        queue.remove(pid);
                    } else {
                        logger.warn("Update pid returned with <" + httpStatus + ">");
                        logger.warn("Could not remove pid <" + pid.getIdentifier() + "> url <" 
                                + pid.getUrl() + "> from queue");
                    }
                } 
                catch (Exception e) 
                {
                    logger.warn("Error, PID can not be updated on GWDG service.");
                }             
            }
        }
        else
        {
            logger.warn("PID manager at GWDG not available.");
        }
        
        
    }
    
    public boolean isEmpty() throws Exception
    {
        return (new Queue()).isEmpty();
    }

    public void setBlockSize(int size)
    {
        this.blockSize = size;        
    }
}
