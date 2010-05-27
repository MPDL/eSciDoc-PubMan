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

package de.mpg.escidoc.pubman.multipleimport;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.SortColumn;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.SortDirection;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;

/**
 * JSF bean class (request) to hold data for the import workspace.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportWorkspace extends FacesBean
{
    
    private ImportLog.SortColumn sortColumn = SortColumn.STARTDATE;
    private ImportLog.SortDirection sortDirection = SortDirection.DESCENDING;
    
    private List<ImportLog> imports = null;
    
    private UIXIterator importIterator = new UIXIterator();

    /**
     * Default constructor.
     * 
     * Retrieves the sorting information from the request parameters.
     */
    public ImportWorkspace()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        AccountUserVO user = loginHelper.getAccountUser();
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        ImportLog.SortColumn currentColumn = null;
        ImportLog.SortDirection currentDirection = null;
        ImportLog.SortColumn newColumn = null;
        
        String sortColumnString = facesContext.getExternalContext().getRequestParameterMap().get("sortColumn");
        if (sortColumnString != null && !"".equals(sortColumnString))
        {
            newColumn = SortColumn.valueOf(sortColumnString);
        }
        
        String currentColumnString = facesContext.getExternalContext().getRequestParameterMap().get("currentColumn");
        if (currentColumnString != null && !"".equals(currentColumnString))
        {
            currentColumn = SortColumn.valueOf(currentColumnString);
        }
        
        String currentDirectionString
            = facesContext.getExternalContext().getRequestParameterMap().get("currentDirection");
        if (currentDirectionString != null && !"".equals(currentDirectionString))
        {
            currentDirection = SortDirection.valueOf(currentDirectionString);
        }
        
        if (newColumn != null && newColumn.equals(currentColumn))
        {
            this.sortColumn = newColumn;
            if (currentDirection == SortDirection.ASCENDING)
            {
                this.sortDirection = SortDirection.DESCENDING;
            }
            else
            {
                this.sortDirection = SortDirection.ASCENDING;
            }
        }
        else if (newColumn != null)
        {
            this.sortColumn = newColumn;
            this.sortDirection = SortDirection.ASCENDING;
        }
        
        if (user != null)
        {
            imports = ImportLog.getImportLogs("import", user, this.sortColumn, this.sortDirection, true, false);
        }
    }

    /**
     * @return the imports
     */
    public List<ImportLog> getImports()
    {
        return imports;
    }

    /**
     * @param imports the imports to set
     */
    public void setImports(List<ImportLog> imports)
    {
        this.imports = imports;
    }

    /**
     * @return the sortColumn
     */
    public ImportLog.SortColumn getSortColumn()
    {
        return sortColumn;
    }

    /**
     * @param sortColumn the sortColumn to set
     */
    public void setSortColumn(ImportLog.SortColumn sortColumn)
    {
        this.sortColumn = sortColumn;
    }

    /**
     * @return the sortDirection
     */
    public ImportLog.SortDirection getSortDirection()
    {
        return sortDirection;
    }

    /**
     * @param sortDirection the sortDirection to set
     */
    public void setSortDirection(ImportLog.SortDirection sortDirection)
    {
        this.sortDirection = sortDirection;
    }
    
    
    public UIXIterator getImportIterator()
    {
        return importIterator;
    }

    public void setImportIterator(UIXIterator importIterator)
    {
        this.importIterator = importIterator;
    }
    
    public String getFormatLabel()
    {
        String label = "n/a";
        ImportLog currentImport = null;
        
        if (importIterator.getRowIndex() != -1)
        {
            int index = this.importIterator.getRowIndex();
            currentImport = (ImportLog) this.importIterator.getRowData(index);
        }
        
        if (currentImport != null)
        {
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.ENDNOTE_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_ENDNOTE");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.ENDNOTE_ICE_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_ENDNOTEICE");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.BIBTEX_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_BIBTEX");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.EDOC_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_EDOC");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.EDOC_FORMAT_AEI.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_EDOCAEI");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.RIS_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_RIS");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.WOS_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_WOS");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.MAB_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_MAB");}
            if (currentImport.getFormat().equalsIgnoreCase(MultipleImport.ESCIDOC_FORMAT.getName())) {label=getLabel("ENUM_IMPORT_FORMAT_ESCIDOC");}
        }
        return label;
    }
    
}
