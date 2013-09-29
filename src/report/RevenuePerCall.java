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
public final class RevenuePerCall extends Report 
{
	private Statistics stats;
	private final static String SALES_VAL_ATTR = "salesValues";
	private final static String CALL_VOL_ATTR = "callVol";
	private NumberFormatter numFormatter;
	private Roster roster;
	private CallVolume callVolumeReport;
	private RealtimeSales realtimeSalesReport;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public RevenuePerCall() throws ReportSetupException
	{
		super();

		reportName = "Revenue Per Call";
		
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
		
		if(callVolumeReport != null)
		{
			callVolumeReport.close();
		}
		
		if(realtimeSalesReport != null)
		{
			realtimeSalesReport.close();
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

		realtimeSalesReport = new RealtimeSales();
		realtimeSalesReport.setChildReport(true);
		realtimeSalesReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		if(isTimeReport())
		{
			callVolumeReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
			realtimeSalesReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		}
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			callVolumeReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			realtimeSalesReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
		}
		else
		{
			callVolumeReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			realtimeSalesReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		}

		callVolumeReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		callVolumeReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		realtimeSalesReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		realtimeSalesReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));

		Team reportGrainData = new Team();
		Team reportStackData = new Team();

		String reportGrain, numCalls, fullName;

		runner.addReport(CALL_VOL_ATTR, callVolumeReport);
		runner.addReport(SALES_VAL_ATTR, realtimeSalesReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(CALL_VOL_ATTR))
			{
				numCalls = row[1];

				//trust the Call Volume report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//CallVolume report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(CALL_VOL_ATTR);
					reportGrainData.getUser(reportGrain).addData(CALL_VOL_ATTR, numCalls);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(CALL_VOL_ATTR);
					reportStackData.getUser(fullName).addData(CALL_VOL_ATTR, numCalls);
				}
			}			

			/////////////////

			String salesValue;

			for(String[] row : runner.getResults(SALES_VAL_ATTR))
			{
				salesValue = row[1];

				//trust the Sales Count report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//SalesCount report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(SALES_VAL_ATTR);
					reportGrainData.getUser(reportGrain).addData(SALES_VAL_ATTR, salesValue);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(SALES_VAL_ATTR);
					reportStackData.getUser(fullName).addData(SALES_VAL_ATTR, salesValue);
				}
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalNumCalls, finalSalesVal, finalRPC;
		if(isStackReport() )
		{
			User user;
			for(String name : reportStackData.getUserList())
			{
				finalRPC = 0;

				user = reportStackData.getUser(name);

				if(!user.addAttr(CALL_VOL_ATTR) && !user.addAttr(SALES_VAL_ATTR))
				{
					finalNumCalls = stats.getTotal(user.getAttrData(CALL_VOL_ATTR));
					finalSalesVal = stats.getTotal(user.getAttrData(SALES_VAL_ATTR));

					if(finalNumCalls != 0)
					{
						finalRPC = finalSalesVal/finalNumCalls;
					}
				}

				retval.add(new String[]{	name, numFormatter.convertToCurrency(finalRPC)}	);
			}
		}
		else if(isTimeReport())
		{				

			for(String grain : reportGrainData.getUserList())
			{
				finalRPC = 0;
				finalNumCalls = 0;
				finalSalesVal = 0;

				if(!reportGrainData.getUser(grain).addAttr(CALL_VOL_ATTR) && !reportGrainData.getUser(grain).addAttr(SALES_VAL_ATTR))
				{
					finalNumCalls = stats.getTotal(reportGrainData.getUser(grain).getAttrData(CALL_VOL_ATTR));
					finalSalesVal = stats.getTotal(reportGrainData.getUser(grain).getAttrData(SALES_VAL_ATTR));

					if(finalNumCalls != 0)
					{
						finalRPC = finalSalesVal/finalNumCalls;
					}
				}
				
				retval.add(new String[]{	grain,numFormatter.convertToCurrency(finalRPC)}	);
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
		RevenuePerCall rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new RevenuePerCall();

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
			rs = new RevenuePerCall();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
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
			rs = new RevenuePerCall();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

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
			rs = new RevenuePerCall();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

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
