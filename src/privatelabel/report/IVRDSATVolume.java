/**
 * 
 */
package privatelabel.report;

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Aggregation;
import helios.data.granularity.time.TimeGrains;
import helios.data.granularity.user.UserGrains;
import helios.date.parsing.DateParser;
import helios.exceptions.ExceptionFormatter;
import helios.exceptions.ReportSetupException;
import helios.logging.LogIDFactory;
import helios.report.Report;
import helios.report.parameters.groups.ReportParameterGroups;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import privatelabel.report.roster.Attributes;

/**
 * @author Jason Diamond
 *
 */
public final class IVRDSATVolume extends Report {

	private IVRDSATCases dsatCaseReport;

	private PrivateLabelRoster roster;

	private static String DSAT_CASE_ATTR = "dsatCases";
	private final static Logger logger = Logger.getLogger(IVRDSATVolume.class);

	public static String uiGetReportName()
	{
		return  "IVR DSAT Volume";
	}
	
	public static String uiGetReportDesc()
	{
		return "Totals the number of IVR DSAT surveys.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRDSATVolume() throws ReportSetupException 
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
			reportName = IVRDSATVolume.uiGetReportName();
			reportDesc = IVRDSATVolume.uiGetReportDesc();
			
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
		boolean retval = true;

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
		
		if(dsatCaseReport != null)
		{
			dsatCaseReport.close();
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

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();
		
		dsatCaseReport = new IVRDSATCases();
		dsatCaseReport.setChildReport(true);
		dsatCaseReport.setParameters(getParameters());
		
		//hardcode user granularity, so we can resolve ntlogins to userids
		dsatCaseReport.getParameters().setUserGrain(UserGrains.AGENT_GRANULARITY);
		
		Aggregation reportGrainData = new Aggregation();

		int timeGrain, userGrain;
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		String reportGrain, rID, ntlogin, targetUserID;

		for(String[] row : dsatCaseReport.startReport())
		{
			//row from subreport is in: {usergrain, rID,caseID,surveyDate,targetUserID,surveyString};
			ntlogin = row[0];
			
			targetUserID = roster.lookupUserByAttributeName(ntlogin, Attributes.NTLOGIN_ATTR);
			
			if(targetUserID != null && roster.hasUser(targetUserID) )
			{
				rID  = row[1];

				//time grain for time reports
				if(isTimeTrendReport())
				{
					timeGrain = Integer.parseInt(getParameters().getTimeGrain());
					reportGrain = TimeGrains.getDateGrain(timeGrain, DateParser.convertSQLDateToGregorian(row[3]));
				}
				else //if(isStackReport())
				{
					//is stack report
					userGrain = Integer.parseInt(getParameters().getUserGrain());
					reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(targetUserID));
				}	
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(DSAT_CASE_ATTR );
				reportGrainData.getDatum(reportGrain).addData(DSAT_CASE_ATTR, rID);
			}
		}

		int dsatCount;
		for(String grain : reportGrainData.getDatumIDList())
		{
			//not all user/dates will have dsat cases
			dsatCount = 0;

			if( reportGrainData.getDatum(grain).getAttributeData(DSAT_CASE_ATTR) != null)
			{
				dsatCount = reportGrainData.getDatum(grain).getAttributeData(DSAT_CASE_ATTR).size();
			}

			retval.add(new String[]{grain, "" + dsatCount }) ;
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
