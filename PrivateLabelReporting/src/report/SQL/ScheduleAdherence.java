/**
 * 
 */
package report.SQL;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import schedule.Schedule;
import schedule.Shift;
import statistics.Statistics;
import statistics.StatisticsFactory;
import team.Team;
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
public class ScheduleAdherence extends Report 
{
	private RemoteConnection rocjfsdbs27Connection;
	private RemoteConnection rocjfsdev18Connection;

	private Roster roster;

	private Statistics stats;
	private final String rocjfsdbs27PropertiesFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdbs27.properties";
	private final String rocjfsdev18PropertiesFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdev18.properties";

	private DateParser dp;

	//30-min lunch + 2 15 mins breaks
	//private static final int ALLOWED_MISSED_MINUTES = 60;

	private static final long MINS_LATE_THRESHOLD = 5;

	private static final String SCHEDULE_ADH_ATTR = "scheduleAdh";
	private static final double FIVE_MINS_LATE_PENALTY = .005; //.5% penalty 

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public ScheduleAdherence() throws ReportSetupException 
	{
		super();

		reportName = "Schedule Adherence";

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
			stats = StatisticsFactory.getStatsInstance();
			dp = new DateParser();
		}
		finally
		{
			if(stats != null )
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

		String reportType = parameters.get(REPORT_TYPE_PARAM);
		Team reportGrainData = new Team();

		GregorianCalendar thisScheduleStartDate,thisScheduleEndDate, shiftStartDate,shiftEndDate;

		Vector<Shift> agentShifts;
		Vector<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		int timeGrain;

		double minutesAttended, scheduleAdh;

		retval = new Vector<String[]>();

		boolean fiveMinsLate;
		
		//requirements want an improper? average of each day's schedule adherence, minus the 5 min late penalty

		for(Entry<String, User> user : roster.getUsers().entrySet())
		{
			
			schedules = user.getValue().getUserObjects(Roster.SCHEDULE_ATTR);

			if(schedules != null)
			{
				logger.log(Level.INFO, user.getValue().toString());

				for(Object scheduleObject : schedules)
				{
					thisSchedule = (Schedule)scheduleObject;

					minutesAttended = 0;

					agentShifts = thisSchedule.getSortedShifts();

					thisScheduleStartDate = thisSchedule.getInterval().getStartDate();
					thisScheduleEndDate = thisSchedule.getInterval().getEndDate();

					scheduleAdh = 0;

					fiveMinsLate = false;

					double rawMinutesScheduled =  dp.getMinutesBetween(thisScheduleStartDate, thisScheduleEndDate);

					if(agentShifts.size() > 0)
					{
						Shift firstShift = agentShifts.get(0);

						if(firstShift.getStartDate().after(thisScheduleStartDate) && dp.getMinutesBetween(firstShift.getStartDate(), thisScheduleStartDate) > MINS_LATE_THRESHOLD)
						{
							//this shift is more than 5 minutes late to start, subtract an additional .5% from schedule adherence
							//only if a shift is missed. it makes no sense to have a negative adherence
							fiveMinsLate = true;
						}

						for(Shift agentShift : agentShifts)
						{
							shiftStartDate = agentShift.getStartDate();
							shiftEndDate= agentShift.getEndDate();

							if(shiftStartDate.before(thisScheduleStartDate))
							{
								shiftStartDate = thisScheduleStartDate;
								logger.log(Level.INFO, "Adjusting startdate: " + dp.readableGregorian(shiftStartDate));
							}

							if(shiftEndDate.before(thisScheduleStartDate))
							{
								shiftEndDate =  thisScheduleStartDate;
								logger.log(Level.INFO, "Adjusting enddate: " + dp.readableGregorian(shiftEndDate));
							}

							if(shiftStartDate.after(thisScheduleEndDate))
							{
								shiftStartDate = thisScheduleEndDate;
								logger.log(Level.INFO, "Adjusting startdate: " + dp.readableGregorian(shiftStartDate));
							}

							if(shiftEndDate.after(thisScheduleEndDate))
							{
								shiftEndDate =  thisScheduleEndDate;
								logger.log(Level.INFO, "Adjusting enddate: " + dp.readableGregorian(shiftEndDate));
							}

							minutesAttended += dp.getMinutesBetween(shiftStartDate, shiftEndDate);
						}

						//long actualMinutesScheduled = rawMinutesScheduled - (long)ALLOWED_MISSED_MINUTES;

						logger.log(Level.INFO, thisSchedule);
						logger.log(Level.INFO, "Minutes attended for schedule: " + minutesAttended);
						logger.log(Level.INFO, "Scheduled minutes: " + rawMinutesScheduled);

						scheduleAdh = 1 - ((rawMinutesScheduled - minutesAttended)/rawMinutesScheduled);

						if(fiveMinsLate)
						{
							scheduleAdh -= FIVE_MINS_LATE_PENALTY;
						}
					}


					logger.log(Level.INFO, "Scheduled Adh: " + scheduleAdh);
					logger.log(Level.INFO, "===============================");

					roster.getUser(user.getKey()).addAttr(SCHEDULE_ADH_ATTR);
					roster.getUser(user.getKey()).addData(SCHEDULE_ADH_ATTR, "" + scheduleAdh);

					if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
					{
						//time bucket
						timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
						reportGrain = dateParser.getDateGrain(timeGrain, thisScheduleStartDate);

						reportGrainData.addUser(reportGrain);
						reportGrainData.getUser(reportGrain).addAttr(SCHEDULE_ADH_ATTR);
						reportGrainData.getUser(reportGrain).addData(SCHEDULE_ADH_ATTR, "" + scheduleAdh);
					}
				}
			}
		}

		//at this point, scheduleAdh is calced for this schedule, so just assign the output to the right bucket
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			HashMap<String, Vector<String>> stack = new HashMap<String, Vector<String>>();
			
			//user bucket
			String fullName;
			User user;
			for(String userName : roster.getUserIDList())
			{
				user = roster.getUser(userName);
				
				if(!user.addAttr(SCHEDULE_ADH_ATTR))
				{
					scheduleAdh = stats.getAverage(user.getAttrData(SCHEDULE_ADH_ATTR));

					if(reportType.equals("" + AGENT_STACK_REPORT ))
					{
						fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).firstElement());
					}
					else
					{
						fullName = user.getAttrData(Roster.TEAMNAME_ATTR).firstElement();
					}
					
					if(!stack.containsKey(fullName))
					{
						stack.put(fullName, new Vector<String>());
					}
					stack.get(fullName).add("" + scheduleAdh);
				}
			}
			
			for(Entry<String, Vector<String>> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + stats.getAverage(entry.getValue()) });
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			for(String date : reportGrainData.getUserList())
			{
				retval.add(new String[]{date, "" + stats.getAverage(reportGrainData.getUser(date).getAttrData(SCHEDULE_ADH_ATTR))});
			}
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = false;
		boolean validateAgentName = false;

		if
		(
				isValidReportType(new int[]{AGENT_STACK_REPORT,AGENT_TIME_REPORT,TEAM_STACK_REPORT,TEAM_TIME_REPORT}) &&
				hasValidDateInterval()
				)
		{
			try
			{
				switch(Integer.parseInt(parameters.get(REPORT_TYPE_PARAM)))
				{
				case AGENT_TIME_REPORT:
					setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
					validateAgentName = true;
					isTimeReport = true;
					retval = hasValidTimeGrain(new int[] {DateParser.YEARLY_GRANULARITY, DateParser.MONTHLY_GRANULARITY, DateParser.WEEKLY_GRANULARITY, DateParser.DAILY_GRANULARITY});
				case AGENT_STACK_REPORT:
					retval = true;
					break;
					////////////////////////////////////////////////////////////////////////////////////////////////////////////
				case TEAM_TIME_REPORT:
					isTimeReport = true;
					retval = hasValidTimeGrain(new int[] {DateParser.YEARLY_GRANULARITY, DateParser.MONTHLY_GRANULARITY, DateParser.WEEKLY_GRANULARITY, DateParser.DAILY_GRANULARITY});
				case TEAM_STACK_REPORT:
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
						roster.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
						roster.setParameter(END_DATE_PARAM, getParameter(END_DATE_PARAM));

						logger.log(Level.INFO, "Confirmed coherent report roster type: " + rosterType);

						if(Integer.parseInt(getParameter(REPORT_TYPE_PARAM)) == AGENT_TIME_REPORT)
						{
							roster.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
							roster.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
						}
						else
						{
							roster.setParameter(REPORT_TYPE_PARAM, AGENT_STACK_REPORT);
						}


						roster.loadSchedule();

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
		ScheduleAdherence rs = null;

		/*
		try
		{
			rs = new ScheduleAdherence();

			rs.setParameter(REPORT_TYPE_PARAM, 4);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "now");

			//rs.setParameter(REPORT_TYPE_PARAM, AverageOrderValue.AGENT_STACK_REPORT);

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
		 */

		System.out.println("Agent Time Report");

		try
		{
			rs = new ScheduleAdherence();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Perez, Adam");

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-13 23:59:59");

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



		System.out.println("===================\nTeam Time report");


		try
		{
			rs = new ScheduleAdherence();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-13 23:59:59");

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
		
		System.out.println("===================\nAgent Stack report");

		try
		{
			rs = new ScheduleAdherence();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-13 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_STACK_REPORT);

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

		System.out.println("===================\nTeam Stack report");

		try
		{
			rs = new ScheduleAdherence();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-13 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_STACK_REPORT);

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
		 
	}
}
