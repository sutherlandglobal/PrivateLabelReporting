/**
 * 
 */
package privatelabel.report;

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Aggregation;
import helios.exceptions.ExceptionFormatter;
import helios.exceptions.ReportSetupException;
import helios.formatting.NumberFormatter;
import helios.logging.LogIDFactory;
import helios.report.Report;
import helios.report.ReportRunner;
import helios.report.parameters.groups.ReportParameterGroups;
import helios.statistics.Statistics;

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
public final class AverageHandleTime extends Report 
{
	private final static String HANDLE_TIME_ATTR = "handleTime";
	private final static String CALL_VOL_ATTR = "callVol";
	private CallVolume callVolumeReport;
	private HandleTime handleTimeReport;
	private final static Logger logger = Logger.getLogger(AverageHandleTime.class);

	public static String uiGetReportName()
	{
		return "Average Handle Time";
	}
	
	public static String uiGetReportDesc()
	{
		return "Average Handle Time.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AverageHandleTime() throws ReportSetupException
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
			reportName = AverageHandleTime.uiGetReportName();
			reportDesc = AverageHandleTime.uiGetReportDesc();
			
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
			
			logErrorMessage( getErrorMessage());
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
		return true;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{		
		if(callVolumeReport != null)
		{
			callVolumeReport.close();
		}
		
		if(handleTimeReport != null)
		{
			handleTimeReport.close();
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
		
		retval.add("Average Handle Time (Minutes)");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{
		//#calls fielded vs # orders made
		
		ArrayList<String[]> retval = null; 
		
		ReportRunner runner = new ReportRunner();
		
		handleTimeReport = new HandleTime();
		handleTimeReport.setChildReport(true);
		handleTimeReport.setParameters(getParameters());

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();
		String reportGrain;

		runner.addReport(CALL_VOL_ATTR, callVolumeReport);
		runner.addReport(HANDLE_TIME_ATTR, handleTimeReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(CALL_VOL_ATTR))
			{
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(CALL_VOL_ATTR );
				reportGrainData.getDatum(reportGrain).addData(CALL_VOL_ATTR, row[1]);
			}			

			for(String[] row : runner.getResults(HANDLE_TIME_ATTR))
			{
				reportGrain = row[0];
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(HANDLE_TIME_ATTR );
				reportGrainData.getDatum(reportGrain).addData(HANDLE_TIME_ATTR, row[1]);
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalNumCalls, finalHandleTime, finalAHT;
		

		for(String datumID : reportGrainData.getDatumIDList())
		{
			finalAHT = 0;
			finalNumCalls = Statistics.getTotal(reportGrainData.getDatum(datumID).getAttributeData(CALL_VOL_ATTR));
			finalHandleTime = Statistics.getTotal(reportGrainData.getDatum(datumID).getAttributeData(HANDLE_TIME_ATTR));
			
			if(finalNumCalls != 0)
			{
				finalAHT = finalHandleTime/finalNumCalls;
			}

			retval.add(new String[]{datumID, "" + NumberFormatter.convertToCurrency(finalAHT) }) ;
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
