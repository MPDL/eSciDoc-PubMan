package de.mpg.mpdl.tools.csl_test;

import java.util.List;

/**
 * Interface for transforming a list of items into a citation output for each of the items
 * 
 * @author walter
 */
public interface CitationStyleLanguageManagerInterface
{
    List<String> getOutput(String citationstyleId) throws Exception;

    boolean isCitationStyle(String cs);
}
