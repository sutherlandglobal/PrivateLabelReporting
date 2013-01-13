/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.IVRCSAT;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class IVRCSATTest extends ReportOutputTest
{
	private IVRCSAT report;

	public void setUp()
	{
		try 
		{
			report = new IVRCSAT();
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

		report.setParameter(Report.REPORT_TYPE_PARAM, IVRCSAT.TEAM_TIME_REPORT);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();



		expected.add(new String[]{"2011-10-14", "120", "142", "84.50704225352112"});
		expected.add(new String[]{"2011-10-15", "95", "110", "86.36363636363636"});
		expected.add(new String[]{"2011-10-12", "114", "133", "85.71428571428571"});
		expected.add(new String[]{"2011-10-13", "150", "160", "93.75"});
		expected.add(new String[]{"2011-10-18", "117", "133", "87.96992481203007"});
		expected.add(new String[]{"2011-10-19", "118", "138", "85.5072463768116"});
		expected.add(new String[]{"2011-10-16", "77", "83", "92.7710843373494"});
		expected.add(new String[]{"2011-10-17", "122", "136", "89.70588235294117"});
		expected.add(new String[]{"2011-10-22", "93", "97", "95.87628865979381"});
		expected.add(new String[]{"2011-10-21", "123", "132", "93.18181818181817"});
		expected.add(new String[]{"2011-10-20", "122", "135", "90.37037037037037"});
		expected.add(new String[]{"2011-10-01", "92", "105", "87.61904761904762"});
		expected.add(new String[]{"2011-10-02", "75", "90", "83.33333333333334"});
		expected.add(new String[]{"2011-10-30", "83", "97", "85.56701030927834"});
		expected.add(new String[]{"2011-10-03", "104", "118", "88.13559322033898"});
		expected.add(new String[]{"2011-10-31", "97", "109", "88.9908256880734"});
		expected.add(new String[]{"2011-10-04", "93", "103", "90.29126213592234"});
		expected.add(new String[]{"2011-10-05", "71", "83", "85.54216867469879"});
		expected.add(new String[]{"2011-10-06", "135", "155", "87.09677419354838"});
		expected.add(new String[]{"2011-10-07", "140", "159", "88.0503144654088"});
		expected.add(new String[]{"2011-10-08", "66", "70", "94.28571428571428"});
		expected.add(new String[]{"2011-10-09", "62", "80", "77.5"});
		expected.add(new String[]{"2011-10-29", "86", "99", "86.86868686868688"});
		expected.add(new String[]{"2011-10-28", "105", "115", "91.30434782608695"});
		expected.add(new String[]{"2011-10-27", "97", "108", "89.81481481481481"});
		expected.add(new String[]{"2011-10-26", "95", "109", "87.1559633027523"});
		expected.add(new String[]{"2011-10-25", "115", "127", "90.5511811023622"});
		expected.add(new String[]{"2011-10-11", "111", "126", "88.09523809523809"});
		expected.add(new String[]{"2011-10-24", "122", "135", "90.37037037037037"});
		expected.add(new String[]{"2011-10-10", "128", "138", "92.7536231884058"});
		expected.add(new String[]{"2011-10-23", "80", "87", "91.95402298850574"});





		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, "" + IVRCSAT.AGENT_TIME_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.AGENT_NAME_PARAM, "Zioto, Andrew");

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-14", "5", "6", "83.33333333333334"});
		expected.add(new String[]{"2011-10-15", "1", "1", "100.0"});
		expected.add(new String[]{"2011-10-03", "4", "4", "100.0"});
		expected.add(new String[]{"2011-10-12", "3", "3", "100.0"});
		expected.add(new String[]{"2011-10-04", "2", "2", "100.0"});
		expected.add(new String[]{"2011-10-13", "6", "7", "85.71428571428571"});
		expected.add(new String[]{"2011-10-18", "4", "4", "100.0"});
		expected.add(new String[]{"2011-10-05", "1", "1", "100.0"});
		expected.add(new String[]{"2011-10-19", "4", "5", "80.0"});
		expected.add(new String[]{"2011-10-06", "3", "3", "100.0"});
		expected.add(new String[]{"2011-10-07", "4", "5", "80.0"});
		expected.add(new String[]{"2011-10-17", "6", "6", "100.0"});
		expected.add(new String[]{"2011-10-21", "3", "3", "100.0"});
		expected.add(new String[]{"2011-10-11", "1", "1", "100.0"});
		expected.add(new String[]{"2011-10-20", "3", "3", "100.0"});
		expected.add(new String[]{"2011-10-10", "3", "3", "100.0"});



		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentStackReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, IVRCSAT.AGENT_STACK_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"Sager", "Jason", "0.0"});
		expected.add(new String[]{"N", "KiranKumar", "90.9090909090909"});
		expected.add(new String[]{"Sprague", "Diane", "0.0"});
		expected.add(new String[]{"Kumar Mishra", "Saket", "0.0"});
		expected.add(new String[]{"Moreau", "Carole", "0.0"});
		expected.add(new String[]{"Jebaraj S", "Augustin", "0.0"});
		expected.add(new String[]{"Mitkong", "Omem", "0.0"});
		expected.add(new String[]{"Carter", "Julie", "0.0"});
		expected.add(new String[]{"Vernelli", "Diana", "0.0"});
		expected.add(new String[]{"Vanderstyne", "Win", "0.0"});
		expected.add(new String[]{"K", "Vayapparaj", "62.5"});
		expected.add(new String[]{"SalimullaKhan", "Hurmathulla Khan", "0.0"});
		expected.add(new String[]{"Belisle", "Richard", "0.0"});
		expected.add(new String[]{"Nair", "Rajeev", "93.54838709677419"});
		expected.add(new String[]{"K", "AbhilashA", "0.0"});
		expected.add(new String[]{"V", "Pradeepa", "0.0"});
		expected.add(new String[]{"R", "Sudhakar", "76.71232876712328"});
		expected.add(new String[]{"N", "Rajasekaran", "0.0"});
		expected.add(new String[]{"KumarC", "Sathish", "90.0"});
		expected.add(new String[]{"S", "Goutham", "0.0"});
		expected.add(new String[]{"KumarP", "Vinoth", "91.42857142857143"});
		expected.add(new String[]{"Subbiah M", "Prasath", "92.5925925925926"});
		expected.add(new String[]{"Laird", "Hugh", "0.0"});
		expected.add(new String[]{"D", "Vishal", "0.0"});
		expected.add(new String[]{"Hyonsik", "Andy", "81.35593220338984"});
		expected.add(new String[]{"Cobb", "Richard", "93.10344827586206"});
		expected.add(new String[]{"K", "Sankaranarayanan", "93.75"});
		expected.add(new String[]{"Baxter", "Linda", "0.0"});
		expected.add(new String[]{"DilliRao", "Subramania", "0.0"});
		expected.add(new String[]{"T", "Parthiban", "0.0"});
		expected.add(new String[]{"D", "Sudheer", "0.0"});
		expected.add(new String[]{"Carney", "David", "0.0"});
		expected.add(new String[]{"N", "SriRam", "0.0"});
		expected.add(new String[]{"Kuhn", "Andrew", "86.95652173913044"});
		expected.add(new String[]{"Seetharaman", "Srividhya", "0.0"});
		expected.add(new String[]{"Gupta", "Sanjay", "0.0"});
		expected.add(new String[]{"K", "Palaniswamy", "91.66666666666666"});
		expected.add(new String[]{"Kumar P", "Dinesh", "88.75"});
		expected.add(new String[]{"Philbert", "Leander Wenseslaws", "79.54545454545455"});
		expected.add(new String[]{"St. John", "Derrek", "0.0"});
		expected.add(new String[]{"Whalley", "Jason", "0.0"});
		expected.add(new String[]{"Anand", "Vijay", "91.04477611940298"});
		expected.add(new String[]{"Kumar N", "Vinay", "84.81012658227847"});
		expected.add(new String[]{"T", "Sujatha", "0.0"});
		expected.add(new String[]{"Straughter", "Stanley", "0.0"});
		expected.add(new String[]{"Stanley", "James", "78.84615384615384"});
		expected.add(new String[]{"KumarUppaluri", "Pradeep", "0.0"});
		expected.add(new String[]{"Johnson", "Steven", "0.0"});
		expected.add(new String[]{"Ram", "Sanjai", "0.0"});
		expected.add(new String[]{"G", "Soundarajan", "92.3076923076923"});
		expected.add(new String[]{"Sawyer", "Carol", "0.0"});
		expected.add(new String[]{"McAfee", "Thomas", "0.0"});
		expected.add(new String[]{"S", "TamilSelvam", "0.0"});
		expected.add(new String[]{"Davidson", "John", "0.0"});
		expected.add(new String[]{"Mohammad", "Manzoor Ahmed", "0.0"});
		expected.add(new String[]{"Samad", "MDAbdus", "0.0"});
		expected.add(new String[]{"Klopf", "Mark", "0.0"});
		expected.add(new String[]{"Hussain", "Javed", "0.0"});
		expected.add(new String[]{"Arulanandu", "Sagayaraj", "0.0"});
		expected.add(new String[]{"Fouquet", "Todd", "88.88888888888889"});
		expected.add(new String[]{"K", "Subramani", "77.27272727272727"});
		expected.add(new String[]{"SS", "Deepthi", "0.0"});
		expected.add(new String[]{"Emanuel", "Jerry", "0.0"});
		expected.add(new String[]{"Lal", "Selwyn", "0.0"});
		expected.add(new String[]{"Katz", "Richard", "84.44444444444444"});
		expected.add(new String[]{"S", "Mohamed Azarudeen", "0.0"});
		expected.add(new String[]{"S", "Pradeep", "0.0"});
		expected.add(new String[]{"Kumar", "Manoj", "0.0"});
		expected.add(new String[]{"Reddy", "P.Manjunath", "0.0"});
		expected.add(new String[]{"Wendt", "Ryan", "0.0"});
		expected.add(new String[]{"VictorThomasA", "Ernest", "0.0"});
		expected.add(new String[]{"Andersen", "Aleen", "0.0"});
		expected.add(new String[]{"Raj P", "Kamal", "0.0"});
		expected.add(new String[]{"Zioto", "Andrew", "92.98245614035088"});
		expected.add(new String[]{"Baughman", "Keith", "0.0"});
		expected.add(new String[]{"M", "Arun", "0.0"});
		expected.add(new String[]{"Surana", "Kalpesh", "0.0"});
		expected.add(new String[]{"Eckert", "Dennis", "94.64285714285714"});
		expected.add(new String[]{"Danforth", "David", "0.0"});
		expected.add(new String[]{"Obenauer", "Adam", "0.0"});
		expected.add(new String[]{"C", "Rakesh", "71.42857142857143"});
		expected.add(new String[]{"Kandukuri", "Dharmendra", "0.0"});
		expected.add(new String[]{"B", "GokulaKrishnan", "0.0"});
		expected.add(new String[]{"Ahamed", "Riyaz", "0.0"});
		expected.add(new String[]{"Nazeeruddin", "Mohammed", "100.0"});
		expected.add(new String[]{"KumarS", "Kiran", "96.29629629629629"});
		expected.add(new String[]{"PNaik", "Preethi", "0.0"});
		expected.add(new String[]{"S", "Jayan", "0.0"});
		expected.add(new String[]{"S", "RaviBhardwaj", "0.0"});
		expected.add(new String[]{"Dalke", "Dustin C", "0.0"});
		expected.add(new String[]{"Yaseen", "Mohammed", "0.0"});
		expected.add(new String[]{"Davidson", "Christopher", "0.0"});
		expected.add(new String[]{"Selvan J", "Tamil", "90.74074074074075"});
		expected.add(new String[]{"G", "ThirupuraSundari", "0.0"});
		expected.add(new String[]{"Black", "Christopher", "0.0"});
		expected.add(new String[]{"Rockefeller", "Donald", "0.0"});
		expected.add(new String[]{"Durand", "Jason", "90.9090909090909"});
		expected.add(new String[]{"A", "Sudhaker", "0.0"});
		expected.add(new String[]{"S", "Sivaramakrishnan", "0.0"});
		expected.add(new String[]{"Perez", "Adam", "91.01123595505618"});
		expected.add(new String[]{"PrasadC", "Hari", "81.63265306122449"});
		expected.add(new String[]{"H", "Bhavesh", "0.0"});
		expected.add(new String[]{"User", "Joe", "0.0"});
		expected.add(new String[]{"Rao", "Rukmangatha", "90.19607843137256"});
		expected.add(new String[]{"Hernandez", "Ledwing", "0.0"});
		expected.add(new String[]{"Nelson", "Chantelle", "0.0"});
		expected.add(new String[]{"D", "Manoj", "88.05970149253731"});
		expected.add(new String[]{"LaRue", "Daniel", "84.70588235294117"});
		expected.add(new String[]{"Vail", "Thomas", "0.0"});
		expected.add(new String[]{"VanOcker", "Dale", "0.0"});
		expected.add(new String[]{"R", "Shreyas", "87.17948717948718"});
		expected.add(new String[]{"Istvan", "Matthew", "0.0"});
		expected.add(new String[]{"N", "Subha", "88.63636363636364"});
		expected.add(new String[]{"Porter", "Bruce", "0.0"});
		expected.add(new String[]{"Love", "Derrick", "0.0"});
		expected.add(new String[]{"Guru", "Irudayarajprakash", "0.0"});
		expected.add(new String[]{"G", "Abhilash", "0.0"});
		expected.add(new String[]{"Griffin", "Jason", "0.0"});
		expected.add(new String[]{"Comstock", "Bret", "93.54838709677419"});
		expected.add(new String[]{"M", "Jackson", "0.0"});
		expected.add(new String[]{"Vidjan", "Francis", "0.0"});
		expected.add(new String[]{"R", "Vandana", "90.74074074074075"});
		expected.add(new String[]{"A", "Vijay", "91.17647058823529"});
		expected.add(new String[]{"Vij", "Nitin", "100.0"});
		expected.add(new String[]{"Filarowski", "Robert", "94.82758620689656"});
		expected.add(new String[]{"h", "Royson", "81.9672131147541"});
		expected.add(new String[]{"McBride", "Bonnie", "0.0"});
		expected.add(new String[]{"P", "Mukesh", "0.0"});
		expected.add(new String[]{"V", "GangeshRaja", "0.0"});
		expected.add(new String[]{"Slaughter", "Gary", "0.0"});
		expected.add(new String[]{"N", "Vijayaraghavan", "0.0"});
		expected.add(new String[]{"King", "Richard", "0.0"});
		expected.add(new String[]{"Seelan", "Guna", "87.5"});
		expected.add(new String[]{"Cocozzelli", "Daniel", "0.0"});
		expected.add(new String[]{"Purcell", "Patrick", "0.0"});
		expected.add(new String[]{"Pethybridge", "Ryan", "0.0"});
		expected.add(new String[]{"Cutitta", "Brandon", "93.24324324324324"});
		expected.add(new String[]{"S", "SanthoshPrabhu", "0.0"});
		expected.add(new String[]{"RajK", "Mohan", "93.10344827586206"});
		expected.add(new String[]{"FlintAbbott", "Maximo", "0.0"});
		expected.add(new String[]{"K", "Prabitha", "0.0"});
		expected.add(new String[]{"A", "Sudhakar", "0.0"});
		expected.add(new String[]{"SharadKumarJawandhiya", "Rita", "93.61702127659575"});
		expected.add(new String[]{"A", "Ilavazhagan", "0.0"});
		expected.add(new String[]{"Cruz", "Edgar", "0.0"});
		expected.add(new String[]{"Domnic", "Maria", "86.36363636363636"});
		expected.add(new String[]{"Dear", "Trevor", "0.0"});
		expected.add(new String[]{"Mohammed K", "Arabu", "0.0"});
		expected.add(new String[]{"Ekambaram", "Dayalan", "92.45283018867924"});
		expected.add(new String[]{"Wilson", "Vijay", "87.3015873015873"});
		expected.add(new String[]{"Dushia", "Jai", "0.0"});
		expected.add(new String[]{"S", "Amith", "0.0"});
		expected.add(new String[]{"Bray", "George", "0.0"});
		expected.add(new String[]{"KumarR", "Kiran", "0.0"});
		expected.add(new String[]{"V", "LakshmiChaitanya", "0.0"});
		expected.add(new String[]{"V", "Karthikeyan", "0.0"});
		expected.add(new String[]{"Hibbard", "Richard", "0.0"});
		expected.add(new String[]{"Holmes", "Doug", "94.87179487179486"});
		expected.add(new String[]{"Patel", "Ganesh", "0.0"});
		expected.add(new String[]{"J Duncan", "Dave", "0.0"});
		expected.add(new String[]{"Weiland", "Christopher", "0.0"});
		expected.add(new String[]{"G", "Remya", "0.0"});
		expected.add(new String[]{"DeWert", "Elizabeth", "0.0"});
		expected.add(new String[]{"S", "Ganesan", "0.0"});
		expected.add(new String[]{"Ali", "Arshad", "90.0"});
		expected.add(new String[]{"A", "Divya", "0.0"});
		expected.add(new String[]{"Bramadasam Kumaraswamy", "Sunderraman", "0.0"});
		expected.add(new String[]{"Eberstein", "Mark", "82.6086956521739"});
		expected.add(new String[]{"Foglia", "Pietro", "0.0"});
		expected.add(new String[]{"D", "SureshKumar", "0.0"});
		expected.add(new String[]{"S", "Fajardeen", "91.48936170212765"});
		expected.add(new String[]{"Elzey", "James", "0.0"});
		expected.add(new String[]{"Dickinson", "Jeffrey", "0.0"});
		expected.add(new String[]{"Fleisher", "Evan", "88.37209302325581"});
		expected.add(new String[]{"Amitesh", "Kumar", "0.0"});
		expected.add(new String[]{"B", "Nathanraj", "83.05084745762711"});
		expected.add(new String[]{"Reedy", "John", "78.87323943661971"});
		expected.add(new String[]{"J", "Rathish", "98.0"});
		expected.add(new String[]{"B", "Subhashini", "0.0"});
		expected.add(new String[]{"NPauldurai", "KavitaP", "0.0"});
		expected.add(new String[]{"S - Acer", "Vivekananth", "0.0"});
		expected.add(new String[]{"Thomas", "Marc", "0.0"});
		expected.add(new String[]{"Kralovic", "Michael", "0.0"});
		expected.add(new String[]{"S", "SharathKiran", "0.0"});
		expected.add(new String[]{"G", "Md Ayjaz", "0.0"});
		expected.add(new String[]{"JD", "Lejo", "90.0"});
		expected.add(new String[]{"Sekar", "Vijay", "0.0"});
		expected.add(new String[]{"James", "Marshal P M", "0.0"});
		expected.add(new String[]{"P", "Chandrasekar", "0.0"});
		expected.add(new String[]{"Ras", "Larry", "0.0"});
		expected.add(new String[]{"Ahmed", "Thoufail", "85.24590163934425"});
		expected.add(new String[]{"S", "Reagan", "86.53846153846155"});
		expected.add(new String[]{"P", "Gordan", "89.74358974358975"});
		expected.add(new String[]{"NarayananV", "Srinivasa", "0.0"});
		expected.add(new String[]{"Matos", "Wyatt", "0.0"});
		expected.add(new String[]{"KumarS", "Ranjith", "93.67088607594937"});
		expected.add(new String[]{"Sauve", "Cody", "0.0"});
		expected.add(new String[]{"Baldwin", "Rosemary", "0.0"});
		expected.add(new String[]{"W", "Prabakaran", "0.0"});
		expected.add(new String[]{"Santineer", "Herbert George", "0.0"});
		expected.add(new String[]{"Silva", "David", "90.69767441860465"});
		expected.add(new String[]{"Justice", "Kevin", "90.38461538461539"});
		expected.add(new String[]{"Badsha", "Md Jeelani", "0.0"});
		expected.add(new String[]{"R", "Srinivasan", "87.71929824561403"});
		expected.add(new String[]{"SPR", "RamPrakash", "89.0909090909091"});
		expected.add(new String[]{"D", "Sridharan", "0.0"});
		expected.add(new String[]{"R", "Niranjan", "0.0"});
		expected.add(new String[]{"Prabhu R", "Jason", "91.8918918918919"});



		testOutput(expected, report.startReport());
	}

	@Test
	public void testTeamStackReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, IVRCSAT.TEAM_STACK_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"CHNDLF Tech Team 1", "1251.397927996285"});
		expected.add(new String[]{"CHNDLF-New Hire Tech Team", "705.9706505675053"});
		expected.add(new String[]{"CHNDLF Tech Team 3", "853.4770957345585"});
		expected.add(new String[]{"Tech-Associates", "0.0"});
		expected.add(new String[]{"CHNDLF Tech Team 2", "1160.8267274922252"});
		expected.add(new String[]{"Acer US Voice PM", "0.0"});
		expected.add(new String[]{"ACER US Voice - TMs", "0.0"});
		expected.add(new String[]{"ROCJFS Tech Team 1", "1162.3614083838688"});
		expected.add(new String[]{"ACER US Voice DLF- SMEs", "0.0"});
		expected.add(new String[]{"Acer Voice - DLF - Kalpesh", "0.0"});
		expected.add(new String[]{"Acer US Voice - TLs", "0.0"});
		expected.add(new String[]{"ROCJFS-Tech New Hire", "712.9128328482756"});
		expected.add(new String[]{"Retention Team", "0.0"});


		testOutput(expected, report.startReport());
	}

}
