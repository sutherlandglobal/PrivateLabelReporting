/**
 * 
 */
package com.sutherland.privatelabel.report;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sutherland.helios.api.report.frontend.ReportFrontEndGroups;
import com.sutherland.helios.data.Aggregation;
import com.sutherland.helios.data.attributes.DataAttributes;
import com.sutherland.helios.data.formatting.NumberFormatter;
import com.sutherland.helios.data.granularity.user.UserGrains;
import com.sutherland.helios.date.formatting.DateFormatter;
import com.sutherland.helios.date.parsing.DateParser;
import com.sutherland.helios.exceptions.ExceptionFormatter;
import com.sutherland.helios.exceptions.ReportSetupException;
import com.sutherland.helios.logging.LogIDFactory;
import com.sutherland.helios.report.Report;
import com.sutherland.helios.report.parameters.groups.ReportParameterGroups;
import com.sutherland.helios.schedule.Schedule;
import com.sutherland.helios.schedule.Shift;
import com.sutherland.helios.statistics.Statistics;

/**
 * @author Jason Diamond
 *
 */
public class ScheduleAdherence extends Report  implements DataAttributes
{
	private PrivateLabelRoster roster;
	private static final long MINS_LATE_THRESHOLD = 5;
	private static final double FIVE_MINS_LATE_PENALTY = .005; //.5% penalty 
	private final static Logger logger = Logger.getLogger(ScheduleAdherence.class);

	public static String uiGetReportName()
	{
		return  "Schedule Adherence";
	}
	
	public static String uiGetReportDesc()
	{
		return "A comparison of how an agent's shifts adhere to their schedules.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public ScheduleAdherence() throws ReportSetupException 
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
			reportName = LateDays.uiGetReportName();
			reportDesc = LateDays.uiGetReportDesc();
			
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
		
		retval.add("Schedule Adh (%)");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = null;

		Aggregation reportGrainData = new Aggregation();

		GregorianCalendar thisScheduleStartDate,thisScheduleEndDate, shiftStartDate,shiftEndDate;

		ArrayList<Shift> agentShifts;
		ArrayList<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		int timeGrain, userGrain, dateFormat;

		double minutesAttended, scheduleAdh;

		retval = new ArrayList<String[]>();

		boolean isFiveMinsLate;
		
		//requirements want an improper? average of each day's schedule adherence, minus the 5 min late penalty
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.setParameters(getParameters());
		roster.loadSchedule();
		
		for(String userID : roster.getUserIDs())
		{
			
			schedules = roster.getUser(userID).getDatumObjects(PrivateLabelRoster.SCHEDULE_ATTR);

			if(schedules != null)
			{
				logInfoMessage( roster.getUser(userID).toString());

				for(Object scheduleObject : schedules)
				{
					thisSchedule = (Schedule)scheduleObject;

					minutesAttended = 0;

					agentShifts = thisSchedule.getSortedShifts();

					thisScheduleStartDate = thisSchedule.getInterval().getStartDate();
					thisScheduleEndDate = thisSchedule.getInterval().getEndDate();

					scheduleAdh = 0;

					isFiveMinsLate = false;

					double rawMinutesScheduled =  DateParser.getMinutesBetween(thisScheduleStartDate, thisScheduleEndDate);

					if(agentShifts.size() > 0)
					{
						Shift firstShift = agentShifts.get(0);

						if(firstShift.getStartDate().after(thisScheduleStartDate) && DateParser.getMinutesBetween(firstShift.getStartDate(), thisScheduleStartDate) > MINS_LATE_THRESHOLD)
						{
							//this shift is more than 5 minutes late to start, subtract an additional .5% from schedule adherence
							//only if a shift is missed. it makes no sense to have a negative adherence
							isFiveMinsLate = true;
						}

						for(Shift agentShift : agentShifts)
						{
							shiftStartDate = agentShift.getStartDate();
							shiftEndDate= agentShift.getEndDate();

							if(shiftStartDate.before(thisScheduleStartDate))
							{
								shiftStartDate = thisScheduleStartDate;
								logInfoMessage( "Adjusting startdate: " + DateParser.toSQLDateFormat(shiftStartDate));
							}

							if(shiftEndDate.before(thisScheduleStartDate))
							{
								shiftEndDate =  thisScheduleStartDate;
								logInfoMessage( "Adjusting enddate: " + DateParser.toSQLDateFormat(shiftEndDate));
							}

							if(shiftStartDate.after(thisScheduleEndDate))
							{
								shiftStartDate = thisScheduleEndDate;
								logInfoMessage( "Adjusting startdate: " + DateParser.toSQLDateFormat(shiftStartDate));
							}

							if(shiftEndDate.after(thisScheduleEndDate))
							{
								shiftEndDate =  thisScheduleEndDate;
								logInfoMessage( "Adjusting enddate: " + DateParser.toSQLDateFormat(shiftEndDate));
							}

							minutesAttended += DateParser.getMinutesBetween(shiftStartDate, shiftEndDate);
						}

						logInfoMessage( "Minutes attended for schedule: " + minutesAttended);
						logInfoMessage( "Scheduled minutes: " + rawMinutesScheduled);

						scheduleAdh = 1 - ((rawMinutesScheduled - minutesAttended)/rawMinutesScheduled);

						if(isFiveMinsLate)
						{
							scheduleAdh -= FIVE_MINS_LATE_PENALTY;
						}
					}

					logInfoMessage( "Scheduled Adh: " + scheduleAdh + "\n===============================");

					if(isTimeTrendReport())
					{
						timeGrain = Integer.parseInt(getParameters().getTimeGrain());
						dateFormat = Integer.parseInt(getParameters().getDateFormat());
						reportGrain = DateFormatter.getFormattedDate(thisScheduleStartDate, timeGrain, dateFormat);
					}
					else //if(isStackReport())
					{
						userGrain = Integer.parseInt(getParameters().getUserGrain());
						reportGrain = UserGrains.getUserGrain(userGrain, roster.getUser(userID));
					}
					
					reportGrainData.addDatum(reportGrain);
					reportGrainData.getDatum(reportGrain).addAttribute(SCHEDULE_ADH_ATTR);
					reportGrainData.getDatum(reportGrain).addData(SCHEDULE_ADH_ATTR, "" + scheduleAdh);
				}
			}
		}


		for(String date : reportGrainData.getDatumIDList())
		{
			scheduleAdh = Statistics.getAverage(reportGrainData.getDatum(date).getAttributeData(SCHEDULE_ADH_ATTR));
			retval.add(new String[]{date, NumberFormatter.convertToPercentage(scheduleAdh, 4)});
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
