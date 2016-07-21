/**
 * 
 */
package de.mpg.mpdl.tools.csl_test;

import java.util.Calendar;

import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLName;
import de.undercouch.citeproc.csl.CSLNameBuilder;
import de.undercouch.citeproc.csl.CSLType;

/**
 * MetadataProvider provides csl item data for a given escidoc item-List
 * @author walter
 * 
 */
public class MetadataProvider implements ItemDataProvider {
	
	public MetadataProvider () {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.undercouch.citeproc.ItemDataProvider#getIds()
	 */
	public String[] getIds() {
		return new String[] {"1234"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.undercouch.citeproc.ItemDataProvider#retrieveItem(java.lang.String)
	 */
	public CSLItemData retrieveItem(String id) {
		CSLItemDataBuilder cslItem = new CSLItemDataBuilder().id(id);
		
		// Genre
        cslItem.type(CSLType.ARTICLE);
        
        // Title
        cslItem.title("A nice little article");

        
		CSLName[] authorArray = new CSLName[] {
		        new CSLNameBuilder().given("Hans").family("Müller").build(), 
		        new CSLNameBuilder().given("Fritz").family("Fischer").build()
		};
		
		cslItem.author(authorArray);
		
		Calendar calendar = Calendar.getInstance();
		cslItem.issued(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
		
		cslItem.locator("A Locator");
		
		// build an return cslItem
		return cslItem.build();
	}
}
	
