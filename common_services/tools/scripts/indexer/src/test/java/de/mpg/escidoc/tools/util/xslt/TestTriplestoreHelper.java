package de.mpg.escidoc.tools.util.xslt;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTriplestoreHelper
{

	@Test
	public void testGetTableName()
	{
		assertTrue(TriplestoreHelper.getInstance().getContextOuRelationName() != null);
		assertTrue(TriplestoreHelper.getInstance().getContextOuRelationName().startsWith("t"));
	}
	
	@Test
	public void testGetOrganizationFor()
	{
		assertTrue(TriplestoreHelper.getInstance().getOrganizationFor("escidoc:1703283") != null);
		String id = TriplestoreHelper.getInstance().getOrganizationFor("escidoc:1703283");
		assertTrue(id.equals("escidoc:1664137"));
	}

}
