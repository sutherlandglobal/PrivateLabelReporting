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
public final class SalesDocumentationRate extends Report 
{
	private final static String SALES_DOC_COUNT_ATTR = "salesDocCount";
	private final static String CALL_VOL_ATTR = "callVol";
	private CallVolume callVolumeReport;
	private SalesDocumentationCount salesDocCountReport;
	private final static Logger logger = Logger.getLogger(SalesDocumentationRate.class);

	public static String uiGetReportName()
	{
		return "Sales Documentation Rate";
	}
	
	public static String uiGetReportDesc()
	{
		return "Rate of sales documentations in cases.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public SalesDocumentationRate() throws ReportSetupException
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
			reportName = "Sales Documentation Rate";
			reportDesc = "Trends rate of sales documentations.";
			
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
		if(callVolumeReport != null)
		{
			callVolumeReport.close();
		}
		
		if(salesDocCountReport != null)
		{
			salesDocCountReport.close();
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
		
		retval.add("Documentation Rate (%)");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{
		//#calls fielded vs # cases made
		
		ArrayList<String[]> retval = null; 
		
		ReportRunner runner = new ReportRunner();

		salesDocCountReport = new SalesDocumentationCount();
		salesDocCountReport.setChildReport(true);
		salesDocCountReport.setParameters(getParameters());

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameters(getParameters());

		Aggregation reportGrainData = new Aggregation();
		Aggregation reportStackData = new Aggregation();

		runner.addReport(CALL_VOL_ATTR, callVolumeReport);
		runner.addReport(SALES_DOC_COUNT_ATTR, salesDocCountReport);
		
		
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			String reportGrain, numCalls, fullName;
			for(String[] row : runner.getResults(CALL_VOL_ATTR))
			{
				numCalls = row[1];

				//trust the Call Volume report to provide sane date grains and to enforce roster membership
				if(isTimeTrendReport())
				{					
					//CallVolume report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addDatum(reportGrain);
					reportGrainData.getDatum(reportGrain).addAttribute(CALL_VOL_ATTR);
					reportGrainData.getDatum(reportGrain).addData(CALL_VOL_ATTR, numCalls);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addDatum(fullName);
					reportStackData.getDatum(fullName).addAttribute(CALL_VOL_ATTR);
					reportStackData.getDatum(fullName).addData(CALL_VOL_ATTR, numCalls);
				}
			}			

			/////////////////

			String docCount;

			for(String[] row : runner.getResults(SALES_DOC_COUNT_ATTR))
			{
				docCount = row[1];
				
				//trust the Sales Count report to provide sane date grains and to enforce roster membership
				if(isTimeTrendReport())
				{					
					//SalesCount report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addDatum(reportGrain);
					reportGrainData.getDatum(reportGrain).addAttribute(SALES_DOC_COUNT_ATTR);
					reportGrainData.getDatum(reportGrain).addData(SALES_DOC_COUNT_ATTR, docCount);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addDatum(fullName);
					reportStackData.getDatum(fullName).addAttribute(SALES_DOC_COUNT_ATTR);
					reportStackData.getDatum(fullName).addData(SALES_DOC_COUNT_ATTR, docCount);
				}
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalNumCalls, finalNumDocs, finalDocRate;
		for(String grain : reportGrainData.getDatumIDList())
		{
			finalDocRate = 0;
			if(!reportGrainData.getDatum(grain).addAttribute(CALL_VOL_ATTR) && !reportGrainData.getDatum(grain).addAttribute(SALES_DOC_COUNT_ATTR))
			{
				finalNumCalls = Double.parseDouble(reportGrainData.getDatum(grain).getAttributeData(CALL_VOL_ATTR).get(0));
				finalNumDocs = Double.parseDouble(reportGrainData.getDatum(grain).getAttributeData(SALES_DOC_COUNT_ATTR).get(0));

				if(finalNumCalls != 0)
				{
					finalDocRate = finalNumDocs/finalNumCalls;
				}
			}

			retval.add(new String[]{grain, NumberFormatter.convertToPercentage(finalDocRate, 4)});
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
