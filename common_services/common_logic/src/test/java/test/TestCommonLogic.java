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

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.metadata.MetadataTest;
import test.referenceobjects.ReferenceObjectTest;
import test.valueobjects.ValueObjectTest;
import test.valueobjects.comparator.ComparatorTest;
import test.xmltransforming.XmlTransformingTest;
import test.xmltransforming.component.TransformPubItemTest;
import test.xmltransforming.component.TransformStatisticReportTest;

/**
 * Component test suite for common_logic.
 *
 * @author Peter Broszeit (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 06.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                    ReferenceObjectTest.class
                    ,ValueObjectTest.class                                        
                    ,ComparatorTest.class
                    ,XmlTransformingTest.class
                    ,MetadataTest.class
                    })
public class TestCommonLogic
{
}
