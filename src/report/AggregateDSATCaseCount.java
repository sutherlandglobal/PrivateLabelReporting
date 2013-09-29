/**
 * 
 */
package report;

import java.util.ArrayList;
import java.util.Arrays;

import report.Report;
import report.ReportRunner;
import team.Team;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public final class AggregateDSATCaseCount extends Report {

	private LMIDSATCaseCount LMIDSATReport;
	private IVRDSATCaseCount IVRDSATReport;
	private static String DSAT_CASE_ATTR = "dsatCases";
	private static String LMI_DSAT_CASE_ATTR = "lmidsatCases";
	private static String IVR_DSAT_CASE_ATTR = "iverdsatCases";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AggregateDSATCaseCount() throws ReportSetupException 
	{
		super();

		reportName = "Aggregate DSAT Case Count";
		
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
		if(LMIDSATReport != null)
		{
			LMIDSATReport.close();
		}
		
		if(IVRDSATReport != null)
		{
			IVRDSATReport.close();
		}

		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();
		
		Team reportGrainData = new Team();

		String reportGrain;
		//int timeGrain;
		
		ReportRunner runner = new ReportRunner();
		
		LMIDSATReport = new LMIDSATCaseCount();
		LMIDSATReport.setParameter(START_DATE_PARAM, getParameter(Report.START_DATE_PARAM));
		LMIDSATReport.setParameter(END_DATE_PARAM, getParameter(Report.END_DATE_PARAM));
		LMIDSATReport.setParameter(TIME_GRAIN_PARAM, getParameter(Report.TIME_GRAIN_PARAM));
		
		IVRDSATReport = new IVRDSATCaseCount();
		IVRDSATReport.setParameter(START_DATE_PARAM, getParameter(Report.START_DATE_PARAM));
		IVRDSATReport.setParameter(END_DATE_PARAM, getParameter(Report.END_DATE_PARAM));
		IVRDSATReport.setParameter(TIME_GRAIN_PARAM, getParameter(Report.TIME_GRAIN_PARAM));
		IVRDSATReport.setParameter(REPORT_TYPE_PARAM, Report.TEAM_TIME_REPORT);
		IVRDSATReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);

		runner.addReport(LMI_DSAT_CASE_ATTR, IVRDSATReport);
		runner.addReport(IVR_DSAT_CASE_ATTR, LMIDSATReport);

		//this is a time report, but since lmi limits us, we have to set the report type of agg dsat explicitly
		parameters.put(REPORT_TYPE_PARAM, "" + IVRDSATReport.getParameter("" + Report.REPORT_TYPE_PARAM));
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			for(String[] row : runner.getResults(IVR_DSAT_CASE_ATTR))
			{
				//timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
				//reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[0]));
				reportGrain = row[0];
				
				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(DSAT_CASE_ATTR );
				reportGrainData.getUser(reportGrain).addData(DSAT_CASE_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(LMI_DSAT_CASE_ATTR))
			{
				//timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
				//reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[0]));

				reportGrain = row[0];
				
				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(DSAT_CASE_ATTR );
				reportGrainData.getUser(reportGrain).addData(DSAT_CASE_ATTR, row[1]);
			}
		}
		
		int dsatCount;
		for(String user : reportGrainData.getUserList())
		{
			//not all users will have refunds
			dsatCount = 0;
			if( reportGrainData.getUser(user).getAttrData(DSAT_CASE_ATTR) != null)
			{
				for(String datum : reportGrainData.getUser(user).getAttrData(DSAT_CASE_ATTR))
				{
					dsatCount += Integer.parseInt(datum);
				}
			}

			retval.add(new String[]{user, "" + dsatCount }) ;
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
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		AggregateDSATCaseCount rs = null;

		try
		{
			rs = new AggregateDSATCaseCount();

			rs.setParameter(START_DATE_PARAM, "2012-12-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-12-31 23:59:59");
			
			rs.setParameter(TIME_GRAIN_PARAM, DateParser.DAILY_GRANULARITY);

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
