/**
 * 
 */
package report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;

import report.Report;
import schedule.Schedule;
import schedule.Shift;
import statistics.Statistics;
import statistics.StatisticsFactory;
import team.Team;
import team.User;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class MinutesWorked extends Report 
{
	private Roster roster;

	private Statistics stats;

	private DateParser dp;

	private static final String MINS_WORKED_ATTR = "workedMins";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public MinutesWorked() throws ReportSetupException 
	{
		super();

		reportName = "Minutes Worked";

		logger.info("Building report " +  reportName);
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
	protected ArrayList<String[]> runReport() 
	{
		ArrayList<String[]> retval = null;

		String reportType = parameters.get(REPORT_TYPE_PARAM);
		Team reportGrainData = new Team();

		GregorianCalendar thisScheduleStartDate,shiftStartDate,shiftEndDate;

		ArrayList<Shift> agentShifts;
		ArrayList<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;

		double minutesAttended;

		retval = new ArrayList<String[]>();
		
		for(Entry<String, User> user : roster.getUsers().entrySet())
		{
			schedules = user.getValue().getUserObjects(Roster.SCHEDULE_ATTR);

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
							//						if(shiftStartDate.before(thisScheduleStartDate))
							//						{
							//							shiftStartDate = thisScheduleStartDate;
							//						}
							//
							//						if(shiftEndDate.after(thisScheduleEndDate))
							//						{
							//							shiftEndDate =  thisScheduleEndDate;
							//						}

							minutesAttended += dp.getMinutesBetween(shiftStartDate, shiftEndDate);
						}

						roster.getUser(user.getKey()).addAttr(MINS_WORKED_ATTR);


						roster.getUser(user.getKey()).addData(MINS_WORKED_ATTR, "" + minutesAttended);

						if(isTimeReport())
						{
							//time bucket
							timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
							reportGrain = dateParser.getDateGrain(timeGrain, thisScheduleStartDate);

							reportGrainData.addUser(reportGrain);
							reportGrainData.getUser(reportGrain).addAttr(MINS_WORKED_ATTR);
							reportGrainData.getUser(reportGrain).addData(MINS_WORKED_ATTR, "" + minutesAttended);
						}
					}
				}
			}
		}

		//at this point, scheduleAdh is calced for this schedule, so just assign the output to the right bucket
		if(isStackReport() )
		{
			HashMap<String, Double> stack = new HashMap<String, Double>();

			double minutesWorked;
			//user bucket
			String fullName;
			User user;
			for(String userName : roster.getUserIDList())
			{
				user = roster.getUser(userName);

				if(!user.addAttr(MINS_WORKED_ATTR))
				{
					minutesWorked = stats.getTotal(user.getAttrData(MINS_WORKED_ATTR));

					if(reportType.equals("" + AGENT_STACK_REPORT ))
					{
						fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));
					}
					else
					{
						fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);
					}

					if(!stack.containsKey(fullName))
					{
						stack.put(fullName, minutesWorked);
					}
					else
					{
						stack.put(fullName, stack.get(fullName) + minutesWorked);
					}
				}
			}

			for(Entry<String, Double> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
			}
		}
		else if(isTimeReport())
		{
			for(String date : reportGrainData.getUserList())
			{
				retval.add(new String[]{date, "" + stats.getTotal(reportGrainData.getUser(date).getAttrData(MINS_WORKED_ATTR))});
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
		
		ReportVisitor visitor = new ReportVisitor();
		
		retval = visitor.validate(this);
		
		roster = visitor.getRoster();
		
		roster.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		roster.setParameter(END_DATE_PARAM, getParameter(END_DATE_PARAM));
		roster.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		roster.loadSchedule();
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		MinutesWorked rs = null;

		System.out.println("Agent Time Report");

		try
		{
			rs = new MinutesWorked();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Holmes, Doug");


			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

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
			rs = new MinutesWorked();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

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
			rs = new MinutesWorked();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

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
			rs = new MinutesWorked();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

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
