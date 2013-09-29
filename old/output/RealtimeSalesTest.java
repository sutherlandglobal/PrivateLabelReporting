/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.RealtimeSales;
import report.SQL.Roster;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class RealtimeSalesTest extends ReportOutputTest
{
	private RealtimeSales realtimeSalesReport;

	public void setUp()
	{
		try 
		{
			realtimeSalesReport = new RealtimeSales();
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build Realtime Sales report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(realtimeSalesReport != null)
		{
			realtimeSalesReport.close();
		}
	}

	@Test
	public void testTeamReport()
	{
		realtimeSalesReport.setParameter(Report.REPORT_TYPE_PARAM, "" + RealtimeSales.TEAM_STACK_REPORT);

		realtimeSalesReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SALES_ROSTER);

		realtimeSalesReport.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		realtimeSalesReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"ROCJFS Sales Team", "137100.19000000172"});
		expected.add(new String[]{"CHNDLF-New Hire Tech Team", "1449.8300000000002"});
		expected.add(new String[]{"CHNDLF Sales Team", "167288.4099999997"});


		testOutput(expected, realtimeSalesReport.startReport());
	}

	@Test
	public void testAgentReport()
	{
		realtimeSalesReport.setParameter(Report.REPORT_TYPE_PARAM, "" + RealtimeSales.AGENT_STACK_REPORT);

		realtimeSalesReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.SALES_ROSTER);

		realtimeSalesReport.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		realtimeSalesReport.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"Davis", "Jamal", "0.0", "ROCJFS Sales Team"});
		expected.add(new String[]{"Blackmon", "Marvin", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Theriault", "Dave", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Blais", "Nancy", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Gallo", "Joseph", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Washington", "Latasha", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Iverson", "Jerry", "0.0", "Sales-Associates"});
		expected.add(new String[]{"S", "Prakash", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Giddens", "Ometries", "8799.33999999999", "ROCJFS Sales Team"});
		expected.add(new String[]{"Smith", "Russell", "9599.229999999987", "ROCJFS Sales Team"});
		expected.add(new String[]{"S", "MuthuKrishnan", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Washington", "Dorie", "0.0", "ROCJFS Save Team"});
		expected.add(new String[]{"Vandersluis", "Corey", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Robinson", "Anthony", "9349.309999999989", "ROCJFS Sales Team"});
		expected.add(new String[]{"Washington", "Korea", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Carter", "Janice", "10749.239999999987", "ROCJFS Sales Team"});
		expected.add(new String[]{"Shoemaker", "Jared", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Tyszko", "Michael", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Belluccio", "Nicholas", "9329.309999999989", "ROCJFS Sales Team"});
		expected.add(new String[]{"Morse", "Robert", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Riley", "Anthony", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Males", "Karen", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"S", "Vivekananth", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Lumia", "Vincent", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Goessl", "Laurie", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Edmondson", "Magnus", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Deo", "Michelle", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Vari", "Joseph", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Gibson", "Natasha", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Falls", "Donna", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Chiarella", "Samuel", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Wainwright", "Derek", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Baic", "Jessica", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Blackstock", "Michael", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Krishna", "Anu", "0.0", "Retention Team"});
		expected.add(new String[]{"Belanger", "Johnathan", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Castillo", "Norely", "0.0", "Sales-Associates"});
		expected.add(new String[]{"O'Neill", "Amy", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Dearing", "Lori", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Pollack", "Joel", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Fortin", "Rachelle", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Stadninsky", "Dean", "0.0", "Sales-Associates"});
		expected.add(new String[]{"KempReynolds", "Trevor", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Morrison", "Tym", "0.0", "Sales-Associates"});
		expected.add(new String[]{"McGuire", "James", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Raghunath P", "Shravan", "8799.439999999991", "CHNDLF Sales Team"});
		expected.add(new String[]{"Brandon", "Latonya", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Davis", "Joshua", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Dayal", "Rakesh", "0.0", "SYRGAL Sales Team"});
		expected.add(new String[]{"Strachan", "Andrea", "0.0", "Sales-Associates"});
		expected.add(new String[]{"VLN", "Kumar", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"S", "Vijay", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Narayanan S", "Sathya", "199.99", "CHNDLF Sales Team"});
		expected.add(new String[]{"Bera", "Sanchita", "0.0", "CHNDLF Sales Team"});
		expected.add(new String[]{"Saha", "Sushmita", "0.0", "CHNDLF Sales Team"});
		expected.add(new String[]{"Shekem", "Shatda", "17548.85", "ROCJFS Sales Team"});
		expected.add(new String[]{"Scialpa", "Marie", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Brewington", "Charles", "10999.189999999986", "ROCJFS Sales Team"});
		expected.add(new String[]{"Popplewell", "Brian", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Vasta", "Rocco", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Dave", "Shyam", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Kruger", "Cory", "0.0", "ROCJFS-Sales New Hire"});
		expected.add(new String[]{"DeLeon", "Robert", "0.0", "ROCJFS-Sales New Hire"});
		expected.add(new String[]{"LiButti", "Anthony", "0.0", "ROCJFS-Sales New Hire"});
		expected.add(new String[]{"M", "Shyamnath", "12499.029999999984", "CHNDLF Sales Team"});
		expected.add(new String[]{"Robinson", "Jason", "0.0", "Sales-Associates"});
		expected.add(new String[]{"M", "ShakeelAhamed", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Litolff", "John", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Hagan", "Natasha", "0.0", "Sales-Associates"});
		expected.add(new String[]{"PG", "Vishwanathan", "0.0", "Acer US Voice PM"});
		expected.add(new String[]{"Johnston", "Zack", "8099.3999999999905", "ROCJFS Sales Team"});
		expected.add(new String[]{"BogaWilliams", "Benita", "0.0", "ROCJFS-Sales New Hire"});
		expected.add(new String[]{"Haas", "Don", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Wilson", "Carol", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Jensen", "Kate", "0.0", "Acer US Voice - TLs"});
		expected.add(new String[]{"Troidl", "Sylvia", "0.0", "ROCJFS Save Team"});
		expected.add(new String[]{"Wynn", "Peter", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Muniappan", "Hashini", "0.0", "Sales-Associates"});
		expected.add(new String[]{"LeBlance", "Eleanor", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Kumar Rudra", "Prabir", "299.98", "CHNDLF Sales Team"});
		expected.add(new String[]{"Ponvely", "Roseline", "0.0", "CHNDLF Sales Team"});
		expected.add(new String[]{"D", "Gopinath", "0.0", "Sales-Associates"});
		expected.add(new String[]{"K", "Vinothini", "11299.239999999987", "CHNDLF Sales Team"});
		expected.add(new String[]{"Mcevans", "Tyrone", "10049.329999999989", "CHNDLF Sales Team"});
		expected.add(new String[]{"N", "Promod", "17948.889999999996", "CHNDLF Sales Team"});
		expected.add(new String[]{"Samuel", "Angelyn", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Cerame", "Robert", "0.0", "Sales-Associates"});
		expected.add(new String[]{"George", "Jackson", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Rajan", "Divya", "6299.529999999993", "CHNDLF Sales Team"});
		expected.add(new String[]{"Bennett", "Christopher", "0.0", "Sales-Associates"});
		expected.add(new String[]{"L", "Chitra", "499.95000000000005", "CHNDLF-New Hire Tech Team"});
		expected.add(new String[]{"Xavier", "John", "0.0", "Sales-Associates"});
		expected.add(new String[]{"G", "Meenatchi", "0.0", "Sales-Associates"});
		expected.add(new String[]{"N", "Guruvayurappan", "7199.519999999993", "CHNDLF Sales Team"});
		expected.add(new String[]{"G", "Hariharan", "15548.92999999998", "CHNDLF Sales Team"});
		expected.add(new String[]{"Kharsahnoh K", "Artisha", "0.0", "Sales-Associates"});
		expected.add(new String[]{"D'Angelo", "Kathy", "0.0", "Sales-Associates"});
		expected.add(new String[]{"M", "Pradeep", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Bellomio", "Phillip", "0.0", "ROCJFS Sales Team"});
		expected.add(new String[]{"Wood", "Elizabeth", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Thompson", "Paul", "9479.289999999988", "ROCJFS Sales Team"});
		expected.add(new String[]{"Henry", "Jonathan", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Robert", "Emmanuel", "11899.079999999984", "CHNDLF Sales Team"});
		expected.add(new String[]{"Theriault", "David", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Robinson", "Elizabeth", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Gates", "Clayton", "12649.079999999984", "ROCJFS Sales Team"});
		expected.add(new String[]{"Paul", "Kayla", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Baker", "Michael", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Penner", "William", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Goodchild", "Nicholas", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Correya", "Avalon", "0.0", "Acer US Voice - TLs"});
		expected.add(new String[]{"Peters", "Erika", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Nicholas", "Orville", "15888.94999999998", "ROCJFS Sales Team"});
		expected.add(new String[]{"Roy", "Michael", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Roach", "Lorraine", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Delle Fave", "David", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Chapman", "FeliciaCandace", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Day", "Heather", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Barratt", "Desiree", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Peters", "Johnathan", "0.0", "Sales-Associates"});
		expected.add(new String[]{"N", "Sudarshan", "0.0", "Retention Team"});
		expected.add(new String[]{"Dhayan", "Lenin", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"V", "Lakshmi Narasimhan", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"B", "Karthick", "0.0", "Retention Team"});
		expected.add(new String[]{"K", "Gopinath", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Kniahnicki", "Julie", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Ekka", "MickyPriyanka", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"Maashook", "Mohamed", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"G", "MarcusDominic", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"J", "Senthil Kumar", "349.94", "CHNDLF-New Hire Tech Team"});
		expected.add(new String[]{"A", "Sridevi", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"V", "PraveenKumar", "0.0", "Acer Voice - DLF - Kalpesh"});
		expected.add(new String[]{"G", "Salman Ali", "9399.439999999991", "CHNDLF Sales Team"});
		expected.add(new String[]{"V", "Vasanthi", "8149.449999999992", "CHNDLF Sales Team"});
		expected.add(new String[]{"Bartholomew", "Shiobion", "14608.999999999982", "ROCJFS Sales Team"});
		expected.add(new String[]{"Reed", "Jamie", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Wong", "Kevin", "0.0", "Sales-Associates"});
		expected.add(new String[]{"JinSuen", "Timothy", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Donaldson", "Robert", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Harrison", "Miranda", "0.0", "Sales-Associates"});
		expected.add(new String[]{"LeClair", "Wendy", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Yukich", "Anthony", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Hickey", "Carlos", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Bonnell", "Lori", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Shah", "Mohammed", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Senior", "Herbert", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Smith", "Terry", "0.0", "Sales-Associates"});
		expected.add(new String[]{"Gagnon", "Leslie", "0.0", "Sales-Associates"});
		expected.add(new String[]{"M", "Balakuthalanathan", "599.94", "CHNDLF-New Hire Tech Team"});
		expected.add(new String[]{"Bharathy", "Satheesh", "11149.219999999987", "CHNDLF Sales Team"});
		expected.add(new String[]{"S", "Gowtham", "20298.450000000026", "CHNDLF Sales Team"});
		expected.add(new String[]{"Raja", "DharmarajM", "16248.88999999998", "CHNDLF Sales Team"});
		expected.add(new String[]{"Ward", "David", "0.0", "Sales-Associates"});
		expected.add(new String[]{"F", "Fathima Jayaselvi", "0.0", "Acer Voice - DLF - Kalpesh"});


		testOutput(expected, realtimeSalesReport.startReport());
	}
}
