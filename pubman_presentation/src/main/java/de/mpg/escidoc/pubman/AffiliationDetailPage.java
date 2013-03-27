/*
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
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.affiliation.AffiliationTree;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * 
 * Request Bean for affiliation details page (which is a popup taht is opened when clicking an info button on affiliation tree)
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 1891 $ $LastChangedDate: 2008-12-23 19:13:59 +0900 (火, 23 12月 2008) $
 *
 */
public class AffiliationDetailPage extends FacesBean
{
    public static String BEAN_NAME = "AffiliationDetailPage";
    
	private static Logger logger = Logger.getLogger(AffiliationDetailPage.class);

	private AffiliationVOPresentation affilitation;

    private XmlTransforming xmlTransforming;

	/**
	 * Construct a new Page bean instance.
	 */
	public AffiliationDetailPage()
    {
	    this.init();
	    try
        {
            InitialContext initialContext = new InitialContext();
            this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            
            AffiliationTree affTree = (AffiliationTree) getApplicationBean(AffiliationTree.class);
            String affiliationId = getFacesContext().getExternalContext().getRequestParameterMap().get("id");
            
            OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();
            String ouXml = ouHandler.retrieve(affiliationId);
            AffiliationVO affVO = xmlTransforming.transformToAffiliation(ouXml);
            this.affilitation = new AffiliationVOPresentation(affVO);
        }
        catch (Exception e)
        {
            error(getMessage("AffiliationDetailPage_detailsNotRetrieved"));
        }

    
	}

	
	public void init()
    {
		
	}


    public void setAffilitation(AffiliationVOPresentation affilitation)
    {
        this.affilitation = affilitation;
    }


    public AffiliationVOPresentation getAffilitation()
    {
        return affilitation;
    }
    
    public String getDescription()
    {
        if (affilitation!=null && affilitation.getDefaultMetadata()!=null && affilitation.getDefaultMetadata().getDescriptions().size()>0)
            return affilitation.getDefaultMetadata().getDescriptions().get(0);
        else
            return "";
    }
}