/**
 * 
 */
package report.SQL;

import java.util.Arrays;
import java.util.Vector;

import report.Report;
import team.Team;
import util.DateParser;
import database.connection.SQL.RemoteConnection;
import exceptions.ReportSetupException;

/**
 * @author jdiamond
 *
 */
public final class LMIDSATCaseCount extends Report {

	private RemoteConnection dbConnection;
	private LMIDSATCases dsatCaseReport;
	private static String DSAT_CASE_ATTR = "dsatCases";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LMIDSATCaseCount() throws ReportSetupException 
	{
		super();

		reportName = "LMI DSAT Case Count";
		
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
		
		dsatCaseReport = new LMIDSATCases();
		dsatCaseReport.setParameter(START_DATE_PARAM, getParameter(Report.START_DATE_PARAM));
		dsatCaseReport.setParameter(END_DATE_PARAM, getParameter(Report.END_DATE_PARAM));
		dsatCaseReport.setParameter(TIME_GRAIN_PARAM, getParameter(Report.TIME_GRAIN_PARAM));

		Team reportGrainData = new Team();

		String reportGrain;
		int timeGrain;

		for(String[] row : dsatCaseReport.runReport())
		{
			timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
			reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[0]));

			reportGrainData.addUser(reportGrain);
			reportGrainData.getUser(reportGrain).addAttr(DSAT_CASE_ATTR );
			reportGrainData.getUser(reportGrain).addData(DSAT_CASE_ATTR, row[0]);
		}

		int dsatCount;
		for(String user : reportGrainData.getUserList())
		{
			//not all users will have refunds
			dsatCount = 0;
			if( reportGrainData.getUser(user).getAttrData(DSAT_CASE_ATTR) != null)
			{
				dsatCount = reportGrainData.getUser(user).getAttrData(DSAT_CASE_ATTR).size();
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
		
		if
		(
				hasValidDateInterval() &&
				hasValidTimeGrain()
		)
		{
			isTimeReport = true;
			retval = true;
		}
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		LMIDSATCaseCount rs = null;

		try
		{
			rs = new LMIDSATCaseCount();

			rs.setParameter(START_DATE_PARAM, "2012-09-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-09-30 23:59:59");
			
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
