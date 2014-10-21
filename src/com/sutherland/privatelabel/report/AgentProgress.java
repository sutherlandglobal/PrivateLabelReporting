package com.sutherland.privatelabel.report;
/**
 * 
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sutherland.helios.api.report.frontend.ReportFrontEndGroups;
import com.sutherland.helios.data.Aggregation;
import com.sutherland.helios.data.attributes.DataAttributes;
import com.sutherland.helios.date.parsing.DateParser;
import com.sutherland.helios.exceptions.ExceptionFormatter;
import com.sutherland.helios.exceptions.ReportSetupException;
import com.sutherland.helios.logging.LogIDFactory;
import com.sutherland.helios.report.Report;
import com.sutherland.helios.report.ReportRunner;
import com.sutherland.helios.report.parameters.groups.ReportParameterGroups;

/**
 * @author Jason Diamond
 *
 */
public class AgentProgress extends Report implements DataAttributes
{
	private RealtimeSales realtimeSalesReport;
	private RefundTotals refundTotalsReport;
	private CallVolume callVolumeReport;
	private Conversion conversionReport;
	private ScheduleAdherence scheduleAdherenceReport;
	private LateDays lateDaysReport;
	private RevenuePerCall rpcReport;
	private IVRCSATRate ivrCSATReport;
	private AverageOrderValue aovReport;
	
	private final static Logger logger = Logger.getLogger(AgentProgress.class);
	
	public static String uiGetReportName()
	{
		return "Agent Progress";
	}
	
	public static String uiGetReportDesc()
	{
		return "Trends an agent's performance in many metrics.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.STACK_RANK_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.DASHBOARD_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AgentProgress() throws ReportSetupException
	{
		super();
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

	/* (non-Javadoc)
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport()
	{
		boolean retval = false;

		try
		{
			reportName = AgentProgress.uiGetReportName();
			reportDesc = AgentProgress.uiGetReportDesc();
			
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


		Aggregation reportGrainData = new Aggregation();
		
		ReportRunner runner = new ReportRunner();

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

		//schedule adherence
		scheduleAdherenceReport = new ScheduleAdherence();
		scheduleAdherenceReport.setChildReport(true);
		scheduleAdherenceReport.setParameters(getParameters());
		scheduleAdherenceReport.getParameters().setEndDate(endDate);
		
		//late days
		lateDaysReport = new LateDays();
		lateDaysReport.setChildReport(true);
		lateDaysReport.setParameters(getParameters());
		lateDaysReport.getParameters().setEndDate(endDate);
				
		//rpc
		rpcReport = new RevenuePerCall();
		rpcReport.setChildReport(true);
		rpcReport.setParameters(getParameters());
		rpcReport.getParameters().setEndDate(endDate);
		
		//IVR CSAT
		ivrCSATReport = new IVRCSATRate();
		ivrCSATReport.setChildReport(true);
		ivrCSATReport.setParameters(getParameters());
		ivrCSATReport.getParameters().setEndDate(endDate);
		
		aovReport = new AverageOrderValue();
		aovReport.setChildReport(true);
		aovReport.setParameters(getParameters());
		aovReport.getParameters().setEndDate(endDate);
		
		runner.addReport(SALES_AMTS_ATTR, realtimeSalesReport);
		runner.addReport(TOTAL_REFUNDS_ATTR, refundTotalsReport);
		runner.addReport(CALL_VOL_ATTR, callVolumeReport);
		runner.addReport(CONVERSION_ATTR, conversionReport);
		runner.addReport(SCHEDULE_ADH_ATTR, scheduleAdherenceReport);
		runner.addReport(LATE_DAYS_ATTR, lateDaysReport);
		runner.addReport(REV_PER_CALL_ATTR, rpcReport);
		runner.addReport(IVR_CSAT_ATTR, ivrCSATReport);
		runner.addReport(AVG_ORDER_VAL_ATTR, aovReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			for(String[] row : runner.getResults(SALES_AMTS_ATTR))
			{
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);
				reportGrainData.getDatum(row[0]).addData(SALES_AMTS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(TOTAL_REFUNDS_ATTR))
			{
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);
				reportGrainData.getDatum(row[0]).addData(TOTAL_REFUNDS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(CALL_VOL_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(CALL_VOL_ATTR   );
				reportGrainData.getDatum(row[0]).addData(CALL_VOL_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(CONVERSION_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(CONVERSION_ATTR    );
				reportGrainData.getDatum(row[0]).addData(CONVERSION_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(AVG_ORDER_VAL_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(AVG_ORDER_VAL_ATTR    );
				reportGrainData.getDatum(row[0]).addData(AVG_ORDER_VAL_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(SCHEDULE_ADH_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(SCHEDULE_ADH_ATTR    );
				reportGrainData.getDatum(row[0]).addData(SCHEDULE_ADH_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(LATE_DAYS_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(LATE_DAYS_ATTR    );
				reportGrainData.getDatum(row[0]).addData(LATE_DAYS_ATTR, row[1]);
			}

			for(String[] row : runner.getResults(REV_PER_CALL_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(REV_PER_CALL_ATTR    );
				reportGrainData.getDatum(row[0]).addData(REV_PER_CALL_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(IVR_CSAT_ATTR))
			{
				reportGrainData.addDatum(row[0]);
				reportGrainData.getDatum(row[0]).addAttribute(DATE_ATTR );
				reportGrainData.getDatum(row[0]).addData(DATE_ATTR, row[0]);

				reportGrainData.getDatum(row[0]).addAttribute(IVR_CSAT_ATTR    );
				reportGrainData.getDatum(row[0]).addData(IVR_CSAT_ATTR, row[1]);
			}
		}

		for(String thisDateGrain : reportGrainData.getDatumIDList())
		{
			String date = reportGrainData.getDatum(thisDateGrain).getAttributeData(DATE_ATTR).get(0);

			double rpc = 0;
			int lateDays = 0;
			
			double totalSales = 0;
			double totalRefunds = 0;

			int callVolume = 0;

			double conversion = 0; 
			double schAdh = 0;
			
			double ivrCSAT =0;
			double aov = 0;

			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(REV_PER_CALL_ATTR) != null )
			{
				rpc = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(REV_PER_CALL_ATTR).get(0));
			}

			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(SALES_AMTS_ATTR) != null)
			{
				totalSales = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(SALES_AMTS_ATTR).get(0));
			}

			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(TOTAL_REFUNDS_ATTR) != null)
			{
				totalRefunds = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(TOTAL_REFUNDS_ATTR).get(0));
			}

			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(CALL_VOL_ATTR) != null)
			{
				callVolume = Integer.parseInt(reportGrainData.getDatum(thisDateGrain).getAttributeData(CALL_VOL_ATTR).get(0));
			}
			
			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(AVG_ORDER_VAL_ATTR) != null)
			{
				aov = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(AVG_ORDER_VAL_ATTR).get(0));
			}
			
			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(LATE_DAYS_ATTR) != null)
			{
				lateDays = Integer.parseInt(reportGrainData.getDatum(thisDateGrain).getAttributeData(LATE_DAYS_ATTR).get(0));
			}

			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(CONVERSION_ATTR) != null)
			{
				conversion = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(CONVERSION_ATTR).get(0));
			}

			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(SCHEDULE_ADH_ATTR) != null)
			{
				schAdh = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(SCHEDULE_ADH_ATTR).get(0));
			}
			
			if(reportGrainData.getDatum(thisDateGrain).getAttributeData(IVR_CSAT_ATTR) != null)
			{
				ivrCSAT = Double.parseDouble(reportGrainData.getDatum(thisDateGrain).getAttributeData(IVR_CSAT_ATTR).get(0));
			}

			retval.add(new String[]
					{
					date, 
					"" + callVolume,
					"" + totalSales, 
					"" + totalRefunds, 
					"" + conversion,
					"" + rpc, 
					"" + aov,
					"" + schAdh, 
					"" + lateDays,
					"" + ivrCSAT
					});
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
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
		
		if(scheduleAdherenceReport != null)
		{
			scheduleAdherenceReport.close();
		}
		
		if(lateDaysReport != null)
		{
			lateDaysReport.close();
		}
		
		if(rpcReport != null)
		{
			rpcReport.close();
		}
		
		if(ivrCSATReport != null)
		{
			ivrCSATReport.close();
		}
		
		if(aovReport != null)
		{
			aovReport.close();
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
		
		retval.add("User Grain");
		
		retval.add("Call Volume");
		retval.add("Total Sales ($)");
		retval.add("Total Refunds ($)");
		retval.add("Conversion (%)");
		retval.add("RPC ($)");
		retval.add("AOV ($)");
		retval.add("Sch Adh (%)");
		retval.add("Late Days");
		retval.add("IVR CSAT (%)");
		
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
