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
public final class RedemptionRate extends Report 
{
	private final static String SALES_COUNT_ATTR = "salesCount";
	private final static String USED_ISSUES_ATTR = "usedIssues";
	private UsedIssues usedIssuesReport;
	private SalesCount salesCountReport;
	private final static Logger logger = Logger.getLogger(RedemptionRate.class);
	
	public static String uiGetReportName()
	{
		return "Redemption Rate";
	}
	
	public static String uiGetReportDesc()
	{
		return "Rate of PIN redemption.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public RedemptionRate() throws ReportSetupException
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
			reportName = RedemptionRate.uiGetReportName();
			reportDesc = RedemptionRate.uiGetReportDesc();
			
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
		if(usedIssuesReport != null)
		{
			usedIssuesReport.close();
		}
		
		if(salesCountReport != null)
		{
			salesCountReport.close();
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
		
		retval.add("Redemption Rate (%)");
		
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

		salesCountReport = new SalesCount();
		salesCountReport.setChildReport(true);
		salesCountReport.setParameters(getParameters());

		usedIssuesReport = new UsedIssues();
		usedIssuesReport.setChildReport(true);
		usedIssuesReport.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();

		String reportGrain, usedIssues, salesCount;

		runner.addReport(USED_ISSUES_ATTR, usedIssuesReport);
		runner.addReport(SALES_COUNT_ATTR, salesCountReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(USED_ISSUES_ATTR))
			{
				usedIssues = row[1];
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(USED_ISSUES_ATTR);
				reportGrainData.getDatum(reportGrain).addData(USED_ISSUES_ATTR, usedIssues);
			}			

			/////////////////

			for(String[] row : runner.getResults(SALES_COUNT_ATTR))
			{
				salesCount = row[1];
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(SALES_COUNT_ATTR);
				reportGrainData.getDatum(reportGrain).addData(SALES_COUNT_ATTR, salesCount);
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalUsedIssues, finalSalesCount, finalRedemptionRate;

		for(String grain : reportGrainData.getDatumIDList())
		{
			finalRedemptionRate = 0;
			finalUsedIssues = 0;
			finalSalesCount = 0;

			if(!reportGrainData.getDatum(grain).addAttribute(USED_ISSUES_ATTR) && !reportGrainData.getDatum(grain).addAttribute(SALES_COUNT_ATTR))
			{
				finalUsedIssues = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(USED_ISSUES_ATTR));
				finalSalesCount = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(SALES_COUNT_ATTR));

				if(finalSalesCount != 0)
				{
					finalRedemptionRate = finalUsedIssues/finalSalesCount;
				}
			}

			retval.add(new String[]{	grain,NumberFormatter.convertToPercentage(finalRedemptionRate, 4)}	);
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
