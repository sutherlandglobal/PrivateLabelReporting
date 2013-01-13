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
public class MinutesLate extends Report 
{
	private RemoteConnection rocjfsdbs27Connection;
	private RemoteConnection rocjfsdev18Connection;

	private Roster roster;

	private Statistics stats;
	private final String rocjfsdbs27PropertiesFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdbs27.properties";
	private final String rocjfsdev18PropertiesFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdev18.properties";
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

		GregorianCalendar thisScheduleStartDate;

		Vector<Shift> agentShifts;
		Vector<Object> schedules;

		Schedule thisSchedule;
		
		String reportGrain;
		int timeGrain;
		
		retval = new Vector<String[]>();
		
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

							if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
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

							if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
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
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
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
						fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).firstElement());
					}
					else
					{
						fullName = user.getAttrData(Roster.TEAMNAME_ATTR).firstElement();
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
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
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
