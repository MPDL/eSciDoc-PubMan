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
* or http://www.escidoc.org/license.
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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;

/**
 * Bean to handle the IdentifierCollection on a single jsp.
 * A IdentifierCollection is represented by a List<IdentifierVO>.
 *
 * @author Mario Wagner
 */
public class IdentifierCollection
{
    private List<IdentifierVO> parentVO;
    private IdentifierManager identifierManager;

    public IdentifierCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<IdentifierVO>());
    }

    public IdentifierCollection(List<IdentifierVO> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<IdentifierVO> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<IdentifierVO> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        identifierManager = new IdentifierManager(parentVO);
    }

    /**
     * localized creation of SelectItems for the identifier types available
     * @return SelectItem[] with Strings representing identifier types
     */
    public SelectItem[] getIdentifierTypes()
    {
        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());

        
        IdType[] typesToDisplay = new IdType[]{
        		IdType.CONE, 
        		IdType.URI, 
        		IdType.ISBN, 
        		IdType.ISSN, 
        		IdType.DOI, 
        		IdType.URN,
        		IdType.PII,
        		IdType.EDOC, 
        		IdType.ESCIDOC, 
        		IdType.ISI, 
        		IdType.PND,
        		IdType.ZDB ,
        		IdType.PMID ,
        		IdType.ARXIV ,
        		IdType.PMC ,
        		IdType.BMC,
        		IdType.BIBTEX_CITEKEY ,
        		IdType.REPORT_NR,
        		IdType.SSRN,
        		IdType.PATENT_NR ,
        		IdType.PATENT_APPLICATION_NR ,
        		IdType.PATENT_PUBLICATION_NR,
        		IdType.OTHER};
        
        ArrayList<SelectItem> selectItemList = new ArrayList<SelectItem>();
        
        // constants for comboBoxes
        selectItemList.add(new SelectItem("", labelBundle.getString("EditItem_NO_ITEM_SET")));
        
        
        
        
        for (IdentifierVO.IdType type : typesToDisplay)
        {
            selectItemList.add(new SelectItem(type.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_" + type.toString())));
        }
        
        // Sort identifiers alphabetically
        Collections.sort(selectItemList, new Comparator<SelectItem>()
        {
            @Override
            public int compare(SelectItem o1, SelectItem o2)
            {
                return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
            }
        });
        
        return selectItemList.toArray(new SelectItem[]{});
    }

    /**
     * Specialized DataModelManager to deal with objects of type IdentifierVO
     * @author Mario Wagner
     */
    public class IdentifierManager extends DataModelManager<IdentifierVO>
    {
        List<IdentifierVO> parentVO;
        
        public IdentifierManager(List<IdentifierVO> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public IdentifierVO createNewObject()
        {
            IdentifierVO newIdentifier = new IdentifierVO();
            return newIdentifier;
        }
        
        public List<IdentifierVO> getDataListFromVO()
        {
            return parentVO;
        }

        public void setParentVO(List<IdentifierVO> parentVO)
        {
            this.parentVO = parentVO;
            setObjectList(parentVO);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }

    public IdentifierManager getIdentifierManager()
    {
        return identifierManager;
    }

    public void setIdentifierManager(IdentifierManager identifierManager)
    {
        this.identifierManager = identifierManager;
    }

}
