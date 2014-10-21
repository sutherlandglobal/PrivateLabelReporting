/**
 * 
 */
package com.sutherland.privatelabel.report;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sutherland.helios.api.report.frontend.ReportFrontEndGroups;
import com.sutherland.helios.data.Aggregation;
import com.sutherland.helios.data.Datum;
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
public final class PINsConsumedCount extends Report implements DataAttributes
{
	private RemoteConnection dbConnection;
	private PrivateLabelRoster roster;
	private final String dbPropFile = DatabaseConfigs.PRIVATE_LABEL_PROD_DB;
	private final static Logger logger = Logger.getLogger(PINsConsumedCount.class);

	public static String uiGetReportName()
	{
		return "PINs Consumed Count";
	}
	
	public static String uiGetReportDesc()
	{
		return "Trends count of PIN usage.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public PINsConsumedCount() throws ReportSetupException
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
			reportName = PINsConsumedCount.uiGetReportName();
			reportDesc = PINsConsumedCount.uiGetReportDesc(); 
			
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
		ArrayList<String[]> retval = null;

		String casesQuery = "SELECT " +
				"CRM_TRN_PROSPECT.PROSPECT_UPDATEDDATE,CRM_MST_USER.USER_USERID,CRM_TRN_PROSPECT.PROSPECT_CONSUMESERVICEID " + 
				"FROM CRM_TRN_PROSPECT LEFT JOIN CRM_MST_USER ON CRM_TRN_PROSPECT.PROSPECT_CREATEDBY = CRM_MST_USER.USER_USERID "+
				" WHERE PROSPECT_UPDATEDDATE >= '" + 
				getParameters().getStartDate() + 
				"' AND PROSPECT_UPDATEDDATE < '" + 
				getParameters().getEndDate() + 
				"' ";
		
		casesQuery += " AND CRM_TRN_PROSPECT.PROSPECT_PAGESOURCE='UPDATED' " ;
		casesQuery += " GROUP BY PROSPECT_UPDATEDDATE,CRM_MST_USER.USER_USERID, CRM_TRN_PROSPECT.PROSPECT_CONSUMESERVICEID";

		Aggregation reportGrainData = new Aggregation();

		String userID, caseID, reportGrain;

		int timeGrain, dateFormat;

		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		for(String[] row:  dbConnection.runQuery(casesQuery))
		{
			userID = row[1];

			if(roster.hasUser(userID) )
			{
				caseID = row[2];

				roster.getUser(userID).addAttribute(CASE_IDS_ATTR);
				roster.getUser(userID).addData(CASE_IDS_ATTR, caseID);

				//time grain for time reports
				if(isTimeTrendReport())
				{
					timeGrain = Integer.parseInt(getParameters().getTimeGrain());
					dateFormat = Integer.parseInt(getParameters().getDateFormat());
					reportGrain = DateFormatter.getFormattedDate(DateParser.convertSQLDateToGregorian(row[0]), timeGrain, dateFormat);

					reportGrainData.addDatum(reportGrain);
					reportGrainData.getDatum(reportGrain).addAttribute(CASE_IDS_ATTR);
					reportGrainData.getDatum(reportGrain).addData(CASE_IDS_ATTR, caseID);
				}
			}
		}
		
		for( Entry<String, String> queryStats  : dbConnection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}

		/////////////////
		//processing the buckets
		//want to put this into it's own method, can't think of a way without introducing terrible coupling

		retval =  new ArrayList<String[]>();

		int pinCount;
		if(isStackReport() )
		{
			String userGrain = getParameters().getUserGrain();
			
			HashMap<String, Integer> stack = new HashMap<String, Integer>();

			String fullName;
			Datum user;
			for(String id : roster.getUserIDs())
			{
				user = roster.getUser(id);

				pinCount = 0;
				
				if(userGrain.equals("" + UserGrains.AGENT_GRANULARITY ))
				{
					fullName = roster.getFullName(user.getAttributeData(PrivateLabelRoster.USER_ID_ATTR).get(0));
				}
				else
				{
					fullName = user.getAttributeData(PrivateLabelRoster.TEAMNAME_ATTR).get(0);
				}
				
				if( !user.addAttribute(CASE_IDS_ATTR))
				{
					pinCount = user.getAttributeData(CASE_IDS_ATTR).size();
				}
				
				if(stack.containsKey(fullName))
				{
					stack.put(fullName, stack.get(fullName) + pinCount);
				}
				else
				{
					stack.put(fullName, pinCount);
				}
			}

			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
			}
		}
		else if(isTimeTrendReport())
		{
			for(String user : reportGrainData.getDatumIDList())
			{
				//not all users will have refunds
				pinCount = 0;
				if( reportGrainData.getDatum(user).getAttributeData(CASE_IDS_ATTR) != null)
				{
					pinCount = reportGrainData.getDatum(user).getAttributeData(CASE_IDS_ATTR).size();
				}

				retval.add(new String[]{user, "" + pinCount }) ;
			}
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
		
		retval.add("PINs Consumed");
		
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
