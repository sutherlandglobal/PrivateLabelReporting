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
public final class CallsPerCase extends Report 
{
	private Statistics stats;
	private final static String CALL_VOLUME_ATTR = "callVolume";
	private final static String UPDATED_CASES_ATTR = "updatedCases";
	private final static String CREATED_CASES_ATTR = "createdCases";
	private NumberFormatter numFormatter;

	private UpdatedCases updatedCasesReport;
	private CreatedCases createdCasesReport;
	private CallVolume callVolumeReport;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public CallsPerCase() throws ReportSetupException
	{
		super();

		reportName = "Calls Per Case";
		
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
		if(createdCasesReport != null)
		{
			createdCasesReport.close();
		}
		
		if(updatedCasesReport != null)
		{
			updatedCasesReport.close();
		}
		
		if(callVolumeReport != null)
		{
			callVolumeReport.close();
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
		
		ReportRunner runner = new ReportRunner();

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));

		updatedCasesReport = new UpdatedCases();
		updatedCasesReport.setChildReport(true);
		updatedCasesReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		createdCasesReport = new CreatedCases();
		createdCasesReport.setChildReport(true);
		createdCasesReport.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
		
		if(isTimeReport())
		{
			updatedCasesReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
			callVolumeReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
			createdCasesReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		}
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			updatedCasesReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			callVolumeReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
			createdCasesReport.setParameter(AGENT_NAME_PARAM, parameters.get(AGENT_NAME_PARAM));
		}
		else
		{
			updatedCasesReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			callVolumeReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
			createdCasesReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		}
		
		updatedCasesReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		updatedCasesReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		callVolumeReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		callVolumeReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));
		
		createdCasesReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		createdCasesReport.setParameter(END_DATE_PARAM, parameters.get(END_DATE_PARAM));

		Team reportGrainData = new Team();
		Team reportStackData = new Team();

		String reportGrain, numUpdatedCases, fullName, numCalls, numCreatedCases;

		runner.addReport(UPDATED_CASES_ATTR, updatedCasesReport);
		runner.addReport(CALL_VOLUME_ATTR, callVolumeReport);
		runner.addReport(CREATED_CASES_ATTR, createdCasesReport);
		
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{	
			for(String[] row : runner.getResults(UPDATED_CASES_ATTR))
			{
				numUpdatedCases = row[1];

				//trust the report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//eport gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(UPDATED_CASES_ATTR);
					reportGrainData.getUser(reportGrain).addData(UPDATED_CASES_ATTR, numUpdatedCases);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(UPDATED_CASES_ATTR);
					reportStackData.getUser(fullName).addData(UPDATED_CASES_ATTR, numUpdatedCases);
				}
			}			

			/////////////////

			for(String[] row : runner.getResults(CALL_VOLUME_ATTR))
			{
				numCalls = row[1];

				//trust the report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(CALL_VOLUME_ATTR);
					reportGrainData.getUser(reportGrain).addData(CALL_VOLUME_ATTR, numCalls);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(CALL_VOLUME_ATTR);
					reportStackData.getUser(fullName).addData(CALL_VOLUME_ATTR, numCalls);
				}
			}
			
			/////////////////

			for(String[] row : runner.getResults(CREATED_CASES_ATTR))
			{
				numCreatedCases = row[1];

				//trust the report to provide sane date grains and to enforce roster membership
				if(isTimeReport())
				{					
					//SalesCount report gives results in [report grain] [val]
					reportGrain = row[0];

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(CREATED_CASES_ATTR);
					reportGrainData.getUser(reportGrain).addData(CREATED_CASES_ATTR, numCreatedCases);
				}
				else if(isStackReport())
				{
					fullName = row[0];

					//format in name,numCalls
					reportStackData.addUser(fullName);
					reportStackData.getUser(fullName).addAttr(CREATED_CASES_ATTR);
					reportStackData.getUser(fullName).addData(CREATED_CASES_ATTR, numCreatedCases);
				}
			}
		}

		retval = new ArrayList<String[]>();
		
		double finalCreatedCases, finalCallVolume, finalUpdatedCases, callsPerCase;
		if(isStackReport() )
		{
			User user;
			for(String name : reportStackData.getUserList())
			{
				callsPerCase = 0;

				user = reportStackData.getUser(name);

				if(!user.addAttr(UPDATED_CASES_ATTR) && !user.addAttr(CALL_VOLUME_ATTR) && !user.addAttr(CREATED_CASES_ATTR))
				{
					finalCallVolume = stats.getTotal(user.getAttrData(CALL_VOLUME_ATTR));
					finalUpdatedCases = stats.getTotal(user.getAttrData(UPDATED_CASES_ATTR));
					finalCreatedCases = stats.getTotal(user.getAttrData(CREATED_CASES_ATTR));

					if(finalCallVolume != 0)
					{
						callsPerCase = (finalUpdatedCases + finalCreatedCases)/finalCallVolume;
					}
				}

				retval.add(new String[]{name, numFormatter.convertToCurrency(callsPerCase)});
			}
		}
		else if(isTimeReport())
		{				
			for(String grain : reportGrainData.getUserList())
			{
				callsPerCase = 0;

				if(!reportGrainData.getUser(grain).addAttr(UPDATED_CASES_ATTR) && !reportGrainData.getUser(grain).addAttr(CALL_VOLUME_ATTR) && !reportGrainData.getUser(grain).addAttr(CREATED_CASES_ATTR))
				{
					finalCallVolume = stats.getTotal(reportGrainData.getUser(grain).getAttrData(CALL_VOLUME_ATTR));
					finalUpdatedCases = stats.getTotal(reportGrainData.getUser(grain).getAttrData(UPDATED_CASES_ATTR));
					finalCreatedCases = stats.getTotal(reportGrainData.getUser(grain).getAttrData(CREATED_CASES_ATTR));

					if(finalCallVolume != 0)
					{
						callsPerCase = (finalUpdatedCases + finalCreatedCases)/finalCallVolume;
					}
				}
				
				retval.add(new String[]{	grain,numFormatter.convertToCurrency(callsPerCase)}	);
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
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		CallsPerCase rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new CallsPerCase();

			rs.setParameter(REPORT_TYPE_PARAM, "" + AGENT_TIME_REPORT);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Zioto, Andrew");

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
		
		System.out.println("===================\nTeam Time report");
		
		try
		{
			rs = new CallsPerCase();

			rs.setParameter(REPORT_TYPE_PARAM, CallsPerCase.TEAM_TIME_REPORT);
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
			rs = new CallsPerCase();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, CallsPerCase.AGENT_STACK_REPORT);

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
			rs = new CallsPerCase();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.ACTIVE_SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-01-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, CallsPerCase.TEAM_STACK_REPORT);

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
