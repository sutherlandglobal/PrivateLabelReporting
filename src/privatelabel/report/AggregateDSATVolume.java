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
public final class AggregateDSATVolume extends Report {

	private LMIDSATVolume lmiDSATReport;
	private IVRDSATVolume ivrDSATReport;
	private static String DSAT_CASE_ATTR = "dsatCases";
	private static String LMI_DSAT_CASE_ATTR = "lmidsatCases";
	private static String IVR_DSAT_CASE_ATTR = "iverdsatCases";
	
	private final static Logger logger = Logger.getLogger(AggregateDSATVolume.class);

	public static String uiGetReportName()
	{
		return "Aggregate DSAT Survey Volume";
	}
	
	public static String uiGetReportDesc()
	{
		return "Total amount of all DSAT surveys.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AggregateDSATVolume() throws ReportSetupException 
	{
		super();
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
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport() 
	{
		boolean retval = false;

		try
		{
			reportName = AggregateDSATVolume.uiGetReportName();
			reportDesc =  AggregateDSATVolume.uiGetReportDesc();
			
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

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(lmiDSATReport != null)
		{
			lmiDSATReport.close();
		}
		
		if(ivrDSATReport != null)
		{
			ivrDSATReport.close();
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
		
		retval.add("DSAT Volume");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();
		
		Aggregation reportGrainData = new Aggregation();

		String reportGrain;
		//int timeGrain;
		
		ReportRunner runner = new ReportRunner();
		
		lmiDSATReport = new LMIDSATVolume();
		lmiDSATReport.setChildReport(true);
		lmiDSATReport.setParameters(getParameters());
		
		ivrDSATReport = new IVRDSATVolume();
		ivrDSATReport.setChildReport(true);
		ivrDSATReport.setParameters(getParameters());


		runner.addReport(LMI_DSAT_CASE_ATTR, ivrDSATReport);
		runner.addReport(IVR_DSAT_CASE_ATTR, lmiDSATReport);

		//this is a time report, but since lmi limits us, we have to set the report type of agg dsat explicitly
		getParameters().setReportType(ivrDSATReport.getParameters().getReportType());
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			for(String[] row : runner.getResults(IVR_DSAT_CASE_ATTR))
			{
				reportGrain = row[0];
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(DSAT_CASE_ATTR );
				reportGrainData.getDatum(reportGrain).addData(DSAT_CASE_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(LMI_DSAT_CASE_ATTR))
			{
				reportGrain = row[0];
				
				reportGrainData.addDatum(reportGrain);
				reportGrainData.getDatum(reportGrain).addAttribute(DSAT_CASE_ATTR );
				reportGrainData.getDatum(reportGrain).addData(DSAT_CASE_ATTR, row[1]);
			}
		}
		
		int dsatCount;
		for(String datumID : reportGrainData.getDatumIDList())
		{
			//not all users will have refunds
			dsatCount = 0;
			if( reportGrainData.getDatum(datumID).getAttributeData(DSAT_CASE_ATTR) != null)
			{
				for(String attrDatum : reportGrainData.getDatum(datumID).getAttributeData(DSAT_CASE_ATTR))
				{
					dsatCount += Integer.parseInt(attrDatum);
				}
			}

			retval.add(new String[]{datumID, "" + dsatCount }) ;
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
