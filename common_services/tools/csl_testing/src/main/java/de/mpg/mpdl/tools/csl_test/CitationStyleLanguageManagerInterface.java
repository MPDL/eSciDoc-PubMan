package de.mpg.mpdl.tools.csl_test;

import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;

/**
 * Interface for transforming a list of items into a citation output for each of the items
 * 
 * @author walter
 */
public interface CitationStyleLanguageManagerInterface
{

    byte[] getOutput(ExportFormatVO exportFormat, String itemList) throws Exception;

    boolean isCitationStyle(String cs);
}
