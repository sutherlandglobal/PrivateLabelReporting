/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.Roster;
import report.SQL.TopRefundDrivers;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class TopRefundReasonsTest extends ReportOutputTest
{
	private TopRefundDrivers topRefundReasonsReport;

	public void setUp()
	{
		try 
		{
			topRefundReasonsReport = new TopRefundDrivers();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build Top Refund Reasons report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(topRefundReasonsReport != null)
		{
			topRefundReasonsReport.close();
		}
	}

	@Test
	public void testTeamTimeReport()
	{
		topRefundReasonsReport.setParameter(Report.REPORT_TYPE_PARAM, "" + TopRefundDrivers.TEAM_TIME_REPORT);
		topRefundReasonsReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
		topRefundReasonsReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

		topRefundReasonsReport.setParameter(Report.START_DATE_PARAM, "2010-10-01 00:00:00");
		topRefundReasonsReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");
		topRefundReasonsReport.setParameter(Report.NUM_DRIVERS_PARAM, 10);

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-09", "No solution exist", "37"});
		expected.add(new String[]{"2011-09", "Customer not happy with service", "34"});
		expected.add(new String[]{"2011-09", "Inappropriate/incorrect expectations set", "21"});
		expected.add(new String[]{"2011-09", "Unused pin", "14"});
		expected.add(new String[]{"2011-09", "Out of Scope(Password issue)", "5"});
		expected.add(new String[]{"2011-09", "Billing issues", "4"});
		expected.add(new String[]{"2011-09", "Out of Scope(Screen damage)", "4"});
		expected.add(new String[]{"2011-09", "Out of Scope(Illegal software)", "3"});
		expected.add(new String[]{"2011-09", "Only wanted recovery media", "3"});
		expected.add(new String[]{"2011-09", "Expectations not set clearly", "2"});
		expected.add(new String[]{"2011-08", "Customer not happy with service", "69"});
		expected.add(new String[]{"2011-08", "Expectations not set clearly", "40"});
		expected.add(new String[]{"2011-08", "Unused pin", "28"});
		expected.add(new String[]{"2011-08", "Inappropriate/incorrect expectations set", "19"});
		expected.add(new String[]{"2011-08", "No solution exist", "11"});
		expected.add(new String[]{"2011-08", "Others", "5"});
		expected.add(new String[]{"2011-08", "Billing issues", "3"});
		expected.add(new String[]{"2011-08", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-08", "Incorrect troubleshooting done", "3"});
		expected.add(new String[]{"2011-08", "Dissatisfied with prior experience", "2"});
		expected.add(new String[]{"2011-07", "Expectations not set clearly", "47"});
		expected.add(new String[]{"2011-07", "Customer not happy with service", "34"});
		expected.add(new String[]{"2011-07", "Unused pin", "15"});
		expected.add(new String[]{"2011-07", "No solution exist", "13"});
		expected.add(new String[]{"2011-07", "Inappropriate/incorrect expectations set", "10"});
		expected.add(new String[]{"2011-07", "Out of Scope(Password issue)", "4"});
		expected.add(new String[]{"2011-07", "Incorrect troubleshooting done", "2"});
		expected.add(new String[]{"2011-07", "Others", "2"});
		expected.add(new String[]{"2011-07", "Billing issues", "1"});
		expected.add(new String[]{"2011-07", "Opting for different product", "1"});
		expected.add(new String[]{"2010-11", "Billing issues", "70"});
		expected.add(new String[]{"2010-11", "Expectations not set clearly", "26"});
		expected.add(new String[]{"2010-11", "Dissatisfied with prior experience", "21"});
		expected.add(new String[]{"2010-11", "Issue not in scope", "20"});
		expected.add(new String[]{"2010-11", "Others", "8"});
		expected.add(new String[]{"2010-11", "No solution exist", "5"});
		expected.add(new String[]{"2010-11", "Will try competitor", "2"});
		expected.add(new String[]{"2010-11", "Wrong commitment given", "2"});
		expected.add(new String[]{"2010-11", "Financial difficulty", "1"});
		expected.add(new String[]{"2010-11", "Opting for different product", "1"});
		expected.add(new String[]{"2011-06", "Customer not happy with service", "40"});
		expected.add(new String[]{"2011-06", "Expectations not set clearly", "34"});
		expected.add(new String[]{"2011-06", "No solution exist", "11"});
		expected.add(new String[]{"2011-06", "Unused pin", "9"});
		expected.add(new String[]{"2011-06", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-06", "Duplicate pin sold", "3"});
		expected.add(new String[]{"2011-06", "Billing issues", "2"});
		expected.add(new String[]{"2011-06", "Inappropriate/incorrect expectations set", "2"});
		expected.add(new String[]{"2011-06", "Wrong commitment given", "2"});
		expected.add(new String[]{"2011-06", "Dissatisfied with prior experience", "1"});
		expected.add(new String[]{"2010-12", "Billing issues", "76"});
		expected.add(new String[]{"2010-12", "No solution exist", "25"});
		expected.add(new String[]{"2010-12", "Dissatisfied with prior experience", "24"});
		expected.add(new String[]{"2010-12", "Expectations not set clearly", "20"});
		expected.add(new String[]{"2010-12", "Issue not in scope", "11"});
		expected.add(new String[]{"2010-12", "Others", "2"});
		expected.add(new String[]{"2010-12", "Wrong commitment given", "2"});
		expected.add(new String[]{"2010-12", "Not happy as diagnosis took long", "1"});
		expected.add(new String[]{"2010-12", "No follow-up", "1"});
		expected.add(new String[]{"2010-12", "Will contact local technician", "1"});
		expected.add(new String[]{"2011-05", "Unused pin", "30"});
		expected.add(new String[]{"2011-05", "Customer not happy with service", "25"});
		expected.add(new String[]{"2011-05", "Expectations not set clearly", "19"});
		expected.add(new String[]{"2011-05", "No solution exist", "12"});
		expected.add(new String[]{"2011-05", "Out of Scope(Password issue)", "4"});
		expected.add(new String[]{"2011-05", "Billing issues", "2"});
		expected.add(new String[]{"2011-05", "Only wanted recovery media", "2"});
		expected.add(new String[]{"2011-05", "Opting for different product", "1"});
		expected.add(new String[]{"2011-05", "Will try myself", "1"});
		expected.add(new String[]{"2011-05", "Not happy with support boundaries", "1"});
		expected.add(new String[]{"2011-04", "Unused pin", "15"});
		expected.add(new String[]{"2011-04", "Expectations not set clearly", "14"});
		expected.add(new String[]{"2011-04", "Billing issues", "7"});
		expected.add(new String[]{"2011-04", "Customer not happy with service", "6"});
		expected.add(new String[]{"2011-04", "No solution exist", "4"});
		expected.add(new String[]{"2011-04", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-04", "Will contact local technician", "3"});
		expected.add(new String[]{"2011-04", "Issue not in scope", "2"});
		expected.add(new String[]{"2011-04", "Incorrect troubleshooting done", "1"});
		expected.add(new String[]{"2011-04", "Inappropriate/incorrect expectations set", "1"});
		expected.add(new String[]{"2011-03", "Expectations not set clearly", "40"});
		expected.add(new String[]{"2011-03", "Customer not happy with service", "33"});
		expected.add(new String[]{"2011-03", "Unused pin", "27"});
		expected.add(new String[]{"2011-03", "Billing issues", "20"});
		expected.add(new String[]{"2011-03", "No solution exist", "19"});
		expected.add(new String[]{"2011-03", "Others", "11"});
		expected.add(new String[]{"2011-03", "Inappropriate/incorrect expectations set", "10"});
		expected.add(new String[]{"2011-03", "Dissatisfied with prior experience", "7"});
		expected.add(new String[]{"2011-03", "Out of Scope(Password issue)", "7"});
		expected.add(new String[]{"2011-03", "Issue not in scope", "4"});
		expected.add(new String[]{"2011-02", "Customer not happy with service", "58"});
		expected.add(new String[]{"2011-02", "Billing issues", "34"});
		expected.add(new String[]{"2011-02", "Expectations not set clearly", "25"});
		expected.add(new String[]{"2011-02", "No solution exist", "24"});
		expected.add(new String[]{"2011-02", "Unused pin", "13"});
		expected.add(new String[]{"2011-02", "Issue not in scope", "10"});
		expected.add(new String[]{"2011-02", "Incorrect troubleshooting done", "9"});
		expected.add(new String[]{"2011-02", "Dissatisfied with prior experience", "7"});
		expected.add(new String[]{"2011-02", "Others", "5"});
		expected.add(new String[]{"2011-02", "Will contact local technician", "4"});
		expected.add(new String[]{"2011-01", "Billing issues", "34"});
		expected.add(new String[]{"2011-01", "Customer not happy with service", "33"});
		expected.add(new String[]{"2011-01", "Dissatisfied with prior experience", "25"});
		expected.add(new String[]{"2011-01", "Expectations not set clearly", "19"});
		expected.add(new String[]{"2011-01", "Unused pin", "17"});
		expected.add(new String[]{"2011-01", "No solution exist", "17"});
		expected.add(new String[]{"2011-01", "Others", "8"});
		expected.add(new String[]{"2011-01", "Will contact local technician", "7"});
		expected.add(new String[]{"2011-01", "Incorrect troubleshooting done", "5"});
		expected.add(new String[]{"2011-01", "Out of Scope(Password issue)", "2"});
		expected.add(new String[]{"2011-10", "Customer not happy with service", "34"});
		expected.add(new String[]{"2011-10", "No solution exist", "17"});
		expected.add(new String[]{"2011-10", "Unused pin", "16"});
		expected.add(new String[]{"2011-10", "Inappropriate/incorrect expectations set", "8"});
		expected.add(new String[]{"2011-10", "Billing issues", "7"});
		expected.add(new String[]{"2011-10", "Only wanted recovery media", "6"});
		expected.add(new String[]{"2011-10", "Others", "5"});
		expected.add(new String[]{"2011-10", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-10", "Out of Scope(Screen damage)", "2"});
		expected.add(new String[]{"2011-10", "Incorrect troubleshooting done", "2"});
		expected.add(new String[]{"2010-10", "Billing issues", "85"});
		expected.add(new String[]{"2010-10", "Expectations not set clearly", "24"});
		expected.add(new String[]{"2010-10", "Dissatisfied with prior experience", "18"});
		expected.add(new String[]{"2010-10", "Issue not in scope", "17"});
		expected.add(new String[]{"2010-10", "No solution exist", "8"});
		expected.add(new String[]{"2010-10", "Will contact local technician", "4"});
		expected.add(new String[]{"2010-10", "Others", "4"});
		expected.add(new String[]{"2010-10", "Opting for different product", "2"});
		expected.add(new String[]{"2010-10", "No follow-up", "1"});
		expected.add(new String[]{"2010-10", "Will try competitor", "1"});


		testOutput(expected, topRefundReasonsReport.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		topRefundReasonsReport.setParameter(Report.REPORT_TYPE_PARAM, "" + TopRefundDrivers.AGENT_TIME_REPORT);
		topRefundReasonsReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
		topRefundReasonsReport.setParameter(Report.AGENT_NAME_PARAM, "Bray, George");

		topRefundReasonsReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

		topRefundReasonsReport.setParameter(Report.START_DATE_PARAM, "2010-10-01 00:00:00");
		topRefundReasonsReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");
		topRefundReasonsReport.setParameter(Report.NUM_DRIVERS_PARAM, 10);

		Vector<String[]> expected = new Vector<String[]>();


		expected.add(new String[]{"2011-09", "No solution exist", "37"});
		expected.add(new String[]{"2011-09", "Customer not happy with service", "34"});
		expected.add(new String[]{"2011-09", "Inappropriate/incorrect expectations set", "21"});
		expected.add(new String[]{"2011-09", "Unused pin", "14"});
		expected.add(new String[]{"2011-09", "Out of Scope(Password issue)", "5"});
		expected.add(new String[]{"2011-09", "Billing issues", "4"});
		expected.add(new String[]{"2011-09", "Out of Scope(Screen damage)", "4"});
		expected.add(new String[]{"2011-09", "Out of Scope(Illegal software)", "3"});
		expected.add(new String[]{"2011-09", "Only wanted recovery media", "3"});
		expected.add(new String[]{"2011-09", "Expectations not set clearly", "2"});
		expected.add(new String[]{"2011-08", "Customer not happy with service", "69"});
		expected.add(new String[]{"2011-08", "Expectations not set clearly", "40"});
		expected.add(new String[]{"2011-08", "Unused pin", "28"});
		expected.add(new String[]{"2011-08", "Inappropriate/incorrect expectations set", "19"});
		expected.add(new String[]{"2011-08", "No solution exist", "11"});
		expected.add(new String[]{"2011-08", "Others", "4"});
		expected.add(new String[]{"2011-08", "Billing issues", "3"});
		expected.add(new String[]{"2011-08", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-08", "Incorrect troubleshooting done", "3"});
		expected.add(new String[]{"2011-08", "Dissatisfied with prior experience", "2"});
		expected.add(new String[]{"2011-07", "Expectations not set clearly", "47"});
		expected.add(new String[]{"2011-07", "Customer not happy with service", "34"});
		expected.add(new String[]{"2011-07", "Unused pin", "15"});
		expected.add(new String[]{"2011-07", "No solution exist", "13"});
		expected.add(new String[]{"2011-07", "Inappropriate/incorrect expectations set", "10"});
		expected.add(new String[]{"2011-07", "Out of Scope(Password issue)", "4"});
		expected.add(new String[]{"2011-07", "Others", "2"});
		expected.add(new String[]{"2011-07", "Billing issues", "1"});
		expected.add(new String[]{"2011-07", "Opting for different product", "1"});
		expected.add(new String[]{"2011-07", "Out of Scope(Server issue)", "1"});
		expected.add(new String[]{"2010-11", "Billing issues", "70"});
		expected.add(new String[]{"2010-11", "Expectations not set clearly", "26"});
		expected.add(new String[]{"2010-11", "Issue not in scope", "20"});
		expected.add(new String[]{"2010-11", "Dissatisfied with prior experience", "18"});
		expected.add(new String[]{"2010-11", "No solution exist", "5"});
		expected.add(new String[]{"2010-11", "Others", "2"});
		expected.add(new String[]{"2010-11", "Wrong commitment given", "2"});
		expected.add(new String[]{"2010-11", "Will try competitor", "2"});
		expected.add(new String[]{"2010-11", "Financial difficulty", "1"});
		expected.add(new String[]{"2010-11", "Opting for different product", "1"});
		expected.add(new String[]{"2011-06", "Customer not happy with service", "40"});
		expected.add(new String[]{"2011-06", "Expectations not set clearly", "33"});
		expected.add(new String[]{"2011-06", "No solution exist", "11"});
		expected.add(new String[]{"2011-06", "Unused pin", "9"});
		expected.add(new String[]{"2011-06", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-06", "Duplicate pin sold", "3"});
		expected.add(new String[]{"2011-06", "Billing issues", "2"});
		expected.add(new String[]{"2011-06", "Inappropriate/incorrect expectations set", "2"});
		expected.add(new String[]{"2011-06", "Wrong commitment given", "2"});
		expected.add(new String[]{"2011-06", "Dissatisfied with prior experience", "1"});
		expected.add(new String[]{"2010-12", "Billing issues", "76"});
		expected.add(new String[]{"2010-12", "No solution exist", "25"});
		expected.add(new String[]{"2010-12", "Expectations not set clearly", "20"});
		expected.add(new String[]{"2010-12", "Dissatisfied with prior experience", "14"});
		expected.add(new String[]{"2010-12", "Issue not in scope", "10"});
		expected.add(new String[]{"2010-12", "Wrong commitment given", "2"});
		expected.add(new String[]{"2010-12", "No follow-up", "1"});
		expected.add(new String[]{"2010-12", "Will contact local technician", "1"});
		expected.add(new String[]{"2011-05", "Unused pin", "30"});
		expected.add(new String[]{"2011-05", "Customer not happy with service", "25"});
		expected.add(new String[]{"2011-05", "Expectations not set clearly", "19"});
		expected.add(new String[]{"2011-05", "No solution exist", "12"});
		expected.add(new String[]{"2011-05", "Out of Scope(Password issue)", "4"});
		expected.add(new String[]{"2011-05", "Billing issues", "2"});
		expected.add(new String[]{"2011-05", "Only wanted recovery media", "2"});
		expected.add(new String[]{"2011-05", "Opting for different product", "1"});
		expected.add(new String[]{"2011-05", "Will try myself", "1"});
		expected.add(new String[]{"2011-05", "Not happy with support boundaries", "1"});
		expected.add(new String[]{"2011-04", "Unused pin", "15"});
		expected.add(new String[]{"2011-04", "Expectations not set clearly", "14"});
		expected.add(new String[]{"2011-04", "Billing issues", "7"});
		expected.add(new String[]{"2011-04", "Customer not happy with service", "6"});
		expected.add(new String[]{"2011-04", "No solution exist", "4"});
		expected.add(new String[]{"2011-04", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-04", "Will contact local technician", "3"});
		expected.add(new String[]{"2011-04", "Issue not in scope", "2"});
		expected.add(new String[]{"2011-04", "Incorrect troubleshooting done", "1"});
		expected.add(new String[]{"2011-04", "Inappropriate/incorrect expectations set", "1"});
		expected.add(new String[]{"2011-03", "Expectations not set clearly", "40"});
		expected.add(new String[]{"2011-03", "Customer not happy with service", "31"});
		expected.add(new String[]{"2011-03", "Unused pin", "27"});
		expected.add(new String[]{"2011-03", "Billing issues", "20"});
		expected.add(new String[]{"2011-03", "No solution exist", "19"});
		expected.add(new String[]{"2011-03", "Inappropriate/incorrect expectations set", "9"});
		expected.add(new String[]{"2011-03", "Others", "8"});
		expected.add(new String[]{"2011-03", "Out of Scope(Password issue)", "7"});
		expected.add(new String[]{"2011-03", "Dissatisfied with prior experience", "5"});
		expected.add(new String[]{"2011-03", "Issue not in scope", "4"});
		expected.add(new String[]{"2011-02", "Customer not happy with service", "51"});
		expected.add(new String[]{"2011-02", "Billing issues", "34"});
		expected.add(new String[]{"2011-02", "No solution exist", "24"});
		expected.add(new String[]{"2011-02", "Expectations not set clearly", "22"});
		expected.add(new String[]{"2011-02", "Unused pin", "13"});
		expected.add(new String[]{"2011-02", "Issue not in scope", "10"});
		expected.add(new String[]{"2011-02", "Dissatisfied with prior experience", "6"});
		expected.add(new String[]{"2011-02", "Incorrect troubleshooting done", "6"});
		expected.add(new String[]{"2011-02", "Will contact local technician", "4"});
		expected.add(new String[]{"2011-02", "Others", "3"});
		expected.add(new String[]{"2011-01", "Billing issues", "34"});
		expected.add(new String[]{"2011-01", "Customer not happy with service", "31"});
		expected.add(new String[]{"2011-01", "Expectations not set clearly", "19"});
		expected.add(new String[]{"2011-01", "Dissatisfied with prior experience", "18"});
		expected.add(new String[]{"2011-01", "Unused pin", "17"});
		expected.add(new String[]{"2011-01", "No solution exist", "16"});
		expected.add(new String[]{"2011-01", "Will contact local technician", "7"});
		expected.add(new String[]{"2011-01", "Incorrect troubleshooting done", "5"});
		expected.add(new String[]{"2011-01", "Others", "4"});
		expected.add(new String[]{"2011-01", "Out of Scope(Password issue)", "2"});
		expected.add(new String[]{"2011-10", "Customer not happy with service", "34"});
		expected.add(new String[]{"2011-10", "No solution exist", "17"});
		expected.add(new String[]{"2011-10", "Unused pin", "15"});
		expected.add(new String[]{"2011-10", "Inappropriate/incorrect expectations set", "8"});
		expected.add(new String[]{"2011-10", "Billing issues", "7"});
		expected.add(new String[]{"2011-10", "Only wanted recovery media", "6"});
		expected.add(new String[]{"2011-10", "Out of Scope(Password issue)", "3"});
		expected.add(new String[]{"2011-10", "Out of Scope(Screen damage)", "2"});
		expected.add(new String[]{"2011-10", "Incorrect troubleshooting done", "2"});
		expected.add(new String[]{"2011-10", "Others", "2"});
		expected.add(new String[]{"2010-10", "Billing issues", "85"});
		expected.add(new String[]{"2010-10", "Expectations not set clearly", "23"});
		expected.add(new String[]{"2010-10", "Issue not in scope", "17"});
		expected.add(new String[]{"2010-10", "Dissatisfied with prior experience", "15"});
		expected.add(new String[]{"2010-10", "No solution exist", "8"});
		expected.add(new String[]{"2010-10", "Will contact local technician", "4"});
		expected.add(new String[]{"2010-10", "Opting for different product", "2"});
		expected.add(new String[]{"2010-10", "No follow-up", "1"});
		expected.add(new String[]{"2010-10", "Will try competitor", "1"});
		expected.add(new String[]{"2010-10", "High cost", "1"});



		testOutput(expected, topRefundReasonsReport.startReport());
	}
}
