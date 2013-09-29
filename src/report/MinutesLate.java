/**
 * 
 */
package report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;

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
public class MinutesLate extends Report 
{
	private Roster roster;

	private Statistics stats;

	private DateParser dp;

	private static final String LATE_MINS_ATTR = "lateMins";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public MinutesLate() throws ReportSetupException 
	{
		super();

		reportName = "Minutes Late";

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

		GregorianCalendar thisScheduleStartDate;

		ArrayList<Shift> agentShifts;
		ArrayList<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		int timeGrain;
		
		retval = new ArrayList<String[]>();
		
		for(Entry<String, User> user : roster.getUsers().entrySet())
		{
			schedules = user.getValue().getUserObjects(Roster.SCHEDULE_ATTR);

			if(schedules != null)
			{

				//no shows are late, but we don't care about the minutes
				for(Object scheduleObject : schedules)
				{

					thisSchedule = (Schedule)scheduleObject;

					agentShifts = thisSchedule.getSortedShifts();

					thisScheduleStartDate = thisSchedule.getInterval().getStartDate();

					if(agentShifts.size() > 0)
					{
						Shift firstShift = agentShifts.get(0);

						roster.getUser(user.getKey()).addAttr(LATE_MINS_ATTR);

						if( firstShift.getStartDate().after(thisScheduleStartDate) )
						{
							logger.log(Level.INFO, "Late Day: " + thisSchedule.getStartDate());

							roster.getUser(user.getKey()).addData(LATE_MINS_ATTR, "" + dp.getMinutesBetween(firstShift.getStartDate(), thisScheduleStartDate));

							if(isTimeReport())
							{
								//time bucket
								timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
								reportGrain = dateParser.getDateGrain(timeGrain, thisScheduleStartDate);

								reportGrainData.addUser(reportGrain);
								reportGrainData.getUser(reportGrain).addAttr(LATE_MINS_ATTR);
								reportGrainData.getUser(reportGrain).addData(LATE_MINS_ATTR, "" + dp.getMinutesBetween(firstShift.getStartDate(), thisScheduleStartDate));
							}
						}
						else
						{
							roster.getUser(user.getKey()).addData(LATE_MINS_ATTR, "" + 0);

							if(isTimeReport())
							{
								//time bucket
								timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
								reportGrain = dateParser.getDateGrain(timeGrain, thisScheduleStartDate);

								reportGrainData.addUser(reportGrain);
								reportGrainData.getUser(reportGrain).addAttr(LATE_MINS_ATTR);
								reportGrainData.getUser(reportGrain).addData(LATE_MINS_ATTR, "" + 0);
							}
						}
					}
				}
			}
		}

		//at this point, scheduleAdh is calced for this schedule, so just assign the output to the right bucket
		if(isStackReport() )
		{
			HashMap<String, Double> stack = new HashMap<String, Double>();
			
			double numMinutesLate;
			//user bucket
			String fullName;
			User user;
			for(String userName : roster.getUserIDList())
			{
				user = roster.getUser(userName);
				
				if(!user.addAttr(LATE_MINS_ATTR))
				{
					numMinutesLate = stats.getTotal(user.getAttrData(LATE_MINS_ATTR));

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
						stack.put(fullName, numMinutesLate);
					}
					else
					{
						stack.put(fullName, stack.get(fullName) + numMinutesLate);
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
				retval.add(new String[]{date, "" + stats.getTotal(reportGrainData.getUser(date).getAttrData(LATE_MINS_ATTR))});
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
		MinutesLate rs = null;

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
			rs = new MinutesLate();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
//			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.WEEKLY_GRANULARITY);
//
//			rs.setParameter(AGENT_NAME_PARAM, "Vij, Nitin");
			
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Holmes, Doug");


			rs.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-07-31 23:59:59");

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
			rs = new MinutesLate();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-07-19 23:59:59");

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
			rs = new MinutesLate();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-07-19 23:59:59");

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
			rs = new MinutesLate();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-07-19 23:59:59");

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
