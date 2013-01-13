/**
 * 
 */
package report.SQL;

import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
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
public final class Conversion extends Report 
{
	private RemoteConnection dbConnection;
	private Statistics stats;
	private final static String ORDER_COUNT_ATTR = "orderCount";
	private final static String CALL_VOL_ATTR = "callVol";
	private Roster roster;
	private final String dbPropFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdbs27.properties";
	private CallVolume callVolumeReport;
	private SalesCount salesCountReport;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public Conversion() throws ReportSetupException
	{
		super();

		reportName = "Conversion";
		
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
			
			factory.load(dbPropFile);
			
			dbConnection = factory.getConnection();
		}
		catch(DatabaseConnectionCreationException e )
		{
			logger.log(Level.ERROR, "DatabaseConnectionCreationException on attempt to access database: " + e.getMessage());	
		}
		finally
		{
			if(dbConnection != null)
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

		//roster sub-report to get the roster

		try
		{
			stats = StatisticsFactory.getStatsInstance();
		}
		finally
		{
			if( stats != null )
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
		if(dbConnection != null)
		{
			dbConnection.close();
		}

		if(roster != null)
		{
			roster.close();
		}
		
		if(callVolumeReport != null)
		{
			callVolumeReport.close();
		}
		
		if(salesCountReport != null)
		{
			salesCountReport.close();
		}

		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected Vector<String[]> runReport() throws ReportSetupException
	{
		//#calls fielded vs # orders made
		
		Vector<String[]> retval = null; 

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		salesCountReport = new SalesCount();
		salesCountReport.setChildReport(true);
		salesCountReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			callVolumeReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			salesCountReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
		}
		else
		{
			callVolumeReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			salesCountReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		}

		callVolumeReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		callVolumeReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		callVolumeReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		salesCountReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		salesCountReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		salesCountReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));

		Team reportGrainData = new Team();
		Team reportStackData = new Team();

		String reportGrain, numCalls, fullName;

		for(String[] row : callVolumeReport.startReport())
		{
			numCalls = row[1];

			//trust the Call Volume report to provide sane date grains and to enforce roster membership
			if(isTimeReport)
			{					
				//CallVolume report gives results in [report grain] [val]
				reportGrain = row[0];

				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(CALL_VOL_ATTR);
				reportGrainData.getUser(reportGrain).addData(CALL_VOL_ATTR, numCalls);
			}
			else //if(reportType.equals("" + AGENT_STACK_REPORT) || reportType.equals("" + TEAM_STACK_REPORT))
			{
				fullName = row[0];

				//format in name,numCalls
				reportStackData.addUser(fullName);
				reportStackData.getUser(fullName).addAttr(CALL_VOL_ATTR);
				reportStackData.getUser(fullName).addData(CALL_VOL_ATTR, numCalls);
			}
		}			

		/////////////////

		String orderCount;

		for(String[] row:  salesCountReport.startReport())
		{
			orderCount = row[1];
			
			//trust the Sales Count report to provide sane date grains and to enforce roster membership
			if(isTimeReport)
			{					
				//SalesCount report gives results in [report grain] [val]
				reportGrain = row[0];

				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(ORDER_COUNT_ATTR);
				reportGrainData.getUser(reportGrain).addData(ORDER_COUNT_ATTR, orderCount);
			}
			else //if(reportType.equals("" + AGENT_STACK_REPORT) || reportType.equals("" + TEAM_STACK_REPORT))
			{
				fullName = row[0];

				//format in name,numCalls
				reportStackData.addUser(fullName);
				reportStackData.getUser(fullName).addAttr(ORDER_COUNT_ATTR);
				reportStackData.getUser(fullName).addData(ORDER_COUNT_ATTR, orderCount);
			}
		}

		double finalNumCalls, finalNumOrders, finalConversion;
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			retval = new Vector<String[]>();
			User user;
			for(String name : reportStackData.getUserList())
			{
				finalConversion = 0;

				user = reportStackData.getUser(name);

				if(!user.addAttr(CALL_VOL_ATTR) && !user.addAttr(ORDER_COUNT_ATTR))
				{
					finalNumCalls = stats.getTotal(user.getAttrData(CALL_VOL_ATTR));
					finalNumOrders = stats.getTotal(user.getAttrData(ORDER_COUNT_ATTR));

					if(finalNumCalls != 0)
					{
						finalConversion = finalNumOrders/finalNumCalls;
					}
				}

				retval.add(new String[]{name, "" + finalConversion });
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{				
			retval = new Vector<String[]>();
			for(String grain : reportGrainData.getUserList())
			{
				finalConversion = 0;
				finalNumCalls = 0;
				finalNumOrders = 0;

				if(!reportGrainData.getUser(grain).addAttr(CALL_VOL_ATTR) && !reportGrainData.getUser(grain).addAttr(ORDER_COUNT_ATTR))
				{
					finalNumCalls = stats.getTotal(reportGrainData.getUser(grain).getAttrData(CALL_VOL_ATTR));
					finalNumOrders = stats.getTotal(reportGrainData.getUser(grain).getAttrData(ORDER_COUNT_ATTR));

					if(finalNumCalls != 0)
					{
						finalConversion = finalNumOrders/finalNumCalls;
					}
				}

				retval.add(new String[]{	grain,"" + finalConversion}	);
			}
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	public boolean validateParameters() 
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
		Conversion rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new Conversion();

			rs.setParameter(REPORT_TYPE_PARAM, "" + AGENT_TIME_REPORT);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			//rs.setParameter(REPORT_TYPE_PARAM, CallVolume.STACK_REPORT);

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
		
		System.out.println("===================\nTeam Time report");
		
		try
		{
			rs = new Conversion();

			rs.setParameter(REPORT_TYPE_PARAM, Conversion.TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			//rs.setParameter(REPORT_TYPE_PARAM, CallVolume.STACK_REPORT);

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
			rs = new Conversion();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, Conversion.AGENT_STACK_REPORT);

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
			rs = new Conversion();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, Conversion.TEAM_STACK_REPORT);

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
