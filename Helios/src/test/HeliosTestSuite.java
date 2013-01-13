/**
 * 
 */
package test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author jdiamond
 *
 */
public class HeliosTestSuite extends TestSuite
{

	/**
	 * 
	 */
	public HeliosTestSuite()
	{
		super();
	}

	/**
	 * @param arg0
	 */
	public HeliosTestSuite(Class<? extends TestCase> arg0)
	{
		super(arg0);
	}

	/**
	 * @param name
	 */
	public HeliosTestSuite(String name)
	{
		super(name);
	}

	/**
	 * @param arg0
	 */
	public HeliosTestSuite(Class<?>... arg0)
	{
		super(arg0);
	}

	/**
	 * @param theClass
	 * @param name
	 */
	public HeliosTestSuite(Class<? extends TestCase> theClass, String name)
	{
		super(theClass, name);
	}

	/**
	 * @param classes
	 * @param name
	 */
	public HeliosTestSuite(Class<? extends TestCase>[] classes, String name)
	{
		super(classes, name);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		HeliosTestSuite suite = new HeliosTestSuite();
		
		//ConstantsTest c = new ConstantsTest()
		
		suite.addTestSuite(ConstantsTest.class);
		suite.addTestSuite(DateParserTest.class);
//		
//		List<Test> testCases = Collections.list(suite.tests());
//		
//		for(Test test : testCases)
//		{
//			TestResult testResult = new TestResult();
//			test.run(testResult);
//			
//			test.
//		}
		
		TestRunner.run(suite);
		
	}

}
