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
public final class NetSalesCount extends Report 
{
	private Statistics stats;
	private final static String REFUNDS_ATTR = "refunds";
	private final static String SALES_ATTR = "sales";
	private NumberFormatter numFormatter;
	private Roster roster;
	private SalesCount salesCount;
	private RefundCount refundCountReport;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public NetSalesCount() throws ReportSetupException
	{
		super();

		reportName = "Net Sales";
		
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
		
		if(salesCount != null)
		{
			salesCount.close();
		}
		
		if(refundCountReport != null)
		{
			refundCountReport.close();
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

		refundCountReport = new RefundCount();
		refundCountReport.setChildReport(true);
		refundCountReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));

		salesCount = new SalesCount();
		salesCount.setChildReport(true);
		salesCount.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		if(isTimeReport())
		{
			salesCount.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
			refundCountReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		}
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			salesCount.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			refundCountReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
		}
		else
		{
			salesCount.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			refundCountReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		}

		salesCount.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		salesCount.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		refundCountReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		refundCountReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));

		Team reportGrainData = new Team();
		Team reportStackData = new Team();

		String reportGrain, numCalls, fullName;

		runner.addReport(SALES_ATTR, salesCount);
		runner.addReport(REFUNDS_ATTR, refundCountReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(SALES_ATTR))
			{
				numCalls = row[1];

				//trust the subreport report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//subreport report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(SALES_ATTR);
					reportGrainData.getUser(reportGrain).addData(SALES_ATTR, numCalls);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(SALES_ATTR);
					reportStackData.getUser(fullName).addData(SALES_ATTR, numCalls);
				}
			}			

			/////////////////

			String orderCount;

			for(String[] row : runner.getResults(REFUNDS_ATTR))
			{
				orderCount = row[1];

				//trust the Sales Count report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//SalesCount report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(REFUNDS_ATTR);
					reportGrainData.getUser(reportGrain).addData(REFUNDS_ATTR, orderCount);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(REFUNDS_ATTR);
					reportStackData.getUser(fullName).addData(REFUNDS_ATTR, orderCount);
				}
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalSales, finalRefunds, finalRevenue;
		if(isStackReport() )
		{
			User user;
			for(String name : reportStackData.getUserList())
			{
				finalRevenue = 0;

				user = reportStackData.getUser(name);

				if(!user.addAttr(SALES_ATTR) && !user.addAttr(REFUNDS_ATTR))
				{
					finalSales = stats.getTotal(user.getAttrData(SALES_ATTR));
					finalRefunds = stats.getTotal(user.getAttrData(REFUNDS_ATTR));

					if(finalSales != 0)
					{
						finalRevenue = finalSales - finalRefunds;
					}
				}

				retval.add(new String[]{name, numFormatter.convertToCurrency(finalRevenue)});
			}
		}
		else if(isTimeReport())
		{				

			for(String grain : reportGrainData.getUserList())
			{
				finalRevenue = 0;
				finalSales = 0;
				finalRefunds = 0;

				if(!reportGrainData.getUser(grain).addAttr(SALES_ATTR) && !reportGrainData.getUser(grain).addAttr(REFUNDS_ATTR))
				{
					finalSales = stats.getTotal(reportGrainData.getUser(grain).getAttrData(SALES_ATTR));
					finalRefunds = stats.getTotal(reportGrainData.getUser(grain).getAttrData(REFUNDS_ATTR));

					if(finalSales != 0)
					{
						finalRevenue = finalSales - finalRefunds;
					}
				}
				
				retval.add(new String[]{	grain,numFormatter.convertToCurrency(finalRevenue)}	);
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
		NetSalesCount rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new NetSalesCount();

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
			rs = new NetSalesCount();

			rs.setParameter(REPORT_TYPE_PARAM, NetSalesCount.TEAM_TIME_REPORT);
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
			rs = new NetSalesCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, NetSalesCount.AGENT_STACK_REPORT);

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
			rs = new NetSalesCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, NetSalesCount.TEAM_STACK_REPORT);

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
