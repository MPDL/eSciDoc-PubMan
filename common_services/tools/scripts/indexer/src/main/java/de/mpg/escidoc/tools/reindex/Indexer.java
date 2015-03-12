/**
 * 
 */
package de.mpg.escidoc.tools.reindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.trans.DynamicError;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author franke
 *
 */
public class Indexer
{

	static String indexPath = null;
	
	static final String defaultDate = "0000-01-01 00:00:00";
	
	boolean create = true;
	
	private File baseDir;
	private File dbFile;
	private File indexStylesheet;
	private String indexName;
	private String indexAttributesName;
	private String fulltextDir;
	private long mDateMillis;
	
	private String mimetypes;
	
	private TransformerFactory saxonFactory = new TransformerFactoryImpl();
	private TransformerFactory xalanFactory = new org.apache.xalan.processor.TransformerFactoryImpl();
	
	private Transformer transformer1 = saxonFactory.newTransformer(new StreamSource(new File("foxml2escidoc.xsl")));
	private Transformer transformer3 = saxonFactory.newTransformer(new StreamSource(new File("prepareStylesheet.xsl")));
	private Transformer transformer2;

	IndexWriter writer;
	
	/**
	 * Constructor with initial base directory, should be the fedora "objects" directory.
	 * @param baseDir
	 */
	public Indexer(File baseDir, File dbFile, File indexStylesheet, String indexName, String indexAttributesName, long mDateMillis, String fulltextDir) throws Exception
	{
		this.baseDir = baseDir;
		this.dbFile = dbFile;
		this.indexStylesheet = indexStylesheet;
		this.indexName = indexName;
		this.indexAttributesName = indexAttributesName;
		this.fulltextDir = fulltextDir;
		this.mDateMillis = mDateMillis;
		this.mimetypes = readMimetypes();
		
		// Create temp file for modified index stylesheet
		File tmpFile = File.createTempFile("file", ".tmp");
		
		System.out.println(tmpFile);
		
		transformer3.setParameter("attributes-file", indexAttributesName.replace("\\", "/"));
		transformer3.transform(new StreamSource(indexStylesheet), new StreamResult(tmpFile));

		transformer2 = saxonFactory.newTransformer(new StreamSource(tmpFile));
		//Xalan transformation not possible due to needed XSLT2 functions
		//transformer2 = xalanFactory.newTransformer(new StreamSource(tmpFile));
		
		transformer1.setParameter("index-db", dbFile.getAbsolutePath().replace("\\", "/"));
		transformer2.setParameter("index-db", dbFile.getAbsolutePath().replace("\\", "/"));
		transformer2.setParameter("SUPPORTED_MIMETYPES", mimetypes);
		transformer2.setParameter("fulltext-directory", fulltextDir.replace("\\", "/"));
	}
	
	private String readMimetypes() throws Exception
	{
		File file = new File("mimetypes.txt");
		String line;
		StringWriter result = new StringWriter();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null)
		{
			result.write(line);
			result.write("\n");
		}
		return result.toString();
	}

	public void prepareIndex()
	{
	    try {
	    	System.out.println("Indexing to directory '" + indexPath + "'...");

	    	Directory dir = FSDirectory.open(new File(indexPath));
	    	// :Post-Release-Update-Version.LUCENE_XY:
	    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
	    	IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_34, analyzer);

	    	if (create) {
	    	  // Create a new index in the directory, removing any
	    	  // previously indexed documents:
	    	  iwc.setOpenMode(OpenMode.CREATE);
	    	} else {
	    	  // Add new documents to an existing index:
	    	  iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    	}

	    	// Optional: for better indexing performance, if you
	    	// are indexing many documents, increase the RAM
	    	// buffer.  But if you do this, increase the max heap
	    	// size to the JVM (eg add -Xmx512m or -Xmx1g):
	    	//
	    	iwc.setRAMBufferSizeMB(256.0);

	    	writer = new IndexWriter(dir, iwc);

	    	// NOTE: if you want to maximize search performance,
	    	// you can optionally call forceMerge here.  This can be
	    	// a terribly costly operation, so generally it's only
	    	// worth it when your index is relatively static (ie
	    	// you're done adding documents to it):
	    	//
	    	// writer.forceMerge(1);

	    } catch (IOException e) {
	    	System.out.println(" caught a " + e.getClass() +
	    	 "\n with message: " + e.getMessage());
	    }
	}
	
	public void finalizeIndex() throws Exception
	{
		writer.close();
	}
	
	public void indexDoc(InputStream inputStream) throws Exception
	{

	      try {

	        // make a new, empty document
	        Document doc = new Document();
	        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	        DefaultHandler dh = new IndexDocument(doc);
	        parser.parse(inputStream, dh);

	        if (writer.getConfig().getOpenMode() == OpenMode.CREATE)
	        {
				// New index, so we just add the document (no old document can be there):
				writer.addDocument(doc);
	        }
	        else
	        {
	        	// Existing index (an old copy of this document may have been indexed) so 
	        	// we use updateDocument instead to replace the old one matching the exact 
	        	// path, if present:
	        	writer.updateDocument(new Term("PID", doc.get("PID")), doc);
	        }
	        
	      }
	      finally
	      {
	    	  inputStream.close();
	      }
	}

	/**
	 * Gather information from the FOXMLs and write them into the given file.
	 * 
	 * @param file The file where to write the data to.
	 */
	public void createDatabase() throws Exception
	{
		FileOutputStream fileOutputStream = new FileOutputStream(dbFile);
		fileOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
		fileOutputStream.write("<index>\n".getBytes("UTF-8"));
		checkDir(baseDir, fileOutputStream);
		fileOutputStream.write("</index>\n".getBytes("UTF-8"));
		fileOutputStream.close();
	}
	
	private void checkDir(File dir, FileOutputStream fileOutputStream) throws Exception
	{
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				checkDir(file, fileOutputStream);
			}
			else
			{
				fileOutputStream.write(("<object name=\"" + file.getName().replace("_", ":") + "\" path=\"" + file.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			}
		}
	}
	
	private void indexItems(File dir) throws Exception
	{
		Arrays.sort(dir.listFiles());
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				System.out.println();
				System.out.println("Indexing directory " + file);
				indexItems(file);
			}
			else if (file.lastModified() >= mDateMillis)
			{
				System.out.println(file);
				indexItem(file);
			}
		}
	}
	
	private void indexItem(File file) throws Exception
	{
		File tmpFile1 = File.createTempFile("file", ".tmp");
		File tmpFile2 = File.createTempFile("file", ".tmp");
		System.out.println("FOXML2eSciDoc: " + tmpFile1);
		try
		{
			transformer1.transform(new StreamSource(file), new StreamResult(tmpFile1));
		}
		catch (DynamicError de)
		{
			if ("noitem".equals(de.getErrorCodeLocalPart()))
			{
				return;
			}
		}
		System.out.println("eSciDoc2IndexDoc: " + tmpFile2);
		transformer2.transform(new StreamSource(tmpFile1), new StreamResult(tmpFile2));
		indexDoc(new FileInputStream(tmpFile2));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

		if (null == args || args.length != 9)
		{
			System.out.println("Usage: java Indexer [parameters]");
			System.out.println("Parameters:");
			System.out.println("1 - Base directory");
			System.out.println("2 - Index result directory");
			System.out.println("3 - File for temporary foxml data");
			System.out.println("4 - Generate temporary foxml data (true/false)");
			System.out.println("5 - Index stylesheet");
			System.out.println("6 - Index stylesheet attributes");
			System.out.println("7 - Index name");
			System.out.println("8 - Modification date");
			System.out.println("9 - Fulltext directory");
			System.exit(0);
		}
		
		File baseDir = new File(args[0]);
		indexPath = args[1];
		File dbFile = new File(args[2]);
		File indexStylesheet = new File(args[4]);
		String indexAttributesName = args[5];
		String indexName = args[6];
		
		String mDate = args[7];
		String fulltextDir = args[8];
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String combinedDate = mDate + defaultDate.substring(mDate.length());
		long mDateMillis = dateFormat.parse(combinedDate).getTime();
		
		Indexer indexer = new Indexer(baseDir, dbFile, indexStylesheet, indexName, indexAttributesName, mDateMillis, fulltextDir);
		
		indexer.prepareIndex();
		
		if ("true".equals(args[3]))
		{
			indexer.createDatabase();
		}
		
		indexer.indexItems(baseDir);
		indexer.finalizeIndex();

			
	}

}
