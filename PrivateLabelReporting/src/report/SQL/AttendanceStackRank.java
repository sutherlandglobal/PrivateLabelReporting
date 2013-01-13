package report.SQL;
/**
 * 
 */

import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import team.Team;
import util.ReportParameterValidator;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class AttendanceStackRank extends Report
{
	private final static String NAME_ATTR = "userName";
	private final static String SCH_ADH_ATTR = "scheduleAdherence";
	private final static String LATE_DAYS_ATTR = "lateDays";
	private final static String MINS_LATE_ATTR = "minsLate";
	private final static String MINS_WORKED_ATTR = "minsWorked";
	private ScheduleAdherence schAdherence;
	private LateDays lateDays;
	private MinutesLate minutesLate;
	private MinutesWorked minutesWorked;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AttendanceStackRank() throws ReportSetupException
	{
		super();

		reportName = "Attendance Stack Rank";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected Vector<String[]> runReport() throws ReportSetupException
	{

		Vector<String[]> retval = null;

		String endDate = parameters.get(END_DATE_PARAM);

		retval = new Vector<String[]>();

		Team users = new Team();

		schAdherence = new ScheduleAdherence();
		schAdherence.setChildReport(true);
		schAdherence.setParameter(REPORT_TYPE_PARAM, SalesCount.AGENT_STACK_REPORT);
		schAdherence.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		schAdherence.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		schAdherence.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		schAdherence.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : schAdherence.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(SCH_ADH_ATTR );
			users.getUser(row[0]).addData(SCH_ADH_ATTR, row[1]);
		}

		lateDays = new LateDays();
		lateDays.setChildReport(true);
		lateDays.setParameter(REPORT_TYPE_PARAM, RefundCount.AGENT_STACK_REPORT);
		lateDays.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		lateDays.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		lateDays.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		lateDays.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : lateDays.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(LATE_DAYS_ATTR );
			users.getUser(row[0]).addData(LATE_DAYS_ATTR, row[1]);
		}

		minutesLate = new MinutesLate();
		minutesLate.setChildReport(true);
		minutesLate.setParameter(REPORT_TYPE_PARAM, RealtimeSales.AGENT_STACK_REPORT);
		minutesLate.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		minutesLate.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		minutesLate.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		minutesLate.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : minutesLate.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(MINS_LATE_ATTR );
			users.getUser(row[0]).addData(MINS_LATE_ATTR, row[1]);
		}

		minutesWorked = new MinutesWorked();
		minutesWorked.setChildReport(true);
		minutesWorked.setParameter(REPORT_TYPE_PARAM, RealtimeSales.AGENT_STACK_REPORT);
		minutesWorked.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		minutesWorked.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		minutesWorked.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		minutesWorked.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : minutesWorked.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(MINS_WORKED_ATTR );
			users.getUser(row[0]).addData(MINS_WORKED_ATTR, row[1]);
		}

		for(String userName : users.getUserList())
		{
			String name = users.getUser(userName).getAttrData(NAME_ATTR).firstElement();

			int numLateDays = 0;
			double numLateMins = 0;
			double numWorkedMins = 0;

			double scheduleAdherence = 0;

			if(users.getUser(userName).getAttrData(SCH_ADH_ATTR) != null )
			{
				scheduleAdherence = Double.parseDouble(users.getUser(userName).getAttrData(SCH_ADH_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(LATE_DAYS_ATTR) != null)
			{
				numLateDays = Integer.parseInt(users.getUser(userName).getAttrData(LATE_DAYS_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(MINS_LATE_ATTR) != null)
			{
				numLateMins =Double.parseDouble(users.getUser(userName).getAttrData(MINS_LATE_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(MINS_WORKED_ATTR) != null)
			{
				numWorkedMins = Double.parseDouble(users.getUser(userName).getAttrData(MINS_WORKED_ATTR).firstElement());
			}

			retval.add(new String[]
					{
					name, 
					"" + scheduleAdherence, 
					"" + numLateDays, 
					"" + numLateMins, 
					"" + numWorkedMins,
					});
		}

		return retval;
	}

	/* 
	 * (non-Javadoc)
	 * @see report.Report#close()
	 */
	public void close()
	{
		if(schAdherence != null)
		{
			schAdherence.close();
		}

		if(lateDays != null)
		{
			lateDays.close();
		}

		if(minutesLate != null)
		{
			minutesLate.close();
		}
		
		if(minutesWorked != null)
		{
			minutesWorked.close();
		}
		
		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupDataSourceConnections()
	 */
	@Override
	protected boolean setupDataSourceConnections()
	{
		boolean retval = true;

		//connectivity tests handled by subreports

		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport()
	{
		boolean retval = true;


		return retval;
	}
	
	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = false;			
		
		try
		{
			String rosterType = getParameter(ROSTER_TYPE_PARAM);
			if
			(
					hasValidDateInterval() && 
					ReportParameterValidator.validateRosterType(rosterType) && 
					isValidReportType(new int[]{TEAM_STACK_REPORT, AGENT_STACK_REPORT})
			)
			{
				retval = true;
			}			
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR,  "Exception: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		finally
		{

		}

		return retval;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		AttendanceStackRank s = null;
		try
		{
			s = new AttendanceStackRank();
			
			s.setParameter(REPORT_TYPE_PARAM, AGENT_STACK_REPORT);
			s.setParameter(ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);
			s.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			s.setParameter(END_DATE_PARAM, "2012-07-31 23:59:59");

			for(String[] row : s.startReport())
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
			if(s!= null)
			{
				s.close();
			}
		}
	}
}
