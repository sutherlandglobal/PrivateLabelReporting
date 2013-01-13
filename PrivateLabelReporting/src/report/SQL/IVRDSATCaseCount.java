/**
 * 
 */
package report.SQL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import team.Team;
import team.User;
import util.DateParser;
import util.ReportParameterValidator;
import database.connection.SQL.RemoteConnection;
import exceptions.ReportSetupException;

/**
 * @author Jasonj Diamond
 *
 */
public final class IVRDSATCaseCount extends Report {

	private RemoteConnection dbConnection;
	private Roster roster;
	private IVRDSATCases dsatCaseReport;

	private static String DSAT_CASE_ATTR = "dsatCases";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRDSATCaseCount() throws ReportSetupException 
	{
		super();

		reportName = "IVR DSAT Case Count";
		
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
		boolean retval = true;

		return retval;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(dbConnection != null)
		{
			dbConnection.close();
		}
		
		if(dsatCaseReport != null)
		{
			dsatCaseReport.close();
		}

		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected Vector<String[]> runReport() throws ReportSetupException
	{
		Vector<String[]> retval = new Vector<String[]>();
		
		String reportType = parameters.get(REPORT_TYPE_PARAM);
		
		dsatCaseReport = new IVRDSATCases();
		dsatCaseReport.setParameter(Report.START_DATE_PARAM, getParameter(Report.START_DATE_PARAM));
		dsatCaseReport.setParameter(Report.END_DATE_PARAM, getParameter(Report.END_DATE_PARAM));
		dsatCaseReport.setParameter(Report.TIME_GRAIN_PARAM, getParameter(Report.TIME_GRAIN_PARAM));
		dsatCaseReport.setParameter(Report.ROSTER_TYPE_PARAM, getParameter(Report.ROSTER_TYPE_PARAM));
		dsatCaseReport.setParameter(Report.REPORT_TYPE_PARAM, reportType);
		
		if(getParameter(Report.AGENT_NAME_PARAM) != null)
		{
			dsatCaseReport.setParameter(Report.ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
			dsatCaseReport.setParameter(Report.AGENT_NAME_PARAM, getParameter(Report.AGENT_NAME_PARAM));
		}

		Team reportGrainData = new Team();
		Team stackData = new Team();

		String reportGrain, surveyDate = null, name = null, rID;

		for(String[] row : dsatCaseReport.startReport())
		{
			if(reportType.equals("" + AGENT_STACK_REPORT) || reportType.equals("" + TEAM_STACK_REPORT))
			{
				surveyDate = row[3];
				name = row[0];
				rID  = row[1];
			
				//trust that the sub report enforces the roster
				stackData.addUser(name);
				stackData.getUser(name).addAttr(DSAT_CASE_ATTR );
				stackData.getUser(name).addData(DSAT_CASE_ATTR, rID);
			}
			else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
			{
				surveyDate = row[0];
				name = row[3];
				rID  = row[1];

				//the internal report IVRDSATCases handles the date granularity stuff
				reportGrain = surveyDate;

				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(DSAT_CASE_ATTR );
				reportGrainData.getUser(reportGrain).addData(DSAT_CASE_ATTR, rID);
			}
		}

		int dsatCount;
		
		if(reportType.equals("" + AGENT_STACK_REPORT) || reportType.equals("" + TEAM_STACK_REPORT))
		{
			User user;	
			HashMap<String, Integer> stack = new HashMap<String, Integer>();
			
			for(String id : stackData.getUserList())
			{
				user = stackData.getUser(id);

				dsatCount = 0;

				name = user.getAttrData("name").firstElement();
				
				if(!user.addAttr(DSAT_CASE_ATTR))
				{
					dsatCount  = user.getAttrData(DSAT_CASE_ATTR).size() ;
				}
				
				if(stack.containsKey(name))
				{
					stack.put(name, stack.get(name) + dsatCount);
				}
				else
				{
					stack.put(name, dsatCount);
				}
			}
			
			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue()} );
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			for(String dateGrain : reportGrainData.getUserList())
			{
				//not all user/dates will have dsat cases
				dsatCount = 0;
				
				if( reportGrainData.getUser(dateGrain).getAttrData(DSAT_CASE_ATTR) != null)
				{
					dsatCount = reportGrainData.getUser(dateGrain).getAttrData(DSAT_CASE_ATTR).size();
				}

				retval.add(new String[]{dateGrain, "" + dsatCount }) ;
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
					retval = hasValidTimeGrain();
				case AGENT_STACK_REPORT:
					retval = true;
					break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////
				case TEAM_TIME_REPORT:
					isTimeReport = true;
					retval = hasValidTimeGrain();
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
		IVRDSATCaseCount rs = null;

		System.out.println("Agent Time Report");
		
		try
		{
			rs = new IVRDSATCaseCount();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Zioto, Andrew");

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
		
		System.out.println("===================\nAgent Stack report");
		
		try
		{
			rs = new IVRDSATCaseCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

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
		
		System.out.println("===================\nTeam Time Report");
		
		try
		{
			rs = new IVRDSATCaseCount();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

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
		
		System.out.println("===================\nTeam Stack report");
		
		try
		{
			rs = new IVRDSATCaseCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

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
