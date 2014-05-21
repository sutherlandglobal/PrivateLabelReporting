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
public final class CallsPerCase extends Report 
{
	private final static String CALL_VOLUME_ATTR = "callVolume";
	private final static String UPDATED_CASES_ATTR = "updatedCases";
	private final static String CREATED_CASES_ATTR = "createdCases";

	private UpdatedCases updatedCasesReport;
	private CreatedCases createdCasesReport;
	private CallVolume callVolumeReport;
	
	private final static Logger logger = Logger.getLogger(CallsPerCase.class);

	public static String uiGetReportName()
	{
		return "Calls Per Case";
	}
	
	public static String uiGetReportDesc()
	{
		return "The number of calls received over a case's lifetime.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public CallsPerCase() throws ReportSetupException
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
			reportName = CallsPerCase.uiGetReportName();
			reportDesc = CallsPerCase.uiGetReportDesc();
			
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
		return true;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(createdCasesReport != null)
		{
			createdCasesReport.close();
		}
		
		if(updatedCasesReport != null)
		{
			updatedCasesReport.close();
		}
		
		if(callVolumeReport != null)
		{
			callVolumeReport.close();
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
		
		retval.add("Calls per Case");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{
		ArrayList<String[]> retval = null; 
		
		ReportRunner runner = new ReportRunner();

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameters(getParameters());

		updatedCasesReport = new UpdatedCases();
		updatedCasesReport.setChildReport(true);
		updatedCasesReport.setParameters(getParameters());
		
		createdCasesReport = new CreatedCases();
		createdCasesReport.setChildReport(true);
		createdCasesReport.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();

		String reportGrain, numUpdatedCases, numCalls, numCreatedCases;

		runner.addReport(UPDATED_CASES_ATTR, updatedCasesReport);
		runner.addReport(CALL_VOLUME_ATTR, callVolumeReport);
		runner.addReport(CREATED_CASES_ATTR, createdCasesReport);
		
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(UPDATED_CASES_ATTR))
			{
				numUpdatedCases = row[1];

				//eport gives results in [report grain] [val]
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(UPDATED_CASES_ATTR);
				reportGrainData.getDatum(reportGrain).addData(UPDATED_CASES_ATTR, numUpdatedCases);

			}			

			/////////////////

			for(String[] row : runner.getResults(CALL_VOLUME_ATTR))
			{
				numCalls = row[1];

				//report gives results in [report grain] [val]
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(CALL_VOLUME_ATTR);
				reportGrainData.getDatum(reportGrain).addData(CALL_VOLUME_ATTR, numCalls);
			}
			
			/////////////////

			for(String[] row : runner.getResults(CREATED_CASES_ATTR))
			{
				numCreatedCases = row[1];

				//SalesCount report gives results in [report grain] [val]
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(CREATED_CASES_ATTR);
				reportGrainData.getDatum(reportGrain).addData(CREATED_CASES_ATTR, numCreatedCases);
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalCreatedCases, finalCallVolume, finalUpdatedCases, callsPerCase;


		for(String grain : reportGrainData.getDatumIDList())
		{
			callsPerCase = 0;

			if(!reportGrainData.getDatum(grain).addAttribute(UPDATED_CASES_ATTR) && !reportGrainData.getDatum(grain).addAttribute(CALL_VOLUME_ATTR) && !reportGrainData.getDatum(grain).addAttribute(CREATED_CASES_ATTR))
			{
				finalCallVolume = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(CALL_VOLUME_ATTR));
				finalUpdatedCases = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(UPDATED_CASES_ATTR));
				finalCreatedCases = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(CREATED_CASES_ATTR));

				if(finalCallVolume != 0)
				{
					callsPerCase = (finalUpdatedCases + finalCreatedCases)/finalCallVolume;
				}
			}

			retval.add(new String[]{	grain,NumberFormatter.convertToCurrency(callsPerCase)}	);
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
