/**
 * 
 */
package privatelabel.report;

import helios.api.report.frontend.ReportFrontEndGroups;
import helios.data.Datum;
import helios.data.granularity.user.UserGrains;
import helios.database.connection.SQL.ConnectionFactory;
import helios.database.connection.SQL.RemoteConnection;
import helios.date.parsing.DateParser;
import helios.exceptions.DatabaseConnectionCreationException;
import helios.exceptions.ExceptionFormatter;
import helios.exceptions.ReportSetupException;
import helios.logging.LogIDFactory;
import helios.report.Report;
import helios.report.parameters.groups.ReportParameterGroups;
import helios.schedule.Schedule;
import helios.schedule.Shift;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import privatelabel.constants.Constants;
import privatelabel.report.roster.Attributes;

/**
 * @author Jason Diamond
 *
 */
public class Schedules extends Report 
{
	private RemoteConnection rocjfsdbs27Connection;
	private RemoteConnection rocjfsdev18Connection;
	private final String rocjfsdbs27PropertiesFile = Constants.PRIVATE_LABEL_PROD_DB;
	private final String rocjfsdev18PropertiesFile = Constants.PRIVATE_LABEL_DEV_DB;
	private final int SHIFT_WINDOW_SIZE = 4; //hours
	private final static Logger logger = Logger.getLogger(Schedules.class);
	private PrivateLabelRoster roster;
	
	public static String uiGetReportName()
	{
		return "Schedules";
	}
	
	public static String uiGetReportDesc()
	{
		return "Schedule data for agents, as well as any worked shifts within the schedules.";
	}
	
	public final static LinkedHashMap<String, String> uiSupportedReportFrontEnds = ReportFrontEndGroups.BASIC_METRIC_FRONTENDS;
	
	public final static LinkedHashMap<String, ArrayList<String>> uiReportParameters = ReportParameterGroups.BASIC_METRIC_REPORT_PARAMETERS;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public Schedules() throws ReportSetupException 
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
			reportName = "Schedules";
			reportDesc = "Schedule data for agents, as well as any worked shifts within the schedules.";
			
			reportName = Schedules.uiGetReportName();
			reportDesc = Schedules.uiGetReportDesc();
			
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
		boolean retval = false;

		try 
		{
			ConnectionFactory factory = new ConnectionFactory();

			factory.load(rocjfsdbs27PropertiesFile);

			rocjfsdbs27Connection = factory.getConnection();

			factory.load(rocjfsdev18PropertiesFile);

			rocjfsdev18Connection = factory.getConnection();
		}
		catch(DatabaseConnectionCreationException e )
		{
			setErrorMessage("DatabaseConnectionCreationException on attempt to access database");
					
			logErrorMessage(getErrorMessage());
			logErrorMessage( ExceptionFormatter.asString(e));
		}
		finally
		{
			if(rocjfsdbs27Connection != null && rocjfsdev18Connection != null)
			{
				retval = true;
			}
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(rocjfsdbs27Connection != null)
		{
			rocjfsdbs27Connection.close();
		}

		if(rocjfsdev18Connection != null)
		{
			rocjfsdev18Connection.close();
		}

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

		retval.add("User Grain");
		retval.add("Schedule Start");
		retval.add("Schedule End");
		retval.add("Shifts");
		
		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws Exception
	{
		ArrayList<String[]> retval = null;

		String shiftQuery;

		//prevent reports from looking at scheduling into the future. 
		//an agent can't attend a future shift in the given moment, this will hurt their metric
		//the latest date this report can look at is now. 
		String reportEndDate = getParameters().getEndDate();
		GregorianCalendar now = new GregorianCalendar();
		if(DateParser.convertSQLDateToGregorian(reportEndDate).after(now))
		{
			reportEndDate = DateParser.toSQLDateFormat(now);
		}

		String scheduleQuery = "Select date,empid,start_time,end_time from pl_schedule " +
				" where date>= '" + 
				getParameters().getStartDate()+ 
				"' AND date <= '" + 
				reportEndDate  + "'";

		//scheduled row date seems like the date of scheduleStart
		String empID, scheduleStart,scheduleEnd;
		String shiftStart,shiftEnd;

		GregorianCalendar shiftWindowStart,shiftWindowEnd,scheduleStartDate,scheduleEndDate;

		retval = new ArrayList<String[]>(roster.getSize());
		String userID;
		
		roster = new PrivateLabelRoster();
		roster.setChildReport(true);
		roster.setParameters(getParameters());
		roster.load();
		
		int userGrain;
		for(String[] scheduleRow:  rocjfsdev18Connection.runQuery(scheduleQuery))
		{
			//for each scheduled shift, we look up the actual shifts for that day 
			//=> all shifts after the start time, and before the end time, by user

			empID = scheduleRow[1];
			scheduleStart = DateParser.toSQLDateFormat(DateParser.convertSQLDateToGregorian(scheduleRow[2]));
			scheduleEnd = DateParser.toSQLDateFormat(DateParser.convertSQLDateToGregorian(scheduleRow[3]));

			userID =  roster.lookupUserByAttributeName(empID, Attributes.EMP_ID_ATTR);
			
			if(userID != null)
			{				
				//each row is an agent's interval of login to logout
				//agents can have multiple sets of logins and logouts per schedule interval

				//a person can show up early for their shift, technically in the previous day.
				//a person can leave a shift late, technically in the next day, and cause the same problems.
				//let's pick a random number (say 4) of hours to look for shifts before and after their scheduled times. 
				//assumption: no one pulls double shifts
				//shiftEnd can be null -> disregard those rows, the database will correct itself. --Dale
				//agents cannot double login, they stay logged into the first phone. --Dale
				//shiftStart will probably never be null

				shiftWindowStart = DateParser.convertSQLDateToGregorian(scheduleStart);
				shiftWindowEnd = DateParser.convertSQLDateToGregorian(scheduleEnd);

				shiftWindowStart.add(Calendar.HOUR_OF_DAY, -SHIFT_WINDOW_SIZE);
				shiftWindowEnd.add(Calendar.HOUR_OF_DAY, SHIFT_WINDOW_SIZE);

				//sub 4 hours from schedule start -> window start
				//add 4 hours from schedule end -> window end

				//shiftstarts and ends are in unix time, load them up and subtract 4 hours.
				//could just subtract the interval from the epoch, but i'm preferring a date library to that

				//this query does not need to have anything extra set the agent name for an agent report.
				shiftQuery =  "SELECT " +
						"row_date," + 
						"logid," + 
						"dateadd(second,\"login\", {d '1970-01-01'}) as login," + 
						"dateadd(second,\"logout\", {d '1970-01-01'}) as logout " + 
						"from tbl_PFS_CMS_HAGLOG " + 
						"where dateadd(second,\"login\", {d '1970-01-01'}) >= '" + DateParser.toSQLDateFormat(shiftWindowStart) + 
						"' AND dateadd(second,\"logout\", {d '1970-01-01'}) <= '" +  DateParser.toSQLDateFormat(shiftWindowEnd) + 
						"' AND logid = '" + roster.getUser(userID).getAttributeData(PrivateLabelRoster.EXTENSION_ATTR).get(0)  +"' " + 
						" AND logout is not null";

				Schedule thisSchedule = new Schedule(scheduleStart, scheduleEnd);
				

				
//				System.out.println(scheduleStart);
//				System.out.println(roster.getUser(userID).getSchedule(scheduleStart));

				for(String[] shiftRow : rocjfsdbs27Connection.runQuery(shiftQuery))
				{
					shiftStart = shiftRow[2];
					shiftEnd = shiftRow[3];

					//users -> schedules -> shifts
					thisSchedule.addShift(new Shift(shiftStart, shiftEnd));
				}
				
				for( Entry<String, String> queryStats  : rocjfsdbs27Connection.getStatistics().entrySet())
				{
					logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
				}
				
				roster.getUser(userID).addObject(PrivateLabelRoster.SCHEDULE_ATTR, thisSchedule);
				
				//at this point the user has all shift accumulated for this schedule
				//we get a list of shifts, sort by eariliest to latest

				//we could populate this direct from the db query but then it would likely be unsorted
				StringBuilder shiftString = new StringBuilder();
				for(Shift s : thisSchedule.getSortedShifts())
				{
					shiftString.append(DateParser.toSQLDateFormat(s.getStartDate()));
					shiftString.append(" -> ");
					shiftString.append(DateParser.toSQLDateFormat(s.getEndDate()));
					shiftString.append(",");
				}
				
				scheduleStartDate = thisSchedule.getInterval().getStartDate();
				scheduleEndDate = thisSchedule.getInterval().getEndDate();
				
				//sch start, sch end, shift string with all shifts
				
				userGrain = Integer.parseInt(getParameters().getUserGrain());
				retval.add(new String[] 
				{
							UserGrains.getUserGrain(userGrain, roster.getUser(userID)),
							DateParser.toSQLDateFormat(scheduleStartDate),
							DateParser.toSQLDateFormat(scheduleEndDate),
							shiftString.toString()
				});
			}
		}
		
		for( Entry<String, String> queryStats  : rocjfsdev18Connection.getStatistics().entrySet())
		{
			logInfoMessage( "Query " + queryStats.getKey() + ": " + queryStats.getValue());
		}

		return retval;
	}
		
	/**
	 * Accessor for a specified User.
	 * 
	 * @param userID	 User ID to query a User by.
	 * 
	 * @return	The User discovered.
	 */
	public Datum getUser(String userID)
	{
		return roster.getUser(userID);
	}
	
	public String[] getUserIDs()
	{
		return roster.getUserIDs();
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
