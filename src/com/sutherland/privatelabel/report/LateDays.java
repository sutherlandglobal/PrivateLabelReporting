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
import com.sutherland.helios.data.Datum;
import com.sutherland.helios.data.attributes.DataAttributes;
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
public class LateDays extends Report  implements DataAttributes
{
	private PrivateLabelRoster roster;
	private final static Logger logger = Logger.getLogger(LateDays.class);

	//30-min lunch + 2 15 mins breaks
	//private static final int ALLOWED_MISSED_MINUTES = 60;

	private static final long MINS_LATE_THRESHOLD = 5;

	public static String uiGetReportName()
	{
		return  "Late Days";
	}
	
	public static String uiGetReportDesc()
	{
		return "The count of days where an agent is more than five minutes late for a scheduled shift.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LateDays() throws ReportSetupException 
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
		
		retval.add("Late Day Count");
		
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

		GregorianCalendar thisScheduleStartDate;

		ArrayList<Shift> agentShifts;
		ArrayList<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		int timeGrain, userGrain, dateFormat;

		boolean isLateDay;
		
		retval = new ArrayList<String[]>();
		
		Datum thisUser;
		
		String lateDayVal;
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.setParameters(getParameters());
		roster.loadSchedule();
		
		for(String userID : roster.getUserIDs())
		{
			
			thisUser = roster.getUser(userID);
			schedules = thisUser.getDatumObjects(PrivateLabelRoster.SCHEDULE_ATTR);

			if(schedules != null)
			{

				//no shows are late
				for(Object scheduleObject : schedules)
				{
					thisSchedule = (Schedule)scheduleObject;
					isLateDay = true;

					agentShifts = thisSchedule.getSortedShifts();

					thisScheduleStartDate = thisSchedule.getInterval().getStartDate();

					if(agentShifts.size() > 0)
					{
						Shift firstShift = agentShifts.get(0);

						if( !(firstShift.getStartDate().after(thisScheduleStartDate) && DateParser.getMinutesBetween(firstShift.getStartDate(), thisScheduleStartDate) > MINS_LATE_THRESHOLD))
						{
							isLateDay = false;
						}
					}

					if(isLateDay)
					{
						lateDayVal = "" + 1;
					}
					else
					{
						lateDayVal = "" + 0;
					}
					
					if(isTimeTrendReport())
					{
						//time bucket
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
					reportGrainData.getDatum(reportGrain).addAttribute(LATE_DAYS_ATTR);
					reportGrainData.getDatum(reportGrain).addData(LATE_DAYS_ATTR, lateDayVal);
				}
			}
		}

			for(String grain : reportGrainData.getDatumIDList())
			{
				retval.add(new String[]{grain, "" + (int)Statistics.getTotal(reportGrainData.getDatum(grain).getAttributeData(LATE_DAYS_ATTR))});
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
