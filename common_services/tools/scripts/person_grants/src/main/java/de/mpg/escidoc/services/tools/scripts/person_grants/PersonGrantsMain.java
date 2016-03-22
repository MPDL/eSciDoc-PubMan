package de.mpg.escidoc.services.tools.scripts.person_grants;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.mpg.escidoc.services.tools.scripts.person_grants.data.Person;

/**
 * Script for getting user and their grants for one specific ou and instance
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class PersonGrantsMain
{
    private final static String USERACCOUNT_PATH = "/aa/user-accounts";
    private final static String ORGANIZATION_QUERY = "\"/structural-relations/organizational-unit\"=";
    private final static String XPATH_USERACCOUNT = "/zs:searchRetrieveResponse/zs:records/zs:record/zs:recordData/search-result:search-result-record/user-account:user-account";
    private final static String XPATH_GRANT = "/grants:current-grants/grants:grant";

    public static void main(String[] args)
    {
        try
        {
            System.out.println("---------------------");
            System.out.println("Start processing with following arguments:");
            for (int i = 0; i < args.length; i++)
            {
                System.out.println("Argument[" + i + "]: " + args[i]);
            }
            System.out.println("---------------------");
            List<Person> personList = getPersonList(args[0], args[1], args[2], args[3]);
            System.out.println("Anzahl an Personen: " + personList.size());
            Person person = null;
            List<String> personGrantList = null;
            for (int i = 0; i < personList.size(); i++)
            {
                person = personList.get(i);
                System.out.println("---------------------");
                System.out.println("Person [" + i + "]: " + person.getName());
                System.out.println("Grants:");
                personGrantList = personList.get(i).getGrantList();
                for (int j = 0; j < personList.get(i).getGrantList().size(); j++)
                {
                    System.out.println(personGrantList.get(j));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("---------------------");
            System.out.println("Error running transformation. Please check your arguments.");
            System.out.println("Argument 1: Framework Url");
            System.out.println("Argument 2: admin user name");
            System.out.println("Argument 3: admin user password");
            System.out.println("Argument 4: id of the organizational unit");
            System.out.println("---------------------");
        }
    }

    /**
     * retrieves a list of Persons for a given organizational unit
     * 
     * @param frameworkUrl
     * @param adminUserName
     * @param adminPassword
     * @param ouId
     * @return
     */
    private static List<Person> getPersonList(String frameworkUrl, String adminUserName, String adminPassword,
            String ouId)
    {
        List<Person> personList = new ArrayList<Person>();
        try
        {
            Document xmlDocument = Util.queryFramework(
                    frameworkUrl + USERACCOUNT_PATH, ORGANIZATION_QUERY + "\"" + ouId + "\"", adminUserName,
                    adminPassword, frameworkUrl);
            XPath xPath = XPathFactory.newInstance().newXPath();
            NamespaceContext nsContext = new NamespaceContextImpl();
            xPath.setNamespaceContext(nsContext);
            NodeList nodeListUserAccounts = (NodeList)xPath.compile(XPATH_USERACCOUNT).evaluate(xmlDocument,
                    XPathConstants.NODESET);
            String nodeName;
            NodeList nodeListUserAccount = null;
            Element element = null;
            Node currentNode = null;
            for (int x = 0; x < nodeListUserAccounts.getLength(); x++)
            {
                nodeListUserAccount = nodeListUserAccounts.item(x).getChildNodes();
                Person person = new Person();
                System.out.println("------------------------");
                for (int i = 0; i < nodeListUserAccount.getLength(); i++)
                {
                    NodeList personChildNodes = nodeListUserAccount.item(i).getChildNodes();
                    for (int j = 0; j < personChildNodes.getLength(); j++)
                    {
                        currentNode = personChildNodes.item(j);
                        nodeName = currentNode.getNodeName();
                        System.out.println("NodeName: " + nodeName + " - NodeType: " + currentNode.getNodeType());
                        String childNodeValue = null;
                        if (currentNode.getFirstChild() != null)
                        {
                            childNodeValue = currentNode.getFirstChild().getNodeValue();
                            System.out.println("ChildNodeValue: " + childNodeValue);
                        }
                        if ("uid".equals(nodeName))
                        {
                            if (currentNode.getFirstChild() != null)
                            {
                                person.setId(childNodeValue);
                            }
                        }
                        else if ("prop:name".equals(nodeName))
                        {
                            if (currentNode.getFirstChild() != null)
                            {
                                person.setName(childNodeValue);
                            }
                        }
                        else if ("user-account:current-grants".equals(nodeName))
                        {
                            element = (Element) currentNode;
                            person = addUserGrants(frameworkUrl, adminUserName, adminPassword, element.getAttribute("xlink:href"), person);
                        }
                    }
                }
                if (!person.isEmpty())
                {
                    personList.add(person);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error reading file [" + PersonGrantsMain.class.getEnclosingMethod() + "]");
            e.printStackTrace();
        }
        return personList;
    }
    
    /**
     * adds all grants to a given person for a specified  instance
     * 
     * @param frameworkUrl
     * @param adminUserName
     * @param adminPassword
     * @param href
     * @param person
     * @return
     */
    private static Person addUserGrants(String frameworkUrl, String adminUserName, String adminPassword,
            String href, Person person)
    {
        Document xmlDocument = Util.queryFramework(
                frameworkUrl + href, null, adminUserName,
                adminPassword, frameworkUrl);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NamespaceContext nsContext = new NamespaceContextImpl();
        xPath.setNamespaceContext(nsContext);
        try
        {
            NodeList nodeListGrants = (NodeList)xPath.compile(XPATH_GRANT).evaluate(xmlDocument,
                    XPathConstants.NODESET);
            String nodeName = null;
            System.out.println("Grant list size: " + nodeListGrants.getLength());
            Element element = null;
            Node currentNode = null;
            for (int i = 0; i < nodeListGrants.getLength(); i++)
            {
                currentNode = nodeListGrants.item(i);
                nodeName = currentNode.getNodeName();
                System.out.println("NodeName: " + nodeName + " - NodeType: " + currentNode.getNodeType());
                element = (Element) currentNode;
                System.out.println("Grant title: " + element.getAttribute("xlink:title"));
                person.addGrant(element.getAttribute("xlink:title"));
            }
        }
        catch (Exception e)
        {
            System.out.println("Error reading file [" + PersonGrantsMain.class.getEnclosingMethod() + "]");
            e.printStackTrace();
        }
        
        return person;
    }

}
