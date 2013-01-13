/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.Conversion;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class ConversionTest extends ReportOutputTest
{
	private Conversion report;

	public void setUp()
	{
		try 
		{
			report = new Conversion();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build Call Volume report", true);

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

		report.setParameter(Report.REPORT_TYPE_PARAM, Conversion.TEAM_TIME_REPORT);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-14", "81", "868", "0.09331797235023041"});
		expected.add(new String[]{"2011-10-15", "80", "677", "0.11816838995568685"});
		expected.add(new String[]{"2011-10-12", "71", "966", "0.07349896480331262"});
		expected.add(new String[]{"2011-10-13", "81", "945", "0.08571428571428572"});
		expected.add(new String[]{"2011-10-18", "76", "870", "0.08735632183908046"});
		expected.add(new String[]{"2011-10-19", "84", "897", "0.09364548494983277"});
		expected.add(new String[]{"2011-10-16", "58", "586", "0.09897610921501707"});
		expected.add(new String[]{"2011-10-17", "87", "1076", "0.08085501858736059"});
		expected.add(new String[]{"2011-10-22", "55", "615", "0.08943089430894309"});
		expected.add(new String[]{"2011-10-21", "62", "742", "0.08355795148247978"});
		expected.add(new String[]{"2011-10-20", "70", "823", "0.0850546780072904"});
		expected.add(new String[]{"2011-10-01", "60", "622", "0.09646302250803858"});
		expected.add(new String[]{"2011-10-02", "53", "604", "0.08774834437086093"});
		expected.add(new String[]{"2011-10-30", "51", "517", "0.09864603481624758"});
		expected.add(new String[]{"2011-10-03", "108", "1087", "0.09935602575896964"});
		expected.add(new String[]{"2011-10-31", "68", "802", "0.08478802992518704"});
		expected.add(new String[]{"2011-10-04", "77", "876", "0.08789954337899543"});
		expected.add(new String[]{"2011-10-05", "73", "884", "0.08257918552036199"});
		expected.add(new String[]{"2011-10-06", "64", "784", "0.08163265306122448"});
		expected.add(new String[]{"2011-10-07", "81", "814", "0.09950859950859951"});
		expected.add(new String[]{"2011-10-08", "46", "586", "0.07849829351535836"});
		expected.add(new String[]{"2011-10-09", "38", "486", "0.07818930041152264"});
		expected.add(new String[]{"2011-10-29", "54", "580", "0.09310344827586207"});
		expected.add(new String[]{"2011-10-28", "89", "792", "0.11237373737373738"});
		expected.add(new String[]{"2011-10-27", "62", "846", "0.07328605200945626"});
		expected.add(new String[]{"2011-10-26", "68", "874", "0.07780320366132723"});
		expected.add(new String[]{"2011-10-25", "66", "888", "0.07432432432432433"});
		expected.add(new String[]{"2011-10-24", "77", "1008", "0.0763888888888889"});
		expected.add(new String[]{"2011-10-11", "81", "878", "0.09225512528473805"});
		expected.add(new String[]{"2011-10-23", "64", "544", "0.11764705882352941"});
		expected.add(new String[]{"2011-10-10", "82", "1008", "0.08134920634920635"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, "" + Conversion.AGENT_TIME_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.AGENT_NAME_PARAM, "Carter, Janice");

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-02", "3", "36", "0.08333333333333333"});
		expected.add(new String[]{"2011-10-30", "3", "37", "0.08108108108108109"});
		expected.add(new String[]{"2011-10-03", "4", "40", "0.1"});
		expected.add(new String[]{"2011-10-12", "5", "37", "0.13513513513513514"});
		expected.add(new String[]{"2011-10-04", "3", "31", "0.0967741935483871"});
		expected.add(new String[]{"2011-10-31", "2", "38", "0.05263157894736842"});
		expected.add(new String[]{"2011-10-13", "5", "36", "0.1388888888888889"});
		expected.add(new String[]{"2011-10-05", "4", "35", "0.11428571428571428"});
		expected.add(new String[]{"2011-10-18", "0", "0", "0.0"});
		expected.add(new String[]{"2011-10-06", "4", "41", "0.0975609756097561"});
		expected.add(new String[]{"2011-10-19", "4", "46", "0.08695652173913043"});
		expected.add(new String[]{"2011-10-16", "6", "33", "0.18181818181818182"});
		expected.add(new String[]{"2011-10-17", "5", "45", "0.1111111111111111"});
		expected.add(new String[]{"2011-10-09", "1", "33", "0.030303030303030304"});
		expected.add(new String[]{"2011-10-29", "1", "23", "0.043478260869565216"});
		expected.add(new String[]{"2011-10-27", "5", "40", "0.125"});
		expected.add(new String[]{"2011-10-26", "2", "35", "0.05714285714285714"});
		expected.add(new String[]{"2011-10-25", "4", "37", "0.10810810810810811"});
		expected.add(new String[]{"2011-10-24", "1", "27", "0.037037037037037035"});
		expected.add(new String[]{"2011-10-20", "3", "36", "0.08333333333333333"});
		expected.add(new String[]{"2011-10-11", "3", "25", "0.12"});
		expected.add(new String[]{"2011-10-23", "5", "31", "0.16129032258064516"});
		expected.add(new String[]{"2011-10-10", "3", "41", "0.07317073170731707"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentStackReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, Conversion.AGENT_STACK_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();


		expected.add(new String[]{"Narayanan S", "Sathya", "0.07142857142857142"});
		expected.add(new String[]{"Males", "Karen", "0.0"});
		expected.add(new String[]{"Deo", "Michelle", "0.0"});
		expected.add(new String[]{"O'Neill", "Amy", "0.0"});
		expected.add(new String[]{"Ponvely", "Roseline", "0.0"});
		expected.add(new String[]{"Washington", "Dorie", "0.0"});
		expected.add(new String[]{"Popplewell", "Brian", "0.0"});
		expected.add(new String[]{"Washington", "Latasha", "0.0"});
		expected.add(new String[]{"J", "Senthil Kumar", "0.03333333333333333"});
		expected.add(new String[]{"Baker", "Michael", "0.0"});
		expected.add(new String[]{"N", "Promod", "0.09762532981530343"});
		expected.add(new String[]{"Bharathy", "Satheesh", "0.07379375591296121"});
		expected.add(new String[]{"Wynn", "Peter", "0.0"});
		expected.add(new String[]{"V", "PraveenKumar", "0.0"});
		expected.add(new String[]{"Pollack", "Joel", "0.0"});
		expected.add(new String[]{"Donaldson", "Robert", "0.0"});
		expected.add(new String[]{"N", "Sudarshan", "0.0"});
		expected.add(new String[]{"Wood", "Elizabeth", "0.0"});
		expected.add(new String[]{"Fortin", "Rachelle", "0.0"});
		expected.add(new String[]{"Iverson", "Jerry", "0.0"});
		expected.add(new String[]{"Litolff", "John", "0.0"});
		expected.add(new String[]{"Mcevans", "Tyrone", "0.07135250266240682"});
		expected.add(new String[]{"KempReynolds", "Trevor", "0.0"});
		expected.add(new String[]{"Falls", "Donna", "0.0"});
		expected.add(new String[]{"Wilson", "Carol", "0.0"});
		expected.add(new String[]{"M", "Pradeep", "0.0"});
		expected.add(new String[]{"VLN", "Kumar", "0.0"});
		expected.add(new String[]{"Krishna", "Anu", "0.0"});
		expected.add(new String[]{"Kumar Rudra", "Prabir", "0.3333333333333333"});
		expected.add(new String[]{"D'Angelo", "Kathy", "0.0"});
		expected.add(new String[]{"S", "MuthuKrishnan", "0.0"});
		expected.add(new String[]{"Delle Fave", "David", "0.0"});
		expected.add(new String[]{"George", "Jackson", "0.0"});
		expected.add(new String[]{"Raghunath P", "Shravan", "0.0"});
		expected.add(new String[]{"Robinson", "Elizabeth", "0.0"});
		expected.add(new String[]{"Dave", "Shyam", "0.0"});
		expected.add(new String[]{"Stadninsky", "Dean", "0.0"});
		expected.add(new String[]{"V", "Lakshmi Narasimhan", "0.0"});
		expected.add(new String[]{"Belluccio", "Nicholas", "0.11734693877551021"});
		expected.add(new String[]{"Rajan", "Divya", "0.05745721271393643"});
		expected.add(new String[]{"Edmondson", "Magnus", "0.0"});
		expected.add(new String[]{"S", "Prakash", "0.0"});
		expected.add(new String[]{"M", "Shyamnath", "0.08110367892976589"});
		expected.add(new String[]{"Davis", "Jamal", "0.0"});
		expected.add(new String[]{"Vandersluis", "Corey", "0.0"});
		expected.add(new String[]{"McGuire", "James", "0.0"});
		expected.add(new String[]{"Kniahnicki", "Julie", "0.0"});
		expected.add(new String[]{"Lumia", "Vincent", "0.0"});
		expected.add(new String[]{"Strachan", "Andrea", "0.0"});
		expected.add(new String[]{"Barratt", "Desiree", "0.0"});
		expected.add(new String[]{"Jensen", "Kate", "0.0"});
		expected.add(new String[]{"BogaWilliams", "Benita", "0.0"});
		expected.add(new String[]{"Chapman", "FeliciaCandace", "0.0"});
		expected.add(new String[]{"DeLeon", "Robert", "0.0"});
		expected.add(new String[]{"Kruger", "Cory", "0.0"});
		expected.add(new String[]{"Muniappan", "Hashini", "0.0"});
		expected.add(new String[]{"Roy", "Michael", "0.0"});
		expected.add(new String[]{"Washington", "Korea", "0.0"});
		expected.add(new String[]{"M", "Balakuthalanathan", "0.04411764705882353"});
		expected.add(new String[]{"Brandon", "Latonya", "0.0"});
		expected.add(new String[]{"Smith", "Russell", "0.09846547314578005"});
		expected.add(new String[]{"Shoemaker", "Jared", "0.0"});
		expected.add(new String[]{"Giddens", "Ometries", "0.07525655644241733"});
		expected.add(new String[]{"S", "Vivekananth", "0.0"});
		expected.add(new String[]{"Bartholomew", "Shiobion", "0.09805825242718447"});
		expected.add(new String[]{"Henry", "Jonathan", "0.0"});
		expected.add(new String[]{"Robinson", "Anthony", "0.07796610169491526"});
		expected.add(new String[]{"G", "Hariharan", "0.09674502712477397"});
		expected.add(new String[]{"Bonnell", "Lori", "0.0"});
		expected.add(new String[]{"Paul", "Kayla", "0.0"});
		expected.add(new String[]{"B", "Karthick", "0.0"});
		expected.add(new String[]{"Goessl", "Laurie", "0.0"});
		expected.add(new String[]{"Shah", "Mohammed", "0.0"});
		expected.add(new String[]{"Senior", "Herbert", "0.0"});
		expected.add(new String[]{"Belanger", "Johnathan", "0.0"});
		expected.add(new String[]{"Wong", "Kevin", "0.0"});
		expected.add(new String[]{"Peters", "Johnathan", "0.0"});
		expected.add(new String[]{"Nicholas", "Orville", "0.09275834011391375"});
		expected.add(new String[]{"N", "Guruvayurappan", "0.12903225806451613"});
		expected.add(new String[]{"Ward", "David", "0.0"});
		expected.add(new String[]{"Troidl", "Sylvia", "0.0"});
		expected.add(new String[]{"PG", "Vishwanathan", "0.0"});
		expected.add(new String[]{"Reed", "Jamie", "0.0"});
		expected.add(new String[]{"LeBlance", "Eleanor", "0.0"});
		expected.add(new String[]{"G", "Salman Ali", "0.07142857142857142"});
		expected.add(new String[]{"L", "Chitra", "0.021834061135371178"});
		expected.add(new String[]{"Carter", "Janice", "0.0927960927960928"});
		expected.add(new String[]{"V", "Vasanthi", "0.09632224168126094"});
		expected.add(new String[]{"Harrison", "Miranda", "0.0"});
		expected.add(new String[]{"M", "ShakeelAhamed", "0.0"});
		expected.add(new String[]{"Dayal", "Rakesh", "0.0"});
		expected.add(new String[]{"Gallo", "Joseph", "0.0"});
		expected.add(new String[]{"Bera", "Sanchita", "0.0"});
		expected.add(new String[]{"Chiarella", "Samuel", "0.0"});
		expected.add(new String[]{"Baic", "Jessica", "0.0"});
		expected.add(new String[]{"Correya", "Avalon", "0.0"});
		expected.add(new String[]{"Penner", "William", "0.0"});
		expected.add(new String[]{"Wainwright", "Derek", "0.0"});
		expected.add(new String[]{"Vasta", "Rocco", "0.0"});
		expected.add(new String[]{"LeClair", "Wendy", "0.0"});
		expected.add(new String[]{"Saha", "Sushmita", "0.0"});
		expected.add(new String[]{"S", "Vijay", "0.0"});
		expected.add(new String[]{"Yukich", "Anthony", "0.0"});
		expected.add(new String[]{"Day", "Heather", "0.0"});
		expected.add(new String[]{"Riley", "Anthony", "0.0"});
		expected.add(new String[]{"Gibson", "Natasha", "0.0"});
		expected.add(new String[]{"Kharsahnoh K", "Artisha", "0.0"});
		expected.add(new String[]{"Hickey", "Carlos", "0.0"});
		expected.add(new String[]{"G", "Meenatchi", "0.0"});
		expected.add(new String[]{"Morrison", "Tym", "0.0"});
		expected.add(new String[]{"Roach", "Lorraine", "0.0"});
		expected.add(new String[]{"K", "Gopinath", "0.0"});
		expected.add(new String[]{"Maashook", "Mohamed", "0.0"});
		expected.add(new String[]{"Shekem", "Shatda", "0.09696458684654301"});
		expected.add(new String[]{"Ekka", "MickyPriyanka", "0.0"});
		expected.add(new String[]{"Blais", "Nancy", "0.0"});
		expected.add(new String[]{"A", "Sridevi", "0.0"});
		expected.add(new String[]{"Blackmon", "Marvin", "0.0"});
		expected.add(new String[]{"Bennett", "Christopher", "0.0"});
		expected.add(new String[]{"Dhayan", "Lenin", "0.0"});
		expected.add(new String[]{"Cerame", "Robert", "0.0"});
		expected.add(new String[]{"LiButti", "Anthony", "0.0"});
		expected.add(new String[]{"Vari", "Joseph", "0.0"});
		expected.add(new String[]{"Blackstock", "Michael", "0.0"});
		expected.add(new String[]{"Gagnon", "Leslie", "0.0"});
		expected.add(new String[]{"S", "Gowtham", "0.10869565217391304"});
		expected.add(new String[]{"Castillo", "Norely", "0.0"});
		expected.add(new String[]{"Hagan", "Natasha", "0.0"});
		expected.add(new String[]{"Xavier", "John", "0.0"});
		expected.add(new String[]{"Theriault", "David", "0.0"});
		expected.add(new String[]{"Davis", "Joshua", "0.0"});
		expected.add(new String[]{"Johnston", "Zack", "0.07575757575757576"});
		expected.add(new String[]{"Brewington", "Charles", "0.10150375939849623"});
		expected.add(new String[]{"Robinson", "Jason", "0.0"});
		expected.add(new String[]{"Bellomio", "Phillip", "0.0"});
		expected.add(new String[]{"Morse", "Robert", "0.0"});
		expected.add(new String[]{"Gates", "Clayton", "0.08141592920353982"});
		expected.add(new String[]{"Haas", "Don", "0.0"});
		expected.add(new String[]{"Samuel", "Angelyn", "0.0"});
		expected.add(new String[]{"G", "MarcusDominic", "0.0"});
		expected.add(new String[]{"Dearing", "Lori", "0.0"});
		expected.add(new String[]{"Thompson", "Paul", "0.08616504854368932"});
		expected.add(new String[]{"Scialpa", "Marie", "0.0"});
		expected.add(new String[]{"Goodchild", "Nicholas", "0.0"});
		expected.add(new String[]{"Tyszko", "Michael", "0.0"});
		expected.add(new String[]{"Smith", "Terry", "0.0"});
		expected.add(new String[]{"Theriault", "Dave", "0.0"});
		expected.add(new String[]{"Robert", "Emmanuel", "0.07890222984562607"});
		expected.add(new String[]{"F", "Fathima Jayaselvi", "0.0"});
		expected.add(new String[]{"D", "Gopinath", "0.0"});
		expected.add(new String[]{"K", "Vinothini", "0.06761565836298933"});
		expected.add(new String[]{"JinSuen", "Timothy", "0.0"});
		expected.add(new String[]{"Raja", "DharmarajM", "0.08271236959761549"});
		expected.add(new String[]{"Peters", "Erika", "0.0"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testTeamStackReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, Conversion.TEAM_STACK_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"ROCJFS Sales Team", "0.09058500914076782"});
		expected.add(new String[]{"Sales-Associates", "0.0"});
		expected.add(new String[]{"CHNDLF-New Hire Tech Team", "0.031192660550458717"});
		expected.add(new String[]{"Acer Voice - DLF - Kalpesh", "0.0"});
		expected.add(new String[]{"Acer US Voice - TLs", "0.0"});
		expected.add(new String[]{"ROCJFS Save Team", "0.0"});
		expected.add(new String[]{"SYRGAL Sales Team", "0.0"});
		expected.add(new String[]{"CHNDLF Sales Team", "0.08875784959411855"});
		expected.add(new String[]{"Acer US Voice PM", "0.0"});
		expected.add(new String[]{"ROCJFS-Sales New Hire", "0.0"});
		expected.add(new String[]{"Retention Team", "0.0"});

		testOutput(expected, report.startReport());
	}

}
