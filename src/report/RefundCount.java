/**
 * 
 */
package report;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Level;

import constants.Constants;

import report.Report;
import statistics.Statistics;
import statistics.StatisticsFactory;
import team.Team;
import team.User;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class RefundCount extends Report
{
	private static final String REFUNDS_AMTS_ATTR = "refundAmounts";
	private RemoteConnection dbConnection;
	private Roster roster;
	private Statistics stats;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public RefundCount() throws ReportSetupException 
	{
		super();

		reportName = "Refund Count";

		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport(java.lang.String, java.lang.String)
	 */
	@Override
	protected ArrayList<String[]> runReport()
	{
		ArrayList<String[]> retval = null;

		String targetUserID;
		
		String refundQuery = "SELECT CRM_TRN_REFUND.REFUND_CREATEDDATE AS Refund_Date,CRM_TRN_ORDER.ORDER_CREATEDBY AS User_ID,CRM_TRN_REFUND.REFUND_REFUNDAMOUNT AS Refund_Amount " +
				" FROM CRM_TRN_REFUND INNER JOIN CRM_TRN_ORDER ON CRM_TRN_REFUND.REFUND_ORDERID = CRM_TRN_ORDER.ORDER_ORDERID " +  
				" WHERE CRM_TRN_REFUND.REFUND_CREATEDDATE >= '" + 
				parameters.get(START_DATE_PARAM) + "' AND CRM_TRN_REFUND.REFUND_CREATEDDATE <= '" + 
				parameters.get(END_DATE_PARAM) +
				"' AND CRM_TRN_REFUND.REFUND_REFUNDTYPEID != 20000570 ";

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			refundQuery += " AND CRM_TRN_ORDER.ORDER_CREATEDBY = '" + targetUserID + "'";
		}
		
		Team reportGrainData = new Team();

		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		String userID, refundAmount, reportGrain, date;
		for(String[] row:  dbConnection.runQuery(refundQuery))
		{
			userID = row[1];
			date = row[0];

			if(roster.getUser(userID) != null)
			{
				refundAmount = row[2];

				roster.getUser(userID).addAttr(REFUNDS_AMTS_ATTR);
				roster.getUser(userID).addData(REFUNDS_AMTS_ATTR, refundAmount);
				
				//time grain for time reports
				if(isTimeReport())
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(date));
					
					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(REFUNDS_AMTS_ATTR);
					reportGrainData.getUser(reportGrain).addData(REFUNDS_AMTS_ATTR, refundAmount);
				}
			}
		}

		retval =  new ArrayList<String[]>();
		
		//format the output
		int numRefunds;
		if(isStackReport())
		{
			String fullName;
			User user;

			HashMap<String, Integer> stack = new HashMap<String, Integer>();
			
			for(String id : roster.getUsers().keySet())
			{
				user = roster.getUser(id);

				if(reportType.equals("" + AGENT_STACK_REPORT ))
				{
					fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));
				}
				else
				{
					fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);
				}
				
				//not all users will have refunds
				numRefunds = 0;
				if( user.getAttrData(REFUNDS_AMTS_ATTR) != null)
				{
					numRefunds = user.getAttrData(REFUNDS_AMTS_ATTR).size();
				}
				
				if(stack.containsKey(fullName))
				{
					stack.put(fullName, stack.get(fullName) + numRefunds);
				}
				else
				{
					stack.put(fullName, numRefunds);
				}
			}
			
			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
			}
		}
		else if(isTimeReport())
		{
			for(String grain : reportGrainData.getUserList())
			{
				//not all users will have refunds
				numRefunds = 0;
				if( reportGrainData.getUser(grain).getAttrData(REFUNDS_AMTS_ATTR) != null)
				{
					numRefunds = reportGrainData.getUser(grain).getAttrData(REFUNDS_AMTS_ATTR).size();
				}

				retval.add(new String[]{grain, "" + reportGrainData.getUser(grain).getAttrData(REFUNDS_AMTS_ATTR).size() }) ;
			}
		}

		return retval;
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
			logger.log(Level.ERROR,   "DatabaseConnectionCreationException on attempt to access database: " + e.getMessage());	
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
			if(stats != null)
			{
				retval = true;
			}
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
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
		RefundCount rs = null;

		System.out.println("Agent Time Report");
		
		try
		{
			rs = new RefundCount();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Robinson, Anthony");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			//rs.setParameter(REPORT_TYPE_PARAM, RefundCount.AGENT_STACK_REPORT);

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
			rs = new RefundCount();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			//rs.setParameter(REPORT_TYPE_PARAM, RefundCount.AGENT_STACK_REPORT);

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
			rs = new RefundCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

			rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, RefundCount.AGENT_STACK_REPORT);

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
			rs = new RefundCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, RefundCount.TEAM_STACK_REPORT);

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
