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
public final class PINConsumptionRate extends Report 
{

	private Statistics stats;
	private final static String PINS_CONSUMED_ATTR = "pinsConsumed";
	private final static String OPENED_CASES_ATTR = "openedCases";
	private NumberFormatter numFormatter;
	private Roster roster;
	private OpenedCases openedCasesReport;
	private PINsConsumedCount pinsConsumedReport;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public PINConsumptionRate() throws ReportSetupException
	{
		super();

		reportName = "PIN Consumption Rate";
		
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
		
		if(openedCasesReport != null)
		{
			openedCasesReport.close();
		}
		
		if(pinsConsumedReport != null)
		{
			pinsConsumedReport.close();
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

		pinsConsumedReport = new PINsConsumedCount();
		pinsConsumedReport.setChildReport(true);
		pinsConsumedReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));

		openedCasesReport = new OpenedCases();
		openedCasesReport.setChildReport(true);
		openedCasesReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		if(isTimeReport())
		{
			openedCasesReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
			pinsConsumedReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		}
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			openedCasesReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			pinsConsumedReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
		}
		else
		{
			openedCasesReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			pinsConsumedReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		}
		
		openedCasesReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		openedCasesReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		pinsConsumedReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		pinsConsumedReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));

		Team reportGrainData = new Team();
		Team reportStackData = new Team();

		String reportGrain, numOpenedCases, fullName;

		runner.addReport(OPENED_CASES_ATTR, openedCasesReport);
		runner.addReport(PINS_CONSUMED_ATTR, pinsConsumedReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(OPENED_CASES_ATTR))
			{
				numOpenedCases = row[1];

				//trust the Call Volume report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//CallVolume report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(OPENED_CASES_ATTR);
					reportGrainData.getUser(reportGrain).addData(OPENED_CASES_ATTR, numOpenedCases);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(OPENED_CASES_ATTR);
					reportStackData.getUser(fullName).addData(OPENED_CASES_ATTR, numOpenedCases);
				}
			}			

			/////////////////

			String numPINsConsumed;

			for(String[] row : runner.getResults(PINS_CONSUMED_ATTR))
			{
				numPINsConsumed = row[1];

				//trust the Sales Count report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//SalesCount report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(PINS_CONSUMED_ATTR);
					reportGrainData.getUser(reportGrain).addData(PINS_CONSUMED_ATTR, numPINsConsumed);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(PINS_CONSUMED_ATTR);
					reportStackData.getUser(fullName).addData(PINS_CONSUMED_ATTR, numPINsConsumed);
				}
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalNumCalls, finalNumOrders, finalPinConsumptionRate;
		if( isStackReport() )
		{
			User user;
			for(String name : reportStackData.getUserList())
			{
				finalPinConsumptionRate = 0;

				user = reportStackData.getUser(name);

				if(!user.addAttr(OPENED_CASES_ATTR) && !user.addAttr(PINS_CONSUMED_ATTR))
				{
					finalNumCalls = stats.getTotal(user.getAttrData(OPENED_CASES_ATTR));
					finalNumOrders = stats.getTotal(user.getAttrData(PINS_CONSUMED_ATTR));

					if(finalNumCalls != 0)
					{
						finalPinConsumptionRate = finalNumOrders/finalNumCalls;
					}
				}

				retval.add(new String[]{name, numFormatter.convertToPercentage(finalPinConsumptionRate, 4)});
			}
		}
		else if(isTimeReport())
		{				

			for(String grain : reportGrainData.getUserList())
			{
				finalPinConsumptionRate = 0;
				finalNumCalls = 0;
				finalNumOrders = 0;

				if(!reportGrainData.getUser(grain).addAttr(OPENED_CASES_ATTR) && !reportGrainData.getUser(grain).addAttr(PINS_CONSUMED_ATTR))
				{
					finalNumCalls = stats.getTotal(reportGrainData.getUser(grain).getAttrData(OPENED_CASES_ATTR));
					finalNumOrders = stats.getTotal(reportGrainData.getUser(grain).getAttrData(PINS_CONSUMED_ATTR));

					if(finalNumCalls != 0)
					{
						finalPinConsumptionRate = finalNumOrders/finalNumCalls;
					}
				}
				
				retval.add(new String[]{	grain,numFormatter.convertToPercentage(finalPinConsumptionRate, 4)}	);
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
		PINConsumptionRate rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new PINConsumptionRate();

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
			rs = new PINConsumptionRate();

			rs.setParameter(REPORT_TYPE_PARAM, PINConsumptionRate.TEAM_TIME_REPORT);
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
			rs = new PINConsumptionRate();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, PINConsumptionRate.AGENT_STACK_REPORT);

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
			rs = new PINConsumptionRate();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, PINConsumptionRate.TEAM_STACK_REPORT);

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
