/**
 * 
 */
package report;

import java.util.ArrayList;
import java.util.Arrays;

import report.Report;
import report.ReportRunner;
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
public final class RedemptionRate extends Report 
{
	private Statistics stats;
	private final static String SALES_COUNT_ATTR = "salesCount";
	private final static String USED_ISSUES_ATTR = "usedIssues";
	private NumberFormatter numFormatter;
	private Roster roster;
	private UsedIssues usedIssuesReport;
	private SalesCount salesCountReport;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public RedemptionRate() throws ReportSetupException
	{
		super();

		reportName = "Redemption Rate";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupDataSourceConnections()
	 */
	@Override
	protected boolean setupDataSourceConnections()
	{
		return true;
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
		}
		finally
		{
			if( stats != null && numFormatter != null)
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
		
		if(usedIssuesReport != null)
		{
			usedIssuesReport.close();
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
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{
		//#calls fielded vs # orders made
		
		ArrayList<String[]> retval = null; 
		
		ReportRunner runner = new ReportRunner();

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		salesCountReport = new SalesCount();
		salesCountReport.setChildReport(true);
		salesCountReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));

		usedIssuesReport = new UsedIssues();
		usedIssuesReport.setChildReport(true);
		usedIssuesReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		if(isTimeReport())
		{
			usedIssuesReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
			salesCountReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		}
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			usedIssuesReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			salesCountReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
		}
		else
		{
			usedIssuesReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			salesCountReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		}
		
		usedIssuesReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		usedIssuesReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		salesCountReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		salesCountReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));

		Team reportGrainData = new Team();
		Team reportStackData = new Team();

		String reportGrain, numCalls, fullName;

		runner.addReport(USED_ISSUES_ATTR, usedIssuesReport);
		runner.addReport(SALES_COUNT_ATTR, salesCountReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(USED_ISSUES_ATTR))
			{
				numCalls = row[1];

				//trust the Call Volume report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//CallVolume report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(USED_ISSUES_ATTR);
					reportGrainData.getUser(reportGrain).addData(USED_ISSUES_ATTR, numCalls);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(USED_ISSUES_ATTR);
					reportStackData.getUser(fullName).addData(USED_ISSUES_ATTR, numCalls);
				}
			}			

			/////////////////

			String orderCount;

			for(String[] row : runner.getResults(SALES_COUNT_ATTR))
			{
				orderCount = row[1];

				//trust the Sales Count report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//SalesCount report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(SALES_COUNT_ATTR);
					reportGrainData.getUser(reportGrain).addData(SALES_COUNT_ATTR, orderCount);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(SALES_COUNT_ATTR);
					reportStackData.getUser(fullName).addData(SALES_COUNT_ATTR, orderCount);
				}
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalUsedIssues, finalSalesCount, finalRedemptionRate;
		if(isStackReport() )
		{
			User user;
			for(String name : reportStackData.getUserList())
			{
				finalRedemptionRate = 0;

				user = reportStackData.getUser(name);

				if(!user.addAttr(USED_ISSUES_ATTR) && !user.addAttr(SALES_COUNT_ATTR))
				{
					finalUsedIssues = stats.getTotal(user.getAttrData(USED_ISSUES_ATTR));
					finalSalesCount = stats.getTotal(user.getAttrData(SALES_COUNT_ATTR));

					if(finalSalesCount != 0)
					{
						finalRedemptionRate = finalUsedIssues/finalSalesCount;
					}
				}

				retval.add(new String[]{name, numFormatter.convertToPercentage(finalRedemptionRate, 4)});
			}
		}
		else if(isTimeReport())
		{				

			for(String grain : reportGrainData.getUserList())
			{
				finalRedemptionRate = 0;
				finalUsedIssues = 0;
				finalSalesCount = 0;

				if(!reportGrainData.getUser(grain).addAttr(USED_ISSUES_ATTR) && !reportGrainData.getUser(grain).addAttr(SALES_COUNT_ATTR))
				{
					finalUsedIssues = stats.getTotal(reportGrainData.getUser(grain).getAttrData(USED_ISSUES_ATTR));
					finalSalesCount = stats.getTotal(reportGrainData.getUser(grain).getAttrData(SALES_COUNT_ATTR));

					if(finalSalesCount != 0)
					{
						finalRedemptionRate = finalUsedIssues/finalSalesCount;
					}
				}
				
				retval.add(new String[]{	grain,numFormatter.convertToPercentage(finalRedemptionRate, 4)}	);
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
		
		ReportVisitor visitor = new ReportVisitor();
		
		retval = visitor.validate(this);
		
		roster = visitor.getRoster();
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		RedemptionRate rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new RedemptionRate();

			rs.setParameter(REPORT_TYPE_PARAM, "" + AGENT_TIME_REPORT);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-10-31 23:59:59");

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
			rs = new RedemptionRate();

			rs.setParameter(REPORT_TYPE_PARAM, RedemptionRate.TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

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
			rs = new RedemptionRate();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ALL_ROSTER);
			//rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-08-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-08-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, RedemptionRate.AGENT_STACK_REPORT);

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
			rs = new RedemptionRate();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, RedemptionRate.TEAM_STACK_REPORT);

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
