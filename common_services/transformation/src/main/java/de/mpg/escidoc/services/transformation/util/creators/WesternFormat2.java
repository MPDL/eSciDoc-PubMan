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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.transformation.util.creators;

import java.util.List;

public class WesternFormat2 extends AuthorFormat
{

    @Override
    public String getPattern()
    {
        return "^(" + INITIALS + "[ -]*)+ ?" + NAME + "( *(,| and | AND | und | et |\\n ) *(" + INITIALS + "[ -]?)+ ?" + NAME + ")*\\s*$";
    }

    @Override
    public List<Author> getAuthors(String authorsString)
    {

        if (authorsString.contains(";") || contains(authorsString, "0123456789"))
        {
            return null;
        }
        else
        {
            String[] potentialAuthors = split(authorsString, ',');
            for (String potentialAuthor : potentialAuthors)
            {
                if (!contains(potentialAuthor, " "))
                {
                    return null;
                }
            }
        }
        
        String[] authors = authorsString.split(" *(,| and | AND | und | et | \\n) *");

        return getAuthorListWithInitials(authors);
    }

    @Override
    public int getSignificance()
    {
        return 10;
    }

    @Override
    public String getDescription()
    {
        return "I. Nachname[, I. I. Nachname]";
    }

    @Override
    public String getName()
    {
        return "Westliches Normalformat mit Initialen, komma-getrennt";
    }

    @Override
    public String getWarning()
    {
        // TODO Auto-generated method stub
        return null;
    }

    
    
}
