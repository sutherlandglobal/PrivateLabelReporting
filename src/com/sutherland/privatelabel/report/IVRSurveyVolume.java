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
public class IVRSurveyVolume extends Report  implements DataAttributes
{
	private RemoteConnection dbConnection;
	private PrivateLabelRoster roster;
	private final String dbPropFile = DatabaseConfigs.PRIVATE_LABEL_PROD_DB;
	private final static Logger logger = Logger.getLogger(IVRSurveyVolume.class);
	
	public static String uiGetReportName()
	{
		return "IVR Survey Volume";
	}
	
	public static String uiGetReportDesc()
	{
		return "A tally of IVR surveys.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRSurveyVolume() throws ReportSetupException 
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
			reportName = IVRSurveyVolume.uiGetReportName();
			reportDesc = IVRSurveyVolume.uiGetReportDesc();
			
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

		if(roster != null)
		{
			roster.close();
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
		
		retval.add("Survey Count");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		//only
		String query = "SELECT " + 
				"tbl_AcerPFSSurveyIVR.RId,CRM_MST_USER.USER_USERID, " +  
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) " + 
				"FROM " + 
				"(tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID) " +
				"WHERE "+ 
				"(Len(tbl_AcerPFSSurveyIVR.Survey_Result)>=7) AND " + 
				" CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) >= '"+
				getParameters().getStartDate()+
				"' AND " +
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) < '"+
				getParameters().getEndDate()+
				"' and user_userid is not null";

		Aggregation reportGrainData = new Aggregation();
		
		String reportGrain, rID, userID;
		
		int timeGrain, userGrain, dateFormat;
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		for(String[] row:  dbConnection.runQuery(query))
		{
			userID = row[0];
			
			if(roster.hasUser(userID) )
			{
				rID = row[2];

				//time grain for time reports
				if(isTimeTrendReport())
				{
					timeGrain = Integer.parseInt(getParameters().getTimeGrain());
					dateFormat = Integer.parseInt(getParameters().getDateFormat());
					reportGrain = DateFormatter.getFormattedDate(DateParser.convertSQLDateToGregorian(row[1]), timeGrain, dateFormat);
				}
				else //if(isStackReport())
				{
					userGrain = Integer.parseInt(getParameters().getUserGrain());
					reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(userID));
				}
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(SURVEY_VOL_ATTR);
				reportGrainData.getDatum(reportGrain).addData(SURVEY_VOL_ATTR, rID);
			}
		}

		for( Entry<String, String> queryStats  : dbConnection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}
		
		double numSurveys;
		for(String grain : reportGrainData.getDatumIDList())
		{
			numSurveys = 0;

			if(!reportGrainData.getDatum(grain).addAttribute(SURVEY_VOL_ATTR))
			{
				numSurveys = reportGrainData.getDatum(grain).getAttributeData(SURVEY_VOL_ATTR).size();
			}

			retval.add(new String[]{grain, "" + numSurveys });
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
