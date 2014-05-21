/**
 * 
 */
package privatelabel.report;

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Aggregation;
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

/**
 * @author Jason Diamond
 *
 */
public final class LMIDSATVolume extends Report 
{

	private LMIDSATCases dsatCaseReport;
	private static String DSAT_CASE_ATTR = "dsatCases";
	private final static Logger logger = Logger.getLogger(LMIDSATVolume.class);

	public static String uiGetReportName()
	{
		return "LMI DSAT Volume";
	}
	
	public static String uiGetReportDesc()
	{
		return "Customer dissatisfaction survey count for LMI surveys.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LMIDSATVolume() throws ReportSetupException 
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
			reportName = LMIDSATVolume.uiGetReportName();
			reportDesc = LMIDSATVolume.uiGetReportDesc(); 
			
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
		
		dsatCaseReport = new LMIDSATCases();
		dsatCaseReport.setChildReport(true);
		dsatCaseReport.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();

		String reportGrain;
		//int timeGrain, userGrain;

		for(String[] row : dsatCaseReport.runReport())
		{
			reportGrain = row[4];

			reportGrainData.addDatum(reportGrain);
			reportGrainData.getDatum(reportGrain).addAttribute(DSAT_CASE_ATTR );
			reportGrainData.getDatum(reportGrain).addData(DSAT_CASE_ATTR, row[0]);
		}

		int dsatCount;
		for(String user : reportGrainData.getDatumIDList())
		{
			//not all users will have refunds
			dsatCount = 0;
			if( reportGrainData.getDatum(user).getAttributeData(DSAT_CASE_ATTR) != null)
			{
				dsatCount = reportGrainData.getDatum(user).getAttributeData(DSAT_CASE_ATTR).size();
			}

			retval.add(new String[]{user, "" + dsatCount }) ;
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
