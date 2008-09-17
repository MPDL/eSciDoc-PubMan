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

package de.mpg.escidoc.services.search.query;

import java.io.IOException;
import java.util.ArrayList;

import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLNotNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.ModifierSet;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.parser.ParseException;

/**
 * @author endres
 *
 */
public class MetadataSearchQuery extends SearchQuery implements StandardSearchQuery  {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<MetadataSearchCriterion> searchCriteria = null;
	private ArrayList<MetadataSearchCriterion> contentTypes = null;
		
	public MetadataSearchQuery( ArrayList<String> contentTypes ) throws TechnicalException {
		this.contentTypes = new ArrayList<MetadataSearchCriterion>();
		addContentTypeCriterions( contentTypes );
		this.searchCriteria = new ArrayList<MetadataSearchCriterion>();
	}
	
	public MetadataSearchQuery( ArrayList<String> contentTypes, ArrayList<MetadataSearchCriterion> criteria ) throws TechnicalException {
		this.contentTypes = new ArrayList<MetadataSearchCriterion>();
		addContentTypeCriterions( contentTypes );
		this.searchCriteria = criteria;
	}
	
	public void addCriterion( MetadataSearchCriterion criterion ) {
		this.searchCriteria.add( criterion );
	}
	
	public CQLNode getCqlNode() throws CQLParseException, IOException, ParseException, TechnicalException {
		
		CQLNode node = null;
		if( searchCriteria.size() != 0 ) {
			node = searchCriteria.get( 0 ).generateCqlTree();
		}
		// first add the nodes from the list
		for( int i = 1; i < searchCriteria.size(); i++ ) {
			node = generateNodeWithCriterion( node, searchCriteria.get( i ) );
		}
		// then add the content type nodes
		for( int i = 0; i < contentTypes.size(); i++ ) {
			node = generateNodeWithCriterion( node, contentTypes.get( i ) );
		}
		return node;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getCqlQuery() throws CQLParseException, IOException, ParseException, TechnicalException {
		return getCqlNode().toCQL();
	}
	
	private CQLNode generateNodeWithCriterion( CQLNode node, MetadataSearchCriterion criterion ) throws CQLParseException, IOException, ParseException, TechnicalException {
		CQLNode newRoot = null;
		switch( criterion.getLogicalOperator() ) {
			case AND:
				newRoot = new CQLAndNode( node, criterion.generateCqlTree(), 
						new ModifierSet( criterion.getLogicalOperatorAsString() ) );
				break;
			case OR:
				newRoot = new CQLOrNode( node, criterion.generateCqlTree(), 
						new ModifierSet( criterion.getLogicalOperatorAsString() ) );
				break;
			case NOT:
				newRoot = new CQLNotNode( node, criterion.generateCqlTree(), 
						new ModifierSet( criterion.getLogicalOperatorAsString() ) );
				break;
			default:
				throw new TechnicalException("No logical operator specified. Cannot bind to node.");
		}
		return newRoot;
	}
	
	private void addContentTypeCriterions( ArrayList<String> contentTypes ) throws TechnicalException {
		for( int i = 0; i < contentTypes.size(); i++ ) {
		this.contentTypes.add( new MetadataSearchCriterion( MetadataSearchCriterion.CriterionType.CONTENT_TYPE,
				contentTypes.get( i ), MetadataSearchCriterion.LogicalOperator.AND ));
		}
	}
	
}
