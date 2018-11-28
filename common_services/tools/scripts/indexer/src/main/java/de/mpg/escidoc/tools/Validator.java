package de.mpg.escidoc.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;

public class Validator
{
    private static final String JBOSS_SERVER_LUCENE_ESCIDOC_ALL = "C:/Test/tmp/escidoc_all";
    private static final String JBOSS_SERVER_LUCENE_ITEM_CONTAINER_ADMIN = "C:/Test/tmp/item_container_admin";
    
	protected static Indexer indexer;
	protected String referenceIndexPath;
	
	IndexReader indexReader1 = null;
	IndexReader indexReader2 = null;
	IndexSearcher indexSearcher2 = null;
	
	protected static Logger logger = Logger.getLogger(Validator.class);
	
	protected static String[] fieldNamesToSkipInValidate = {
	    
	    // for LATEST_RELEASE
		"xml_representation", 
		"xml_metadata",
		
		// for LATEST_VERSION
		"aa_xml_representation", 
		"aa_xml_metadata",
		"/base",
		
		// the following indexes cause problems because of encoding of '&' -> '&amp;'
		// occurs only for item_container_admin index
		"/title",
		"/components/component/xLinkTitle"
		};
	
	protected static String[] fieldNamesToCompare = {
        
        // for LATEST_RELEASE
        "escidoc.property.latest-version.number", 
        "escidoc.property.version.number",
        "escidoc.property.version.status",
        
        // for LATEST_VERSION
        "properties/latest-version/number",
        "/properties/version/number", 
        "/properties/version/status"
        };
	
	protected static String[] objidsToSkipInValidate = {
		"escidoc:2111614",
		"escidoc:2111636",
		"escidoc:2111643",
		"escidoc:2111653",
		"escidoc:2111721",
		"escidoc:2111712",
		"escidoc:2116439"	
	};
	
	protected static int numberOfDocuments = 10;
	
	public Validator()
	{
	}
	
	public Validator(Indexer indexer) throws CorruptIndexException, IOException
	{
	    this.indexer = indexer;
		this.setReferencePath();
	}
	
	public Validator(String path1, String path2) throws CorruptIndexException, IOException
    {
	    indexer = new Indexer();
	    
	    if (!new File(path1).exists())
        {
            logger.warn("Invalid reference index path <" + path1 + ">");
            throw new FileNotFoundException("Invalid reference index path <" + path1 + ">");
        }
        this.referenceIndexPath = path2;
        
        indexReader1 = IndexReader.open(FSDirectory.open(new File(path1)));
        indexReader2 = IndexReader.open(FSDirectory.open(new File(path2)));
        indexSearcher2 = new IndexSearcher(indexReader2);
    }
	
	public void setReferencePath() throws CorruptIndexException, IOException
	{
	    String path = "";
	    
	    switch (indexer.getCurrentIndexMode())
        {
        case LATEST_RELEASE:
            path = JBOSS_SERVER_LUCENE_ESCIDOC_ALL;
            break;
        case LATEST_VERSION:
        case BOTH:    
            path = JBOSS_SERVER_LUCENE_ITEM_CONTAINER_ADMIN;
            break;
        }
	    
		if (!new File(path).exists())
		{
			logger.warn("Invalid reference index path <" + path + ">");
			throw new FileNotFoundException("Invalid reference index path <" + path + ">");
		}
		this.referenceIndexPath = path;
		
		logger.info("Index path <" + indexer.getIndexPath() + ">");
		logger.info("Reference path <" + this.referenceIndexPath + ">");
		
		indexReader1 = IndexReader.open(FSDirectory.open(new File(indexer.getIndexPath())), true);
		indexReader2 = IndexReader.open(FSDirectory.open(new File(this.referenceIndexPath)), true);
		indexSearcher2 = new IndexSearcher(indexReader2);
		
	}
	
	public void setNumberOfFilesToValidate(int n) {
	    this.numberOfDocuments = n;
	}
	
	public void compareToReferenceIndex() throws CorruptIndexException, IOException
	{
		Document document1 = null;
		Document document2 = null;
		
		for (int i = 0; i < Math.min(indexReader1.maxDoc(), numberOfDocuments); i++)
		{			
			document1 = indexReader1.document(i);	
			document2 = getReferenceDocument(getObjidFieldName(), document1.get(getObjidFieldName()), indexSearcher2);
			
			if (Arrays.asList(objidsToSkipInValidate).contains(document1.get(getObjidFieldName())))
			{
				logger.warn("Skipping verify for <" + document1.get(getObjidFieldName()) + ">");
				continue;
			}
			
			if (document2 == null)
			{
				indexer.getIndexingReport().addToErrorList("No reference document found for <"
						+ document1.get(getObjidFieldName()) + ">");	
				continue;
			}
			
			logger.info("Verify comparing index documents with <" + document1.get(getObjidFieldName()) + ">");
			
			List<Fieldable> fields1 = document1.getFields();	
			List<Fieldable> fields2 = document2.getFields();
	
			if (fields1.size() != fields2.size())
			{
				indexer.getIndexingReport().addToErrorList("Different amount of fields " 
						+ fields1.size() + " - " + fields2.size() + " for <" +  document1.get(getObjidFieldName()) + ">\n");
			}
			
			Map<String, Set<Fieldable>> m1 = getMap(fields1);
			Map<String, Set<Fieldable>> m2 = getMap(fields2);
			
			logger.info("comparing 1 [" + indexer.getIndexPath() + "] - 2 [" + this.referenceIndexPath + "]");
			compareFields(m1, m2);
			
			logger.info("comparing 2 [" + this.referenceIndexPath + "] - 1 [" + indexer.getIndexPath() + "]");
			compareFields(m2, m1);
			
			logger.info("comparing skipped fields 1 - 2");
			compareSkippedFields(m1, m2);
			
			logger.info("End verifying index documents with <" + document1.get(getObjidFieldName()) + ">");
		}
		
		logger.info("Validator result \n" + indexer.getIndexingReport().toString());
		
		indexSearcher2.close();
		indexReader1.close();
		indexReader2.close();
	}
	
	private String getObjidFieldName()
	{	    
		switch (indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			return "escidoc.objid";
		case BOTH:
		case LATEST_VERSION:
			return "/id";
		default:
			logger.warn("No valid indexMode");
			
		}
		return null;
	}
	
	
	public Map<String, Set<Fieldable>> getFieldsOfDocument() throws CorruptIndexException, IOException
	{
		Document document1 = null;
		Map<String, Set<Fieldable>> m1 = null;
		
		IndexReader indexReader1 = IndexReader.open(FSDirectory.open(new File(indexer.getIndexPath())), true);
		
		for (int i = 0; i < indexReader1.maxDoc(); i++)
		{			
			document1 = indexReader1.document(i);			
			
			List<Fieldable> fields1 = document1.getFields();	
		
			m1 = getMap(fields1);
			
		}
		return m1;
	}

	private void compareFields(Map<String, Set<Fieldable>> m1, Map<String, Set<Fieldable>> m2)
	{
		for (String name : m1.keySet())
		{
			if (Arrays.asList(fieldNamesToSkipInValidate).contains(name))
				continue;
			
			if (!Arrays.asList(fieldNamesToCompare).contains(name))
                continue;
			
			Set<Fieldable> sf1 = m1.get(name);
			Set<Fieldable> sf2 = m2.get(name);
			
			if (("stored_fulltext".equals(name) || "stored_filename".equals(name)) || "/components/component/xLinkTitle".equalsIgnoreCase(name))					
			{
				int i = 1;
				i++;
			}
			
			if (!((sf1 != null && sf2 != null) || (sf1 == null && sf2 == null)))
			{
				indexer.getIndexingReport().addToErrorList("Nothing found for <" + name + ">" + " in <" +  m1.get(getObjidFieldName()) + ">\n");
				continue;
			}
			
			if (sf1.size() != sf2.size())
			{
				indexer.getIndexingReport().addToErrorList("Different field sizes sf1 - sf2 <" + sf1.size() + "><" + sf2.size() + ">\n");
			}
			
			logger.debug("<" + name + ">sf1.size  <" + sf1.size() + "> - sf2.size <" + sf2.size() + ">");
			
			for (Fieldable f1 : sf1)
			{
				Fieldable f2 = findFieldFor(f1, sf2);
				
				if (f2 == null && !Arrays.asList(fieldNamesToSkipInValidate).contains(f1.name()))
				{
					indexer.getIndexingReport().addToErrorList("Different field values found for <" + name + ">" + " in <" +  m1.get(getObjidFieldName()) + ">\n");
					continue;
				}
			
				IndexOptions o1 = f1.getIndexOptions();
				IndexOptions o2 = f1.getIndexOptions();
				
				if (!o1.equals(o2))
				{
					indexer.getIndexingReport().addToErrorList("Different index options for <" + name + ">" + " in <" +  m1.get(getObjidFieldName()) + ">\n");
				};
				
			if (!shorten(f1.stringValue()).equals(shorten(f2.stringValue())))
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") value " + (f1.stringValue()) + " XXXXXXXXXXXXXXXXX " + (f2.stringValue())  + "\n");
				}
				
				if (f1.isIndexed() != f2.isIndexed())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isIndexed " + f1.isIndexed() + " - " + f2.isIndexed());
				}
				if (f1.isLazy() != f2.isLazy())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isLazy" + f1.isLazy() + " - " + f2.isLazy());
				}
				if (f1.isStored() != f2.isStored())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isStored" + f1.isStored() + " - " + f2.isStored());
				}
				if (f1.isTermVectorStored() != f2.isTermVectorStored())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isTermVectorStored" + f1.isTermVectorStored() + " - " + f2.isTermVectorStored());
				}
				if (f1.isTokenized() != f2.isTokenized())
				{
					indexer.getIndexingReport().addToErrorList("Difference in field(" + name + ") isTokenized" + f1.isTokenized() + " - " + f2.isTokenized());
				}
				
				logger.debug("comparing field <" + name + "> ok <" + (f1.stringValue()) + " XXXXXXXXX " + (f2.stringValue()) + ">");
			
			}
		}
	}

	private Fieldable findFieldFor(Fieldable f1, Set<Fieldable> sf2)
	{
		if (f1 == null || sf2 == null)
			return null;
		
		for (Fieldable f2 : sf2)
		{
			int c;
			
			if ("stored_fulltext".equals(f1.name()) || "stored_filename".equals(f1.name()))					
			{
				int i = 1;
				i++;
			}
			
			logger.trace("findFieldsFor compares <" + f1.stringValue() + "> with <" + f2.stringValue() + ">"); 
			if (shorten(f1.stringValue()).equals(shorten(f2.stringValue())))
			{
				logger.trace("Returning <" + f2.stringValue() +">" );
				return f2;
			}
		}
		
		logger.info("Nothing found for <" +  f1.name() + "><" + f1.stringValue() + "> in <" + sf2.iterator().next().stringValue() + ">");

		return null;
	}

	private Map<String, Set<Fieldable>> getMap(List<Fieldable> fields)
	{
		Map<String, Set<Fieldable>> map = new HashMap<String, Set<Fieldable>>();
		
		if (fields == null)
			return map;
		
		for (Fieldable f : fields)
		{
			String name = f.name();
			
			// we put all values together in the same HashSet for the fields "stored_filename1",  "stored_filename2" ..., same for aa_stored_filename
			// because the ordering of the components may differ
			if (name.startsWith("stored_filename"))
			{
				name = "stored_filename";
			}
			if (name.startsWith("stored_fulltext"))
			{
				name = "stored_fulltext";
			}

			if (name.startsWith("aa_stored_filename"))
			{
				name = "aa_stored_filename";
			}
			if (name.startsWith("aa_stored_fulltext"))
			{
				name = "aa_stored_fulltext";
			}
			Set<Fieldable> hset = map.get(name);
			
			if ("stored_fulltext".equals(name) || "stored_filename".equals(name))
			{
				int j = 0;
				j++;
			}
			
			if (hset == null)
			{
				hset = new HashSet<Fieldable>();	
			}
			
			if (!hset.contains(f))
			{
				hset.add(f);
			}
			
			map.put(name, hset);
		}

		return map;
	}
	
	private void compareSkippedFields(Map<String, Set<Fieldable>> m1,
			Map<String, Set<Fieldable>> m2)
	{
	   Set<Fieldable> s1 = m1.get("xml_representation");
	   Set<Fieldable> s2 = m1.get("xml_representation");
	   
	   if (s1 == null || s2 == null)
	       return;
		String xml_representation1 = m1.get("xml_representation").iterator().next().stringValue();
		String xml_representation2 = m2.get("xml_representation").iterator().next().stringValue();
		
		if (xml_representation1 == null || xml_representation2 == null)
		    return;
		
		de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO pubItem1 = null;
		de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO pubItem2 = null;
		
		de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean xmlTransforming = new de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean();
		
		try
		{
			pubItem1 = xmlTransforming.transformToPubItem(xml_representation1);
			pubItem2 = xmlTransforming.transformToPubItem(xml_representation2);
		} catch (TechnicalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!comparePubItemVOs(pubItem1, pubItem2))
		{
			indexer.getIndexingReport().addToErrorList("Difference when comparing <" + pubItem1.getPid() + "> with <" + pubItem2.getPid() + ">");					
		}
		
	}

	
	// the "0" is omitted because if we compare time stamps the last position may be withdrawn by escidoc in case of an ending "0"
	private String shorten(String stringValue)
	{
		String s = stringValue.replaceAll("[^A-Za-z1-9]", "");
		
		return s;
	}
	
	private Document getReferenceDocument(String field, String value, IndexSearcher searcher)
            throws IOException
	{
	     Document doc = null;
	     
	     Query query = new TermQuery(new Term(field, value));        
	     TopDocs topDocs = searcher.search(query, 1);
	     
	     ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	     
	     for (int j = 0; j < scoreDocs.length; ++j)
	     {
	            int docId = scoreDocs[j].doc;
	            
	            doc = searcher.getIndexReader().document(docId);
	     }
	     
	     return doc;
	}
	
	private boolean comparePubItemVOs(final de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO pubItem1, final de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO pubItem2)
	{
		if (pubItem1.getBaseUrl() != null && pubItem2.getBaseUrl() != null && !pubItem1.getBaseUrl().equals(pubItem2.getBaseUrl()))
				return false;
		if (!pubItem1.getContentModel().equals(pubItem2.getContentModel()))
				return false;
		if (!pubItem1.getContext().equals(pubItem2.getContext()))
			return false;
		if (!pubItem1.getCreationDate().equals(pubItem2.getCreationDate()))
			return false;
		if (!pubItem1.getFiles().equals(pubItem2.getFiles()))
			return false;
		if (!pubItem1.getLatestRelease().equals(pubItem2.getLatestRelease()))
			return false;
		if (!pubItem1.getLatestVersion().equals(pubItem2.getLatestVersion()))
			return false;
		if (!pubItem1.getLocalTags().equals(pubItem2.getLocalTags()))
			return false;
		if (!pubItem1.getLockStatus().equals(pubItem2.getLockStatus()))
			return false;
		if (!pubItem1.getLockStatus().equals(pubItem2.getLockStatus()))
			return false;
		if (!pubItem1.getMetadata().equals(pubItem2.getMetadata()))
			return false;
		if (!pubItem1.getModificationDate().equals(pubItem2.getModificationDate()))
			return false;
		if (!pubItem1.getOwner().equals(pubItem2.getOwner()))
			return false;
		if (!pubItem1.getPid().equals(pubItem2.getPid()))
			return false;
		if (!pubItem1.getPublicStatus().equals(pubItem2.getPublicStatus()))
			return false;
		if (!pubItem1.getPublicStatusComment().equals(pubItem2.getPublicStatusComment()))
			return false;
		if (!pubItem1.getVersion().equals(pubItem2.getVersion()))
			return false;
		if (pubItem1.getWithdrawalComment() != null && pubItem2.getWithdrawalComment() != null && !pubItem1.getWithdrawalComment().equals(pubItem2.getWithdrawalComment()))
			return false;
		
		return true;
	}
	
	public static void main(String[] args)
	{
	    try {
            Validator validator = new Validator(args[0], args[1]);
            
            logger.info("Comparing <" + args[0]+ " to <" + args[1] + ">");
            
            if (args[2] != null) {
                validator.setNumberOfFilesToValidate(Integer.valueOf(args[2]));
            }
            
            validator.compareToReferenceIndex();
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    
	}


}