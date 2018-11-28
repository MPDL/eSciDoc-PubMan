package de.mpg.escidoc.tools;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.tools.util.Util;

public class TestLastModificationDateIndexAppend {
    
    private static Indexer indexer;
    private static FullTextExtractor extractor;
    
    private List<File> filesToDelete = new ArrayList<File>();;
    private int numOfFilesBeforeCopy = 0;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        
        extractor = new FullTextExtractor();
        extractor.init(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
        extractor.extractFulltexts(new File(TestIndexerSmall.TEST_RESOURCES_FULLTEXTS));
        
        indexer = new Indexer(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        indexer.init();

        indexer.setCreateIndex(true);
        indexer.prepareIndex();
        indexer.getIndexingReport().clear();
    }

    // Create new index with a fixed number of foxmls
    @Before
    public void setUp() throws Exception {
        
        indexer.indexItemsStart(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        indexer.finalizeIndex();
    }
    
    // Create new index with a fixed number of foxmls
    @After
    public void tearDown() {
        for (File f : filesToDelete) {
            try {
                FileUtils.forceDelete(f);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }   
    }

    // Check the number of already indexed items
    // Set indexing mode to append
    // index additional items of newer date 
    @Test
    public void test() throws Exception {
        
        assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
        
        switch (indexer.getCurrentIndexMode())
        {
        case LATEST_RELEASE:
            assertTrue("Expected 20 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == TestIndexerSmall.INDEXING_DONE_LATEST_RELEASE);           
            assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 1);
            assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
                    indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == TestIndexerSmall.SKIPPED_LATEST_RELEASE);
            assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == TestIndexerSmall.INDEXING_DONE_LATEST_RELEASE);
            break;
        case BOTH:
            assertTrue("Expected 25 Found " + indexer.getIndexingReport().getFilesIndexingDone(), 
                    indexer.getIndexingReport().getFilesIndexingDone() == TestIndexerSmall.INDEXING_DONE_BOTH);            
            assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 2);
            assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
                    indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == TestIndexerSmall.SKIPPED_BOTH);
            assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == TestIndexerSmall.INDEXING_DONE_BOTH);
            break;
        case LATEST_VERSION:
            assertTrue("Expected 25 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == TestIndexerSmall.INDEXING_DONE_LATEST_VERSION);            
            assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 2);
            assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
                    indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == TestIndexerSmall.SKIPPED_LATEST_VERSION);
            assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == TestIndexerSmall.INDEXING_DONE_LATEST_VERSION);
            break;
        }
        
        // copy items modified since lastModificationDate to itemsAndComponents directory
        numOfFilesBeforeCopy = Util.countFilesInDirectory(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        
        File[] files = new File("src/test/resources/newItems").listFiles();
        int numCopies = Util.countFilesInDirectory(new File("src/test/resources/newItems"));

        // adding 6 items in status submitted 
        for (File f : files) {
            FileUtils.copyFileToDirectory(f, new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
            filesToDelete.add(new File("src/test/resources/20", f.getName()));
        }
        assertTrue(Util.countFilesInDirectory(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS)) == numOfFilesBeforeCopy + numCopies);

        indexer = new Indexer(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        indexer.init();
        indexer.setCreateIndex(false);
        indexer.setLastModificationDate("2016-11-21");
        indexer.prepareIndex();
        
        indexer.indexItemsStart(new File(TestIndexerSmall.TEST_RESOURCES_OBJECTS));
        indexer.finalizeIndex();
        
        // 1 files have time stamp from 2017 (escidoc_2351094), 
        assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == numOfFilesBeforeCopy - 2);
        
        switch (indexer.getCurrentIndexMode()) {
     
        case LATEST_RELEASE:
            assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), 
                    indexer.getIndexingReport().getFilesIndexingDone() == 1);
            
            assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);

            assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
                    indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == numCopies + 1);
            assertTrue("Found " + indexer.getIndexWriter().maxDoc(), 
                    indexer.getIndexWriter().maxDoc() == TestIndexerSmall.INDEXING_DONE_LATEST_RELEASE + 1);
            break;
        case BOTH:
            assertTrue("Expected " + numCopies + 2 + " Found " + indexer.getIndexingReport().getFilesIndexingDone(),
                    indexer.getIndexingReport().getFilesIndexingDone() == numCopies + 2);
            
            assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);

            assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
                    indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 2);
            assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == TestIndexerSmall.INDEXING_DONE_LATEST_VERSION + numCopies);
            break;
        case LATEST_VERSION:
            assertTrue("Expected " + numCopies + 2 + " Found " + indexer.getIndexingReport().getFilesIndexingDone(),
                    indexer.getIndexingReport().getFilesIndexingDone() == numCopies + 2);
            
            assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);

            assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
                    indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
            assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == TestIndexerSmall.INDEXING_DONE_LATEST_VERSION + numCopies);
            break;
        }
        
        
    }

}
