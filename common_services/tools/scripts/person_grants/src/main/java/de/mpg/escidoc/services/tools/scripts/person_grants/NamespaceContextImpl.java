package de.mpg.escidoc.services.tools.scripts.person_grants;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * NamespaceContext for parsing eSciDoc search, grant and user account XMLs
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class NamespaceContextImpl implements NamespaceContext
{
    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    @Override
    public String getNamespaceURI(String prefix)
    {
        switch (prefix) {
            case "zs": return "http://www.loc.gov/zing/srw/";
            case "search-result": return "http://www.escidoc.de/schemas/searchresult/0.8";
            case "user-account": return "http://www.escidoc.de/schemas/useraccount/0.7";
            case "grants": return "http://www.escidoc.de/schemas/grants/0.5";
            case "xlink": return "http://www.w3.org/1999/xlink";
            default: return XMLConstants.NULL_NS_URI;
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    @Override
    public String getPrefix(String namespaceURI)
    {
        return null; // we are not using this.
    }

    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    @Override
    public Iterator getPrefixes(String namespaceURI)
    {
        return null; // we are not using this.
    }
}
