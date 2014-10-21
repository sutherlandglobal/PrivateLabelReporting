/**
 * 
 */
package com.sutherland.privatelabel.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sutherland.helios.api.report.frontend.ReportFrontEndGroups;
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
public final class LMIDSATCases extends Report 
{
	private RemoteConnection dbConnection;
	
	private final static String[] FLAGGED_COMMENT_WORDS = {"unhappy","awful","worthless","fuck","crap","bitch","shit","suck","stupid","terrible","horrible", "asshole","jerk","unsatisfied","refund", "hate", "insult", "unprofessional","sarcastic", "angry", "disappointed","wors","rude"}; 
	private final static String[] FLAGGED_COMMENT_PHRASES = {"not satisfied", "money back", "bad service", "not happy", "not resolved","better business bureau","not recommend"}; 
	
	private final String dbPropFile = DatabaseConfigs.PRIVATE_LABEL_DEV_DB;
	private PrivateLabelRoster roster;
	private final static Logger logger = Logger.getLogger(LMIDSATCases.class);

	public static String uiGetReportName()
	{
		return "LMI DSAT Cases";
	}
	
	public static String uiGetReportDesc()
	{
		return "Customer dissatisfaction survey details for LMI surveys.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.STACK_RANK_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.STACK_RANK_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LMIDSATCases() throws ReportSetupException 
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
			reportName = LMIDSATCases.uiGetReportName();
			reportDesc = LMIDSATCases.uiGetReportDesc(); 
			
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
		
		retval.add("SessionID");
		retval.add("CustName");
		retval.add("TechName");
		retval.add("Q6");
		retval.add("Comments");
		retval.add("Case Count");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		String L1query = "SELECT Date,Session_ID,Customer_Name,Technician_Name,Technician_ID,Q6,Comments FROM LMI_6971911_Customer_Survey WHERE Date >= '" + 
				getParameters().getStartDate() + 
				"' AND Date < '" + 
				getParameters().getEndDate() + 
				"'"; 
		
		String L2query = "SELECT Date,Session_ID,Customer_Name,Technician_Name,Technician_ID,Q6,Comments FROM LMI_5452038_Customer_Survey WHERE Date >= '" + 
				getParameters().getStartDate() + 
				"' AND Date < '" + 
				getParameters().getEndDate() + 
				"'";
		
		ArrayList<String> queries = new ArrayList<String>();
		queries.add(L1query);
		queries.add(L2query);

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
		
		String surveyComments;
		String q6;
		String tID;
		String userID;
		
		for(Entry<String, ArrayList<String[]>> query :aggregateResults.entrySet()  )
		{
			for(String[] row : query.getValue())
			{
				tID = row[4];
				userID = roster.lookupUserByAttributeName(tID, PrivateLabelRoster.LMI_LOGIN_NODE_ID_ATTR);
				if(roster.hasUser(userID) )
				{
					surveyComments = row[6];
					q6 = row[5];

					if(q6 != null && (q6.equals("1") || q6.equals("2") || q6.equals("3") ))
					{
						retval.add(row);
					}
					else if(surveyComments != null)
					{
						boolean isDSAT = false;
						for(String word : surveyComments.split("\\s+"))
						{
							for(String flaggedWord : FLAGGED_COMMENT_WORDS)
							{
								if(word.toLowerCase().equals(flaggedWord.toLowerCase()))
								{
									isDSAT = true;
									break;
								}
							}

							if(!isDSAT)
							{
								String whitespaceCleanedComments = surveyComments.replaceAll("\\s+", " ").toLowerCase();

								for(String flaggedPhrase : FLAGGED_COMMENT_PHRASES)
								{
									if(whitespaceCleanedComments.contains(flaggedPhrase.toLowerCase()))
									{
										isDSAT = true;
										break;
									}
								}
							}

							if(isDSAT)
							{
								retval.add(row);
								break;
							}
						}
					}
				}
			}
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
