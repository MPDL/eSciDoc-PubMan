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

package de.mpg.escidoc.pubman.desktop;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Fragment class for the corresponding Header-JSP.
 * 
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class Header extends FacesBean
{
    /** Logo for dev environment. */
    private static final String LOGO_DEV = "overlayDev";
    /** Logo for qa environment. */
    private static final String LOGO_QA = "overlayQA";
    /** Logo for test environment. */
    private static final String LOGO_TEST = "overlayTest";
    
    private String type;
    
    /**
     * Public constructor.
     */
    public Header()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }
    
    /**
     * Getter for the logo definition f the type of the server. E.g a dev
     * server gets another logo than an demo server.
     * @return  logo definition
     */
    public String getServerLogo() 
    {
        String serverLogo = "";
        try
        {
            
            if (getType().equals("dev"))
            {
                serverLogo = LOGO_DEV;
            }
            else if (getType().equals("test"))
            {
                serverLogo = LOGO_TEST;
            }
            else if (getType().equals("qa"))
            {
                serverLogo = LOGO_QA;
            }
            return serverLogo.toString();        
        } 
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * Get instance type property. Return an empty string if it is not defined.
     * 
     * @return A string representing the instance type, e.g. "dev", "qa".
     * @throws Exception Any exception from PropertyReader
     */
    public String getType() throws Exception
    {
        if (type == null)
        {
            type = PropertyReader.getProperty("escidoc.systemtype");
            if (type == null)
            {
                type = "";
            }
        }
        return type;
    }
    
}