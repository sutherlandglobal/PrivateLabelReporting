/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.NoSaleDrivers;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class NoSaleDriversTest extends ReportOutputTest
{
	private NoSaleDrivers noSaleDriversReport;

	public void setUp()
	{
		try 
		{
			noSaleDriversReport = new NoSaleDrivers();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build No Sale Drivers report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(noSaleDriversReport != null)
		{
			noSaleDriversReport.close();
		}
	}

	@Test
	public void testTeamTimeReport()
	{
		noSaleDriversReport.setParameter(Report.REPORT_TYPE_PARAM, "" + NoSaleDrivers.TEAM_TIME_REPORT);
		noSaleDriversReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
		noSaleDriversReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

		noSaleDriversReport.setParameter(Report.START_DATE_PARAM, "2010-10-01 00:00:00");
		noSaleDriversReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");
		noSaleDriversReport.setParameter(Report.NUM_DRIVERS_PARAM, 4);

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10", "Already has support plan", "747"});
		expected.add(new String[]{"2011-10", "Other", "7"});
		expected.add(new String[]{"2011-10", "Cannot pay/no support CC", "4"});
		expected.add(new String[]{"2011-10", "Needs RCD", "1"});
		expected.add(new String[]{"2011-09", "Already has support plan", "805"});
		expected.add(new String[]{"2011-09", "Other", "9"});
		expected.add(new String[]{"2011-09", "Dropped Call", "2"});
		expected.add(new String[]{"2011-09", "Cannot pay/no support CC", "2"});
		expected.add(new String[]{"2011-08", "Already has support plan", "1166"});
		expected.add(new String[]{"2011-08", "Other", "19"});
		expected.add(new String[]{"2011-08", "Cannot pay/no support CC", "8"});
		expected.add(new String[]{"2011-08", "Hardware issue", "3"});
		expected.add(new String[]{"2011-07", "Already has support plan", "978"});
		expected.add(new String[]{"2011-07", "Other", "45"});
		expected.add(new String[]{"2011-07", "Cannot pay/no support CC", "14"});
		expected.add(new String[]{"2011-07", "Wants Refund", "6"});
		expected.add(new String[]{"2011-06", "Already has support plan", "14"});
		expected.add(new String[]{"2011-06", "Other", "3"});
		expected.add(new String[]{"2011-06", "Costs too much", "2"});


		testOutput(expected, noSaleDriversReport.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		noSaleDriversReport.setParameter(Report.REPORT_TYPE_PARAM, "" + NoSaleDrivers.AGENT_TIME_REPORT);
		noSaleDriversReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
		noSaleDriversReport.setParameter(Report.AGENT_NAME_PARAM, "Zioto, Andrew");
		
		noSaleDriversReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

		noSaleDriversReport.setParameter(Report.START_DATE_PARAM, "2010-10-01 00:00:00");
		noSaleDriversReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");
		noSaleDriversReport.setParameter(Report.NUM_DRIVERS_PARAM, 4);

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10", "Already has support plan", "5"});
		expected.add(new String[]{"2011-09", "Already has support plan", "11"});
		expected.add(new String[]{"2011-08", "Already has support plan", "4"});
		expected.add(new String[]{"2011-07", "Already has support plan", "8"});
		expected.add(new String[]{"2011-07", "Other", "1"});


		testOutput(expected, noSaleDriversReport.startReport());
	}
}
