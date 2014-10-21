/**
 * 
 */
package com.sutherland.privatelabel.report;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sutherland.helios.api.report.frontend.ReportFrontEndGroups;
import com.sutherland.helios.data.granularity.user.UserGrains;
import com.sutherland.helios.database.connection.SQL.ConnectionFactory;
import com.sutherland.helios.database.connection.SQL.RemoteConnection;
import com.sutherland.helios.exceptions.DatabaseConnectionCreationException;
import com.sutherland.helios.exceptions.ExceptionFormatter;
import com.sutherland.helios.exceptions.ReportSetupException;
import com.sutherland.helios.logging.LogIDFactory;
import com.sutherland.helios.report.Report;
import com.sutherland.helios.report.parameters.groups.ReportParameterGroups;
import com.sutherland.privatelabel.datasources.DatabaseConfigs;

/**
 * @author Jason Diamond
 *
 */
public class IVRAgentCSATDetail extends Report
{
	private RemoteConnection dbConnection;
	private PrivateLabelRoster roster;
	private final String dbPropFile = DatabaseConfigs.PRIVATE_LABEL_PROD_DB;
	private final static Logger logger = Logger.getLogger(IVRAgentCSATDetail.class);

	public static String uiGetReportName()
	{
		return  "IVR Agent CSAT Detail";
	}
	
	public static String uiGetReportDesc()
	{
		return "Visualizes IVR CSAT survey details.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.STACK_RANK_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.DASHBOARD_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRAgentCSATDetail() throws ReportSetupException
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport() 
	{
		boolean retval = false;

		try
		{
			reportName = IVRAgentCSATDetail.uiGetReportName();
			reportDesc = IVRAgentCSATDetail.uiGetReportDesc();
			
			for(Entry<String, ArrayList<String>> reportType : uiReportParameters.entrySet())
			{
				for(String paramName :  reportType.getValue())
				{
					getParameters().addSupportedParameter(paramName);
				}
			}
			
			retval = true;
		}
		catch (Exception e)
		{
			setErrorMessage("Error setting up report");
			
			logErrorMessage(getErrorMessage());
			logErrorMessage( ExceptionFormatter.asString(e));
		}

		return retval;
	}
	
	@Override
	protected boolean setupLogger() 
	{
		logID = LogIDFactory.getLogID().toString();

		if (MDC.get(LOG_ID_PREFIX) == null) 
		{
			MDC.put(LOG_ID_PREFIX, LOG_ID_PREFIX + logID);
		}

		return (logger != null);
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
			setErrorMessage("DatabaseConnectionCreationException on attempt to access database");
			
			logErrorMessage(getErrorMessage());
			logErrorMessage( ExceptionFormatter.asString(e));
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
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		String query = "SELECT " + 
				"tbl_AcerPFSSurveyIVR.RId," + 
				"CRM_MST_USER.USER_USERID," + 
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) ,"+ 
				"tbl_AcerPFSSurveyIVR.Survey_Result," +
				"tbl_AcerPFSSurveyIVR.Customer_Firstname ,"+
				"tbl_AcerPFSSurveyIVR.Customer_Lastname ,"+
				"CASE WHEN LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 1 " +
				"THEN 'null' " +
				"Else " +       
				"CASE WHEN LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END, " +
				"CASE WHEN LEN(tbl_AcerPFSSurveyIVR.Survey_Result) < 2 " +
				"Then 'null' " +
				"Else " +
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 1 " + 
				"THEN 'Yes' " +
				"Else 'No' " + 
				"END " +
				"END, " +
				"CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 AND LEN(tbl_AcerPFSSurveyIVR.Survey_Result)>2 " +
				"Then " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,3),1) = 1 " +
				"THEN 'Yes' " +
				"Else 'No' END " +
				"Else " + 
				"'null' " + 
				"END, " +
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
				"END, " +
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
				"END, " +
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
				"END, " + 
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
				"END, " +
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
				"END " +
				"FROM (tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID) " + 
				"WHERE " +
				" CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) >= '"+
				getParameters().getStartDate()+
				"' AND " +
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) < '"+
				getParameters().getEndDate() + 
				"'";

		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		String custfName, userID, reportGrain;
		int userGrain;
		for(String[] row:  dbConnection.runQuery(query))
		{
			userID = row[1];
			
			if(roster.hasUser(userID) )
			{
				if(row[4].length() == 0)
				{
					custfName = "";
				}
				else
				{
					custfName = row[4].substring(0, 1);
				}

				userGrain = Integer.parseInt(getParameters().getUserGrain());
				reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(userID));
			
			
				retval.add(new String[]
				{
						row[2].substring(0, row[2].length() - 2), 
						reportGrain,
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
		}
		
		for( Entry<String, String> queryStats  : dbConnection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}


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
		
		if (!isChildReport) 
		{
			MDC.remove(LOG_ID_PREFIX);
		}
	}
	
	@Override
	public ArrayList<String> getReportSchema() 
	{
		ArrayList<String> retval = new ArrayList<String>();
		
		retval.add("Date");
		retval.add("Results");
		retval.add("FName");
		retval.add("LName");
		retval.add("Q1");
		retval.add("Q2");
		retval.add("Q3");
		retval.add("Q4");
		retval.add("Q5");
		retval.add("Q6");
		retval.add("Q7");
		retval.add("Q8");
		
		return retval;
	}
	
	@Override
	protected void logErrorMessage(String message) 
	{
		logger.log(Level.ERROR, message);
	}

	@Override
	protected void logInfoMessage(String message) 
	{
		logger.log(Level.INFO, message);
	}

	@Override
	protected void logWarnMessage(String message) 
	{
		logger.log(Level.WARN, message);
	}
}
