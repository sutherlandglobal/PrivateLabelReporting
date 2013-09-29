/**
 * 
 */
package report;


import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;

import constants.Constants;

import report.Report;
import team.Team;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import util.results.Filter;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class TopRefundDrivers extends Report
{
	private RemoteConnection dbConnection;
	private Roster roster;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public TopRefundDrivers() throws ReportSetupException
	{
		super();

		reportName = "Top Refund Drivers";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport()
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();
		
		String targetUserID;
		
		String query = "SELECT CRM_TRN_REFUND.REFUND_CREATEDDATE AS Refund_Date,CRM_TRN_REFUND.REFUND_CREATEDBY AS User_ID,CRM_MST_REFVALUES.REFVAL_DISPLAYVALUE AS Refund_Reason "+ 
		" FROM CRM_TRN_REFUND INNER JOIN CRM_MST_REFVALUES ON CRM_TRN_REFUND.REFUND_REASON = CRM_MST_REFVALUES.REFVAL_REFVALID " + 
		" WHERE CRM_TRN_REFUND.REFUND_CREATEDDATE >= '" + 
		parameters.get(START_DATE_PARAM) + "' AND CRM_TRN_REFUND.REFUND_CREATEDDATE <= '" + 
		parameters.get(END_DATE_PARAM) + "'";
		
		String reportType = parameters.get(REPORT_TYPE_PARAM);
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			query += " AND CRM_TRN_REFUND.REFUND_CREATEDBY = '" + targetUserID + "'";
		}

		Team reportGrainData = new Team();
		
		String userID, reportGrain, reason; 
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(String[] row : dbConnection.runQuery(query))
		{
			userID = row[1];
			reason = row[2];
			
			if(roster.getUser(userID) != null)
			{
				timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
				reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[0]));

				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(reason);
				reportGrainData.getUser(reportGrain).addData(reason, userID);	
			}
		}

		for(String grain : reportGrainData.getUserList())
		{
			for(String[] row : Filter.filterTopDrivers(reportGrainData.getUser(grain), Integer.parseInt(getParameter(NUM_DRIVERS_PARAM))))
			{
				retval.add(new String[]{grain, row[0], row[1] });
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

		if(dbConnection != null)
		{
			dbConnection.close();
		}

		super.close();
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
		return true;
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
		
		roster = visitor.getRoster();
		
		return retval;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		TopRefundDrivers rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new TopRefundDrivers();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(NUM_DRIVERS_PARAM, 10);
			
			rs.setParameter(AGENT_NAME_PARAM, "Bray, George");
			
			rs.setParameter(START_DATE_PARAM, "2010-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");
			
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
			
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
			rs = new TopRefundDrivers();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(NUM_DRIVERS_PARAM, 10);
			
			rs.setParameter(START_DATE_PARAM, "2010-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");
			
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.MONTHLY_GRANULARITY);
			
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
