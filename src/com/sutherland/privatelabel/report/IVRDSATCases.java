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
import com.sutherland.helios.exceptions.DatabaseConnectionCreationException;
import com.sutherland.helios.exceptions.ExceptionFormatter;
import com.sutherland.helios.exceptions.ReportSetupException;
import com.sutherland.helios.logging.LogIDFactory;
import com.sutherland.helios.report.Report;
import com.sutherland.helios.report.parameters.groups.ReportParameterGroups;
import com.sutherland.privatelabel.datasources.DatabaseConfigs;
import com.sutherland.privatelabel.report.roster.Attributes;

/**
 * @author Jason Diamond
 *
 */
public final class IVRDSATCases extends Report implements DataAttributes 
{

	private RemoteConnection dbConnection;
	private final String dbPropFile = DatabaseConfigs.PRIVATE_LABEL_PROD_DB;
	private PrivateLabelRoster roster;
	private final static Logger logger = Logger.getLogger(IVRDSATCases.class);

	public static String uiGetReportName()
	{
		return "IVR DSAT Cases";
	}
	
	public static String uiGetReportDesc()
	{
		return "A list of IVR DSAT Cases.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.STACK_RANK_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.STACK_RANK_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRDSATCases() throws ReportSetupException 
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
			reportName = IVRDSATCases.uiGetReportName();
			reportDesc = IVRDSATCases.uiGetReportDesc();
			
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
		
		retval.add("rID");
		retval.add("Case ID");
		retval.add("Date");
		retval.add("Results");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		String query = "select * from "+
		"(select tbl_AcerPFSSurveyIVR.rID,"+
		"tbl_AcerPFSSurveyIVR.CaseID,"+
		"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) as Date, " + 
		"tbl_AcerPFSSurveyIVR.Survey_Result as Survey, " + 
		"crm_MST_USER.USER_NTLOGINID " +  
		"from " +  
		"(tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID) " +
		") t " + 
		" where t.Date >= '"+ 
		getParameters().getStartDate() +
		"' and " +
		" t.Date < '" + 
		getParameters().getEndDate() + 
		"'  and " +
		" Len(t.Survey) = 6 "; 
				
		String targetUserID;
	
		String rID,caseID,surveyDate,ntlogin,surveyString;
		
		int userGrain;
		String reportGrain;
		
		Aggregation reportGrainData = new Aggregation();

		String[] surveyData; 
		
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.getParameters().setAgentNames(getParameters().getAgentNames());
		roster.getParameters().setTeamNames(getParameters().getTeamNames());
		roster.load();
		
		for(String[] surveyRow : dbConnection.runQuery(query))
		{
			rID  = surveyRow[0];
			caseID = surveyRow[1];
			surveyDate = surveyRow[2];
			ntlogin = surveyRow[4];
			surveyString = surveyRow[3];
			
			targetUserID = roster.lookupUserByAttributeName(ntlogin, Attributes.NTLOGIN_ATTR);
			
			//just add the survey data to the roster's user
			if(targetUserID != null && roster.hasUser(targetUserID) && surveyString.charAt(5) == '2')
			{
				userGrain = Integer.parseInt(getParameters().getUserGrain());
				reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(targetUserID));

				surveyData = new String[]{rID,caseID,surveyDate,targetUserID,surveyString};
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(IVR_DSAT_CASE_ATTR);
				reportGrainData.getDatum(reportGrain).addObject(IVR_DSAT_CASE_ATTR, surveyData);
			}			
		}
		
		for( Entry<String, String> queryStats  : dbConnection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}

		String[] rowData;

		retval = new ArrayList<String[]>(reportGrainData.getSize());

		for(String grain : reportGrainData.getDatumIDList())
		{
			for(Object row : reportGrainData.getDatum(grain).getDatumObjects(IVR_DSAT_CASE_ATTR))
			{
				rowData = (String[]) row;

				retval.add(new String[]{grain, rowData[0],rowData[1], rowData[2], rowData[4]});
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
