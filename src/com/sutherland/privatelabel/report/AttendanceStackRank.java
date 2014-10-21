package com.sutherland.privatelabel.report;
/**
 * 
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sutherland.helios.api.report.frontend.ReportFrontEndGroups;
import com.sutherland.helios.data.Aggregation;
import com.sutherland.helios.data.attributes.DataAttributes;
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
public class AttendanceStackRank extends Report implements DataAttributes
{
	private ScheduleAdherence schAdherence;
	private LateDays lateDays;
	private MinutesLate minutesLate;
	private MinutesWorked minutesWorked;
	private final static Logger logger = Logger.getLogger(AttendanceStackRank.class);
	
	public static String uiGetReportName()
	{
		return "Attendance Stack Rank";
	}
	
	public static String uiGetReportDesc()
	{
		return "A stack ranking of Attendance-based metrics.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.STACK_RANK_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.DASHBOARD_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AttendanceStackRank() throws ReportSetupException
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
			reportName = AttendanceStackRank.uiGetReportName();
			reportDesc = AttendanceStackRank.uiGetReportDesc();
			
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

		retval = new ArrayList<String[]>();

		Aggregation users = new Aggregation();

		ReportRunner runner = new ReportRunner();
		
		schAdherence = new ScheduleAdherence();
		schAdherence.setChildReport(true);
		schAdherence.setParameters(getParameters());
		schAdherence.getParameters().setEndDate(endDate);

		lateDays = new LateDays();
		lateDays.setChildReport(true);
		lateDays.setParameters(getParameters());
		lateDays.getParameters().setEndDate(endDate);

		minutesLate = new MinutesLate();
		minutesLate.setChildReport(true);
		minutesLate.setParameters(getParameters());
		minutesLate.getParameters().setEndDate(endDate);

		minutesWorked = new MinutesWorked();
		minutesWorked.setChildReport(true);
		minutesWorked.setParameters(getParameters());
		minutesWorked.getParameters().setEndDate(endDate);

		runner.addReport(MINS_LATE_ATTR, minutesLate);
		runner.addReport(LATE_DAYS_ATTR, lateDays);
		runner.addReport(SCHEDULE_ADH_ATTR, schAdherence);
		runner.addReport(MINS_WORKED_ATTR, minutesWorked);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			for(String[] row : runner.getResults(MINS_LATE_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);
	
				users.getDatum(row[0]).addAttribute(MINS_LATE_ATTR );
				users.getDatum(row[0]).addData(MINS_LATE_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(LATE_DAYS_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);
	
				users.getDatum(row[0]).addAttribute(LATE_DAYS_ATTR );
				users.getDatum(row[0]).addData(LATE_DAYS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(SCHEDULE_ADH_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);
	
				users.getDatum(row[0]).addAttribute(SCHEDULE_ADH_ATTR );
				users.getDatum(row[0]).addData(SCHEDULE_ADH_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(MINS_WORKED_ATTR))
			{
				users.addDatum(row[0]);
				users.getDatum(row[0]).addAttribute(NAME_ATTR );
				users.getDatum(row[0]).addData(NAME_ATTR, row[0]);
	
				users.getDatum(row[0]).addAttribute(MINS_WORKED_ATTR );
				users.getDatum(row[0]).addData(MINS_WORKED_ATTR, row[1]);
			}
		}

		for(String userName : users.getDatumIDList())
		{
			String name = users.getDatum(userName).getAttributeData(NAME_ATTR).get(0);

			int numLateDays = 0;
			double numLateMins = 0;
			double numWorkedMins = 0;

			double scheduleAdherence = 0;

			if(users.getDatum(userName).getAttributeData(SCHEDULE_ADH_ATTR) != null )
			{
				scheduleAdherence = Double.parseDouble(users.getDatum(userName).getAttributeData(SCHEDULE_ADH_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(LATE_DAYS_ATTR) != null)
			{
				numLateDays = Integer.parseInt(users.getDatum(userName).getAttributeData(LATE_DAYS_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(MINS_LATE_ATTR) != null)
			{
				numLateMins =Double.parseDouble(users.getDatum(userName).getAttributeData(MINS_LATE_ATTR).get(0));
			}

			if(users.getDatum(userName).getAttributeData(MINS_WORKED_ATTR) != null)
			{
				numWorkedMins = Double.parseDouble(users.getDatum(userName).getAttributeData(MINS_WORKED_ATTR).get(0));
			}

			retval.add(new String[]
					{
					name, 
					"" + scheduleAdherence, 
					"" + numLateDays, 
					"" + numLateMins, 
					"" + numWorkedMins,
					});
		}

		return retval;
	}

	/* 
	 * (non-Javadoc)
	 * @see report.Report#close()
	 */
	public void close()
	{
		if(schAdherence != null)
		{
			schAdherence.close();
		}

		if(lateDays != null)
		{
			lateDays.close();
		}

		if(minutesLate != null)
		{
			minutesLate.close();
		}
		
		if(minutesWorked != null)
		{
			minutesWorked.close();
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
		retval.add("Schedule Adherence (%)");
		retval.add("Late Days");
		retval.add("Late Minutes");
		retval.add("Worked Minutes");
		
		return retval;
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
