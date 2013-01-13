/**
 * 
 */
package test.statistics;

import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;

import statistics.Statistics;
import statistics.StatisticsFactory;

/**
 * @author jdiamond
 *
 */
public class BasicStatisticsTest extends TestCase
{

	private Statistics stats;

	protected void setUp()
	{
		stats = StatisticsFactory.getStatsInstance();
		
		if(stats == null)
		{
			assertTrue("Failure building statistics object", false);
		}
	}

	@Test
	public void testSum()
	{
			Vector<String> actual = new Vector<String>();

			actual.add("-5");
			actual.add("-4");
			actual.add("-3");
			actual.add( "-2");
			actual.add("-1");
			actual.add("0");
			actual.add("1");
			actual.add("2");
			actual.add("3");
			actual.add("4");
			actual.add("5");
			actual.add("6");

			assertEquals("Sum test", stats.getTotal(actual), 6.0);
	}
	
	@Test
	public void testAverage()
	{
			Vector<String> actual = new Vector<String>();

			actual.add("-5");
			actual.add("-4");
			actual.add("-3");
			actual.add( "-2");
			actual.add("-1");
			actual.add("0");
			actual.add("1");
			actual.add("2");
			actual.add("3");
			actual.add("4");
			actual.add("5");
			actual.add("6");

			assertEquals("Average test", stats.getAverage(actual), .5);
	}
}
