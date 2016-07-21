package de.mpg.mpdl.tools.csl_test;

import java.util.List;

/**
 * CitationStyleLanguageExecutor will simply process a sample item with a
 * citationstyle
 * 
 * @author walter
 * 
 */
public class CitationStyleLanguageExecutor {

	
	
	public static void main(String[] args) throws Exception {
		CitationStyleLanguageManagerInterface cslManager = new CitationStyleLanguageManagerDefaultImpl();
		List<String> citations = cslManager.getOutput(args[0]);
		for(String citation : citations)
		{
		    System.out.println(citation);
		}
	}
	

}
