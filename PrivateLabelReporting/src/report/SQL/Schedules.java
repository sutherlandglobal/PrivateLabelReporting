/**
 * 
 */
package report.SQL;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import schedule.Schedule;
import schedule.Shift;
import team.User;
import util.DateParser;
import util.ReportParameterValidator;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class Schedules extends Report 
{
	private RemoteConnection rocjfsdbs27Connection;
	private RemoteConnection rocjfsdev18Connection;
	private final String rocjfsdbs27PropertiesFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdbs27.properties";
	private final String rocjfsdev18PropertiesFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdev18.properties";
	private final int SHIFT_WINDOW_SIZE = 4; //hours

	private DateParser dp;
	
	private Roster roster;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public Schedules() throws ReportSetupException 
	{
		super();

		reportName = "Schedules";

		logger.info("Building report " +  reportName);

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
			logger.log(Level.ERROR,  "DatabaseConnectionCreationException on attempt to access database: " + e.getMessage());	
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
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport() 
	{
		boolean retval = false;

		try
		{
			dp = new DateParser();
		}
		finally
		{
			if(dp != null )
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
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected Vector<String[]> runReport() 
	{
		Vector<String[]> retval = null;

		String shiftQuery;

		//prevent reports from looking at scheduling into the future. 
		//an agent can't attend a future shift in the given moment, this will hurt their metric
		//the latest date this report can look at is now. 
		String reportEndDate = parameters.get(END_DATE_PARAM);
		GregorianCalendar now = new GregorianCalendar();
		if(dateParser.convertSQLDateToGregorian(reportEndDate).after(now))
		{
			reportEndDate = dateParser.readableGregorian(now);
		}

		String scheduleQuery = "Select date,empid,start_time,end_time from pl_schedule " +
				" where date>= '" + parameters.get(START_DATE_PARAM) + "' AND date <= '" + reportEndDate  + "'";

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			//tbl_PFS_CMS_HAGLOG stores agents as their extensions
			String targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.EMP_ID_ATTR).firstElement();
			scheduleQuery += " AND empid = '" + targetUserID +"'" ;
		}

		//scheduled row date seems like the date of scheduleStart
		String userID, scheduleStart,scheduleEnd;
		String shiftStart,shiftEnd;

		GregorianCalendar shiftWindowStart,shiftWindowEnd,scheduleStartDate,scheduleEndDate;

		retval = new Vector<String[]>(roster.getSize());
		
		for(String[] scheduleRow:  rocjfsdev18Connection.runQuery(scheduleQuery))
		{
			//for each scheduled shift, we look up the actual shifts for that day 
			//=> all shifts after the start time, and before the end time, by user

			userID = scheduleRow[1];
			scheduleStart = dp.readableGregorian(dp.convertSQLDateToGregorian(scheduleRow[2]));
			scheduleEnd = dp.readableGregorian(dp.convertSQLDateToGregorian(scheduleRow[3]));

			if(roster.getUser(userID) != null)
			{
				//each row is an agent's interval of login to logout
				//agents can have multiple sets of logins and logouts per schedule interval

				//a person can show up early for their shift, tectnically in the previous day.
				//a person can leave a shift late, technically in the next day, and cause the same problems.
				//let's pick a random number (say 4) of hours to look for shifts before and after their scheduled times. 
				//assumption: no one pulls double shifts
				//shiftEnd can be null -> disregard those rows, the database will correct itself. --Dale
				//agents cannot double login, they stay logged into the first phone. --Dale
				//shiftStart will probably never be null

				shiftWindowStart = dateParser.convertSQLDateToGregorian(scheduleStart);
				shiftWindowEnd = dateParser.convertSQLDateToGregorian(scheduleEnd);

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
						"where dateadd(second,\"login\", {d '1970-01-01'}) >= '" + dateParser.readableGregorian(shiftWindowStart) + 
						"' AND dateadd(second,\"logout\", {d '1970-01-01'}) <= '" +  dateParser.readableGregorian(shiftWindowEnd) + 
						"' AND logid = '" + roster.getUser(userID).getAttrData(Roster.EXTENSION_ATTR).firstElement()  +"' " + 
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
				
				roster.getUser(userID).addObject(Roster.SCHEDULE_ATTR, thisSchedule);
				
				//at this point the user has all shift accumulated for this schedule
				//we get a list of shifts, sort by eariliest to latest

				//we could populate this direct from the db query but then it would likely be unsorted
				StringBuilder shiftString = new StringBuilder();
				for(Shift s : thisSchedule.getSortedShifts())
				{
					shiftString.append(dp.readableGregorian(s.getStartDate()));
					shiftString.append(" -> ");
					shiftString.append(dp.readableGregorian(s.getEndDate()));
					shiftString.append(",");
				}
				
				scheduleStartDate = thisSchedule.getInterval().getStartDate();
				scheduleEndDate = thisSchedule.getInterval().getEndDate();
				
				//sch start, sch end, shift string with all shifts
				
				retval.add(new String[] 
						{
							roster.getUser(userID).getAttrData(Roster.FULLNAME_ATTR).firstElement(),
							dp.readableGregorian(scheduleStartDate),
							dp.readableGregorian(scheduleEndDate),
							shiftString.toString()
						});
			}
		}

		return retval;
	}
	
	/**
	 * Load the schedules into the userlying roster for reference later.
	 * 
	 * @return	The roster generated by the Schedule report.
	 */
	public HashMap<String, User> load()
	{
		startReport();
		
		//roster is good at this point, startReport runs the parameter validation which loads the roster.
		//ignore the report output, since startReport adds shifts and schedules to the internal roster
		
		return roster.getUsers();
	}
	
	/**
	 * Accessor for a specified User.
	 * 
	 * @param userParameter	String to query the Users by. Can be Employee ID or User ID.
	 * 
	 * @return	The User discovered.
	 */
	public User getUser(String userParameter)
	{
		return roster.getUser(userParameter);
	}
	
	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = false;
		boolean validateAgentName = false;

		if(
				isValidReportType(new int[]{AGENT_STACK_REPORT,AGENT_TIME_REPORT}) &&
				hasValidDateInterval()
		 )
		{
			try
			{
				//schedule has a time interval, and a roster
				//we should be able to view the roster's full schedule, or an agent's schedule
				//time interval can go into the future, since we're not doing any metrics
				switch(Integer.parseInt(parameters.get(REPORT_TYPE_PARAM)))
				{
				case AGENT_TIME_REPORT:
					setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
					validateAgentName = true;
					isTimeReport = true;
				case AGENT_STACK_REPORT:
					retval = true;
					break;
				default:
					//nothing, verified valid by isValidReportType
					break;
				}

				if(retval)
				{					
					String rosterType = getParameter(ROSTER_TYPE_PARAM);

					if(ReportParameterValidator.validateRosterType(rosterType))
					{
						roster = new Roster();
						roster.setChildReport(true);

						roster.setParameter(ROSTER_TYPE_PARAM, getParameter(ROSTER_TYPE_PARAM));
						roster.load();

						logger.log(Level.INFO, "Confirmed coherent report roster type: " + rosterType);

						if(validateAgentName)
						{
							String agentName = getParameter(AGENT_NAME_PARAM);
							if(ReportParameterValidator.validateAgentName(agentName, roster))
							{
								logger.log(Level.INFO, "Confirmed coherent agentName: " + agentName);
							}
							else
							{
								logger.log(Level.ERROR,  "Agent name not found in report's roster, aborting report" );
							}
						}
					}
					else
					{
						logger.log(Level.ERROR,  "Unexpected report roster type: " + rosterType );
					}
				}
			} 
			catch (ReportSetupException e) 
			{
				logger.log(Level.ERROR,  "Failed running roster subreport");
				retval = false;
			}
			catch (Exception e)
			{
				logger.log(Level.ERROR,  "Exception: " + e.getMessage() + " processing report parameters");	
				retval = false;
			}
			finally
			{
				if(retval == false && roster != null)
				{
					roster.close();
				}
			}
		}

		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Schedules rs = null;
		 
		System.out.println("Agent Time Report");

		try
		{
			rs = new Schedules();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			//rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Perez, Adam");

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

			for(String[] row : rs.startReport())
			{
				System.out.println(Arrays.asList(row).toString());
			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
			}
		}

		System.out.println("===================\nAgent Stack report");

		
		try
		{
			rs = new Schedules();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_STACK_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

			for(String[] row : rs.startReport())
			{
				System.out.println(Arrays.asList(row).toString());
			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs != null)
			{
				rs.close();
			}
		}
		System.out.println("===================\nSchedule querying");
		
		
//		try
//		{
//			rs = new Schedules();
//
//			rs.setParameter(REPORT_TYPE_PARAM, AGENT_STACK_REPORT);
//			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
//
//			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");
//
//			rs.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
//			rs.setParameter(END_DATE_PARAM, "2012-07-31 23:59:59");
//
//			rs.load();
//			
//			for(Shift s : rs.getUser("Justice, Kevin").getSchedule("2012-07-18 18:00:00").getSortedShifts())
//			{
//				System.out.println(s.toString());
//			}
//			
//		} 
//		catch (ReportSetupException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			if(rs != null)
//			{
//				rs.close();
//			}
//		}
	}
}
