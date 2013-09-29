/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.TopCaseDrivers;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class TopCaseDriversTest extends ReportOutputTest
{
	private TopCaseDrivers topCaseDriversReport;

	public void setUp()
	{
		try 
		{
			topCaseDriversReport = new TopCaseDrivers();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build Top Case Drivers report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(topCaseDriversReport != null)
		{
			topCaseDriversReport.close();
		}
	}

	@Test
	public void testTeamTimeReport()
	{
		topCaseDriversReport.setParameter(Report.REPORT_TYPE_PARAM, "" + TopCaseDrivers.TEAM_TIME_REPORT);
		topCaseDriversReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
		topCaseDriversReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

		topCaseDriversReport.setParameter(Report.START_DATE_PARAM, "2010-10-01 00:00:00");
		topCaseDriversReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");
		topCaseDriversReport.setParameter(Report.NUM_DRIVERS_PARAM, 10);

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-09", "Software-OS", "2068"});
		expected.add(new String[]{"2011-09", "Software-Other", "784"});
		expected.add(new String[]{"2011-09", "Software-Browser", "338"});
		expected.add(new String[]{"2011-09", "Software-Malware Protection", "256"});
		expected.add(new String[]{"2011-09", "Hardware-Network/Communications-Other", "170"});
		expected.add(new String[]{"2011-09", "Hardware-Printer-Printer", "168"});
		expected.add(new String[]{"2011-09", "Hardware-Network/Communications-Router", "128"});
		expected.add(new String[]{"2011-09", "Software-Email", "122"});
		expected.add(new String[]{"2011-09", "Hardware-Network/Communications-Broadband Modem", "117"});
		expected.add(new String[]{"2011-09", "Hardware-Other-Other", "89"});
		expected.add(new String[]{"2011-08", "Software-OS", "1890"});
		expected.add(new String[]{"2011-08", "Software-Other", "847"});
		expected.add(new String[]{"2011-08", "Software-Browser", "359"});
		expected.add(new String[]{"2011-08", "Software-Malware Protection", "317"});
		expected.add(new String[]{"2011-08", "Hardware-Network/Communications-Other", "176"});
		expected.add(new String[]{"2011-08", "Software-Email", "148"});
		expected.add(new String[]{"2011-08", "Hardware-Network/Communications-Router", "147"});
		expected.add(new String[]{"2011-08", "Hardware-Printer-Printer", "130"});
		expected.add(new String[]{"2011-08", "Hardware-Network/Communications-Broadband Modem", "108"});
		expected.add(new String[]{"2011-08", "Hardware-Network/Communications-NIC", "88"});
		expected.add(new String[]{"2011-07", "Software-OS", "2048"});
		expected.add(new String[]{"2011-07", "Software-Other", "878"});
		expected.add(new String[]{"2011-07", "Software-Malware Protection", "450"});
		expected.add(new String[]{"2011-07", "Software-Browser", "391"});
		expected.add(new String[]{"2011-07", "Hardware-Network/Communications-Other", "183"});
		expected.add(new String[]{"2011-07", "Hardware-Network/Communications-Router", "151"});
		expected.add(new String[]{"2011-07", "Software-Email", "141"});
		expected.add(new String[]{"2011-07", "Hardware-Printer-Printer", "137"});
		expected.add(new String[]{"2011-07", "Hardware-Network/Communications-NIC", "86"});
		expected.add(new String[]{"2011-07", "Hardware-Network/Communications-Broadband Modem", "83"});
		expected.add(new String[]{"2010-11", "Software-OS", "1585"});
		expected.add(new String[]{"2010-11", "Software-Malware Protection", "777"});
		expected.add(new String[]{"2010-11", "Software-Other", "408"});
		expected.add(new String[]{"2010-11", "Software-Browser", "222"});
		expected.add(new String[]{"2010-11", "Hardware-Network/Communications-Other", "194"});
		expected.add(new String[]{"2010-11", "Hardware-Other-Other", "173"});
		expected.add(new String[]{"2010-11", "Hardware-Network/Communications-Broadband Modem", "171"});
		expected.add(new String[]{"2010-11", "Software-Email", "112"});
		expected.add(new String[]{"2010-11", "Hardware-Printer-Printer", "87"});
		expected.add(new String[]{"2010-11", "Hardware-Network/Communications-Router", "78"});
		expected.add(new String[]{"2011-06", "Software-OS", "2013"});
		expected.add(new String[]{"2011-06", "Software-Other", "883"});
		expected.add(new String[]{"2011-06", "Software-Malware Protection", "705"});
		expected.add(new String[]{"2011-06", "Software-Browser", "350"});
		expected.add(new String[]{"2011-06", "Hardware-Network/Communications-Other", "147"});
		expected.add(new String[]{"2011-06", "Software-Email", "140"});
		expected.add(new String[]{"2011-06", "Hardware-Printer-Printer", "136"});
		expected.add(new String[]{"2011-06", "Hardware-Network/Communications-Router", "135"});
		expected.add(new String[]{"2011-06", "Hardware-Other-Other", "133"});
		expected.add(new String[]{"2011-06", "Hardware-Network/Communications-Broadband Modem", "107"});
		expected.add(new String[]{"2010-12", "Software-OS", "1774"});
		expected.add(new String[]{"2010-12", "Software-Malware Protection", "710"});
		expected.add(new String[]{"2010-12", "Software-Other", "544"});
		expected.add(new String[]{"2010-12", "Software-Browser", "427"});
		expected.add(new String[]{"2010-12", "Hardware-Network/Communications-Other", "209"});
		expected.add(new String[]{"2010-12", "Hardware-Network/Communications-Broadband Modem", "151"});
		expected.add(new String[]{"2010-12", "Hardware-Other-Other", "119"});
		expected.add(new String[]{"2010-12", "Software-Email", "116"});
		expected.add(new String[]{"2010-12", "Hardware-Network/Communications-Router", "83"});
		expected.add(new String[]{"2010-12", "Hardware-Printer-Printer", "80"});
		expected.add(new String[]{"2011-05", "Software-OS", "1780"});
		expected.add(new String[]{"2011-05", "Software-Malware Protection", "789"});
		expected.add(new String[]{"2011-05", "Software-Other", "762"});
		expected.add(new String[]{"2011-05", "Software-Browser", "344"});
		expected.add(new String[]{"2011-05", "Hardware-Network/Communications-Other", "199"});
		expected.add(new String[]{"2011-05", "Software-Email", "154"});
		expected.add(new String[]{"2011-05", "Hardware-Other-Other", "149"});
		expected.add(new String[]{"2011-05", "Hardware-Network/Communications-Router", "120"});
		expected.add(new String[]{"2011-05", "Hardware-Printer-Printer", "104"});
		expected.add(new String[]{"2011-05", "Hardware-Network/Communications-Broadband Modem", "103"});
		expected.add(new String[]{"2011-04", "Software-OS", "1806"});
		expected.add(new String[]{"2011-04", "Software-Malware Protection", "781"});
		expected.add(new String[]{"2011-04", "Software-Other", "698"});
		expected.add(new String[]{"2011-04", "Software-Browser", "323"});
		expected.add(new String[]{"2011-04", "Hardware-Network/Communications-Other", "163"});
		expected.add(new String[]{"2011-04", "Software-Email", "136"});
		expected.add(new String[]{"2011-04", "Hardware-Network/Communications-Router", "135"});
		expected.add(new String[]{"2011-04", "Hardware-Printer-Printer", "115"});
		expected.add(new String[]{"2011-04", "Hardware-Network/Communications-Broadband Modem", "106"});
		expected.add(new String[]{"2011-04", "Hardware-Other-Other", "78"});
		expected.add(new String[]{"2011-03", "Software-OS", "1896"});
		expected.add(new String[]{"2011-03", "Software-Malware Protection", "836"});
		expected.add(new String[]{"2011-03", "Software-Other", "809"});
		expected.add(new String[]{"2011-03", "Software-Browser", "349"});
		expected.add(new String[]{"2011-03", "Hardware-Network/Communications-Other", "208"});
		expected.add(new String[]{"2011-03", "Hardware-Network/Communications-Router", "139"});
		expected.add(new String[]{"2011-03", "Software-Email", "132"});
		expected.add(new String[]{"2011-03", "Hardware-Printer-Printer", "115"});
		expected.add(new String[]{"2011-03", "Hardware-Network/Communications-Broadband Modem", "101"});
		expected.add(new String[]{"2011-03", "Hardware-Other-Other", "75"});
		expected.add(new String[]{"2011-02", "Software-OS", "1688"});
		expected.add(new String[]{"2011-02", "Software-Malware Protection", "793"});
		expected.add(new String[]{"2011-02", "Software-Other", "653"});
		expected.add(new String[]{"2011-02", "Software-Browser", "282"});
		expected.add(new String[]{"2011-02", "Hardware-Network/Communications-Other", "185"});
		expected.add(new String[]{"2011-02", "Software-Email", "149"});
		expected.add(new String[]{"2011-02", "Hardware-Printer-Printer", "107"});
		expected.add(new String[]{"2011-02", "Hardware-Network/Communications-Router", "96"});
		expected.add(new String[]{"2011-02", "Hardware-Other-Other", "83"});
		expected.add(new String[]{"2011-02", "Hardware-Network/Communications-Broadband Modem", "77"});
		expected.add(new String[]{"2011-01", "Software-OS", "1675"});
		expected.add(new String[]{"2011-01", "Software-Malware Protection", "724"});
		expected.add(new String[]{"2011-01", "Software-Other", "584"});
		expected.add(new String[]{"2011-01", "Software-Browser", "321"});
		expected.add(new String[]{"2011-01", "Hardware-Network/Communications-Other", "177"});
		expected.add(new String[]{"2011-01", "Software-Email", "138"});
		expected.add(new String[]{"2011-01", "Hardware-Network/Communications-Broadband Modem", "107"});
		expected.add(new String[]{"2011-01", "Hardware-Printer-Printer", "84"});
		expected.add(new String[]{"2011-01", "Hardware-Network/Communications-Router", "79"});
		expected.add(new String[]{"2011-01", "Hardware-Other-Other", "69"});
		expected.add(new String[]{"2011-10", "Software-OS", "1930"});
		expected.add(new String[]{"2011-10", "Software-Other", "627"});
		expected.add(new String[]{"2011-10", "Software-Browser", "312"});
		expected.add(new String[]{"2011-10", "Software-Malware Protection", "192"});
		expected.add(new String[]{"2011-10", "Hardware-Network/Communications-Other", "161"});
		expected.add(new String[]{"2011-10", "Hardware-Printer-Printer", "139"});
		expected.add(new String[]{"2011-10", "Software-Email", "127"});
		expected.add(new String[]{"2011-10", "Hardware-Network/Communications-Broadband Modem", "98"});
		expected.add(new String[]{"2011-10", "Hardware-Network/Communications-Router", "85"});
		expected.add(new String[]{"2011-10", "Hardware-Other-Other", "83"});
		expected.add(new String[]{"2010-10", "Software-OS", "1368"});
		expected.add(new String[]{"2010-10", "Software-Malware Protection", "589"});
		expected.add(new String[]{"2010-10", "Software-Other", "377"});
		expected.add(new String[]{"2010-10", "Software-Browser", "201"});
		expected.add(new String[]{"2010-10", "Hardware-Network/Communications-Broadband Modem", "166"});
		expected.add(new String[]{"2010-10", "Hardware-Network/Communications-Other", "147"});
		expected.add(new String[]{"2010-10", "Hardware-Other-Other", "121"});
		expected.add(new String[]{"2010-10", "Hardware-Network/Communications-Router", "94"});
		expected.add(new String[]{"2010-10", "Software-Email", "92"});
		expected.add(new String[]{"2010-10", "Hardware-Network/Communications-NIC", "88"});


		testOutput(expected, topCaseDriversReport.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		topCaseDriversReport.setParameter(Report.REPORT_TYPE_PARAM, "" + TopCaseDrivers.AGENT_TIME_REPORT);
		topCaseDriversReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
		topCaseDriversReport.setParameter(Report.AGENT_NAME_PARAM, "Zioto, Andrew");

		topCaseDriversReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

		topCaseDriversReport.setParameter(Report.START_DATE_PARAM, "2010-10-01 00:00:00");
		topCaseDriversReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");
		topCaseDriversReport.setParameter(Report.NUM_DRIVERS_PARAM, 10);

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-09", "Software-OS", "5"});
		expected.add(new String[]{"2011-09", "Hardware-Printer-Other", "1"});
		expected.add(new String[]{"2011-09", "Software-Microsoft(non-os)", "1"});
		expected.add(new String[]{"2011-09", "Hardware-Printer-Printer", "1"});
		expected.add(new String[]{"2011-09", "Hardware-Sound-Sound Card", "1"});
		expected.add(new String[]{"2011-08", "Software-OS", "4"});
		expected.add(new String[]{"2011-07", "Software-OS", "11"});
		expected.add(new String[]{"2011-07", "Hardware-Video-Video Card", "1"});
		expected.add(new String[]{"2010-11", "Software-OS", "26"});
		expected.add(new String[]{"2010-11", "Software-Browser", "5"});
		expected.add(new String[]{"2010-11", "Hardware-Network/Communications-Broadband Modem", "2"});
		expected.add(new String[]{"2010-11", "Hardware-Network/Communications-Other", "2"});
		expected.add(new String[]{"2010-11", "Hardware-Video-Other", "1"});
		expected.add(new String[]{"2010-11", "Software-Other", "1"});
		expected.add(new String[]{"2010-11", "Hardware-Storage-HDD", "1"});
		expected.add(new String[]{"2010-11", "Hardware-Power-Power Suppy", "1"});
		expected.add(new String[]{"2010-11", "Hardware-Network/Communications-Router", "1"});
		expected.add(new String[]{"2011-06", "Software-OS", "12"});
		expected.add(new String[]{"2011-06", "Hardware-Printer-Printer", "2"});
		expected.add(new String[]{"2011-06", "Software-Browser", "1"});
		expected.add(new String[]{"2011-06", "Software-Microsoft(non-os)", "1"});
		expected.add(new String[]{"2010-12", "Software-OS", "11"});
		expected.add(new String[]{"2010-12", "Software-Browser", "1"});
		expected.add(new String[]{"2010-12", "Hardware-Network/Communications-Broadband Modem", "1"});
		expected.add(new String[]{"2010-12", "Hardware-Video-Monitor", "1"});
		expected.add(new String[]{"2010-12", "Hardware-Network/Communications-NIC", "1"});
		expected.add(new String[]{"2011-05", "Software-OS", "6"});
		expected.add(new String[]{"2011-05", "Software-Other", "1"});
		expected.add(new String[]{"2011-05", "Hardware-Video-Monitor", "1"});
		expected.add(new String[]{"2011-04", "Software-OS", "10"});
		expected.add(new String[]{"2011-04", "Hardware-Printer-Printer", "3"});
		expected.add(new String[]{"2011-04", "Software-Other", "1"});
		expected.add(new String[]{"2011-03", "Software-OS", "8"});
		expected.add(new String[]{"2011-03", "Software-Other", "2"});
		expected.add(new String[]{"2011-03", "Hardware-Printer-Printer", "1"});
		expected.add(new String[]{"2011-03", "Hardware-Network/Communications-Other", "1"});
		expected.add(new String[]{"2011-03", "Hardware-Network/Communications-NIC", "1"});
		expected.add(new String[]{"2011-02", "Software-OS", "5"});
		expected.add(new String[]{"2011-02", "Hardware-Printer-Printer", "1"});
		expected.add(new String[]{"2011-02", "Hardware-Storage-Other", "1"});
		expected.add(new String[]{"2011-02", "Hardware-Video-Video Card", "1"});
		expected.add(new String[]{"2011-02", "Hardware-Sound-Sound Card", "1"});
		expected.add(new String[]{"2011-01", "Software-OS", "1"});
		expected.add(new String[]{"2011-01", "Tutorial-Other", "1"});
		expected.add(new String[]{"2011-10", "Software-OS", "4"});
		expected.add(new String[]{"2011-10", "Hardware-Printer-Printer", "1"});
		expected.add(new String[]{"2011-10", "Hardware-Storage-HDD", "1"});
		expected.add(new String[]{"2010-10", "Software-OS", "28"});
		expected.add(new String[]{"2010-10", "Hardware-Storage-HDD", "3"});
		expected.add(new String[]{"2010-10", "Software-Browser", "3"});
		expected.add(new String[]{"2010-10", "Software-Other", "3"});
		expected.add(new String[]{"2010-10", "Software-Malware Protection", "2"});
		expected.add(new String[]{"2010-10", "Hardware-Network/Communications-Broadband Modem", "2"});
		expected.add(new String[]{"2010-10", "Hardware-Network/Communications-Router", "2"});
		expected.add(new String[]{"2010-10", "Hardware-Video-Video Card", "2"});
		expected.add(new String[]{"2010-10", "Hardware-Printer-Printer", "1"});
		expected.add(new String[]{"2010-10", "Hardware-Other-Other", "1"});


		testOutput(expected, topCaseDriversReport.startReport());
	}
}
