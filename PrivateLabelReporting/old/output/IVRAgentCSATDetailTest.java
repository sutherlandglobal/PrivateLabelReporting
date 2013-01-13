/**
 * 
 */
package test.report.output;

import java.util.Vector;

import org.junit.Test;

import report.Report;
import report.SQL.IVRAgentCSATDetail;
import report.SQL.Roster;
import util.DateParser;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class IVRAgentCSATDetailTest extends ReportOutputTest
{
	private IVRAgentCSATDetail report;

	public void setUp()
	{
		try 
		{
			report = new IVRAgentCSATDetail();
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

		report.setParameter(Report.REPORT_TYPE_PARAM, IVRAgentCSATDetail.TEAM_TIME_REPORT);
		report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-02 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-02 12:14:43", "Cutitta", "Brandon", "2111111", "W", "Garrett", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:45:43", "RajK", "Mohan", "2111121", "S", "L Frank", "No", "Yes", "null", "Yes", "Yes", "Yes", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 17:34:58", "Prabhu R", "Jason", "", "S", "L Frank", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 14:19:16", "LaRue", "Daniel", "", "S", "L Frank", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 14:20:46", "Filarowski", "Robert", "", "C", "Fosse", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 10:52:29", "Fleisher", "Evan", "22211111", "C", "Fosse", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 12:13:28", "Eberstein", "Mark", "", "C", "Fosse", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 22:05:59", "Domnic", "Maria", "22111112", "M", "Pendleton", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 23:31:13", "JD", "Lejo", "2111111", "M", "Pendleton", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:47:24", "Vij", "Nitin", "2111111", "A", "Thomas", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 17:42:20", "Cobb", "Richard", "2111111", "A", "Thomas", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 09:23:19", "K", "Palaniswamy", "1111111", "R", "Liptchmote", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 14:17:47", "Eberstein", "Mark", "22111211", "R", "Liptchmote", "No", "No", "Yes", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:34:38", "Cutitta", "Brandon", "12211111", "R", "Liptchmote", "Yes", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 18:10:39", "Fleisher", "Evan", "1111111", "R", "Liptchmote", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:00:46", "LaRue", "Daniel", "111111", "M", "Polychronas", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-02 19:26:22", "R", "Shreyas", "", "C", "Ricks", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 00:45:12", "C", "Rakesh", "12211211", "J", "Howard", "Yes", "No", "No", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 01:07:06", "Comstock", "Bret", "22111111", "R", "Royer", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 01:29:00", "R", "Vandana", "2111111", "G", "S Mendoza", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:54:18", "Fouquet", "Todd", "1111111", "C", "Ouellette", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:02:10", "null", "1111", "D", "Williams", "Yes", "Yes", "null", "Yes", "Yes", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 17:33:27", "Ahmed", "Thoufail", "12222222", "j", "augustine", "Yes", "No", "No", "No", "No", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 16:03:30", "P", "Gordan", "", "h", "garrett", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 20:48:53", "JD", "Lejo", "2111221", "H", "Garrett", "No", "Yes", "null", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-02 15:25:19", "B", "Nathanraj", "22111222", "h", "garrett", "No", "No", "Yes", "Yes", "Yes", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 16:20:37", "A", "Vijay", "1111111", "g", "stump", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:32:57", "1244", "2111111", "T", "Ray", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:47:12", "RajK", "Mohan", "1111111", "j", "Causey", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:36:24", "K", "Palaniswamy", "", "r", "beeler", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 17:23:21", "KumarC", "Sathish", "1111111", "T", "Gordon", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 17:58:09", "null", "22111211", "j", "Causey", "No", "No", "Yes", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:19:37", "1244", "1111111", "R", "Walters", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:24:47", "Fleisher", "Evan", "12211111", "J", "Portmann", "Yes", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:40:24", "R", "Sudhakar", "", "D", "Cash", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 19:20:14", "JD", "Lejo", "1111111", "M", "Cardwell", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 09:14:34", "SharadKumarJawandhiya", "Rita", "2111111", "M", "Cardwell", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:15:45", "JD", "Lejo", "1111111", "M", "Frances", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:55:06", "Ahmed", "Thoufail", "1111111", "l", "maria", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 20:52:50", "K", "Vayapparaj", "1111111", "S", "Spirer", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:24:03", "KumarC", "Sathish", "1111111", "R", "Haynes", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:12:20", "null", "1111111", "M", "Reza", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 20:49:59", "PrasadC", "Hari", "1111111", "M", "Reza", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:10:38", "KumarP", "Vinoth", "12111111", "j", "rindt", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 20:44:09", "Prabhu R", "Jason", "12211111", "J", "Gilton", "Yes", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 23:10:50", "1163", "2111111", "W", "L Hill", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:48:07", "N", "Subha", "2111111", "W", "L Hill", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:40:48", "Anand", "Vijay", "", "W", "L Hill", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 01:39:32", "K", "Vayapparaj", "2", "E", "Ouellette", "No", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 19:53:12", "LaRue", "Daniel", "211111", "E", "Gallo", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 20:56:20", "R", "Shreyas", "", "M", "hamlett-cashgriffin", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 23:22:34", "Kuhn", "Andrew", "12211211", "M", "hamlett-cashgriffin", "Yes", "No", "No", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 21:47:18", "JD", "Lejo", "2111111", "D", "K Donaldson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 18:33:26", "A", "Vijay", "1111111", "f", "sauders", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 18:55:30", "B", "Nathanraj", "1111111", "M", "CANNARD", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 23:25:08", "Eckert", "Dennis", "", "K", "Kurtz", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 20:44:47", "Cutitta", "Brandon", "1111111", "K", "Kurtz", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 18:49:06", "Rao", "Rukmangatha", "1111111", "J", "Babis", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 19:04:24", "Nazeeruddin", "Mohammed", "1111111", "G", "Abonyi", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:18:05", "Fleisher", "Evan", "12111111", "S", "Daly", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 03:23:46", "Ahmed", "Thoufail", "22111111", "j", "Soran", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 02:21:07", "JD", "Lejo", "2", "j", "Soran", "No", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 11:37:56", "B", "Nathanraj", "", "J", "Soran", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 10:35:20", "P", "Gordan", "2111111", "L", "Grelle", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 20:46:39", "KumarC", "Sathish", "2111111", "P", "Enfield", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 10:31:19", "Fleisher", "Evan", "2111111", "E", "Kuni", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 22:37:31", "Comstock", "Bret", "2111111", "E", "Kuni", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 10:26:26", "K", "Subramani", "", "T", "Morris", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:09:06", "D", "Manoj", "", "T", "Morris", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 12:31:02", "B", "Nathanraj", "", "T", "Morris", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 14:13:08", "null", "", "T", "Morris", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:34:52", "Cutitta", "Brandon", "2111111", "M", "Gallagher", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 00:42:21", "Ekambaram", "Dayalan", "", "C", "Ricks", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 22:16:08", "SPR", "RamPrakash", "", "C", "Ricks", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 21:02:05", "1163", "22211222", "K", "Asimpi", "No", "No", "No", "Yes", "Yes", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 13:20:42", "Fleisher", "Evan", "2121121", "J", "Woods", "No", "Yes", "null", "No", "Yes", "Yes", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 14:57:10", "Eberstein", "Mark", "", "d", "gore-wilson", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 00:59:11", "Cobb", "Richard", "22121111", "S", "simpson", "No", "No", "Yes", "No", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 04:02:31", "SharadKumarJawandhiya", "Rita", "", "A", "Wilson", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 21:32:01", "PrasadC", "Hari", "12111112", "C", "Wiggins", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 16:07:48", "Fleisher", "Evan", "", "S", "Robertson", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:42:10", "R", "Sudhakar", "22122222", "p", "mcdermott", "No", "No", "Yes", "No", "No", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 16:13:31", "R", "Shreyas", "2111111", "B", "Holbrook", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:55:25", "KumarP", "Vinoth", "1111111", "v", "samvorsky", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 21:11:36", "PrasadC", "Hari", "", "a", "Peterson", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 22:35:45", "Fouquet", "Todd", "12111111", "a", "Peterson", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 11:41:00", "Eberstein", "Mark", "12111111", "a", "Peterson", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 21:35:48", "K", "Vayapparaj", "12111111", "a", "Peterson", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:05:32", "null", "1111111", "D", "Barda", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:56:35", "R", "Shreyas", "22111122", "C", "Edward", "No", "No", "Yes", "Yes", "Yes", "Yes", "No", "No"});
		expected.add(new String[]{"2011-10-01 18:55:23", "R", "Shreyas", "12111111", "M", "hoewer", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:40:32", "Domnic", "Maria", "22111111", "G", "M Cummin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:43:45", "Comstock", "Bret", "", "C", "McLellan", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 19:52:05", "R", "Shreyas", "2111122", "J", "Latchford", "No", "Yes", "null", "Yes", "Yes", "Yes", "No", "No"});
		expected.add(new String[]{"2011-10-01 18:42:13", "N", "Subha", "2111122", "J", "Latchford", "No", "Yes", "null", "Yes", "Yes", "Yes", "No", "No"});
		expected.add(new String[]{"2011-10-01 18:29:03", "Prabhu R", "Jason", "", "S", "Williams", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 18:47:48", "A", "Vijay", "", "K", "Allen", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 22:28:25", "Cobb", "Richard", "1", "M", "Ocean", "Yes", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 23:06:57", "Comstock", "Bret", "", "M", "Ocean", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:41:51", "Fleisher", "Evan", "12211222", "M", "Ocean", "Yes", "No", "No", "Yes", "Yes", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 19:47:35", "RajK", "Mohan", "1111111", "c", "devine", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:31:53", "Prabhu R", "Jason", "12111111", "J", "Marchese", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 21:51:32", "Fouquet", "Todd", "2111111", "J", "Marchese", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 20:07:40", "A", "Vijay", "2111111", "J", "Marchese", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:24:33", "Fouquet", "Todd", "211111", "J", "Jefferson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-02 14:03:16", "Vij", "Nitin", "111111", "J", "Jefferson", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 20:51:58", "JD", "Lejo", "1111111", "B", "Tsai", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:16:15", "1163", "1111111", "A", "Berkowipz", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:14:11", "A", "Vijay", "12211221", "j", "messer", "Yes", "No", "No", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 20:48:39", "1163", "", "C", "Baker", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 20:42:34", "Domnic", "Maria", "1111111", "V", "Cordier", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 21:29:49", "Prabhu R", "Jason", "1111111", "F", "Gambrell", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 22:22:14", "Ahmed", "Thoufail", "2111112", "A", "Richardson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 22:46:00", "null", "12111221", "N", "Guero", "Yes", "No", "Yes", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-02 00:02:52", "Fouquet", "Todd", "", "J", "Helin", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 00:12:56", "JD", "Lejo", "2111111", "D", "Rouge", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 17:33:41", "A", "Vijay", "", "M", "Rivera", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:08:33", "R", "Shreyas", "1111111", "P", "Red", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 19:06:48", "JD", "Lejo", "", "M", "Lymas", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 19:02:04", "Ahmed", "Thoufail", "22222222", "J", "Wilson", "No", "No", "No", "No", "No", "No", "No", "No"});
		expected.add(new String[]{"2011-10-02 19:30:41", "S", "Reagan", "22211212", "L", "Wilson Jr", "No", "No", "No", "Yes", "Yes", "No", "Yes", "No"});
		expected.add(new String[]{"2011-10-02 21:29:23", "null", "", "c", "joans", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 20:32:57", "PrasadC", "Hari", "12222222", "R", "Claudia", "Yes", "No", "No", "No", "No", "No", "No", "No"});
		expected.add(new String[]{"2011-10-02 23:47:52", "SPR", "RamPrakash", "12211211", "R", "Hershisdr", "Yes", "No", "No", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 21:30:47", "S", "Reagan", "12111111", "a", "campisi", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 21:26:51", "KumarS", "Kiran", "12211111", "R", "Swiess", "Yes", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 22:14:38", "1163", "12111112", "R", "Smith", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-02 23:01:37", "K", "Vayapparaj", "2111111", "M", "Pullard", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 23:50:39", "PrasadC", "Hari", "", "R", "Dedeke", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 12:17:03", "Eberstein", "Mark", "1111111", "C", "Krowczyk", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 13:21:44", "Filarowski", "Robert", "2111111", "J", "Montgomery", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:43:43", "N", "Subha", "2111111", "j", "Montgomery", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 06:06:59", "1265", "22211222", "D", "Olden", "No", "No", "No", "Yes", "Yes", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 07:47:04", "K", "Subramani", "22222222", "D", "Olden", "No", "No", "No", "No", "No", "No", "No", "No"});
		expected.add(new String[]{"2011-10-01 10:37:46", "Subbiah M", "Prasath", "2111111", "n", "noel", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:54:21", "RajK", "Mohan", "2111111", "G", "Tebbit", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 10:18:16", "Filarowski", "Robert", "22121111", "G", "Tebbit", "No", "No", "Yes", "No", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 12:19:15", "Vij", "Nitin", "22111111", "G", "Tebbit", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 13:38:33", "LaRue", "Daniel", "22211111", "G", "Tebbit", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 04:03:00", "Ahmed", "Thoufail", "", "B", "Starling", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 00:17:43", "K", "Vayapparaj", "211111", "T", "Morton", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 01:01:10", "R", "Vandana", "2111111", "S", "Sydney", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 05:53:21", "K", "Subramani", "", "J", "A Yetes", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 09:02:56", "P", "Gordan", "12111111", "C", "L Burns", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 08:59:19", "Subbiah M", "Prasath", "", "B", "McElwain", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 10:12:27", "1265", "1111111", "K", "Eickert", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 09:40:24", "D", "Manoj", "1111111", "A", "Brynt", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:07:59", "P", "Gordan", "1111111", "t", "mcenery", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:28:38", "R", "Sudhakar", "1111111", "a", "hightower", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:47:27", "J", "Rathish", "12111111", "a", "hightower", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 12:25:36", "Filarowski", "Robert", "", "P", "Towers", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 12:26:45", "R", "Sudhakar", "1111111", "R", "Robinson", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 12:37:03", "RajK", "Mohan", "1111111", "C", "Morin", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 12:40:47", "Subbiah M", "Prasath", "1111111", "J", "Howard", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 13:20:15", "R", "Sudhakar", "1111111", "S", "Rolins", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 17:08:26", "1244", "", "S", "Rolins", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 14:12:27", "K", "Palaniswamy", "", "s", "ewing", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 14:22:55", "null", "2212222", "J", "Granados", "No", "No", "Yes", "No", "No", "No", "No", "null"});
		expected.add(new String[]{"2011-10-02 19:17:31", "Eckert", "Dennis", "", "J", "Granados", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 21:23:06", "Eckert", "Dennis", "211111", "J", "Granados", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 14:51:44", "A", "Vijay", "1", "W", "Lewis", "Yes", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 14:42:19", "Subbiah M", "Prasath", "", "S", "Goldsdy", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:16:55", "P", "Gordan", "1111111", "I", "Archer", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:22:05", "LaRue", "Daniel", "12221221", "E", "Brecher", "Yes", "No", "No", "No", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 15:33:54", "Eberstein", "Mark", "12121111", "w", "ulrica", "Yes", "No", "Yes", "No", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:58:24", "J", "Rathish", "", "B", "Starling", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:04:54", "K", "Palaniswamy", "2111111", "M", "Rowland", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 17:02:35", "R", "Sudhakar", "2111111", "M", "Rowland", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 21:34:23", "Kuhn", "Andrew", "", "M", "Rowland", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:29:22", "Filarowski", "Robert", "22111111", "P", "Lipinski", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 01:18:29", "JD", "Lejo", "1111111", "A", "Delacruv", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 01:41:27", "1163", "1111111", "C", "Freeman", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 02:22:45", "PrasadC", "Hari", "", "C", "Freeman", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 08:11:04", "K", "Subramani", "1111111", "J", "Shelton", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 18:47:51", "Eckert", "Dennis", "22211111", "J", "Shelton", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 10:33:22", "Anand", "Vijay", "12211221", "R", "R Savage", "Yes", "No", "No", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-02 10:16:46", "1244", "12111111", "M", "Gonzalez", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 18:14:51", "JD", "Lejo", "1111111", "D", "Kohr", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 19:48:23", "RajK", "Mohan", "", "T", "Gonzales", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 19:20:20", "J", "Rathish", "1111111", "r", "iwelomen", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 19:07:11", "PrasadC", "Hari", "2111111", "k", "patterson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 20:35:02", "LaRue", "Daniel", "2111111", "k", "serivis", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 19:19:36", "R", "Sudhakar", "12222221", "M", "S Davis", "Yes", "No", "No", "No", "No", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-02 19:48:43", "KumarP", "Vinoth", "22211111", "a", "serio", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 23:48:06", "Ahmed", "Thoufail", "2111111", "C", "Blackwell", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 20:42:20", "Seelan", "Guna", "22111212", "C", "Blackwell", "No", "No", "Yes", "Yes", "Yes", "No", "Yes", "No"});
		expected.add(new String[]{"2011-10-02 21:09:14", "Nazeeruddin", "Mohammed", "2111111", "L", "Varvel", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 21:46:07", "KumarP", "Vinoth", "", "G", "M Cummin", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 22:31:35", "JD", "Lejo", "1111111", "C", "Norman", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 22:31:14", "R", "Shreyas", "1111111", "j", "talamante", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 23:08:46", "Nazeeruddin", "Mohammed", "1111111", "T", "Kidwell", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 23:11:55", "Rao", "Rukmangatha", "", "M", "Lenihan", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 22:57:37", "KumarS", "Kiran", "", "J", "Schendel", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 12:35:31", "Vij", "Nitin", "22111111", "N", "Zarlinga", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:46:32", "KumarC", "Sathish", "2111111", "N", "Zarlinga", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 20:39:57", "SPR", "RamPrakash", "22111111", "N", "Zarlinga", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 21:05:36", "LaRue", "Daniel", "2111111", "E", "Spoomer", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 08:37:20", "K", "Palaniswamy", "2111111", "S", "Sealscott", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 12:24:47", "P", "Gordan", "2111111", "M", "Rowland", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 10:20:05", "null", "2111121", "L", "Decoff", "No", "Yes", "null", "Yes", "Yes", "Yes", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 01:33:50", "Nazeeruddin", "Mohammed", "111111", "s", "pierce", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 10:52:32", "1265", "", "k", "lee", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 10:52:35", "1265", "", "k", "lee", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 02:19:04", "JD", "Lejo", "12111211", "k", "lee", "Yes", "No", "Yes", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 02:24:53", "C", "Rakesh", "12121221", "M", "Kuying", "Yes", "No", "Yes", "No", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 04:12:04", "1265", "2111112", "M", "Kuying", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 03:04:31", "Ekambaram", "Dayalan", "12111111", "l", "castiglioni", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 03:43:35", "K", "Subramani", "22211111", "l", "castiglioni", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 04:34:00", "1265", "", "C", "Mummelthie", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 06:40:04", "1265", "22111111", "J", "Bartolomeo", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:16:24", "J", "Rathish", "12111111", "J", "Bartolomeo", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 18:25:10", "Comstock", "Bret", "2111111", "C", "Deeble-Wilson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 08:47:07", "Eberstein", "Mark", "2111111", "C", "Deeble-Wilson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 22:15:47", "C", "Rakesh", "22221221", "M", "Harrington", "No", "No", "No", "No", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-01 09:46:04", "Subbiah M", "Prasath", "", "B", "Bennett", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 09:01:46", "K", "Palaniswamy", "12111111", "S", "Alles", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 10:12:47", "K", "Subramani", "1112111", "A", "Poole", "Yes", "Yes", "null", "Yes", "No", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:08:42", "K", "Palaniswamy", "2111111", "C", "Mart", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:26:41", "D", "Manoj", "1111111", "E", "Ramsey", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 13:29:40", "Subbiah M", "Prasath", "1111111", "E", "Ramsey", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:28:47", "R", "Sudhakar", "12111111", "S", "Merryman", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 13:24:45", "Eberstein", "Mark", "22211212", "J", "Pulbratek", "No", "No", "No", "Yes", "Yes", "No", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 13:28:41", "Vij", "Nitin", "22111111", "I", "Manderson", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 11:38:25", "Subbiah M", "Prasath", "12111111", "I", "Manderson", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:03:11", "A", "Vijay", "1111111", "I", "Manderson", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 13:01:27", "Fleisher", "Evan", "22111111", "B", "C Carpenter", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 17:12:29", "Cutitta", "Brandon", "", "j", "greenly", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:12:30", "1244", "", "h", "allison", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:56:00", "RajK", "Mohan", "", "h", "allison", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 12:26:15", "1265", "12111111", "L", "Denton", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:30:21", "K", "Palaniswamy", "2111111", "L", "Denton", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 12:17:06", "1244", "111111", "E", "houseman", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 12:17:12", "N", "Subha", "22121112", "R", "Woods", "No", "No", "Yes", "No", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 17:34:14", "J", "Rathish", "", "R", "Woods", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 13:42:23", "Fleisher", "Evan", "12121211", "t", "williams", "Yes", "No", "Yes", "No", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 19:39:14", "Kuhn", "Andrew", "", "t", "williams", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 11:08:26", "P", "Gordan", "1111111", "S", "Gabriele", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 11:36:52", "1244", "1111111", "J", "Dadyak", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 11:52:33", "Filarowski", "Robert", "12111111", "k", "manges", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 13:00:10", "Eberstein", "Mark", "22111111", "k", "manges", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:01:03", "Fouquet", "Todd", "", "k", "manges", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:06:40", "Fouquet", "Todd", "22111111", "k", "manges", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 21:46:40", "Cobb", "Richard", "22111111", "k", "manges", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:17:54", "Fleisher", "Evan", "22111111", "k", "manges", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 13:51:30", "R", "Sudhakar", "", "m", "cortez", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 11:29:34", "R", "Sudhakar", "22111111", "g", "bedard", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 11:49:52", "Anand", "Vijay", "", "C", "Mckeil", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 12:07:50", "D", "Manoj", "1111111", "P", "Kline", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 12:20:12", "Subbiah M", "Prasath", "1111111", "g", "jackson", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 12:26:03", "Cutitta", "Brandon", "", "A", "amie", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:20:49", "Eberstein", "Mark", "", "A", "amie", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 20:25:12", "Cobb", "Richard", "22111111", "A", "amie", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 14:49:39", "Filarowski", "Robert", "22111111", "A", "amie", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 17:11:22", "null", "22111111", "A", "amie", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 22:58:36", "S", "Reagan", "22111111", "A", "amie", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 13:03:04", "Anand", "Vijay", "", "w", "willis", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:20:04", "Anand", "Vijay", "", "w", "willis", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 12:47:26", "J", "Rathish", "", "R", "LaVine", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:45:26", "D", "Manoj", "12212211", "D", "Winkelman", "Yes", "No", "No", "Yes", "No", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 14:00:07", "J", "Rathish", "22111112", "D", "Winkelman", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-02 14:29:50", "KumarP", "Vinoth", "", "D", "Winkelman", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:24:37", "Filarowski", "Robert", "1111111", "D", "Wilson", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 14:53:01", "A", "Vijay", "2111111", "R", "Williams", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:15:07", "LaRue", "Daniel", "", "K", "Green", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 14:41:15", "Prabhu R", "Jason", "", "s", "melon", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:00:22", "LaRue", "Daniel", "", "S", "Miner", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:48:02", "R", "Shreyas", "1111111", "K", "Thornton", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:02:18", "Subbiah M", "Prasath", "1111111", "p", "marvel", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:00:00", "A", "Vijay", "", "K", "Holowka", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 16:42:45", "B", "Nathanraj", "", "K", "Holowka", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 19:31:05", "1163", "", "K", "Holowka", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 16:09:52", "B", "Nathanraj", "", "J", "Ross", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:29:58", "R", "Sudhakar", "", "J", "Ross", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 16:48:06", "P", "Gordan", "1111111", "l", "gongaliz", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:26:18", "Prabhu R", "Jason", "2211111", "k", "croley", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-02 17:37:58", "B", "Nathanraj", "2211111", "k", "croley", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-02 20:00:27", "JD", "Lejo", "22211211", "R", "Wilson", "No", "No", "No", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 17:28:48", "R", "Sudhakar", "22111111", "R", "Wilson", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:04:59", "P", "Gordan", "", "m", "taylor", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 14:02:37", "R", "Sudhakar", "1111111", "S", "Gilchrist", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:19:35", "Cutitta", "Brandon", "111111", "D", "Rutkowski", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-01 14:31:12", "1244", "1111112", "W", "gutierrez", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 15:41:15", "KumarP", "Vinoth", "1111111", "C", "Prince", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 16:08:08", "null", "1111111", "S", "oppenheim", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 14:44:35", "D", "Manoj", "22111112", "v", "smith", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 15:01:22", "Prabhu R", "Jason", "1111111", "E", "lentz", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-01 15:18:47", "R", "Shreyas", "22211222", "G", "Pabi", "No", "No", "No", "Yes", "Yes", "No", "No", "No"});
		expected.add(new String[]{"2011-10-02 01:18:13", "PrasadC", "Hari", "1111111", "s", "kulju", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:53:30", "Subbiah M", "Prasath", "", "M", "Kuyinu", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 03:15:07", "C", "Rakesh", "1111111", "M", "MacLaughin", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 04:24:38", "K", "Subramani", "22111111", "A", "A Paige", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 03:54:46", "PrasadC", "Hari", "", "A", "A Paige", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 09:02:36", "P", "Gordan", "", "J", "Estabrook", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 10:06:16", "D", "Manoj", "1111111", "L", "Yarussi", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 12:12:34", "1244", "1111111", "L", "Yarussi", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 10:48:05", "SharadKumarJawandhiya", "Rita", "2111111", "A", "Fennell", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 11:37:21", "K", "Subramani", "", "s", "boyd", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:32:39", "LaRue", "Daniel", "", "S", "Gabriele", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 22:25:08", "Fouquet", "Todd", "1111111", "S", "Gabriele", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 13:35:43", "RajK", "Mohan", "", "S", "Gabriele", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:53:01", "Fleisher", "Evan", "1111111", "s", "horn", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 12:52:23", "D", "Manoj", "1111111", "a", "shemanchuk", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:58:03", "Fleisher", "Evan", "2111111", "J", "Mathies", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:35:14", "1244", "1111111", "M", "Macdonald", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:54:46", "null", "12111111", "T", "Brinkman", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:08:20", "R", "Sudhakar", "12222222", "J", "WILLIAMS", "Yes", "No", "No", "No", "No", "No", "No", "No"});
		expected.add(new String[]{"2011-10-02 15:25:19", "P", "Gordan", "", "V", "Ghiselli", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 20:49:55", "Rao", "Rukmangatha", "", "V", "Ghiselli", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:18:16", "Anand", "Vijay", "", "J", "Eversen", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:41:25", "KumarP", "Vinoth", "2111111", "V", "Lyon", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:22:16", "Fouquet", "Todd", "12111111", "G", "gorge", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 15:50:18", "P", "Gordan", "", "m", "foote", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:00:54", "KumarP", "Vinoth", "22211112", "J", "GROHARING", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-02 18:45:13", "Cobb", "Richard", "22111111", "P", "Temoin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 21:10:18", "Fouquet", "Todd", "22111111", "P", "Temoin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 22:06:05", "Cobb", "Richard", "22111111", "P", "Temoin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 22:55:50", "Fouquet", "Todd", "22111111", "P", "Temoin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 16:47:43", "Anand", "Vijay", "", "V", "Williams", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:03:36", "Eckert", "Dennis", "", "W", "F Grayson", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 16:59:48", "Cobb", "Richard", "", "M", "Miller", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 17:22:40", "Prabhu R", "Jason", "2111111", "d", "thompson", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-02 17:33:53", "Cobb", "Richard", "12111212", "t", "Hurley", "Yes", "No", "Yes", "Yes", "Yes", "No", "Yes", "No"});
		expected.add(new String[]{"2011-10-01 05:04:34", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 11:56:49", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 13:16:39", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:29:29", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 15:50:34", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 21:17:16", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 17:58:14", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:02:25", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 23:07:48", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:52:17", "R", "Sudhakar", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 18:30:05", "R", "Sudhakar", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-01 23:42:39", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 12:39:29", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 13:17:25", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:52:07", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 15:55:10", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-02 16:17:46", "null", "", "", "", "null", "null", "null", "null", "null", "null", "null", "null"});


		testOutput(expected, report.startReport());
	}

	@Test
	public void testAgentTimeReport()
	{
		report.setParameter(Report.REPORT_TYPE_PARAM, "" + IVRAgentCSATDetail.AGENT_TIME_REPORT);
		report.setParameter(Report.TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

		report.setParameter(Report.AGENT_NAME_PARAM, "Perez, Adam");

		report.setParameter(Report.START_DATE_PARAM, "2011-10-01 00:00:00");
		report.setParameter(Report.END_DATE_PARAM, "2011-10-31 23:59:59");

		Vector<String[]> expected = new Vector<String[]>();

		expected.add(new String[]{"2011-10-05 17:35:33", "Perez", "Adam", "2111111", "l", "packham", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-08 15:11:22", "Perez", "Adam", "1212", "S", "Conocvaloff", "Yes", "No", "Yes", "No", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-08 11:26:18", "Perez", "Adam", "22111111", "S", "Hunt", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-08 12:55:32", "Perez", "Adam", "1111111", "D", "Dziomba", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-12 17:35:05", "Perez", "Adam", "22111111", "C", "Brown", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-11 13:20:12", "Perez", "Adam", "2111111", "P", "Soutar", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 12:35:41", "Perez", "Adam", "22111111", "D", "Adams", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 12:27:48", "Perez", "Adam", "12111111", "W", "Castelino", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 14:04:56", "Perez", "Adam", "22111111", "W", "Castelino", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 16:58:11", "Perez", "Adam", "22111112", "C", "Robinson", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-14 19:15:16", "Perez", "Adam", "2111111", "H", "Harrison", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-15 18:27:17", "Perez", "Adam", "12111111", "h", "moseley", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-20 16:04:04", "Perez", "Adam", "221111", "m", "Mora", "No", "No", "Yes", "Yes", "Yes", "Yes", "null", "null"});
		expected.add(new String[]{"2011-10-19 12:41:34", "Perez", "Adam", "22211111", "D", "Straub", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-26 16:00:49", "Perez", "Adam", "12111222", "L", "Lavigne", "Yes", "No", "Yes", "Yes", "Yes", "No", "No", "No"});
		expected.add(new String[]{"2011-10-29 12:34:37", "Perez", "Adam", "22111111", "J", "Foster", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-27 16:09:28", "Perez", "Adam", "", "M", "Mallary", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-27 18:06:37", "Perez", "Adam", "22111111", "H", "McMenamin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-27 18:34:59", "Perez", "Adam", "12111111", "r", "talbot", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-06 13:06:16", "Perez", "Adam", "22111111", "S", "Davis", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-06 10:38:16", "Perez", "Adam", "22111121", "C", "Beckett", "No", "No", "Yes", "Yes", "Yes", "Yes", "No", "Yes"});
		expected.add(new String[]{"2011-10-06 12:14:53", "Perez", "Adam", "", "C", "Beckett", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-05 13:11:34", "Perez", "Adam", "", "C", "Beckett", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-05 13:34:37", "Perez", "Adam", "12111111", "G", "Martin", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-08 14:03:12", "Perez", "Adam", "1111112", "R", "Viccora", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-12 12:54:24", "Perez", "Adam", "", "M", "Katz", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-13 13:20:05", "Perez", "Adam", "22111111", "M", "Katz", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 14:49:33", "Perez", "Adam", "22111111", "M", "Katz", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 18:10:22", "Perez", "Adam", "", "A", "Wilson", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-12 18:55:41", "Perez", "Adam", "12111111", "D", "Smith", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 10:37:34", "Perez", "Adam", "1", "S", "Fern", "Yes", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-14 11:08:33", "Perez", "Adam", "22111111", "T", "Knight", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 13:08:09", "Perez", "Adam", "12111111", "W", "Sherow", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 15:27:06", "Perez", "Adam", "1111111", "R", "Jumper", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-15 16:30:56", "Perez", "Adam", "22111112", "K", "Bittle", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-15 15:30:44", "Perez", "Adam", "1111111", "C", "Lowe", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-15 13:48:13", "Perez", "Adam", "1111111", "T", "Carroll", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-20 12:04:21", "Perez", "Adam", "", "J", "Shuchat", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-18 09:34:04", "Perez", "Adam", "22111211", "S", "Yancey", "No", "No", "Yes", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-18 14:01:50", "Perez", "Adam", "1111111", "I", "Rodriguez-Soto", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-21 11:46:30", "Perez", "Adam", "2111111", "j", "carpenter", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-21 12:22:10", "Perez", "Adam", "", "J", "Moneypenny", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-22 12:56:52", "Perez", "Adam", "22111111", "C", "Griffin", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-21 16:36:55", "Perez", "Adam", "22111112", "A", "Cook", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-22 18:05:16", "Perez", "Adam", "1111111", "j", "mccullough", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-28 12:33:09", "Perez", "Adam", "22111211", "J", "Pooren", "No", "No", "Yes", "Yes", "Yes", "No", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-28 11:03:32", "Perez", "Adam", "", "A", "Hartley", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-28 14:09:29", "Perez", "Adam", "1111111", "b", "Ross", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-29 17:34:03", "Perez", "Adam", "22111111", "s", "watson", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-06 15:09:03", "Perez", "Adam", "22111112", "J", "Becker", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-04 17:28:09", "Perez", "Adam", "1111111", "B", "McClendon", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-06 12:01:19", "Perez", "Adam", "22211111", "J", "stone", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-06 14:40:24", "Perez", "Adam", "2111111", "J", "stone", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-06 14:11:03", "Perez", "Adam", "12", "C", "Curtis", "Yes", "No", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-11 09:53:00", "Perez", "Adam", "111111", "M", "Barnhart", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-12 14:43:45", "Perez", "Adam", "22211111", "J", "Castillo", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-12 16:24:07", "Perez", "Adam", "", "J", "Castillo", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-12 11:27:40", "Perez", "Adam", "121111", "H", "Randell", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "null", "null"});
		expected.add(new String[]{"2011-10-13 14:39:04", "Perez", "Adam", "12111111", "f", "hodgson", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 13:31:39", "Perez", "Adam", "22111111", "M", "Oconnell", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 12:06:54", "Perez", "Adam", "", "W", "Terry", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-14 12:53:06", "Perez", "Adam", "", "W", "Terry", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-14 18:15:59", "Perez", "Adam", "2211111", "J", "CAscione", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-18 10:33:33", "Perez", "Adam", "22111122", "D", "Fukuchi", "No", "No", "Yes", "Yes", "Yes", "Yes", "No", "No"});
		expected.add(new String[]{"2011-10-19 10:14:38", "Perez", "Adam", "12111112", "T", "Font", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-19 14:06:55", "Perez", "Adam", "", "S", "Garner", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-19 20:16:11", "Perez", "Adam", "", "J", "Hillington", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-21 14:13:23", "Perez", "Adam", "22111111", "s", "smith", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-20 10:44:22", "Perez", "Adam", "22111111", "W", "Chapman", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-22 10:59:28", "Perez", "Adam", "22211111", "M", "Ogburn", "No", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-20 18:33:36", "Perez", "Adam", "2", "M", "arnoldy", "No", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-21 12:44:31", "Perez", "Adam", "2111111", "M", "Arnoldy", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-21 17:22:41", "Perez", "Adam", "22111111", "A", "Mathis", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-22 13:20:27", "Perez", "Adam", "", "M", "Spanowich", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-22 14:26:45", "Perez", "Adam", "12111221", "B", "Anderson", "Yes", "No", "Yes", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-22 15:39:14", "Perez", "Adam", "22111111", "B", "Anderson", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-22 15:29:08", "Perez", "Adam", "2111111", "E", "MacGregor", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-22 17:13:33", "Perez", "Adam", "1111111", "l", "adkins", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-26 15:23:21", "Perez", "Adam", "22111111", "s", "Holloway", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-26 13:25:24", "Perez", "Adam", "111111", "W", "Rivich", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "null"});
		expected.add(new String[]{"2011-10-26 16:33:56", "Perez", "Adam", "12111111", "R", "Severe", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-28 17:41:19", "Perez", "Adam", "22111111", "R", "Severe", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-28 18:34:26", "Perez", "Adam", "22111112", "R", "Severe", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-27 11:41:19", "Perez", "Adam", "22111111", "A", "Coleman", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-27 10:58:31", "Perez", "Adam", "12111112", "c", "kellner", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-27 15:50:50", "Perez", "Adam", "", "J", "zurbruegg", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-29 18:44:19", "Perez", "Adam", "2111111", "A", "L Shepered", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-05 12:05:26", "Perez", "Adam", "22211221", "A", "Nierenverg", "No", "No", "No", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-11 09:13:05", "Perez", "Adam", "", "c", "williams", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-11 12:00:54", "Perez", "Adam", "1221122", "c", "olivier", "Yes", "No", "No", "Yes", "Yes", "No", "No", "null"});
		expected.add(new String[]{"2011-10-13 16:18:34", "Perez", "Adam", "1111111", "T", "Morton", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 16:09:13", "Perez", "Adam", "12111111", "T", "Morton", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 12:56:13", "Perez", "Adam", "1111111", "J", "Jones", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 15:45:10", "Perez", "Adam", "12111111", "B", "Shelton", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-13 17:03:27", "Perez", "Adam", "1111111", "B", "Shelton", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-14 14:17:22", "Perez", "Adam", "12211221", "j", "groeper", "Yes", "No", "No", "Yes", "Yes", "No", "No", "Yes"});
		expected.add(new String[]{"2011-10-15 17:14:20", "Perez", "Adam", "1111111", "R", "HART", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-19 11:40:34", "Perez", "Adam", "22", "H", "owens", "No", "No", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-18 11:00:18", "Perez", "Adam", "", "K", "Stout", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-20 16:20:29", "Perez", "Adam", "", "K", "Stout", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-18 12:07:50", "Perez", "Adam", "12111111", "W", "TANNER", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-18 14:33:25", "Perez", "Adam", "2111111", "m", "brathwaite", "No", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-19 13:01:43", "Perez", "Adam", "22111111", "E", "Whitehead", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-18 15:14:24", "Perez", "Adam", "22111111", "R", "Jacobsen", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-19 16:29:55", "Perez", "Adam", "", "s", "gelvez", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-19 14:28:25", "Perez", "Adam", "", "M", "Stevens", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-21 15:33:15", "Perez", "Adam", "1111111", "M", "Stevens", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-20 11:43:05", "Perez", "Adam", "12111111", "R", "Sapp", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-20 15:35:12", "Perez", "Adam", "22111112", "R", "Sapp", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-21 11:18:32", "Perez", "Adam", "1111111", "m", "ohern", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-21 10:52:09", "Perez", "Adam", "12111111", "r", "hodges", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-26 17:49:31", "Perez", "Adam", "12111112", "J", "Martino", "Yes", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "No"});
		expected.add(new String[]{"2011-10-27 16:37:47", "Perez", "Adam", "1111111", "p", "vanaschilsgard", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-27 12:32:56", "Perez", "Adam", "", "m", "ohern", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-27 13:11:36", "Perez", "Adam", "1111111", "m", "ohern", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-26 19:20:16", "Perez", "Adam", "1111111", "T", "Gilmore", "Yes", "Yes", "null", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-27 17:26:13", "Perez", "Adam", "22111121", "J", "Crader", "No", "No", "Yes", "Yes", "Yes", "Yes", "No", "Yes"});
		expected.add(new String[]{"2011-10-28 17:17:20", "Perez", "Adam", "", "L", "Lesterero", "null", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-28 12:59:01", "Perez", "Adam", "22111111", "G", "Beernaert", "No", "No", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes"});
		expected.add(new String[]{"2011-10-28 15:58:46", "Perez", "Adam", "1", "R", "Perez", "Yes", "null", "null", "null", "null", "null", "null", "null"});
		expected.add(new String[]{"2011-10-29 15:57:42", "Perez", "Adam", "22211221", "D", "Eeds", "No", "No", "No", "Yes", "Yes", "No", "No", "Yes"});

		testOutput(expected, report.startReport());
	}

}
