/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.LMICSAT;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class LMICSATTest extends ReportOutputTest
{
	private LMICSAT report;

	public void setUp()
	{
		try 
		{
			report = new LMICSAT();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build IVR CSAT report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(report != null)
		{
			report.close();
		}
	}

	@Test
	public void testTeamTimeReport()
	{

		report.setParameter(Report.REPORT_TYPE_PARAM, LMICSAT.TEAM_TIME_REPORT);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();


		expected.add(new String[]{"2011-10-14", "49.0", "52.0", "94.23"});
		expected.add(new String[]{"2011-10-15", "33.0", "38.0", "86.84"});
		expected.add(new String[]{"2011-10-12", "26.0", "31.0", "83.87"});
		expected.add(new String[]{"2011-10-13", "59.0", "68.0", "86.76"});
		expected.add(new String[]{"2011-10-18", "43.0", "50.0", "86"});
		expected.add(new String[]{"2011-10-19", "58.0", "63.0", "92.06"});
		expected.add(new String[]{"2011-10-16", "33.0", "35.0", "94.29"});
		expected.add(new String[]{"2011-10-17", "43.0", "52.0", "82.69"});
		expected.add(new String[]{"2011-10-22", "42.0", "46.0", "91.3"});
		expected.add(new String[]{"2011-10-21", "39.0", "42.0", "92.86"});
		expected.add(new String[]{"2011-10-20", "52.0", "59.0", "88.14"});
		expected.add(new String[]{"2011-10-01", "24.0", "26.0", "92.31"});
		expected.add(new String[]{"2011-10-02", "23.0", "25.0", "92"});
		expected.add(new String[]{"2011-10-30", "33.0", "36.0", "91.67"});
		expected.add(new String[]{"2011-10-03", "29.0", "30.0", "96.67"});
		expected.add(new String[]{"2011-10-31", "43.0", "52.0", "82.69"});
		expected.add(new String[]{"2011-10-04", "21.0", "22.0", "95.45"});
		expected.add(new String[]{"2011-10-05", "32.0", "36.0", "88.89"});
		expected.add(new String[]{"2011-10-06", "36.0", "37.0", "97.3"});
		expected.add(new String[]{"2011-10-07", "31.0", "33.0", "93.94"});
		expected.add(new String[]{"2011-10-08", "12.0", "12.0", "100"});
		expected.add(new String[]{"2011-10-09", "15.0", "18.0", "83.33"});
		expected.add(new String[]{"2011-10-29", "23.0", "27.0", "85.19"});
		expected.add(new String[]{"2011-10-28", "51.0", "51.0", "100"});
		expected.add(new String[]{"2011-10-27", "39.0", "44.0", "88.64"});
		expected.add(new String[]{"2011-10-26", "28.0", "35.0", "80"});
		expected.add(new String[]{"2011-10-25", "53.0", "56.0", "94.64"});
		expected.add(new String[]{"2011-10-11", "24.0", "28.0", "85.71"});
		expected.add(new String[]{"2011-10-24", "51.0", "53.0", "96.23"});
		expected.add(new String[]{"2011-10-10", "32.0", "34.0", "94.12"});
		expected.add(new String[]{"2011-10-23", "43.0", "47.0", "91.49"});





		testOutput(expected, report.startReport());
	}

}
