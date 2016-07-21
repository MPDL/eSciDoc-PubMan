/**
 * 
 */
package de.mpg.mpdl.tools.csl_test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import de.undercouch.citeproc.helper.CSLUtils;

/**
 * Utility class for static functions that are often needed when working with CSL
 * 
 * @author walter
 */
public class CitationStyleLanguageUtils
{

    /**
     * gets a csl style from a url
     * 
     * @param url
     * @return csl style xml as String or null if no style could be found or read
     * @throws Exception
     */
    protected static String loadStyleFromUrl(String url) throws Exception
    {
        String style = null;
        try
        {
            style = CSLUtils.readURLToString(new URL(url), "UTF-8");
        }
        catch (MalformedURLException e)
        {
            System.out.println("URL seems to be malformed, when trying to retrieve the csl style\n" + e.toString());
            throw new Exception(e);
        }
        catch (IOException e)
        {
            System.out.println("IO-Problem, when trying to retrieve the csl style\n" + e.toString());
            throw new Exception(e);
        }
        return style;
    }

    /**
     * gets a csl style from a cone url delivered in json format
     * 
     * @param url
     * @return csl style xml as String or null if no style could be found or read
     * @throws Exception
     */
    protected static String loadStyleFromConeJsonUrl(String url) throws Exception
    {
        String xml = null;
        try
        {
            JsonFactory jfactory = new JsonFactory();
            // read JSON from url
            JsonParser jParser = jfactory.createParser(new URL(url + "?format=json"));
            while (jParser.nextToken() != JsonToken.END_OBJECT)
            {
                String fieldname = jParser.getCurrentName();
                if ("http_www_w3_org_1999_02_22_rdf_syntax_ns_value".equals(fieldname))
                {
                    // current token is "name",
                    // move to next, which is "name"'s value
                    jParser.nextToken();
                    xml = jParser.getText();
                    break;
                }
            }
            jParser.close();
        }
        catch (JsonParseException e)
        {
            System.out.println("Error parsing json from URL (" + url + ")\n" + e.toString());
            throw new Exception(e);
        }
        catch (IOException e)
        {
            System.out.println("Error getting json from URL (" + url + ")\n" + e.toString());
            throw new Exception(e);
        }
        System.out.println("Successfully parsed CSL-XML from URL (" + url + ")\n--------------------\n" + xml
                    + "\n--------------------\n");
        return xml;
    }

    /**
     * parses the default-locale value of a csl citation style
     * 
     * @return
     */
    protected static String parseDefaultLocaleFromStyle(String style)
    {
        String defaultLocale = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(IOUtils.toInputStream(style, "UTF-8"));
            NodeList styleTagList = doc.getElementsByTagName("style");
            if (styleTagList != null && styleTagList.getLength() != 0)
            {
                defaultLocale = styleTagList.item(0).getAttributes().getNamedItem("default-locale").getNodeValue();
            }
        }
        catch (ParserConfigurationException e)
        {
            System.out.println("Wrong parser configuration\n" + e.toString());
            return null;
        }
        catch (SAXException e)
        {
            System.out.println("Problem creating XML\n" + e.toString());
            return null;
        }
        catch (IOException e)
        {
            System.out.println("Problem transforming String to InputStream\n" + e.toString());
            return null;
        }
        catch (Exception e)
        {
            // this is just the case when there is no attribute 'default-locale'
            System.out.println("Error getting default-locale attribute\n" + e.toString());
            return null;
        }
        return defaultLocale;
    }

    /**
     * parses a tag value out of an xml
     * 
     * @param xml
     * @param tagName
     * @return
     */
    public static String parseTagFromXml(String xml, String tagName, String namespaceUrl)
    {
        String tag = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(IOUtils.toInputStream(xml, "UTF-8"));
            NodeList tagList = doc.getElementsByTagNameNS(namespaceUrl, tagName);
            if (tagList != null && tagList.getLength() != 0)
            {
                tag = tagList.item(0).getFirstChild().getNodeValue();
                System.out.println("successfully parsed tag <" + tagList.item(0).getNodeName() + ">");
            }
        }
        catch (ParserConfigurationException e)
        {
            System.out.println("Wrong parser configuration\n" + e.toString());
            return null;
        }
        catch (SAXException e)
        {
            System.out.println("Problem creating XML\n" + e.toString());
            return null;
        }
        catch (IOException e)
        {
            System.out.println("Problem transforming String to InputStream\n" + e.toString());
            return null;
        }
        catch (Exception e)
        {
            // this is just the case when there is no attribute 'default-locale'
            System.out.println("Error getting value for <" + tagName + ">\n" + e.toString());
            return null;
        }
        return tag;
    }
}
