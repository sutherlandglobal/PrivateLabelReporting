/**
 * 
 */
package report;


import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;

import constants.Constants;

import report.Report;
import util.parameter.validation.ReportVisitor;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class IVRAgentCSATDetail extends Report
{
	private RemoteConnection dbConnection;
	private Roster roster;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRAgentCSATDetail() throws ReportSetupException
	{
		super();

		reportName = "IVR Agent CSAT Detail";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport()
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		String query = "SELECT " + 
				"tbl_AcerPFSSurveyIVR.RId," + 
				"CRM_MST_USER.USER_USERID AS UserID," + 
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) AS DATE,"+ 
				"tbl_AcerPFSSurveyIVR.Survey_Result," +
				"tbl_AcerPFSSurveyIVR.Customer_Firstname AS Customer_First_Name,"+
				"tbl_AcerPFSSurveyIVR.Customer_Lastname AS Customer_Last_Name,"+
				"CASE WHEN LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 1 " +
				"THEN 'null' " +
				"Else " +       
				"CASE WHEN LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END AS Q1, " +
				"CASE WHEN LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 2 " +
				"Then 'null' " +
				"Else " +
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 1 " + 
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END AS Q2, " +
				"CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result)>2 " +
				"Then " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,3),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' END " +
				"Else " + 
				"'null' " + 
				"END AS Q3, " +
				"CASE WHEN (Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 4) OR (LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 3) " + 
				"Then 'null' " +
				"Else CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 " + 
				"Then " +
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,4),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' "  +
				"END " +
				"Else " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,3),1) = 1 " + 
				"THEN 'Yes' " +
				"Else 'No' " +
				"END " +
				"END " +
				"END AS Q4, " +
				"CASE WHEN (Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 5) OR (LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 4) " +
				"Then 'null' " +
				"Else CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 " +
				"Then " +
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,5),1) = 1 " +
				"THEN 'Yes' " + 
				"Else 'No' " +  
				"END " + 
				"Else " +  
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,4),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END " +
				"END AS Q5, " +
				"CASE WHEN (Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 6) OR (LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 5) " + 
				"Then 'null' " + 
				"Else CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 " + 
				"Then " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,6),1) = 1 " +
				"THEN 'Yes' " + 
				"Else 'No' " + 
				"END " +
				"Else " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,5),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END " +
				"END AS Q6, " + 
				"CASE WHEN (Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 7) OR (LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 6) " +
				"Then 'null' " +
				"Else CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 " +
				"Then " +
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,7),1) = 1 " + 
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"Else " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,6),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END " + 
				"END AS Q7, " +
				"CASE WHEN (Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 8) OR (LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 7) " +
				"Then 'null' " +
				"Else CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 " +
				"Then " +
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,8),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"Else " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,7),1) = 1 " +
				"THEN 'Yes' " + 
				"Else 'No' " +  
				"END " +
				"END " +
				"END AS Q8 " +
				"FROM (tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID) " + 
				"WHERE " +
				" CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) >= '"+
				parameters.get(START_DATE_PARAM)+"' AND " +
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) <= '"+
				parameters.get(END_DATE_PARAM) + "'";

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		String targetUserID;
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			query += " AND CRM_MST_USER.USER_USERID = '" + targetUserID  + "'";
		}
		
		

		String custfName, userName;
		for(String[] row:  dbConnection.runQuery(query))
		{
			//System.out.println(Arrays.asList(row).toString());
			
			if(row[4].length() == 0)
			{
				custfName = "";
			}
			else
			{
				custfName = row[4].substring(0, 1);
			}

			userName = row[1];
			
			if(roster.getUser(userName) != null)
			{
				userName = roster.getUser(row[1]).getAttrData(Roster.FULLNAME_ATTR).get(0);
			}
			
			retval.add(new String[]
			{
					row[2].substring(0, row[2].length() - 2), 
					userName,
					row[3], 
					custfName, 
					row[5], 
					row[6], 
					row[7], 
					row[8], 
					row[9], 
					row[10], 
					row[11], 
					row[12], 
					row[13]
			});
		}


		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupDataSourceConnections()
	 */
	@Override
	protected boolean setupDataSourceConnections()
	{
		boolean retval = false;

		try 
		{
			ConnectionFactory factory = new ConnectionFactory();
			
			factory.load(dbPropFile);
			
			dbConnection = factory.getConnection();
		}
		catch(DatabaseConnectionCreationException e )
		{
			logger.log(Level.ERROR,   "DatabaseConnectionCreationException on attempt to access database: " + e.getMessage());	
		}
		finally
		{
			if(dbConnection != null)
			{
				retval = true;
			}
		}
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport()
	{
		boolean retval = false;

		retval = true;

		return retval;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(roster != null)
		{
			roster.close();
		}

		if(dbConnection != null)
		{
			dbConnection.close();
		}

		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = false; 
		
		ReportVisitor visitor = new ReportVisitor();
		
		retval = visitor.validate(this);
		
		roster = visitor.getRoster();
		
		return retval;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		IVRAgentCSATDetail rs = null;

		System.out.println("Agent Time Report");
		
		try
		{
			rs = new IVRAgentCSATDetail();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(AGENT_NAME_PARAM, "Perez, Adam");
			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");
					
			
			for(String[] row : rs.startReport())
			{
				System.out.println(Arrays.asList(row).toString());
			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
			}
		}
		
		System.out.println("=====================\nTeam Time Report");
		
		try
		{
			rs = new IVRAgentCSATDetail();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);
			
			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-02 23:59:59");
					
			
			for(String[] row : rs.startReport())
			{
				System.out.println(Arrays.asList(row).toString());
			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
			}
		}
	}
}
