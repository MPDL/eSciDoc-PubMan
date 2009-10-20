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
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.test.gui.modules;

/**
 * @author endres
 *
 */
public class PubmanTypesDefinitions
{
    public enum LoginType {
        DepositorModeratorSimpleWF,
        ModeratorSimpleWF,
        DepositorSimpleWF,
        DepositorModeratorStandardWF,
        ModeratorStandardWF,
        DepositorStandardWF,
        DepositorModeratorSimpleStandardWF,
        ModeratorSimpleStandardWF,
        DepositorSimpleStandardWF,
    }
    
    public enum GenreType {
        ARTICLE, 
        BOOK, 
        BOOK_ITEM, 
        PROCEEDINGS, 
        CONFERENCE_PAPER, 
        TALK_AT_EVENT, 
        CONFERENCE_REPORT, 
        POSTER, 
        COURSEWARE_LECTURE, 
        THESIS, 
        PAPER, 
        REPORT, 
        ISSUE, 
        JOURNAL, 
        MANUSCRIPT, 
        SERIES, 
        OTHER
    }
    
    public enum LanguageSelection {
        English,
        German,
        Japanese
    }
    
    public enum FileContentCategory
    {
        ANY_FULLTEXT, PRE_PRINT, POST_PRINT, PUBLISHER_VERSION, ABSTRACT, TABLE_OF_CONTENTS,
        SUPPLEMENTARY_MATERIAL, CORRESPONDENCE, COPYRIGHT_TRANSFER_AGREEMENT
    }
    public enum ComponentVisibility {
        PUBLIC,
        PRIVATE,
        /** aka restricted */
        AUDIENCE
    }
}
