/**
 * 
 */
package report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;

import constants.Constants;

import report.Report;
import statistics.Statistics;
import statistics.StatisticsFactory;
import team.Team;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author jdiamond
 *
 */
public final class LMICSAT extends Report {

	private RemoteConnection dbConnection;
	private Statistics stats;
	private final static String RAW_SURVEYS_ATTR = "rawSurveys";
	private final static String SAT_SURVEYS_ATTR = "satSurveys";
	private final String dbPropFile = Constants.PRIVATE_LABEL_DEV_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LMICSAT() throws ReportSetupException 
	{
		super();

		reportName = "LMI CSAT";
		
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
			logger.log(Level.ERROR,  "DatabaseConnectionCreationException on attempt to access database: " + e.getMessage());	
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
			if(stats != null )
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

		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport() 
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		String L1query = "Select Date,Technician_ID AS LMI_USER_ID,Q6 FROM LMI_6971911_Customer_Survey Where " + 
				"Date >= '" + parameters.get(START_DATE_PARAM) + "' AND Date <= '" + parameters.get(END_DATE_PARAM) + "' AND  Q6 IS NOT NULL AND Q6 != ''";

		String L2query = "Select Date,Technician_ID AS LMI_USER_ID,Q6 FROM LMI_5452038_Customer_Survey Where " + 
				"Date >= '" + parameters.get(START_DATE_PARAM) + "' AND Date <= '" + parameters.get(END_DATE_PARAM) + "' AND  Q6 IS NOT NULL AND Q6 != ''";

		ArrayList<String> queries = new ArrayList<String>();
		queries.add(L1query);
		queries.add(L2query);

		Team reportData = new Team();

		String q6, reportGrain, tID;

		Map<String, ArrayList<String[]>> aggregateResults = dbConnection.runParallelQueries(queries);

		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(Entry<String, ArrayList<String[]>> query :aggregateResults.entrySet()  )
		{
			for(String[] row : query.getValue())
			{
				tID = row[1];
				q6 = row[2];

				timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
				reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[0]));

				reportData.addUser(reportGrain);
				reportData.getUser(reportGrain).addAttr(RAW_SURVEYS_ATTR);
				reportData.getUser(reportGrain).addAttr(SAT_SURVEYS_ATTR);

				reportData.getUser(reportGrain).addData(RAW_SURVEYS_ATTR, tID);

				if(q6.equals("4") || q6.equals("5"))
				{
					reportData.getUser(reportGrain).addData(SAT_SURVEYS_ATTR, tID);
				}
			}
		}

		double numSatCases, numRawCases;
		for(String grain : reportData.getUserList())
		{
			numSatCases = reportData.getUser(grain).getAttrData(SAT_SURVEYS_ATTR).size();
			numRawCases = reportData.getUser(grain).getAttrData(RAW_SURVEYS_ATTR).size();
			retval.add(new String[]{grain, "" + numSatCases, ""+ numRawCases, (new DecimalFormat("#.##").format(numSatCases * 100 / numRawCases)) });
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
		LMICSAT rs = null;

		try
		{
			rs = new LMICSAT();
			
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

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
