/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test.common.xmltransforming;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import test.common.TestBase;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This class enriches the TestBase class with XML-specific methods.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ @revised by MuJ: 28.08.2007
 */
public class XmlTransformingTestBase extends TestBase
{
    /**
     * The charset encoding, e.g. for DOM to String conversions.
     */
    private final static String XML_CHARSET_ENCODING = "UTF-8";
    /**
     * The {@link javax.xml.parsers.DocumentBuilderFactory} for this class.
     */
    private static DocumentBuilderFactory m_docBuilderFactory = null;
    /**
     * The {@link javax.xml.xpath.XPathFactory} for this class.
     */
    private static XPathFactory m_xPathFactory = null;
    /**
     * The {@link javax.xml.xpath.XPath} for this class.
     */
    private static XPath m_xPath = null;
    /**
     * Flag to determine whether this class has been initialized (by the init() method).
     */
    private static boolean initialized = false;

    /**
     * Initialize the class, i.e. set the member variables so that they can be shared by different methods.
     * 
     * @throws ParserConfigurationException
     */
    protected static void init() throws ParserConfigurationException
    {
        m_docBuilderFactory = DocumentBuilderFactory.newInstance();
        m_docBuilderFactory.setNamespaceAware(true);
        m_xPathFactory = XPathFactory.newInstance();
        m_xPath = m_xPathFactory.newXPath();
        m_xPath.setNamespaceContext(new XPathNamespaceContext());
        setInitialized(true);
    }

    /**
     * Serialize the given Dom Object to a String.
     * 
     * @param xml The Xml Node to serialize.
     * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
     * @return The String representation of the Xml Node.
     * @throws Exception If anything fails.
     */
    protected static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception
    {
        if (!isInitialized())
        {
            init();
        }

        String result = new String();
        if (xml instanceof AttrImpl)
        {
            result = xml.getTextContent();
        }
        else if (xml instanceof Document)
        {
            StringWriter stringOut = new StringWriter();
            // format
            OutputFormat format = new OutputFormat((Document)xml);
            format.setIndenting(true);
            format.setPreserveSpace(false);
            format.setOmitXMLDeclaration(omitXMLDeclaration);
            format.setEncoding(XML_CHARSET_ENCODING);
            // serialize
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();

            serial.serialize((Document)xml);
            result = stringOut.toString();
        }
        else
        {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
            LSOutput lsOutput = impl.createLSOutput();
            lsOutput.setEncoding(XML_CHARSET_ENCODING);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            lsOutput.setByteStream(os);
            LSSerializer writer = impl.createLSSerializer();
            // result = writer.writeToString(xml);
            writer.write(xml, lsOutput);
            result = ((ByteArrayOutputStream)lsOutput.getByteStream()).toString();
            if ((omitXMLDeclaration) && (result.indexOf("?>") != -1))
            {
                result = result.substring(result.indexOf("?>") + 2);
            }
            // result = toString(getDocument(writer.writeToString(xml)),
            // true);
        }
        return result;
    }

    /**
     * Delivers the flag to determine whether this class has been initialized yet.
     * 
     * @return true if this class has been initialized yet.
     */
    public static boolean isInitialized()
    {
        return initialized;
    }

    /**
     * Sets the flag to determine whether this class has been initialized yet.
     * 
     * @param initialized
     */
    private static void setInitialized(boolean initialized)
    {
        XmlTransformingTestBase.initialized = initialized;
    }
}
