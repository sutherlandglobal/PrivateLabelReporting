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
public final class NetSalesCount extends Report 
{
	private final static String REFUNDS_ATTR = "refunds";
	private final static String SALES_ATTR = "sales";
	private SalesCount salesCount;
	private RefundCount refundCountReport;
	private final static Logger logger = Logger.getLogger(NetSalesCount.class);

	public static String uiGetReportName()
	{
		return "Net Sales Count";
	}
	
	public static String uiGetReportDesc()
	{
		return "Trends net sales count (total sales count - total refund count).";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public NetSalesCount() throws ReportSetupException
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
			reportName = NetSalesCount.uiGetReportName();
			reportDesc = NetSalesCount.uiGetReportDesc();
			
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
		if(salesCount != null)
		{
			salesCount.close();
		}
		
		if(refundCountReport != null)
		{
			refundCountReport.close();
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
		
		retval.add("Net Sales Count");
		
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

		refundCountReport = new RefundCount();
		refundCountReport.setChildReport(true);
		refundCountReport.setParameters(getParameters());

		salesCount = new SalesCount();
		salesCount.setChildReport(true);
		salesCount.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();

		String reportGrain, numSales;

		runner.addReport(SALES_ATTR, salesCount);
		runner.addReport(REFUNDS_ATTR, refundCountReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(SALES_ATTR))
			{
				numSales = row[1];
				reportGrain = row[0];
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(SALES_ATTR);
				reportGrainData.getDatum(reportGrain).addData(SALES_ATTR, numSales);
			}			

			/////////////////

			String orderCount;

			for(String[] row : runner.getResults(REFUNDS_ATTR))
			{
				orderCount = row[1];
				reportGrain = row[0];

				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(REFUNDS_ATTR);
				reportGrainData.getDatum(reportGrain).addData(REFUNDS_ATTR, orderCount);
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalSales, finalRefunds, finalRevenue;

		for(String grain : reportGrainData.getDatumIDList())
		{
			finalRevenue = 0;
			finalSales = 0;
			finalRefunds = 0;

			if(!reportGrainData.getDatum(grain).addAttribute(SALES_ATTR) && !reportGrainData.getDatum(grain).addAttribute(REFUNDS_ATTR))
			{
				finalSales = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(SALES_ATTR));
				finalRefunds = Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(REFUNDS_ATTR));

				finalRevenue = finalSales - finalRefunds;
			}

			retval.add(new String[]{	grain,NumberFormatter.convertToCurrency(finalRevenue)}	);
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
