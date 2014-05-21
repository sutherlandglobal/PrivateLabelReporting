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
public final class PINConsumptionRate extends Report 
{
	private final static String PINS_CONSUMED_ATTR = "pinsConsumed";
	private final static String OPENED_CASES_ATTR = "openedCases";
	private OpenedCases openedCasesReport;
	private PINsConsumedCount pinsConsumedReport;
	private final static Logger logger = Logger.getLogger(PINConsumptionRate.class);

	public static String uiGetReportName()
	{
		return "PIN Consumption Rate";
	}
	
	public static String uiGetReportDesc()
	{
		return "Trends rate of PIN consumption.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public PINConsumptionRate() throws ReportSetupException
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
			reportName = PINConsumptionRate.uiGetReportName();
			reportDesc = PINConsumptionRate.uiGetReportDesc();
			
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
		if(openedCasesReport != null)
		{
			openedCasesReport.close();
		}
		
		if(pinsConsumedReport != null)
		{
			pinsConsumedReport.close();
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
		
		retval.add("Consumption Rate (%)");
		
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

		pinsConsumedReport = new PINsConsumedCount();
		pinsConsumedReport.setChildReport(true);
		pinsConsumedReport.setParameters(getParameters());

		openedCasesReport = new OpenedCases();
		openedCasesReport.setChildReport(true);
		openedCasesReport.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();

		String reportGrain, numOpenedCases;

		runner.addReport(OPENED_CASES_ATTR, openedCasesReport);
		runner.addReport(PINS_CONSUMED_ATTR, pinsConsumedReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(OPENED_CASES_ATTR))
			{
				numOpenedCases = row[1];
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(OPENED_CASES_ATTR);
				reportGrainData.getDatum(reportGrain).addData(OPENED_CASES_ATTR, numOpenedCases);
			}			

			/////////////////

			String numPINsConsumed;

			for(String[] row : runner.getResults(PINS_CONSUMED_ATTR))
			{
				numPINsConsumed = row[1];
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(PINS_CONSUMED_ATTR);
				reportGrainData.getDatum(reportGrain).addData(PINS_CONSUMED_ATTR, numPINsConsumed);
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalNumCalls, finalNumOrders, finalPinConsumptionRate;
	

		for(String grain : reportGrainData.getDatumIDList())
		{
			finalPinConsumptionRate = 0;
			finalNumCalls = 0;
			finalNumOrders = 0;

			if(!reportGrainData.getDatum(grain).addAttribute(OPENED_CASES_ATTR) && !reportGrainData.getDatum(grain).addAttribute(PINS_CONSUMED_ATTR))
			{
				finalNumCalls = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(OPENED_CASES_ATTR));
				finalNumOrders = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(PINS_CONSUMED_ATTR));

				if(finalNumCalls != 0)
				{
					finalPinConsumptionRate = finalNumOrders/finalNumCalls;
				}
			}

			retval.add(new String[]{	grain,NumberFormatter.convertToPercentage(finalPinConsumptionRate, 4)}	);
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
