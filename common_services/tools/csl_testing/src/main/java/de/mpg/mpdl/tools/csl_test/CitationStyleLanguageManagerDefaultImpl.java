package de.mpg.mpdl.tools.csl_test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.output.Bibliography;

/**
 * CitationStyleLanguageManagerDefaultImpl is used to generate a citation for an escidoc item or a list of escidoc items
 * 
 * @author walter
 */
@Stateless
@Remote
public class CitationStyleLanguageManagerDefaultImpl implements CitationStyleLanguageManagerInterface
{
    private final static String CITATION_PROCESSOR_OUTPUT_FORMAT = "html";

    private String citationStyle = null;
    
    /**
     * default constructor 
     */
    public CitationStyleLanguageManagerDefaultImpl ()
    {
    }
    
    /**
     * constructor for setting an citation style explicit
     * (not needed if you have a running CoNE instance and use the autosuggest)
     * 
     * @param citationStyleXml
     */
    public CitationStyleLanguageManagerDefaultImpl (String citationStyleXml)
    {
        this.citationStyle = citationStyleXml;
    }
    
    /*
     * (non-Javadoc)
     * @see org.citation_style_language_manager.CitationStyleLanguageManagerInterface #getOutput(java.lang.String)
     */
    public List<String> getOutput(String citationStyleUrl) throws Exception
    {
        List<String> citationList = new ArrayList<String>();
        StringWriter snippet = new StringWriter();
        ItemDataProvider itemDataProvider = new MetadataProvider();
        if(this.citationStyle == null) 
        {    
            this.citationStyle = CitationStyleLanguageUtils.loadStyleFromConeJsonUrl(citationStyleUrl);
        }
        String defaultLocale = CitationStyleLanguageUtils.parseDefaultLocaleFromStyle(citationStyle);
        CSL citeproc = null;
        if (defaultLocale != null)
        {
            citeproc = new CSL(itemDataProvider, citationStyle, defaultLocale);
        }
        else
        {
            citeproc = new CSL(itemDataProvider, citationStyle);
        }
        citeproc.registerCitationItems(itemDataProvider.getIds());
        citeproc.setOutputFormat(CITATION_PROCESSOR_OUTPUT_FORMAT);
        Bibliography bibl = citeproc.makeBibliography();
        
        List<String> biblIds = Arrays.asList(bibl.getEntryIds());
        
        // remove surrounding <div>-tags
        for (String id : itemDataProvider.getIds())
        {
        	String citation = "";
        	
        	int citationPosition = biblIds.indexOf(id);
        	if(citationPosition!=-1)
        	{
            	citation = bibl.getEntries()[citationPosition];
        	}
            citationList.add(citation);
        }
        return citationList;
    }

    /*
     * (non-Javadoc)
     * @see org.citation_style_language_manager.CitationStyleLanguageManagerInterface #isCitationStyle(java.lang.String)
     */
    public boolean isCitationStyle(String cs)
    {
        return false;
    }
}
