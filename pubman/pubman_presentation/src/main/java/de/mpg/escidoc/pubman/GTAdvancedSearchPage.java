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

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * GTAdvancedSearchPage.java Backing bean for GTAdvancedSearchPage.jsp This is for the GUI tool mode.
 * The pubman frame will not be displayed.
 *
 * @author: Hugo Niedermaier
 */
public class GTAdvancedSearchPage extends FacesBean
{

    /**
     * Public constructor.
     */
    public GTAdvancedSearchPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        // Set the current session to GUI Tool
        CommonSessionBean sessionBean = getSessionBean();
        sessionBean.setRunAsGUITool(true);
    }

    /**
     * Returns the CommonSessionBean.
     *
     * @return a reference to the scoped data bean (CommonSessionBean)
     */
    protected CommonSessionBean getSessionBean()
    {
        return (CommonSessionBean) getSessionBean(CommonSessionBean.class);
    }
}
