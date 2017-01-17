package de.mpg.escidoc.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class IndexFieldReader {
    
    IndexReader indexReader = null;
    
    private void init(String indexDir) throws CorruptIndexException, IOException {
        indexReader = IndexReader.open(FSDirectory.open(new File(indexDir)));
        
    }
    
    private void readFieldnames(File indexDir) throws IOException {
        Collection<String> fieldnames = indexReader.getFieldNames(IndexReader.FieldOption.ALL);
        List<String> fieldList = new ArrayList<String>(fieldnames);
        Collections.sort(fieldList);
        
        FileUtils.writeLines(new File(indexDir + "/../" + "fields.txt"), fieldList);
        
    }

    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String indexDir = args[0];
        
        IndexFieldReader reader = new IndexFieldReader();

        reader.init(indexDir);
        reader.readFieldnames(new File(indexDir));
    }
}
