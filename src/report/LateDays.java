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

import constants.Constants;

import report.Report;
import schedule.Schedule;
import schedule.Shift;
import statistics.Statistics;
import statistics.StatisticsFactory;
import team.Team;
import team.User;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class LateDays extends Report 
{
	private RemoteConnection rocjfsdbs27Connection;
	private RemoteConnection rocjfsdev18Connection;

	private Roster roster;

	private Statistics stats;
	private final String rocjfsdbs27PropertiesFile = Constants.PRIVATE_LABEL_PROD_DB;
	private final String rocjfsdev18PropertiesFile = Constants.PRIVATE_LABEL_DEV_DB;
	private DateParser dp;

	//30-min lunch + 2 15 mins breaks
	//private static final int ALLOWED_MISSED_MINUTES = 60;

	private static final long MINS_LATE_THRESHOLD = 5;
	private static final String LATE_DAYS_ATTR = "lateDays";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LateDays() throws ReportSetupException 
	{
		super();

		reportName = "Late Days";

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
	protected ArrayList<String[]> runReport() throws ReportSetupException
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

		boolean isLateDay;
		
		retval = new ArrayList<String[]>();
		
		for(Entry<String, User> user : roster.getUsers().entrySet())
		{
			schedules = user.getValue().getUserObjects(Roster.SCHEDULE_ATTR);

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

						if( !(firstShift.getStartDate().after(thisScheduleStartDate) && dp.getMinutesBetween(firstShift.getStartDate(), thisScheduleStartDate) > MINS_LATE_THRESHOLD))
						{
							isLateDay = false;
						}
					}

					roster.getUser(user.getKey()).addAttr(LATE_DAYS_ATTR);

					if(isLateDay)
					{
						logger.log(Level.INFO, "Late Day: " + thisSchedule.getStartDate());

						roster.getUser(user.getKey()).addData(LATE_DAYS_ATTR, "" + 1);

						if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
						{
							//time bucket
							timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
							reportGrain = dateParser.getDateGrain(timeGrain, thisScheduleStartDate);

							reportGrainData.addUser(reportGrain);
							reportGrainData.getUser(reportGrain).addAttr(LATE_DAYS_ATTR);
							reportGrainData.getUser(reportGrain).addData(LATE_DAYS_ATTR, "" + 1);
						}
					}
					else
					{
						roster.getUser(user.getKey()).addData(LATE_DAYS_ATTR, "" + 0);

						if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
						{
							//time bucket
							timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
							reportGrain = dateParser.getDateGrain(timeGrain, thisScheduleStartDate);

							reportGrainData.addUser(reportGrain);
							reportGrainData.getUser(reportGrain).addAttr(LATE_DAYS_ATTR);
							reportGrainData.getUser(reportGrain).addData(LATE_DAYS_ATTR, "" + 0);
						}
					}
				}
			}
		}

		//at this point, scheduleAdh is calced for this schedule, so just assign the output to the right bucket
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			HashMap<String, Integer> stack = new HashMap<String, Integer>();
			
			double numLateDays;
			//user bucket
			String fullName;
			User user;
			for(String userName : roster.getUserIDList())
			{
				user = roster.getUser(userName);
				
				if(!user.addAttr(LATE_DAYS_ATTR))
				{
					numLateDays = stats.getTotal(user.getAttrData(LATE_DAYS_ATTR));

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
						stack.put(fullName, (int)numLateDays);
					}
					else
					{
						stack.put(fullName, stack.get(fullName) + (int)numLateDays);
					}
				}
			}
			
			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			for(String date : reportGrainData.getUserList())
			{
				retval.add(new String[]{date, "" + stats.getTotal(reportGrainData.getUser(date).getAttrData(LATE_DAYS_ATTR))});
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
		LateDays rs = null;

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
			rs = new LateDays();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.WEEKLY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Vij, Nitin");

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
			rs = new LateDays();

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
			rs = new LateDays();

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
			rs = new LateDays();

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
