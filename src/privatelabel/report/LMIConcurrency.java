/**
 * 
 */
package privatelabel.report;

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Aggregation;
import helios.data.granularity.time.TimeGrains;
import helios.data.granularity.user.UserGrains;
import helios.database.connection.SQL.ConnectionFactory;
import helios.database.connection.SQL.RemoteConnection;
import helios.date.parsing.DateParser;
import helios.exceptions.DatabaseConnectionCreationException;
import helios.exceptions.ExceptionFormatter;
import helios.exceptions.ReportSetupException;
import helios.logging.LogIDFactory;
import helios.report.Report;
import helios.report.parameters.groups.ReportParameterGroups;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import privatelabel.constants.Constants;


/**
 * @author Jason Diamond
 *
 */
public final class LMIConcurrency extends Report {

	private RemoteConnection dbConnection;

	private final static String ALL_SURVEYS_ATTR = "allSurveys";
	private final String dbPropFile = Constants.PRIVATE_LABEL_DEV_DB;
	private PrivateLabelRoster roster;
	private final static Logger logger = Logger.getLogger(LMIConcurrency.class);
	
	public static String uiGetReportName()
	{
		return "LMI Concurrency";
	}
	
	public static String uiGetReportDesc()
	{
		return "Concurrent LMI Sessions.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LMIConcurrency() throws ReportSetupException 
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
			reportName = LMIConcurrency.uiGetReportName();
			reportDesc = LMIConcurrency.uiGetReportDesc(); 
			
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
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
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
		
		retval.add("Case Count");
		
		return retval;
	}
	
	private double convertIntervalLengthToSeconds(String interval)
	{
		//interval in HH:MM:SS
		
		double retval = -1;
		
		String[] parts = interval.split(":");
		
		try
		{
			retval = (60 * 60 * Double.parseDouble(parts[0])) + (60*Double.parseDouble(parts[1])) + (Double.parseDouble(parts[2]));
		}
		catch(Exception e)
		{
			setErrorMessage("Error occured computing interval length");
			
			logErrorMessage(getErrorMessage());
			logErrorMessage( ExceptionFormatter.asString(e));
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

		String L1query = "Select Start_Time,Technician_ID,Active_Time,Work_Time FROM LMI_6971911_Session Where " + 
				"Start_Time >= '" + 
				getParameters().getStartDate() + 
				"' AND End_Time <= '" + 
				getParameters().getEndDate() + "'";

		String L2query = "Select Start_Time,Technician_ID,Active_Time,Work_Time FROM LMI_5452038_Session Where " + 
				"Start_Time >= '" + 
				getParameters().getStartDate() + 
				"' AND End_Time <= '" + 
				getParameters().getEndDate() + "'";

		ArrayList<String> queries = new ArrayList<String>();
		queries.add(L1query);
		queries.add(L2query);

		Aggregation reportData = new Aggregation();

		String reportGrain, tID, userID, conc;
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		Map<String, ArrayList<String[]>> aggregateResults = dbConnection.runParallelQueries(queries);
		
		for( Entry<String, String> queryStats  : dbConnection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain, userGrain;
		

		
		for(Entry<String, ArrayList<String[]>> query : aggregateResults.entrySet()  )
		{
			for(String[] row : query.getValue())
			{
				tID = row[1];
				userID = roster.lookupUserByAttributeName(tID, PrivateLabelRoster.LMI_LOGIN_NODE_ID_ATTR);
				if(roster.hasUser(userID) )
				{
					if(isTimeTrendReport())
					{
						timeGrain = Integer.parseInt(getParameters().getTimeGrain());
						reportGrain = TimeGrains.getDateGrain(timeGrain, DateParser.convertSQLDateToGregorian(row[0]));
					}
					else //if(isStackReport())
					{
						userGrain = Integer.parseInt(getParameters().getUserGrain());
						reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(userID));
					}

					//active_/work_time
					//times are in HH:MM:SS
					
					conc = "" + convertIntervalLengthToSeconds(row[2])/convertIntervalLengthToSeconds(row[3]);
					
					reportData.addDatum(reportGrain);
					reportData.getDatum(reportGrain).addAttribute(ALL_SURVEYS_ATTR);
					reportData.getDatum(reportGrain).addData(ALL_SURVEYS_ATTR, conc);

				}
			}
		}

		double numSatCases;
		for(String grain : reportData.getDatumIDList())
		{
			numSatCases = reportData.getDatum(grain).getAttributeData(ALL_SURVEYS_ATTR).size();
			retval.add(new String[]{grain, "" + numSatCases });
		}

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
