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

package de.mpg.escidoc.services.tools.scripts.person_grants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import javax.annotation.Syntax;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

/**
 * Util Class for person_grants project
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Util
{
    private static String adminUserHandle;
    private static Date loginTime;
    
    /**
     * Queries an eSciDoc instance
     * 
     * @param url
     * @param query
     * @param adminUserName
     * @param adminPassword
     * @param frameworkUrl
     * @return
     */
    public static Document queryFramework(String url, String query, String adminUserName, String adminPassword, String frameworkUrl)
    {
        try
        {
            DocumentBuilder documentBuilder;
            DocumentBuilderFactory documentBuilderFactory =  DocumentBuilderFactoryImpl.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            HttpClient client = new HttpClient();
            client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
            GetMethod getMethod = new GetMethod(url + "?query=" + (query != null ? URLEncoder.encode(query, "UTF-8") :"") + "&eSciDocUserHandle="
                    + Base64.encode(getAdminUserHandle(adminUserName, adminPassword, frameworkUrl)
                            .getBytes("UTF-8")));
            System.out.println("Querying <" + url + "?query=" + (query != null ? URLEncoder.encode(query, "UTF-8") :"") + "&eSciDocUserHandle="
                    + Base64.encode(getAdminUserHandle(adminUserName, adminPassword, frameworkUrl)
                            .getBytes("UTF-8")));
            client.executeMethod(getMethod);
            if (getMethod.getStatusCode() == 200)
            {
                document =  documentBuilder.parse(getMethod.getResponseBodyAsStream());
            }
            else
            {
                System.out.println("Error querying: Status " + getMethod.getStatusCode()
                        + "\n" + getMethod.getResponseBodyAsString());
            }
            return document;
        }
        catch (Exception e)
        {
            try
            {
                System.out.println("Error querying Framework <" +  url + "?query=" + (query != null ? URLEncoder.encode(query, "UTF-8") :"") + "&eSciDocUserHandle="
                        + Base64.encode(getAdminUserHandle(adminUserName, adminPassword, frameworkUrl)
                                .getBytes("UTF-8")) + ">");
            }
            catch (UnsupportedEncodingException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * get a admin user handle for an escidoc instance
     * 
     * @param adminUserName
     * @param adminUserPassword
     * @param frameworkUrl
     * @return
     */
    public static String getAdminUserHandle(String adminUserName, String adminUserPassword, String frameworkUrl)
    {
        Date now = new Date();
        // Renew every hour
        if (adminUserHandle == null || loginTime == null || loginTime.getTime() < now.getTime() - 1 * 60 * 60 * 1000)
        {
            try
            {
                loginTime = new Date();
                adminUserHandle = loginUser(adminUserName, adminUserPassword, frameworkUrl);
            }
            catch (Exception e)
            {
                System.out.println("Exception logging on admin user.");
                e.printStackTrace();
            }
        }
        return adminUserHandle;
    }

    /**
     * Logs in the given user with the given password.
     * 
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public static String loginUser(String userid, String password, String frameworkUrl)
            throws HttpException, IOException, ServiceException, URISyntaxException
    {
        int delim1 = frameworkUrl.indexOf("//");
        int delim2 = frameworkUrl.indexOf(":", delim1);
        String host;
        int port;
        if (delim2 > 0)
        {
            host = frameworkUrl.substring(delim1 + 2, delim2);
            port = Integer.parseInt(frameworkUrl.substring(delim2 + 1));
        }
        else
        {
            host = frameworkUrl.substring(delim1 + 2);
            port = 80;
        }
        HttpClient client = new HttpClient();
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        client.executeMethod(login);
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
//        Cookie[] logoncookies = cookiespec.match(host, port, "/", false, client.getState().getCookies());
        Cookie sessionCookie = client.getState().getCookies()[0];
        PostMethod postMethod = new PostMethod(frameworkUrl + "/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        if (HttpStatus.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
                // System.out.println("location: "+location);
                // System.out.println("handle: "+userHandle);
            }
        }
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }
}
