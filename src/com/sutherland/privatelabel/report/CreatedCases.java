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
import com.sutherland.helios.data.Aggregation;
import com.sutherland.helios.data.attributes.DataAttributes;
import com.sutherland.helios.data.granularity.user.UserGrains;
import com.sutherland.helios.database.connection.SQL.ConnectionFactory;
import com.sutherland.helios.database.connection.SQL.RemoteConnection;
import com.sutherland.helios.date.formatting.DateFormatter;
import com.sutherland.helios.date.parsing.DateParser;
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
public final class CreatedCases extends Report implements DataAttributes
{
	private RemoteConnection dbConnection;
	private PrivateLabelRoster roster;
	private final String dbPropFile = DatabaseConfigs.PRIVATE_LABEL_PROD_DB;
	private final static Logger logger = Logger.getLogger(CreatedCases.class);

	public static String uiGetReportName()
	{
		return "Created Cases";
	}
	
	public static String uiGetReportDesc()
	{
		return "Created Cases.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public CreatedCases() throws ReportSetupException
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
			reportName = CreatedCases.uiGetReportName();
			reportDesc = CreatedCases.uiGetReportDesc();
			
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
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = null;

		String query = "SELECT " +
				" CRM_TRN_ACTIVITY.activity_createddate, " +
				" CRM_TRN_ACTIVITY.activity_createdby," +
				" CRM_TRN_ACTIVITY.activity_activityid" + 
				" FROM CRM_TRN_ACTIVITY" + 
				" WHERE CRM_TRN_ACTIVITY.activity_createddate >= '" + 
				getParameters().getStartDate() +
				"' AND CRM_TRN_ACTIVITY.activity_createddate < '" + 
				getParameters().getEndDate() + 
				"' " + 
				" and CRM_TRN_ACTIVITY.activity_pagesource='QUICKADD' and " + 
					"(CRM_TRN_ACTIVITY.activity_casetype = '151' or CRM_TRN_ACTIVITY.activity_casetype = '0' or CRM_TRN_ACTIVITY.activity_casetype='1') ";

		query += " GROUP BY CRM_TRN_ACTIVITY.activity_createddate,CRM_TRN_ACTIVITY.activity_createdby,CRM_TRN_ACTIVITY.activity_activityid ";
		
		retval = new ArrayList<String[]>();

		Aggregation reportGrainData = new Aggregation();

		String userID, reportGrain, createdCaseID;
		
		
		int timeGrain, userGrain, numCreatedCases, dateFormat;
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		for(String[] row:  dbConnection.runQuery(query))
		{
			userID = row[1];

			if(roster.hasUser(userID) )
			{
				createdCaseID = row[2];

				//time grain for time reports
				if(isTimeTrendReport())
				{
					timeGrain = Integer.parseInt(getParameters().getTimeGrain());
					dateFormat = Integer.parseInt(getParameters().getDateFormat());
					reportGrain = DateFormatter.getFormattedDate(DateParser.convertSQLDateToGregorian(row[0]), timeGrain, dateFormat);
				}
				else //if(isStackReport())
				{
					//is stack report
					userGrain = Integer.parseInt(getParameters().getUserGrain());
					reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(userID));
				}
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(CREATED_CASES_ATTR);
				reportGrainData.getDatum(reportGrain).addData(CREATED_CASES_ATTR, createdCaseID);
			}
		}
		
		for( Entry<String, String> queryStats  : dbConnection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}
		
		retval = new ArrayList<String[]>(reportGrainData.getSize());
		
        for(String grain : reportGrainData.getDatumIDList())
        {
               numCreatedCases = reportGrainData.getDatum(grain).getAttributeData(CREATED_CASES_ATTR).size();
               retval.add(new String[]{grain, ""+ (int)numCreatedCases });
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
		
		if(isTimeTrendReport())
		{
			retval.add("Date Grain");
		}
		else if(isStackReport())
		{
			retval.add("User Grain");
		}
		
		retval.add("Created Cases");
		
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
