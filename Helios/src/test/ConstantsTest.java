package test;


import junit.framework.TestCase;

import org.junit.Test;

import constants.Constants;
import exceptions.InvalidConstantException;

public class ConstantsTest extends TestCase
{

	@Test
	public void testGetInstance()
	{
		assertNotNull(Constants.getInstance());
	}

	@Test
	public void testGetInstanceSingleton()
	{
		assertSame(Constants.getInstance(), Constants.getInstance());
	}

	@Test
	public void testGetSuccess()
	{
		Constants c = Constants.getInstance();
		
		try
		{
			assertEquals("_", c.get("dateSeperator"));
		} 
		catch (InvalidConstantException e)
		{
			fail();
		}
	}
	
	@Test
	public void testGetFailure()
	{
		Constants c = Constants.getInstance();
		
		try
		{
			c.get("");
			fail();
		} 
		catch (InvalidConstantException e)
		{
			assertTrue(true);
		}
	}

}
