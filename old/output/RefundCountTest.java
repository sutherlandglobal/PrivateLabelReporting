/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.RefundCount;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class RefundCountTest extends ReportOutputTest
{
	private RefundCount report;

	public void setUp()
	{
		try 
		{
			report = new RefundCount();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build Refund Count report", true);

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

		report.setParameter(Report.REPORT_TYPE_PARAM, RefundCount.TEAM_TIME_REPORT);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-01", "7"});
		expected.add(new String[]{"2011-10-14", "9"});
		expected.add(new String[]{"2011-10-15", "7"});
		expected.add(new String[]{"2011-10-03", "5"});
		expected.add(new String[]{"2011-10-12", "9"});
		expected.add(new String[]{"2011-10-31", "8"});
		expected.add(new String[]{"2011-10-04", "7"});
		expected.add(new String[]{"2011-10-13", "3"});
		expected.add(new String[]{"2011-10-18", "3"});
		expected.add(new String[]{"2011-10-05", "3"});
		expected.add(new String[]{"2011-10-19", "2"});
		expected.add(new String[]{"2011-10-06", "10"});
		expected.add(new String[]{"2011-10-07", "4"});
		expected.add(new String[]{"2011-10-17", "1"});
		expected.add(new String[]{"2011-10-28", "5"});
		expected.add(new String[]{"2011-10-27", "3"});
		expected.add(new String[]{"2011-10-26", "4"});
		expected.add(new String[]{"2011-10-25", "4"});
		expected.add(new String[]{"2011-10-24", "7"});
		expected.add(new String[]{"2011-10-20", "6"});
		expected.add(new String[]{"2011-10-11", "4"});
		expected.add(new String[]{"2011-10-23", "11"});
		expected.add(new String[]{"2011-10-10", "15"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, "" + RefundCount.AGENT_TIME_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.AGENT_NAME_PARAM, "Robinson, Anthony");

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-14", "1"});
		expected.add(new String[]{"2011-10-12", "1"});
		expected.add(new String[]{"2011-10-13", "1"});
		expected.add(new String[]{"2011-10-11", "1"});
		expected.add(new String[]{"2011-10-23", "1"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentStackReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, RefundCount.AGENT_STACK_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"Narayanan S", "Sathya", "0"});
		expected.add(new String[]{"O'Neill", "Amy", "0"});
		expected.add(new String[]{"Deo", "Michelle", "0"});
		expected.add(new String[]{"Males", "Karen", "1"});
		expected.add(new String[]{"Washington", "Dorie", "0"});
		expected.add(new String[]{"Ponvely", "Roseline", "0"});
		expected.add(new String[]{"Popplewell", "Brian", "0"});
		expected.add(new String[]{"J", "Senthil Kumar", "0"});
		expected.add(new String[]{"Washington", "Latasha", "0"});
		expected.add(new String[]{"Baker", "Michael", "0"});
		expected.add(new String[]{"Bharathy", "Satheesh", "1"});
		expected.add(new String[]{"N", "Promod", "6"});
		expected.add(new String[]{"Wynn", "Peter", "0"});
		expected.add(new String[]{"V", "PraveenKumar", "0"});
		expected.add(new String[]{"Pollack", "Joel", "0"});
		expected.add(new String[]{"Donaldson", "Robert", "0"});
		expected.add(new String[]{"N", "Sudarshan", "0"});
		expected.add(new String[]{"Wood", "Elizabeth", "0"});
		expected.add(new String[]{"Fortin", "Rachelle", "0"});
		expected.add(new String[]{"Iverson", "Jerry", "0"});
		expected.add(new String[]{"Litolff", "John", "0"});
		expected.add(new String[]{"Mcevans", "Tyrone", "4"});
		expected.add(new String[]{"KempReynolds", "Trevor", "0"});
		expected.add(new String[]{"Falls", "Donna", "0"});
		expected.add(new String[]{"M", "Pradeep", "0"});
		expected.add(new String[]{"Wilson", "Carol", "0"});
		expected.add(new String[]{"Krishna", "Anu", "0"});
		expected.add(new String[]{"VLN", "Kumar", "0"});
		expected.add(new String[]{"Kumar Rudra", "Prabir", "0"});
		expected.add(new String[]{"D'Angelo", "Kathy", "0"});
		expected.add(new String[]{"S", "MuthuKrishnan", "0"});
		expected.add(new String[]{"Delle Fave", "David", "0"});
		expected.add(new String[]{"George", "Jackson", "0"});
		expected.add(new String[]{"Robinson", "Elizabeth", "0"});
		expected.add(new String[]{"Raghunath P", "Shravan", "0"});
		expected.add(new String[]{"Stadninsky", "Dean", "0"});
		expected.add(new String[]{"Dave", "Shyam", "0"});
		expected.add(new String[]{"V", "Lakshmi Narasimhan", "0"});
		expected.add(new String[]{"Belluccio", "Nicholas", "12"});
		expected.add(new String[]{"Edmondson", "Magnus", "1"});
		expected.add(new String[]{"Rajan", "Divya", "5"});
		expected.add(new String[]{"S", "Prakash", "0"});
		expected.add(new String[]{"McGuire", "James", "0"});
		expected.add(new String[]{"Vandersluis", "Corey", "0"});
		expected.add(new String[]{"Davis", "Jamal", "0"});
		expected.add(new String[]{"M", "Shyamnath", "5"});
		expected.add(new String[]{"Kniahnicki", "Julie", "0"});
		expected.add(new String[]{"Lumia", "Vincent", "0"});
		expected.add(new String[]{"Strachan", "Andrea", "0"});
		expected.add(new String[]{"Barratt", "Desiree", "0"});
		expected.add(new String[]{"Jensen", "Kate", "0"});
		expected.add(new String[]{"BogaWilliams", "Benita", "0"});
		expected.add(new String[]{"Chapman", "FeliciaCandace", "0"});
		expected.add(new String[]{"DeLeon", "Robert", "0"});
		expected.add(new String[]{"Kruger", "Cory", "0"});
		expected.add(new String[]{"Muniappan", "Hashini", "0"});
		expected.add(new String[]{"Roy", "Michael", "0"});
		expected.add(new String[]{"Washington", "Korea", "0"});
		expected.add(new String[]{"M", "Balakuthalanathan", "0"});
		expected.add(new String[]{"Brandon", "Latonya", "1"});
		expected.add(new String[]{"Smith", "Russell", "7"});
		expected.add(new String[]{"Shoemaker", "Jared", "0"});
		expected.add(new String[]{"Giddens", "Ometries", "6"});
		expected.add(new String[]{"S", "Vivekananth", "0"});
		expected.add(new String[]{"Bartholomew", "Shiobion", "4"});
		expected.add(new String[]{"Henry", "Jonathan", "0"});
		expected.add(new String[]{"Robinson", "Anthony", "5"});
		expected.add(new String[]{"G", "Hariharan", "8"});
		expected.add(new String[]{"Bonnell", "Lori", "0"});
		expected.add(new String[]{"Paul", "Kayla", "0"});
		expected.add(new String[]{"B", "Karthick", "0"});
		expected.add(new String[]{"Goessl", "Laurie", "0"});
		expected.add(new String[]{"Shah", "Mohammed", "0"});
		expected.add(new String[]{"Senior", "Herbert", "0"});
		expected.add(new String[]{"Belanger", "Johnathan", "0"});
		expected.add(new String[]{"Wong", "Kevin", "0"});
		expected.add(new String[]{"Peters", "Johnathan", "0"});
		expected.add(new String[]{"Nicholas", "Orville", "9"});
		expected.add(new String[]{"N", "Guruvayurappan", "0"});
		expected.add(new String[]{"Ward", "David", "0"});
		expected.add(new String[]{"Troidl", "Sylvia", "0"});
		expected.add(new String[]{"PG", "Vishwanathan", "0"});
		expected.add(new String[]{"Reed", "Jamie", "0"});
		expected.add(new String[]{"LeBlance", "Eleanor", "0"});
		expected.add(new String[]{"G", "Salman Ali", "3"});
		expected.add(new String[]{"L", "Chitra", "0"});
		expected.add(new String[]{"Carter", "Janice", "2"});
		expected.add(new String[]{"V", "Vasanthi", "3"});
		expected.add(new String[]{"Harrison", "Miranda", "0"});
		expected.add(new String[]{"M", "ShakeelAhamed", "0"});
		expected.add(new String[]{"Dayal", "Rakesh", "0"});
		expected.add(new String[]{"Gallo", "Joseph", "0"});
		expected.add(new String[]{"Bera", "Sanchita", "0"});
		expected.add(new String[]{"Chiarella", "Samuel", "0"});
		expected.add(new String[]{"Baic", "Jessica", "0"});
		expected.add(new String[]{"Correya", "Avalon", "0"});
		expected.add(new String[]{"Penner", "William", "0"});
		expected.add(new String[]{"Wainwright", "Derek", "0"});
		expected.add(new String[]{"Vasta", "Rocco", "0"});
		expected.add(new String[]{"LeClair", "Wendy", "0"});
		expected.add(new String[]{"Saha", "Sushmita", "0"});
		expected.add(new String[]{"S", "Vijay", "0"});
		expected.add(new String[]{"Yukich", "Anthony", "0"});
		expected.add(new String[]{"Day", "Heather", "0"});
		expected.add(new String[]{"Gibson", "Natasha", "0"});
		expected.add(new String[]{"Riley", "Anthony", "0"});
		expected.add(new String[]{"Kharsahnoh K", "Artisha", "0"});
		expected.add(new String[]{"Hickey", "Carlos", "0"});
		expected.add(new String[]{"G", "Meenatchi", "0"});
		expected.add(new String[]{"Morrison", "Tym", "0"});
		expected.add(new String[]{"K", "Gopinath", "0"});
		expected.add(new String[]{"Roach", "Lorraine", "0"});
		expected.add(new String[]{"Maashook", "Mohamed", "0"});
		expected.add(new String[]{"Shekem", "Shatda", "7"});
		expected.add(new String[]{"Ekka", "MickyPriyanka", "0"});
		expected.add(new String[]{"Blais", "Nancy", "0"});
		expected.add(new String[]{"A", "Sridevi", "0"});
		expected.add(new String[]{"Blackmon", "Marvin", "0"});
		expected.add(new String[]{"Dhayan", "Lenin", "0"});
		expected.add(new String[]{"Bennett", "Christopher", "0"});
		expected.add(new String[]{"Cerame", "Robert", "0"});
		expected.add(new String[]{"LiButti", "Anthony", "0"});
		expected.add(new String[]{"Vari", "Joseph", "0"});
		expected.add(new String[]{"Blackstock", "Michael", "0"});
		expected.add(new String[]{"S", "Gowtham", "6"});
		expected.add(new String[]{"Gagnon", "Leslie", "0"});
		expected.add(new String[]{"Castillo", "Norely", "0"});
		expected.add(new String[]{"Hagan", "Natasha", "0"});
		expected.add(new String[]{"Theriault", "David", "0"});
		expected.add(new String[]{"Xavier", "John", "0"});
		expected.add(new String[]{"Davis", "Joshua", "1"});
		expected.add(new String[]{"Johnston", "Zack", "1"});
		expected.add(new String[]{"Brewington", "Charles", "6"});
		expected.add(new String[]{"Robinson", "Jason", "0"});
		expected.add(new String[]{"Bellomio", "Phillip", "0"});
		expected.add(new String[]{"Gates", "Clayton", "7"});
		expected.add(new String[]{"Morse", "Robert", "0"});
		expected.add(new String[]{"Haas", "Don", "0"});
		expected.add(new String[]{"G", "MarcusDominic", "0"});
		expected.add(new String[]{"Samuel", "Angelyn", "0"});
		expected.add(new String[]{"Dearing", "Lori", "0"});
		expected.add(new String[]{"Thompson", "Paul", "3"});
		expected.add(new String[]{"Goodchild", "Nicholas", "0"});
		expected.add(new String[]{"Scialpa", "Marie", "0"});
		expected.add(new String[]{"Smith", "Terry", "0"});
		expected.add(new String[]{"Tyszko", "Michael", "0"});
		expected.add(new String[]{"F", "Fathima Jayaselvi", "0"});
		expected.add(new String[]{"Robert", "Emmanuel", "6"});
		expected.add(new String[]{"Theriault", "Dave", "0"});
		expected.add(new String[]{"D", "Gopinath", "0"});
		expected.add(new String[]{"JinSuen", "Timothy", "0"});
		expected.add(new String[]{"K", "Vinothini", "7"});
		expected.add(new String[]{"Raja", "DharmarajM", "10"});
		expected.add(new String[]{"Peters", "Erika", "0"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testTeamStackReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, RefundCount.TEAM_STACK_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"ROCJFS Sales Team", "69"});
		expected.add(new String[]{"CHNDLF-New Hire Tech Team", "0"});
		expected.add(new String[]{"Sales-Associates", "0"});
		expected.add(new String[]{"Acer Voice - DLF - Kalpesh", "0"});
		expected.add(new String[]{"Acer US Voice - TLs", "0"});
		expected.add(new String[]{"ROCJFS Save Team", "0"});
		expected.add(new String[]{"CHNDLF Sales Team", "64"});
		expected.add(new String[]{"SYRGAL Sales Team", "4"});
		expected.add(new String[]{"Acer US Voice PM", "0"});
		expected.add(new String[]{"ROCJFS-Sales New Hire", "0"});
		expected.add(new String[]{"Retention Team", "0"});


		testOutput(expected, report.startReport());
	}

}
