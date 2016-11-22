package de.mpg.escidoc.tools;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.tools.util.xslt.LocationHelper;

public class TestLastModificationDate {

    protected static Indexer indexer;
    
    
    @Before
    public void setUp() throws Exception
    {
        indexer = new Indexer(new File("src/test/resources/20"));
        indexer.init();

        indexer.setCreateIndex(true);
        indexer.prepareIndex();
        indexer.getIndexingReport().clear();
        
        LocationHelper.getLocation("escidoc_persistent22");
        
    }
    
    @Test
    public void test1() throws Exception
    {
        indexer.setLastModificationDate("2016-01-01");
        indexer.indexItemsStart(new File("src/test/resources/20"));
        indexer.finalizeIndex();
        
        assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() > 0);
        assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
        assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 0);
    }
    
    @Test
    public void test2() throws Exception
    {
        indexer.setLastModificationDate("2015-01-01");
        
        indexer.indexItemsStart(new File("src/test/resources/20"));
        indexer.finalizeIndex();
        
        assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0); 
    }
 

}
