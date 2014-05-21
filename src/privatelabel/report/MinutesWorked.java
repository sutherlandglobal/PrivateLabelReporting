/**
 * 
 */
package privatelabel.report;

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Aggregation;
import helios.data.granularity.time.TimeGrains;
import helios.data.granularity.user.UserGrains;
import helios.date.parsing.DateParser;
import helios.exceptions.ExceptionFormatter;
import helios.exceptions.ReportSetupException;
import helios.logging.LogIDFactory;
import helios.report.Report;
import helios.report.parameters.groups.ReportParameterGroups;
import helios.schedule.Schedule;
import helios.schedule.Shift;
import helios.statistics.Statistics;

import java.util.ArrayList;
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
public class MinutesWorked extends Report 
{
	private PrivateLabelRoster roster;

	private final static Logger logger = Logger.getLogger(MinutesWorked.class);
	private static final String MINS_WORKED_ATTR = "workedMins";

	public static String uiGetReportName()
	{
		return  "Minutes Worked";
	}
	
	public static String uiGetReportDesc()
	{
		return "The count of minutes an agent works, including outside of a scheduled shift.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public MinutesWorked() throws ReportSetupException 
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
			reportName = MinutesWorked.uiGetReportName();
			reportDesc = MinutesWorked.uiGetReportDesc();
			
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
		if(roster != null)
		{
			roster.close();
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
		
		retval.add("Minutes Worked");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = null;

		Aggregation  reportGrainData = new Aggregation();

		GregorianCalendar thisScheduleStartDate,shiftStartDate,shiftEndDate;

		ArrayList<Shift> agentShifts;
		ArrayList<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain, userGrain;

		double minutesAttended;

		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.setParameters(getParameters());
		roster.loadSchedule();
		
		retval = new ArrayList<String[]>();
		
		for(String userID : roster.getUserIDs())
		{
			schedules = roster.getUser(userID).getDatumObjects(PrivateLabelRoster.SCHEDULE_ATTR);

			if(schedules != null)
			{
				for(Object scheduleObject : schedules)
				{
					thisSchedule = (Schedule)scheduleObject;

					minutesAttended = 0;

					agentShifts = thisSchedule.getSortedShifts();

					thisScheduleStartDate = thisSchedule.getInterval().getStartDate();
					if(agentShifts.size() > 0)
					{
						for(Shift agentShift : agentShifts)
						{
							shiftStartDate = agentShift.getStartDate();
							shiftEndDate= agentShift.getEndDate();

							//we do not care about schedule-adherent minutes
							minutesAttended += DateParser.getMinutesBetween(shiftStartDate, shiftEndDate);
						}
					}
					
					if(isTimeTrendReport())
					{
						//time bucket
						timeGrain = Integer.parseInt(getParameters().getTimeGrain());
						reportGrain = TimeGrains.getDateGrain(timeGrain, thisScheduleStartDate);
					}
					else //if stack
					{
						userGrain = Integer.parseInt(getParameters().getUserGrain());
						reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(userID));
					}
					
					reportGrainData.addDatum(reportGrain);
					reportGrainData.getDatum(reportGrain).addAttribute(MINS_WORKED_ATTR);
					reportGrainData.getDatum(reportGrain).addData(MINS_WORKED_ATTR, "" + minutesAttended);
				}
			}
		}

		for(String grain : reportGrainData.getDatumIDList())
		{
			retval.add(new String[]{grain, "" + Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(MINS_WORKED_ATTR))});
		}		

		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = super.validateParameters();
		
		roster.loadSchedule();
		
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
