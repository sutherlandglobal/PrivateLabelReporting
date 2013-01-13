/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.AverageOrderValue;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class AverageOrderValueTest extends ReportOutputTest
{
	private AverageOrderValue aovReport;

	public void setUp()
	{
		try 
		{
			aovReport = new AverageOrderValue();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build AOV report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(aovReport != null)
		{
			aovReport.close();
		}
	}

	@Test
	public void testTeamTimeReport()
	{
		aovReport.setParameter(Report.REPORT_TYPE_PARAM, AverageOrderValue.TEAM_TIME_REPORT);
		aovReport.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
		aovReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		aovReport.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		aovReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-14", "146.28629629629611"});
		expected.add(new String[]{"2011-10-15", "145.61499999999984"});
		expected.add(new String[]{"2011-10-12", "140.13084507042237"});
		expected.add(new String[]{"2011-10-13", "142.58259259259242"});
		expected.add(new String[]{"2011-10-18", "149.46381578947353"});
		expected.add(new String[]{"2011-10-19", "136.4186904761903"});
		expected.add(new String[]{"2011-10-16", "150.85206896551708"});
		expected.add(new String[]{"2011-10-17", "145.5073563218389"});
		expected.add(new String[]{"2011-10-22", "140.89909090909075"});
		expected.add(new String[]{"2011-10-21", "139.50612903225792"});
		expected.add(new String[]{"2011-10-20", "151.41857142857125"});
		expected.add(new String[]{"2011-10-01", "136.82333333333318"});
		expected.add(new String[]{"2011-10-02", "131.12207547169797"});
		expected.add(new String[]{"2011-10-30", "137.24490196078418"});
		expected.add(new String[]{"2011-10-03", "141.84203703703685"});
		expected.add(new String[]{"2011-10-31", "144.84294117647042"});
		expected.add(new String[]{"2011-10-04", "141.5484415584414"});
		expected.add(new String[]{"2011-10-05", "136.97630136986285"});
		expected.add(new String[]{"2011-10-06", "146.86499999999984"});
		expected.add(new String[]{"2011-10-07", "135.175185185185"});
		expected.add(new String[]{"2011-10-08", "136.9465217391303"});
		expected.add(new String[]{"2011-10-09", "135.51631578947357"});
		expected.add(new String[]{"2011-10-29", "145.3603703703702"});
		expected.add(new String[]{"2011-10-28", "132.57426966292118"});
		expected.add(new String[]{"2011-10-27", "135.635322580645"});
		expected.add(new String[]{"2011-10-26", "141.31367647058806"});
		expected.add(new String[]{"2011-10-25", "144.68696969696953"});
		expected.add(new String[]{"2011-10-11", "137.27419753086403"});
		expected.add(new String[]{"2011-10-24", "144.92519480519465"});
		expected.add(new String[]{"2011-10-10", "137.18512195121934"});
		expected.add(new String[]{"2011-10-23", "138.27124999999984"});

		testOutput(expected, aovReport.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		aovReport.setParameter(Report.REPORT_TYPE_PARAM, "" + AverageOrderValue.AGENT_TIME_REPORT);
		aovReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		aovReport.setParameter(Report.AGENT_NAME_PARAM, "Carter, Janice");

		aovReport.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		aovReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");


		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-02", "133.32333333333335"});
		expected.add(new String[]{"2011-10-30", "149.99"});
		expected.add(new String[]{"2011-10-03", "137.49"});
		expected.add(new String[]{"2011-10-12", "159.99"});
		expected.add(new String[]{"2011-10-31", "124.99000000000001"});
		expected.add(new String[]{"2011-10-04", "116.65666666666668"});
		expected.add(new String[]{"2011-10-13", "129.99"});
		expected.add(new String[]{"2011-10-05", "149.99"});
		expected.add(new String[]{"2011-10-06", "137.49"});
		expected.add(new String[]{"2011-10-19", "149.99"});
		expected.add(new String[]{"2011-10-16", "141.65666666666667"});
		expected.add(new String[]{"2011-10-17", "159.99"});
		expected.add(new String[]{"2011-10-09", "149.99"});
		expected.add(new String[]{"2011-10-29", "199.99"});
		expected.add(new String[]{"2011-10-27", "149.99"});
		expected.add(new String[]{"2011-10-26", "99.99"});
		expected.add(new String[]{"2011-10-25", "162.49"});
		expected.add(new String[]{"2011-10-24", "149.99"});
		expected.add(new String[]{"2011-10-20", "116.65666666666668"});
		expected.add(new String[]{"2011-10-11", "116.65666666666668"});
		expected.add(new String[]{"2011-10-23", "139.99"});
		expected.add(new String[]{"2011-10-10", "133.32333333333335"});


		testOutput(expected, aovReport.startReport());
	}

	@Test
	public void testAgentStackReport()
	{

		aovReport.setParameter(Report.REPORT_TYPE_PARAM, AverageOrderValue.AGENT_STACK_REPORT);
		aovReport.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

		aovReport.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		aovReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");


		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"Narayanan S", "Sathya", "199.99"});
		expected.add(new String[]{"Belluccio", "Nicholas", "135.20739130434765"});
		expected.add(new String[]{"Rajan", "Divya", "134.03255319148923"});
		expected.add(new String[]{"G", "Salman Ali", "167.8471428571427"});
		expected.add(new String[]{"L", "Chitra", "99.99000000000001"});
		expected.add(new String[]{"J", "Senthil Kumar", "58.32333333333333"});
		expected.add(new String[]{"Carter", "Janice", "141.43736842105247"});
		expected.add(new String[]{"M", "Shyamnath", "128.85597938144315"});
		expected.add(new String[]{"Bharathy", "Satheesh", "142.93871794871777"});
		expected.add(new String[]{"N", "Promod", "161.70171171171168"});
		expected.add(new String[]{"V", "Vasanthi", "148.17181818181803"});
		expected.add(new String[]{"Mcevans", "Tyrone", "149.98999999999984"});
		expected.add(new String[]{"S", "Gowtham", "130.95774193548405"});
		expected.add(new String[]{"M", "Balakuthalanathan", "99.99000000000001"});
		expected.add(new String[]{"Smith", "Russell", "124.6653246753245"});
		expected.add(new String[]{"Kumar Rudra", "Prabir", "149.99"});
		expected.add(new String[]{"Giddens", "Ometries", "133.32333333333318"});
		expected.add(new String[]{"Bartholomew", "Shiobion", "144.64356435643546"});
		expected.add(new String[]{"Johnston", "Zack", "134.98999999999984"});
		expected.add(new String[]{"Brewington", "Charles", "135.7924691358023"});
		expected.add(new String[]{"Robinson", "Anthony", "135.49724637681143"});
		expected.add(new String[]{"G", "Hariharan", "145.31710280373812"});
		expected.add(new String[]{"Gates", "Clayton", "137.4899999999998"});
		expected.add(new String[]{"Raghunath P", "Shravan", "157.13285714285698"});
		expected.add(new String[]{"Thompson", "Paul", "133.5111267605632"});
		expected.add(new String[]{"Robert", "Emmanuel", "129.33782608695634"});
		expected.add(new String[]{"N", "Guruvayurappan", "149.98999999999987"});
		expected.add(new String[]{"Nicholas", "Orville", "139.37675438596474"});
		expected.add(new String[]{"K", "Vinothini", "148.67421052631562"});
		expected.add(new String[]{"Raja", "DharmarajM", "146.3863963963962"});
		expected.add(new String[]{"Shekem", "Shatda", "152.5986956521739"});

		testOutput(expected, aovReport.startReport());
	}

	@Test
	public void testTeamStackReport()
	{
		aovReport.setParameter(Report.REPORT_TYPE_PARAM, "" + AverageOrderValue.TEAM_STACK_REPORT);
		aovReport.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		aovReport.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

		aovReport.setParameter(Report.AGENT_NAME_PARAM, "Carter, Janice");

		aovReport.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		aovReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");


		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"ROCJFS Sales Team", "1648.5332744018085"});
		expected.add(new String[]{"CHNDLF-New Hire Tech Team", "258.30333333333334"});
		expected.add(new String[]{"CHNDLF Sales Team", "2391.3140581640696"});


		testOutput(expected, aovReport.startReport());
	}
}
