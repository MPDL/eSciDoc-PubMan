package de.mpg.escidoc.tools;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.mpg.escidoc.tools.util.xslt.LocationHelper;

public class TestLastModificationDateIndexCreate {

    protected static Indexer indexer;
    
    
    @Before
    public void setUp() throws Exception
    {
        indexer = new Indexer(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
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
        indexer.indexItemsStart(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        indexer.finalizeIndex();
        
        assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() > 0);
        assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
        switch(indexer.getCurrentIndexMode()) {
            case LATEST_VERSION:
            case BOTH:
                assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 2);
                break;
            case LATEST_RELEASE:
                assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
                break;
        }
    }
    
    @Test
    public void test2() throws Exception
    {
        indexer.setLastModificationDate("2015-01-01");
        
        indexer.indexItemsStart(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        indexer.finalizeIndex();
        
        assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0); 
    }
 

}
