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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.sword;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDContentTypeException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.ServiceDocumentRequest;
import org.w3.atom.Author;
import org.w3.atom.Content;
import org.w3.atom.Generator;
import org.w3.atom.Source;
import org.w3.atom.Summary;
import org.w3.atom.Title;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.depositing.DepositingException;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.escidoc.services.pubman.exceptions.PubManException;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;
import de.mpg.escidoc.services.validation.ItemInvalidException;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * This class provides helper method for the SWORD Server implementation.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SwordUtil extends FacesBean
{

    private static final long serialVersionUID = 1L;
    public static final String BEAN_NAME = "SwordUtil";

    public static String LOGIN_URL = "/aa/login";
    public static String LOGOUT_URL = "/aa/logout/clear.jsp";
    private static final int NUMBER_OF_URL_TOKENS = 2;

    private Logger logger = Logger.getLogger(SwordUtil.class);
    private PubManDepositServlet depositServlet;

    private Vector<String> filenames = new Vector<String>();
    //Format of the provided Metadata
    private String depositXml ="";
    private String depositXmlFileName;
    
    private String validationPoint;


    //Packaging Format
    private final String acceptedFormat = "application/zip";
    //Metadata Format
    private final String mdFormatTEI = "http://www.tei-c.org/ns/1.0";   //Not yet supported
    private final String mdFormatEscidoc = "http://purl.org/escidoc/metadata/schemas/0.1/publication";
    private final String mdFormatBibTex = "BibTex";
    private final String mdFormatEndnote = "EndNote";
    private final String mdFormatPeerTEI = "http://purl.org/net/sword-types/tei/peer";
    private final String[] fileEndings = {".xml",".bib", ".tei", ".enl"};
    private Deposit currentDeposit;

    private final String itemPath = "/pubman/item/";
    private final String serviceDocUrl = "faces/sword/servicedocument";
    private final String validationPointAccept = "accept_item";
    private final String validationPointSubmit = "submit_item";
    private final String validationPointDefault = "default";
    private final String transformationService = "escidoc";
    private final String treatmentText = "Zip archives recognised as content packages are opened and the individual files contained in them are stored.";

    /**
     * Accepted packagings.
     */
    public String[] Packaging = { this.mdFormatEscidoc, this.mdFormatBibTex, this.mdFormatEndnote, this.mdFormatPeerTEI };
    
    /**
     * Public constructor.
     */
    public SwordUtil()
    {
        this.init();
    }

    /**
     * Initialisation method.
     */
    public void init()
    {
        this.depositServlet = new PubManDepositServlet();
        this.setValidationPoint(this.validationPointDefault);
        this.filenames.clear();
        super.init();
    }

    /**
     * Logs in a user.
     * @return AccountUserVO
     */
    public AccountUserVO checkUser (ServiceDocumentRequest sdr)
    {
        AccountUserVO userVO = null;
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        String username;
        String pwd;

        //Forward http authentification
        if (sdr.getUsername() != null && sdr.getPassword() != null)
        {
            username = sdr.getUsername();
            pwd = sdr.getPassword();
            try
            {
                String handle = this.loginUser(username, pwd);
                loginHelper.setESciDocUserHandle(handle);
                userVO = loginHelper.getAccountUser();
            }
            catch (Exception e)
            {
                this.logger.error(e);
                return null;
            }
        }
        return userVO;
    }
//
//    /**
//     * Retrieves all collections a user may deposit to.
//     * @param user
//     * @return Vector with all collections
//     */
//    public Vector <Collection> getDepositCollection (AccountUserVO user)
//    {
//        Vector <Collection> allCol = new Vector<Collection>();
//        List <PubContextVOPresentation> contextList = null;
//        ContextListSessionBean contextListBean = new ContextListSessionBean();
//        contextList = contextListBean.getDepositorContextList();
//
//        for (int i = 0; i < contextList.size(); i++)
//        {
//            PubContextVOPresentation pubContext = contextList.get(i);
//            //Create collection for all PubContextVOPresentation objects
//            Collection col = new Collection();
//            col.setTitle(pubContext.getName());
//            col.setAbstract(pubContext.getDescription());
//            //standard value for start.
//            col.setMediation(false);
//            col.setCollectionPolicy(this.getWorkflowAsString(pubContext));
//            //Collection identifier
//            col.setLocation(pubContext.getReference().getObjectId());
//            //static value
//            col.setTreatment(this.treatmentText);
//            //static value
//            col.setFormatNamespace(this.acceptedNs);
//            //static value
//            col.addAccepts(this.acceptedFormat);
//
//            allCol.add(col);
//        }
//        return allCol;
//    }

    /**
     * Checks if a user has depositing rights for a collection.
     * @param collection
     * @param user
     * @return true if the user has depositing rights, else false
     */
    public boolean checkCollection (String collection, AccountUserVO user)
    {
        List < PubContextVOPresentation > contextList = null;
        ContextListSessionBean contextListBean = new ContextListSessionBean();
        contextList = contextListBean.getDepositorContextList();
        for (int i = 0; i < contextList.size(); i++)
        {
            String context = contextList.get(i).getReference().getObjectId();
            if (context.toLowerCase().equals(collection.toLowerCase().trim()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Logs in the given user with the given password.
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public String loginUser(String userid, String password)
        throws HttpException, IOException, ServiceException, URISyntaxException
    {
        String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//");
        if (tokens.countTokens() != NUMBER_OF_URL_TOKENS )
        {
            throw new IOException ( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");

        if (hostPort.countTokens() != NUMBER_OF_URL_TOKENS )
        {
            throw new IOException( "Url in the config file is in the wrong format, needs to be http://<host>:<port>" );
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt(hostPort.nextToken());

        HttpClient client = new HttpClient();

        client.getHostConfiguration().setHost(host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        PostMethod login = new PostMethod (frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);

        client.executeMethod(login);

        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(host, port, "/", false,
                client.getState().getCookies());

        Cookie sessionCookie = logoncookies[0];

        PostMethod postMethod = new PostMethod(LOGIN_URL);
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);

        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }

        String userHandle = null;
        Header[] headers = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
            }
        }
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }

    /**
     * @param fc
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public void logoutUser()
        throws IOException, ServiceException, URISyntaxException
    {
        FacesContext fc = FacesContext.getCurrentInstance();

        fc.getExternalContext().redirect(
                ServiceLocator.getFrameworkUrl() + LOGOUT_URL + "?target="
                + URLEncoder.encode(PropertyReader.getProperty("escidoc.pubman.instance.url")
                        + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                        + "?logout=true", "UTF-8"));
    }

    /**
     * Creates a Account User.
     * @param user
     * @param pwd
     * @return AccountUserVO
     */
    public AccountUserVO getAccountUser (String user, String pwd)
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        String handle = "";

        try
        {
            handle = this.loginUser(user, pwd);
            loginHelper.fetchAccountUser(handle);
        }
        catch (Exception e)
        {
            return null;
        }
        return loginHelper.getAccountUser();
    }

    /**
     * This method takes a zip file and reads out the entries.
     * @param in
     * @throws TechnicalException
     * @throws NamingException
     * @throws SWORDContentTypeException
     */
    public PubItemVO readZipFile(InputStream in, AccountUserVO user)
        throws ItemInvalidException, ContentStreamNotFoundException, Exception
    {
        String item = null;
        Vector < byte[]> attachements = new Vector< byte[]>();
        Vector < String> attachementsNames = new Vector< String>();
        PubItemVO pubItem = null;
        int size = 0;
        final int bufLength = 1024;
        byte[] buffer = new byte[ bufLength ];
        int readReturn;
        int count = 0;

        try
        {
            ZipEntry zipentry;
            ZipInputStream zipinputstream = new ZipInputStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((zipentry = zipinputstream.getNextEntry()) != null)
            {
                count++;
                this.logger.debug("Processing zip entry file: " + zipentry.getName());
                baos = new ByteArrayOutputStream();
                while ((readReturn = zipinputstream.read(buffer)) != -1)
                {
                   baos.write(buffer, 0, readReturn);
                }

                String name = URLDecoder.decode(zipentry.getName(), "UTF-8");
                name = name.replaceAll("/", "_slsh_");
                this.filenames.add(name);
                
                //check if the file is a metadata file
                for (int i = 0; i < this.fileEndings.length; i++)
                {
                    String ending = this.fileEndings[i];
                    if (name.endsWith(ending))
                    {
                        //Retrieve the metadata
                        size = (int) zipentry.getSize();
                        item = new String(baos.toByteArray(), 0, size, "UTF-8");
                        this.depositXml = item;
                        this.depositXmlFileName = name;
                    }
                }

                attachements.add(baos.toByteArray());
                attachementsNames.add(name);

                zipinputstream.closeEntry();
            }
        zipinputstream.close();

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        if (count == 0)
        {
            this.logger.info("No zip file was provided.");
            this.depositServlet.setError("No zip file was provided.");
            throw new SWORDContentTypeException();
        }
        pubItem = this.processFiles(item, attachements, attachementsNames, user);

        return pubItem;
    }

    /**
     * @param item
     * @param files
     * @return
     * @throws NamingException
     * @throws TechnicalException
     * @throws SWORDContentTypeException
     */
    private PubItemVO processFiles(String item, Vector<byte[]> files, Vector<String> names, AccountUserVO user)
        throws ItemInvalidException, ContentStreamNotFoundException, Exception
    {
        PubItemVO itemVO = null;

        if (item == null)
        {
            throw new ContentStreamNotFoundException();
        }

        try
        {
            InitialContext initialContext = new InitialContext();
            XmlTransformingBean xmlTransforming = new XmlTransformingBean();
            ApplicationBean appBean = (ApplicationBean)getApplicationBean(ApplicationBean.class);
            Transformation transformer = appBean.getTransformationService();
            
            Format escidocFormat = new Format("escidoc-publication-item", "application/xml", "UTF-8");
            Format trgFormat = null;
            Boolean transform = false;
            
            //Transform from tei to escidoc-publication-item
            if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(this.mdFormatPeerTEI))
            {               
                trgFormat = new Format("peer_tei", "application/xml", "UTF-8");   
                transform = true;
            }
            //Transform from bibtex to escidoc-publication-item
            if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(this.mdFormatBibTex))
            {
                trgFormat = new Format("bibtex", "text/plain", "*");
                transform = true;
            }
            //Transform from endnote to escidoc-publication-item
            if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(this.mdFormatEndnote))
            {
                trgFormat = new Format("endnote", "text/plain", "UTF-8");
                transform = true;
            }
            if (transform)
            {
                item = new String(transformer.transform(item.getBytes("UTF-8"), trgFormat,
                        escidocFormat, this.transformationService), "UTF-8");
            }
            //Create item
            itemVO = xmlTransforming.transformToPubItem(item);
            
            
            //Set Version to null in order to force PubItemDepositingBean to create a new item.
            itemVO.setVersion(null);

            
            this.logger.debug("Item successfully created.");
        }
        catch (Exception e)
        {
           this.logger.error("Transformation to PubItem failed.", e);
           ValidationReportItemVO itemReport = new ValidationReportItemVO();
           itemReport.setContent("Error transforming item into eSciDoc Publication Item.");
           ValidationReportVO report = new ValidationReportVO();
           report.getItems().add(itemReport);
           throw new ItemInvalidException(report);
        }

        //Attach files to the item
        for (int i = 0; i < files.size(); i++)
        {
            byte[] file = files.get(i);
            String name = names.get(i);
            this.convertToFileAndAdd(file, name, user, itemVO);
            //itemVO.getFiles().add(fileVO);
        }

        return itemVO;
    }

    /**
     * @param user
     * @param item
     * @return Saved pubItem
     * @throws NamingException
     * @throws AuthorizationException
     * @throws SecurityException
     * @throws TechnicalException
     * @throws URISyntaxException
     * @throws NamingException
     * @throws ItemInvalidException
     * @throws PubManException
     * @throws DepositingException
     */
    public PubItemVO doDeposit(AccountUserVO user, PubItemVO item)
        throws ItemInvalidException, PubItemStatusInvalidException, Exception
    {
        PubItemVO depositedItem = null;
        InitialContext initialContext = new InitialContext();
        PubItemDepositing depositBean = (PubItemDepositing) 
            initialContext.lookup(PubItemDepositing.SERVICE_NAME);
        String method = this.getMethod(item); 

        if (method == null)
        {
            throw new PubItemStatusInvalidException(null, null);
        }
        if (method.equals("SAVE"))
        {
            depositedItem = depositBean.savePubItem(item, user);
        }
        if (method.equals("SAVE_SUBMIT") || method.equals("SUBMIT") )
        {
            depositedItem = depositBean.savePubItem(item, user);
            depositedItem = depositBean.submitPubItem(depositedItem, "", user);
        }
        if (method.equals("RELEASE"))
        {
            depositedItem = depositBean.savePubItem(item, user);
            depositedItem = depositBean.submitAndReleasePubItem(depositedItem, "", user);
        }

        return depositedItem;
    }

    /**
     * Returns the Workflow of the current context.
     */
    public PublicationAdminDescriptorVO.Workflow getWorkflow()
    {

        if ((getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow()
                == PublicationAdminDescriptorVO.Workflow.STANDARD))
        {
            return Workflow.STANDARD;
        }
        if ((getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow()
                == PublicationAdminDescriptorVO.Workflow.SIMPLE))
        {
            return Workflow.SIMPLE;
        }
        return null;
    }

    public String getMethod (PubItemVO item)
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);

        boolean isWorkflowStandard = false;
        boolean isWorkflowSimple = true;

        boolean isStatePending = true;
        boolean isStateSubmitted = false;
        boolean isStateReleased = false;
        boolean isStateInRevision = false;

        if (item != null && item.getVersion() != null && item.getVersion().getState() != null)
        {
            isStatePending = item.getVersion().getState().equals(PubItemVO.State.PENDING);
            isStateSubmitted = item.getVersion().getState().equals(PubItemVO.State.SUBMITTED);
            isStateReleased = item.getVersion().getState().equals(PubItemVO.State.RELEASED);
            isStateInRevision = item.getVersion().getState().equals(PubItemVO.State.IN_REVISION);
        }

        isWorkflowStandard = getItemControllerSessionBean().getCurrentContext()
            .getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO
            .Workflow.STANDARD;
        isWorkflowSimple = getItemControllerSessionBean().getCurrentContext()
            .getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO
            .Workflow.SIMPLE;

        boolean isModerator = loginHelper.getAccountUser().isModerator(item.getContext());
        boolean isOwner = true;
        if (item.getOwner() != null)
        {
            isOwner = (loginHelper.getAccountUser().getReference() != null ?
                    loginHelper.getAccountUser().getReference().getObjectId()
                    .equals(item.getOwner().getObjectId()) : false);
        }

        if ((isStatePending || isStateSubmitted) && isWorkflowSimple && isOwner)
        {
            this.setValidationPoint(this.validationPointAccept);
            return "RELEASE";
        }
        if ((isStatePending || isStateInRevision) &&  isWorkflowStandard && isOwner)
        {
            this.setValidationPoint(this.validationPointSubmit);
            return "SAVE_SUBMIT";
        }
        if (((isStatePending || isStateInRevision) && isOwner) || (isStateSubmitted && isModerator))
        {
            this.setValidationPoint(this.validationPointSubmit);
            return "SUBMIT";
        }
        return null;
    }

    /**
     * Returns the Workflow for a given context.
     * @param pubContext
     * @return workflow type as string
     */
    public String getWorkflowAsString(PubContextVOPresentation pubContext)
    {
        boolean isWorkflowStandard = pubContext.getAdminDescriptor().getWorkflow() ==
            PublicationAdminDescriptorVO.Workflow.STANDARD;
        boolean isWorkflowSimple = pubContext.getAdminDescriptor().getWorkflow() ==
            PublicationAdminDescriptorVO.Workflow.SIMPLE;


        if (isWorkflowStandard)
        {
            return "Standard Workflow";
        }
        if (isWorkflowSimple)
        {
            return "Simple Workflow";
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns a reference to the scoped data bean
     * (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean
     */
    protected de.mpg.escidoc.pubman.ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (de.mpg.escidoc.pubman.ItemControllerSessionBean)getBean(ItemControllerSessionBean.class);
    }

    public Vector<String> getFileNames()
    {
        return this.filenames;
    }

    /**
     * Converts a byte[] into a FileVO.
     * @param file
     * @param name
     * @param user
     * @return FileVO
     * @throws Exception
     */
    private FileVO convertToFileAndAdd (byte[] file, String name, AccountUserVO user, PubItemVO itemVO)
        throws Exception
    {
        boolean existing = false;
        MdsFileVO mdSet = new MdsFileVO();
        FileVO fileVO = new FileVO();
        String fileXml = null;

        ByteArrayInputStream in = new ByteArrayInputStream(file);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(name);
        ApplicationBean appBean = (ApplicationBean)getApplicationBean(ApplicationBean.class);
        Transformation transformer = appBean.getTransformationService();

        //Hack: FileNameMap class does not know tei, bibtex and endnote
        if (name.endsWith(".tei"))
        {
            mimeType = "application/xml";
        }
        if (name.endsWith(".bib") || name.endsWith(".enl"))
        {
            mimeType = "text/plain";
        }

        URL fileURL = this.uploadFile(in, mimeType, user.getHandle());

        if (fileURL != null && !fileURL.toString().trim().equals(""))
        {
            if (this.currentDeposit.getFormatNamespace().equals(this.mdFormatPeerTEI))
            {
                //Copyright information are imported from metadata file
                InitialContext initialContext = new InitialContext();
                XmlTransformingBean xmlTransforming = new XmlTransformingBean();
                Format teiFormat = 
                    new Format("peer_tei", "application/xml", "UTF-8");
                Format escidocComponentFormat = 
                    new Format("eSciDoc-publication-component", "application/xml", "UTF-8");
                fileXml = new String (transformer.transform(this.depositXml.getBytes(),
                        teiFormat, escidocComponentFormat, "escidoc"),"UTF-8");
                try
                {
                    fileVO = xmlTransforming.transformToFileVO(fileXml);
                    mdSet = fileVO.getDefaultMetadata();
                }
                catch (TechnicalException e)
                {
                    this.logger.error("File Xml could not be transformed into FileVO. " , e);
                }
            }
            
            if (this.currentDeposit.getContentDisposition()!= null
                    && !this.currentDeposit.getContentDisposition().equals(""))
            {
                name = this.currentDeposit.getContentDisposition();
            }
            
          

            fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            fileVO.setVisibility(FileVO.Visibility.PUBLIC);
            fileVO.setDefaultMetadata(mdSet);
            fileVO.getDefaultMetadata().setTitle(new TextVO(name));
            fileVO.setMimeType(mimeType);
            fileVO.setName(name);

            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(mimeType);
            fileVO.getDefaultMetadata().getFormats().add(formatVO);
            fileVO.setContent(fileURL.toString());
            fileVO.getDefaultMetadata().setSize(file.length);
            //This is the provided metadata file which we store as a component
            if (!name.endsWith(".pdf"))
            {
                fileVO.setContentCategory(PubFileVOPresentation.ContentCategory.SUPPLEMENTARY_MATERIAL.getUri());
            }
            else
            {
                fileVO.setContentCategory(PubFileVOPresentation.ContentCategory.PUBLISHER_VERSION.getUri());
            }
            
            
            
            //if escidoc item: check if it has already components with this filename. If true, use existing file information.
            if(this.currentDeposit.getFormatNamespace().equals(this.mdFormatEscidoc))
            {
                for(FileVO existingFile : itemVO.getFiles())
                {
                    if(existingFile.getName().replaceAll("/", "_slsh_").equals(name))
                    {
                        existingFile.setContent(fileURL.toString());
                        existingFile.getDefaultMetadata().setSize(file.length);
                        existing = true;
                    }
                }
                
                //If the file is the metadata file, do not add it for escidoc format
                if(name.equals(depositXmlFileName))
                {
                    existing = true;
                }
            }
        }

        if(!existing)
        {
            itemVO.getFiles().add(fileVO);
        }
        return fileVO;
    }

    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * @param InputStream to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    protected URL uploadFile(InputStream in, String mimetype, String userHandle)
        throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(in));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        InitialContext context = new InitialContext();
        XmlTransformingBean ctransforming = new XmlTransformingBean();
        return ctransforming.transformUploadResponseToFileURL(response);
    }

    public SWORDEntry createResponseAtom (PubItemVO item, Deposit deposit, boolean valid)
    {
        SWORDEntry se = new SWORDEntry();
        PubManSwordServer server = new PubManSwordServer();

        //This info can only be filled if item was successfully created
        if (item != null)
        {
            Title title = new Title();
            title.setContent(item.getMetadata().getTitle().getValue());
            se.setTitle(title);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone utc = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(utc);
            String milliFormat = sdf.format(new Date());
            se.setUpdated(milliFormat);            
        }

        Summary s = new Summary();
        Vector < String> filenames = this.getFileNames();
        String filename = "";
        for (int i = 0; i < filenames.size(); i++)
        {
            if (filename.equals(""))
            {
                filename = filenames.get(i);
            }
            else
            {
                filename = filename + " ," + filenames.get(i);
            }
        }
        s.setContent(filename);
        se.setSummary(s);

        Content content = new Content();
        content.setSource("");
        //Only set content if item was deposited
        if (!deposit.isNoOp() && item != null && valid)
        {            
            content.setSource(server.getCoreserviceURL() + "/ir/item/" + item.getVersion().getObjectId());     
            se.setId(server.getBaseURL() + this.itemPath + item.getVersion().getObjectId());
        }
        se.setContent(content);

        Source source = new Source();
        Generator generator = new Generator();
        generator.setContent(server.getBaseURL());
        source.setGenerator(generator);
        se.setSource(source);

        se.setTreatment(this.treatmentText);
        se.setNoOp(deposit.isNoOp());
        
        //Add the login name
        Author author = new Author();
        author.setName(deposit.getUsername());
        se.addAuthors(author);

        return se;
    }

    public ValidationReportVO validateItem(PubItemVO item)
        throws NamingException
    {
        InitialContext initialContext = new InitialContext();
        ItemValidating itemValidating = (ItemValidating)initialContext.lookup(ItemValidating.SERVICE_NAME);

        //To set the validation point
        this.getMethod(item);

        ValidationReportVO report = new ValidationReportVO();

        try
        {
            report = itemValidating.validateItemObject(item, this.getValidationPoint());
        }
        catch (Exception e)
        {
            this.logger.error("Validation error", e);
        }
        return report;
    }
    
    public boolean checkMetadatFormat(String format)
    {
        for (int i = 0; i < this.Packaging.length; i++) 
        {
            String pack = this.Packaging[i];
            if (format.equalsIgnoreCase(pack))
            {
                return true;
            }
        }

        return false;
    }

    public String getValidationPoint()
    {
        return this.validationPoint;
    }

    public void setValidationPoint(String validationPoint)
    {
        this.validationPoint = validationPoint;
    }

    public String getAcceptedFormat()
    {
        return this.acceptedFormat;
    }

    public String getTreatmentText()
    {
        return this.treatmentText;
    }   


    public Deposit getCurrentDeposit()
    {
        return this.currentDeposit;
    }

    public void setCurrentDeposit(Deposit currentDeposit)
    {
        this.currentDeposit = currentDeposit;
    }
}
