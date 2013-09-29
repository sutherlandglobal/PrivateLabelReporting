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
import formatting.NumberFormatter;

/**
 * @author Jason Diamond
 *
 */
public class ScheduleAdherence extends Report 
{
	private Roster roster;

	private Statistics stats;

	private DateParser dp;
	private NumberFormatter numFormatter;

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
			numFormatter = new NumberFormatter();
			dp = new DateParser();
		}
		finally
		{
			if( stats != null && numFormatter != null && dp != null)
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

		GregorianCalendar thisScheduleStartDate,thisScheduleEndDate, shiftStartDate,shiftEndDate;

		ArrayList<Shift> agentShifts;
		ArrayList<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		int timeGrain;

		double minutesAttended, scheduleAdh;

		retval = new ArrayList<String[]>();

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

//						logger.log(Level.INFO, thisSchedule);
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

					if(isTimeReport())
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
		if(isStackReport() )
		{
			HashMap<String, ArrayList<String>> stack = new HashMap<String, ArrayList<String>>();
			
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
						fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));
					}
					else
					{
						fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);
					}
					
					if(!stack.containsKey(fullName))
					{
						stack.put(fullName, new ArrayList<String>());
					}
					stack.get(fullName).add("" + scheduleAdh);
				}
			}
			
			for(Entry<String, ArrayList<String>> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), numFormatter.convertToPercentage( stats.getAverage(entry.getValue()), 4) });
			}
		}
		else if(isTimeReport())
		{
			for(String date : reportGrainData.getUserList())
			{
				scheduleAdh = stats.getAverage(reportGrainData.getUser(date).getAttrData(SCHEDULE_ADH_ATTR));
				retval.add(new String[]{date, numFormatter.convertToPercentage(scheduleAdh, 4)});
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
		roster.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		roster.setParameter(ROSTER_TYPE_PARAM, getParameter(ROSTER_TYPE_PARAM));
		roster.loadSchedule();
		
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

			rs.setParameter(AGENT_NAME_PARAM, "Beltz, Jason");

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
