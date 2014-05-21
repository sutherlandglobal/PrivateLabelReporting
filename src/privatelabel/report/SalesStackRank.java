package privatelabel.report;
/**
 * 
 */

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Aggregation;
import helios.data.granularity.user.UserGrains;
import helios.date.parsing.DateParser;
import helios.exceptions.ExceptionFormatter;
import helios.exceptions.ReportSetupException;
import helios.logging.LogIDFactory;
import helios.report.Report;
import helios.report.ReportRunner;
import helios.report.parameters.groups.ReportParameterGroups;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * @author Jason Diamond
 *
 */
public class SalesStackRank extends Report
{
	private final static String NAME_ATTR = "userName";
	private final static String NUM_SALES_ATTR = "grossSales";
	private final static String NUM_REFUNDS_ATTR = "numRefunds";
	private final static String TOTAL_SALES_ATTR = "totalSales";
	private final static String TOTAL_REFUNDS_ATTR = "totalRefunds";
	private final static String AOV_ATTR = "aov";
	private final static String CALL_VOL_ATTR = "callVolume";
	private final static String CONV_ATTR = "conversion";
	private final static String NET_REV_ATTR = "netRev";
	private final static String NET_SALES_COUNT_ATTR = "netSalesCount";
	
	private final static Logger logger = Logger.getLogger(SalesStackRank.class);
	
	private SalesCount salesCountReport;
	private RefundCount refundCountReport;
	private RealtimeSales realtimeSalesReport;
	private RefundTotals refundTotalsReport;
	private CallVolume callVolumeReport;
	private Conversion conversionReport;
	private AverageOrderValue aovReport;
	private NetRevenue netRevReport;
	private NetSalesCount netSalesCountReport;

	public static String uiGetReportName()
	{
		return "Sales Stack Rank";
	}
	
	public static String uiGetReportDesc()
	{
		return "Stack ranking of Sales-based metric.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.STACK_RANK_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.STACK_RANK_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public SalesStackRank() throws ReportSetupException
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
			reportName = SalesStackRank.uiGetReportName();
			reportDesc = SalesStackRank.uiGetReportDesc();
			
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
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{

		ArrayList<String[]> retval = null;

		String endDate = getParameters().getEndDate();

		
		//call stats update only once per day because stupid reasons. only valid to have report current to previous day.
		GregorianCalendar previousDay = new GregorianCalendar();
		previousDay.add(Calendar.DAY_OF_MONTH, -1);
		previousDay.set(Calendar.HOUR_OF_DAY, 23);
		previousDay.set(Calendar.MINUTE, 59);
		previousDay.set(Calendar.SECOND, 59);
		
		if(!DateParser.convertSQLDateToGregorian(endDate).before(previousDay))
		{
			endDate = DateParser.toSQLDateFormat(previousDay);
		}

		retval = new ArrayList<String[]>();


		Aggregation users = new Aggregation();
		
		ReportRunner runner = new ReportRunner();

		salesCountReport = new SalesCount();
		salesCountReport.setChildReport(true);
		salesCountReport.setParameters(getParameters());
		salesCountReport.getParameters().setEndDate(endDate);

		refundCountReport = new RefundCount();
		refundCountReport.setChildReport(true);
		refundCountReport.setParameters(getParameters());
		refundCountReport.getParameters().setEndDate(endDate);

		realtimeSalesReport = new RealtimeSales();
		realtimeSalesReport.setChildReport(true);
		realtimeSalesReport.setParameters(getParameters());
		realtimeSalesReport.getParameters().setEndDate(endDate);

		refundTotalsReport = new RefundTotals();
		refundTotalsReport.setChildReport(true);
		refundTotalsReport.setParameters(getParameters());
		refundTotalsReport.getParameters().setEndDate(endDate);

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameters(getParameters());
		callVolumeReport.getParameters().setEndDate(endDate);

		conversionReport = new Conversion();
		conversionReport.setChildReport(true);
		conversionReport.setParameters(getParameters());
		conversionReport.getParameters().setEndDate(endDate);

		aovReport = new AverageOrderValue();
		aovReport.setChildReport(true);
		aovReport.setParameters(getParameters());
		aovReport.getParameters().setEndDate(endDate);
		
		netRevReport = new NetRevenue();
		netRevReport.setChildReport(true);
		netRevReport.setParameters(getParameters());
		netRevReport.getParameters().setEndDate(endDate);
		
		netSalesCountReport = new NetSalesCount();
		netSalesCountReport.setChildReport(true);
		netSalesCountReport.setParameters(getParameters());
		netSalesCountReport.getParameters().setEndDate(endDate);
		
		runner.addReport(NUM_SALES_ATTR, salesCountReport);
		runner.addReport(NUM_REFUNDS_ATTR, refundCountReport);
		runner.addReport(TOTAL_SALES_ATTR, realtimeSalesReport);
		runner.addReport(TOTAL_REFUNDS_ATTR, refundTotalsReport);
		runner.addReport(CALL_VOL_ATTR, callVolumeReport);
		runner.addReport(CONV_ATTR, conversionReport);
		runner.addReport(AOV_ATTR, aovReport);
		runner.addReport(NET_REV_ATTR, netRevReport);
		runner.addReport(NET_SALES_COUNT_ATTR, netSalesCountReport);

		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			for(String[] row : runner.getResults(NUM_SALES_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);
	
				users.getDatum(row[0]).addAttribute(NUM_SALES_ATTR );
				users.getDatum(row[0]).addData(NUM_SALES_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(NUM_REFUNDS_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(NUM_REFUNDS_ATTR );
				users.getDatum(row[0]).addData(NUM_REFUNDS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(TOTAL_SALES_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(TOTAL_SALES_ATTR );
				users.getDatum(row[0]).addData(TOTAL_SALES_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(TOTAL_REFUNDS_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(TOTAL_REFUNDS_ATTR  );
				users.getDatum(row[0]).addData(TOTAL_REFUNDS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(CALL_VOL_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(CALL_VOL_ATTR   );
				users.getDatum(row[0]).addData(CALL_VOL_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(CONV_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(CONV_ATTR    );
				users.getDatum(row[0]).addData(CONV_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(AOV_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(AOV_ATTR  );
				users.getDatum(row[0]).addData(AOV_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(NET_REV_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(NET_REV_ATTR  );
				users.getDatum(row[0]).addData(NET_REV_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(NET_SALES_COUNT_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);

				users.getDatum(row[0]).addAttribute(NET_SALES_COUNT_ATTR  );
				users.getDatum(row[0]).addData(NET_SALES_COUNT_ATTR, row[1]);
			}
		}

		int userGrain;
		String reportGrain;
		for(String userName : users.getDatumIDList())
		{
			userGrain = Integer.parseInt(getParameters().getUserGrain());
			reportGrain = UserGrains.getUserGrain(userGrain, users.getDatum(userName));

			int numSales = 0;
			int numRefunds = 0;

			double totalSales = 0;
			double totalRefunds = 0;

			int callVolume = 0;

			double conversion = 0; 
			double aov = 0;
			double netRev = 0;
			int netSalesCount =0;

			if(users.getDatum(userName).getAttributeData(NUM_SALES_ATTR) != null )
			{
				numSales = Integer.parseInt(users.getDatum(userName).getAttributeData(NUM_SALES_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(NUM_REFUNDS_ATTR) != null)
			{
				numRefunds = Integer.parseInt(users.getDatum(userName).getAttributeData(NUM_REFUNDS_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(TOTAL_SALES_ATTR) != null)
			{
				totalSales = Double.parseDouble(users.getDatum(userName).getAttributeData(TOTAL_SALES_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(TOTAL_REFUNDS_ATTR) != null)
			{
				totalRefunds = Double.parseDouble(users.getDatum(userName).getAttributeData(TOTAL_REFUNDS_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(CALL_VOL_ATTR) != null)
			{
				callVolume = Integer.parseInt(users.getDatum(userName).getAttributeData(CALL_VOL_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(CONV_ATTR) != null)
			{
				conversion = Double.parseDouble(users.getDatum(userName).getAttributeData(CONV_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(AOV_ATTR) != null)
			{
				aov = Double.parseDouble(users.getDatum(userName).getAttributeData(AOV_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(NET_REV_ATTR) != null)
			{
				netRev = Double.parseDouble(users.getDatum(userName).getAttributeData(NET_REV_ATTR).get(0));
			}
			
			if(users.getDatum(userName).getAttributeData(NET_SALES_COUNT_ATTR) != null)
			{
				netSalesCount = Integer.parseInt(users.getDatum(userName).getAttributeData(NET_SALES_COUNT_ATTR).get(0));
			}
			
			retval.add(new String[]
					{
					reportGrain, 
					"" + numSales, 
					"" + numRefunds, 
					"" + netSalesCount, 
					"" + totalSales, 
					"" + totalRefunds, 
					"" + netRev,
					"" + callVolume,
					"" + conversion,
					"" + aov, 
					});
		}

		return retval;
	}
	
	@Override
	public ArrayList<String> getReportSchema() 
	{
		ArrayList<String> retval = new ArrayList<String>();
		
		retval.add("User Grain");
		retval.add("Sales Count");
		retval.add("Refund Count");
		retval.add("Net Sales Count");
		retval.add("Total Sales ($)");
		retval.add("Total Refunds ($)");
		retval.add("Net Revenue ($)");
		retval.add("Call Volume");
		retval.add("Conversion (%)");
		retval.add("AOV ($)");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(salesCountReport != null)
		{
			salesCountReport.close();
		}

		if(refundCountReport != null)
		{
			refundCountReport.close();
		}

		if(realtimeSalesReport != null)
		{
			realtimeSalesReport.close();
		}

		if(refundTotalsReport != null)
		{
			refundTotalsReport.close();
		}

		if(callVolumeReport != null)
		{
			callVolumeReport.close();
		}

		if(conversionReport != null)
		{
			conversionReport.close();
		}

		if(aovReport != null)
		{
			aovReport.close();
		}
		
		if(netRevReport != null)
		{
			netRevReport.close();
		}
		
		if(netSalesCountReport != null)
		{
			netSalesCountReport.close();
		}
		
		super.close();
		
		if (!isChildReport) 
		{
			MDC.remove(LOG_ID_PREFIX);
		}
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupDataSourceConnections()
	 */
	@Override
	protected boolean setupDataSourceConnections()
	{
		boolean retval = true;

		//connectivity tests handled by subreports

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
