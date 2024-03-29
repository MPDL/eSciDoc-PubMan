/*
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
package de.mpg.escidoc.services.search.query;

import java.util.List;
import org.apache.axis.types.NonNegativeInteger;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;


/**
 * @author endres
 *
 */
public class OrgUnitsSearchResult extends SearchResult
{
    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** Result list. */
    private List<AffiliationVO> results = null;

    /**
     * Create a result.
     * 
     * @param results
     *            list of results
     * @param cqlQuery
     *            cql query
     * @param totalNumberOfResults
     *          total number of results
     */
    public OrgUnitsSearchResult(List<AffiliationVO> results, String cqlQuery, 
            NonNegativeInteger totalNumberOfResults)
    {
        super(cqlQuery, totalNumberOfResults);
        this.results = results;
    }
    
    /**
     * Getter for search results.
     * @return search results
     */
    public List<AffiliationVO> getResults()
    {
        return results;
    }
}
