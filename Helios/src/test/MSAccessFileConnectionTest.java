/**
 * 
 */
package test;

//import static org.junit.Assert

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.connection.MSAccess.FileConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author jdiamond
 *
 */
public class MSAccessFileConnectionTest extends TestCase
{

	private FileConnection databaseConnection;
	private String dbFile;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		databaseConnection = null;
		dbFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/testAccessDB.accdb";
		
		try
		{
			databaseConnection = new FileConnection(new File(dbFile).getAbsolutePath());
		} 
		catch (SecurityException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if(databaseConnection == null)
			{
				throw new ReportSetupException("Database connection was null in final check.");
			}
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		if(databaseConnection != null)
		{
			databaseConnection.close();
		}
		
	}

	/**
	 * Test method for {@link database.connection.MSAccess.FileConnection#runQuery(java.lang.String)}.
	 */
	@Test
	public void testRunQueryString()
	{
		String query = "SELECT * FROM Employees WHERE Champion Name == Thomas Vail";
		String expected = "Thomas Vail,US020676,TPVail,Rochester - Tech,GWAH605,469933,Technical Support,true,PL_Vail  Thomas P,North America,AnswersBy,5625682,Thomas.Vail@SutherlandGlobal.COM,Chris Paddon,true,";
		
		Vector<String[]> results = databaseConnection.runQuery(query);
		
		StringBuilder sb = new StringBuilder();
		
		if(results != null && results.size() == 1)
		{
			for(String cell : results.get(0))
			{
				sb.append(cell);
				sb.append(",");
			}
		
			assertEquals(sb.toString(), expected);
		}
		else
		{
			fail("Query " + query + " returned invalid results");
		}
		
		////////////////////////
		
		final int EXPECTED_ROWS = 54;
		
		query = "SELECT * FROM Employees WHERE Supervisor == Chris Paddon";
		
		assertEquals(EXPECTED_ROWS, databaseConnection.runQuery(query).size());
		
	}

	/**
	 * Test method for {@link database.connection.MSAccess.FileConnection#showTables()}.
	 */
	@Test
	public void testShowTables()
	{
		assertEquals(
				"Call Info\nCases\nCases Version 2\nContacts\nEmployees\nEntitlements\nGroupings\nOrders\nPIN_Usage\nPrograms\nRefunds\nRoles\nSkillsets\nSupport_Minute_Details\nz_Private Label Tickets\n", 
				databaseConnection.showTables());
	}

	/**
	 * Test method for {@link database.connection.MSAccess.FileConnection#describe(java.lang.String)}.
	 */
	@Test
	public void testDescribe()
	{
		HashMap<String,String> schemaMapping = new HashMap<String,String>();
		
		schemaMapping.put("Call Info", "Date,Agent Name,ACD Calls,Avg ACD Time,Avg ACW Time,ACD Time,ACW Time,Agent Ring Time,AUX Time,Avail Time,Staffed Time,Assists,Trans Out,Held Calls,Avg Hold Time,Aux 2,Aux 3,Hold Time,SkillSet,");
		schemaMapping.put("Cases Version 2", "Created Date,Field2,Case Id,New Case,Contact ID,Vendor Name,Contact Name,Phone Number,Alternate Phone Number,Email Id,Case Source,Case Type,Status,Sub Status,Service Type,Field16,Service Name,Case Category1,Case Category2,Case Category3,Operating System,Manufacturer,Memory Amount,Connection Type,Virus Protection,Spyware Protection,Model Number,Assigned To,Assigned Team,Modified By,Work Notes,Callback Date,Callback Time,Callback Time Zone,Callback Reason,Case Created Champion Name,Case Created Champion Employee Id,Case Modified Champion Name,Case Modified Champion Employee Id,");
		schemaMapping.put("Contacts", "Contact ID,Empty,Source,Vendor Name,Salutation,Customer Name,Gender,Address 1,Address 2,City,Empty 2,State,Country,Postal Code,Time Zone,Email,Phone,Alt Phone,Ext,Mobile,Pager,Dept,Title,Assistant Name,Reachable by Phone,Reachable by Email,Reachable by Pager,Reachable by Mobile,Contact Method,Insert From,Created Date,Employee ID,F33,");
		schemaMapping.put("Employees", "Champion Name,Employee ID,NT Login,Group,Badge Number,Avaya ID,Role,Active,CMS Name,Geography,Program,LMI_Agent_Number,Email_Address,Supervisor,Include_In_Reporting,");
		schemaMapping.put("Entitlements","Entitlements,$/Issue,Active,");
		schemaMapping.put("Groupings","Group,");
		schemaMapping.put("Orders","Order Date,Empty,Order ID,Contact ID,Source,Type,Case ID,Billing First Name,Billing Last Name,Billing City,Billing State,Billing Zip,Billing Country,Merchant Ref ID,Card Charging Date,Request ID,Min Contract Period Type,Min Contract Period,Service Type,Service Name,Service Minutes,Max User,Service Price,Quantity,Total Minutes,Promotion Code,Discount Type,Discount Percent or Mins,Discount Price,Net Amount,Champion Name,Employee ID,Company_Sold,Field2,F35,");
		schemaMapping.put("PIN_Usage","Created Date,Field2,Contact ID,Customer Name,Case ID,Service Type,Service Name,Start Time,Field9,End Time,Event Name,Employee Name,Employee ID,");
		schemaMapping.put("Programs","Program Name,");
		schemaMapping.put("Refunds","Refund Date,Empty,Order ID,Contact Name,Empty 2,Empty 3,Service Type,Empty 4,Service Name,Reason,Other Reason,Amount,Qty,Minutes,Refund Type,Refund Amount,Refund Conf Num,Merchant Ref ID,Employee ID,Champion Name,F21,F22,F23,F24,F25,F26,F27,F28,F29,F30,F31,F32,F33,F34,F35,F36,F37,F38,F39,");
		schemaMapping.put("Roles","Role,");
		schemaMapping.put("Skillsets","Skillset,Description,Type,VDN,");
		schemaMapping.put("Support_Minute_Details","Name of the Product,Field2,Service Type,Customer Name,Order Id,Field6,Order Date,Eligible Entitlement Mins/Qty,Total Used Mins/Qty,Total Grace Mins/Qty,Price,");
		schemaMapping.put("z_Private Label Tickets","Date Submitted,Altiris Ticket Number,TCM Ticket Number,Project,Description,Completed,Date Completed,");
		
		for(Entry<String, String> tableName : schemaMapping.entrySet())
		{
			assertEquals(tableName.getKey(), databaseConnection.describe(tableName.getKey()));
		}
	}

//	/**
//	 * Test method for {@link database.connection.MSAccess.FileConnection#getSchema(java.lang.String)}.
//	 */
//	@Test
//	public void testGetSchema()
//	{
//		fail("Not yet implemented"); // TODO
//	}

	/**
	 * Test method for {@link database.connection.MSAccess.FileConnection#getRowCount(java.lang.String)}.
	 */
	@Test
	public void testGetRowCount()
	{
		HashMap<String,String> schemaMapping = new HashMap<String,String>();
		
		schemaMapping.put("Call Info","113235");
		schemaMapping.put("Cases Version 2", "196868");
		schemaMapping.put("Contacts", "98407");
		schemaMapping.put("Employees", "255");
		schemaMapping.put("Entitlements","35");
		schemaMapping.put("Groupings","7");
		schemaMapping.put("Orders","29694");
		schemaMapping.put("PIN_Usage","57073");
		schemaMapping.put("Programs","3");
		schemaMapping.put("Refunds","1546");
		schemaMapping.put("Roles","5");
		schemaMapping.put("Skillsets","27");
		schemaMapping.put("Support_Minute_Details","19181");
		schemaMapping.put("z_Private Label Tickets","34");
		
		//bad tables -> 0 rows
		schemaMapping.put("lol","0");
		schemaMapping.put("","0");
		schemaMapping.put("     ","0");
		schemaMapping.put(null,"0");
		schemaMapping.put("\"","0");
		schemaMapping.put("\'","0");
		
		for(Entry<String, String> tableName : schemaMapping.entrySet())
		{
			try
			{
				assertEquals(tableName.getValue(), ""+databaseConnection.getRowCount(tableName.getKey()));
			}
			catch(AssertionFailedError e)
			{
				
				throw new AssertionFailedError(e.getMessage() + "\ntableName: \"" + tableName + "\"\n");
			}
		}
	}

//	/**
//	 * Test method for {@link database.connection.MSAccess.FileConnection#printTable(java.lang.String)}.
//	 */
//	@Test
//	public void testPrintTable()
//	{
//		fail("Not yet implemented"); // TODO
//	}
//
//	/**
//	 * Test method for {@link database.connection.MSAccess.FileConnection#runQuery(java.lang.String, int)}.
//	 */
//	@Test
//	public void testRunQueryStringInt()
//	{
//		fail("Not yet implemented"); // TODO
//	}

	/**
	 * Test method for {@link database.connection.MSAccess.FileConnection#runParallelQueries(java.util.Vector)}.
	 */
	@Test
	public void testRunParallelQueries()
	{
		Vector<String> queries = new Vector<String>();
		
		queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
		queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
		queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
		queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
		
//		queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
//		queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
//		queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
//		queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
//
//		queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
//		queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
//		queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
//		queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");

		int serialRowCount = 0;
		for(String query : queries)
		{
			serialRowCount += databaseConnection.runQuery(query).size();
		}
		
		Map<String, Vector<String[]>> results = databaseConnection.runParallelQueries(queries);
		
		int parallelRowCount = 0;
		
		for(Entry<String, Vector<String[]>> query : results.entrySet())
		{
			parallelRowCount += query.getValue().size();
		}
		
		assertEquals(serialRowCount, parallelRowCount);
	}
	
	public static void main(String[] args)
	{
		//time serial queries and parallel queries
		
		FileConnection databaseConnection = null;

		String dbFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/testAccessDB.accdb";
		
		try
		{
			databaseConnection = new FileConnection(new File(dbFile).getAbsolutePath());
			
			Vector<String> queries = new Vector<String>();
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
			
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");
	
			queries.add("SELECT Case Id,Created Date,Case Category1,Case Category2,Case Modified Champion Name FROM Cases Version 2 WHERE Created Date =} 2010-12-01 00:00:00,Created Date {= 2010-12-31 23:59:59,Case Category1 !~ ^$");
			queries.add("SELECT Champion Name,CMS Name FROM Employees WHERE Champion Name == Thomas Vail");
			queries.add("SELECT Date,Agent Name,ACD Calls,SkillSet FROM Call Info WHERE SkillSet =~ 70|784|1163|1164|1165|1166|1167|1168|1169|1195|1247,Date =} 2010-10-01 00:00:00,Date {= 2010-10-31 00:00:00");
			queries.add("SELECT Created Date,Case Modified Champion Name,New Case,Case Type FROM Cases Version 2 WHERE Case Type == Tech Support,New Case != Yes,Case Modified Champion Name == Thomas Vail,Created Date =} 2011-03-24 00:00:00,Created Date {= 2011-03-24 19:53:22");

			long startTime = System.currentTimeMillis();
			
			int rowCount = 0;
//			for(String query : queries)
//			{
//				rowCount += database.connection.runQuery(query).size();
//			}
			
			
			long endTime = System.currentTimeMillis();
			
			System.out.println("=========================");
			System.out.println("Rows returned: " + rowCount);
			System.out.println("Serial queries Time: " + (endTime-startTime) + " ms");
			System.out.println("=========================");
			//////////////////////
			
			
			startTime = System.currentTimeMillis();
			
			Map<String, Vector<String[]>> results = databaseConnection.runParallelQueries(queries);
			
			endTime = System.currentTimeMillis();
			
			rowCount = 0;
			
			for(Entry<String, Vector<String[]>> query : results.entrySet())
			{
				//System.out.println("Query in results: " + query );
				rowCount += query.getValue().size();
			}
			
			System.out.println("=========================");
			System.out.println("Rows returned: " + rowCount);
			System.out.println("Parallel queries Time: " + (endTime-startTime) + " ms");
			System.out.println("=========================");
			
		} 
		catch (SecurityException e)
		{
			e.printStackTrace();
		} 
		catch (DatabaseConnectionCreationException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(databaseConnection != null)
			{
				databaseConnection.close();
			}
		}
	}
}
