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
import formatting.NumberFormatter;

/**
 * @author Jason Diamond
 *
 */
public final class RealtimeSales extends Report
{
	private RemoteConnection dbConnection;
	private Roster roster;
	private final static String ORDER_AMTS_ATTR = "orderAmounts";
	private Statistics stats;
	private NumberFormatter numFormatter;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public RealtimeSales() throws ReportSetupException
	{
		super();

		reportName = "Realtime Sales";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() 
	{
		ArrayList<String[]> retval = null;

		String targetUserID;

		String ordersQuery = "SELECT " +
				" CRM_MST_USER.USER_USERID AS USER_USERID,CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE AS ORDER_DATE, " +
				" CRM_TRN_ORDERDETAILS.ORDDET_AMOUNT AS ORDER_AMOUNT " + 
				" FROM CRM_MST_USER INNER JOIN CRM_TRN_ORDERDETAILS ON CRM_MST_USER.USER_USERID = CRM_TRN_ORDERDETAILS.ORDDET_CREATEDBY " + 
				" WHERE CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE >= '" + parameters.get(START_DATE_PARAM) +
				"' AND CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE <= '" + parameters.get(END_DATE_PARAM) + "' "; 

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			ordersQuery += " AND CRM_MST_USER.USER_USERID = '" + targetUserID + "'";
		}
		
		retval = new ArrayList<String[]>();

		Team reportGrainData = new Team();

		String userID, reportGrain, orderAmounts;
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(String[] row:  dbConnection.runQuery(ordersQuery))
		{
			userID = row[0];	
			orderAmounts = row[2];

			if(roster.getUser(userID) != null )
			{
				roster.getUser(userID).addAttr(ORDER_AMTS_ATTR);
				roster.getUser(userID).addData(ORDER_AMTS_ATTR, orderAmounts);

				//time grain for time reports
				if(isTimeReport())
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[1]));

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(ORDER_AMTS_ATTR);
					reportGrainData.getUser(reportGrain).addData(ORDER_AMTS_ATTR, orderAmounts);
				}
			}
		}

		//format the output
		double finalSales;
		if(isStackReport())
		{
			HashMap<String, Double> stack = new HashMap<String, Double>();


			String fullName;
			User user;
			for(String id : roster.getUsers().keySet())
			{
				user = roster.getUser(id);
				finalSales = 0 ;

				if(!user.addAttr(ORDER_AMTS_ATTR))
				{
					finalSales = stats.getTotal(user.getAttrData(ORDER_AMTS_ATTR));
				}

				if(reportType.equals("" + AGENT_STACK_REPORT ))
				{
					fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));
				}
				else
				{
					fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);
				}

				if(stack.containsKey(fullName))
				{
					stack.put(fullName, stack.get(fullName) + finalSales);
				}
				else
				{
					stack.put(fullName, finalSales);
				}
			}

			for(Entry<String, Double> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), numFormatter.convertToCurrency(entry.getValue()) });
			}
		}
		else if(isTimeReport())
		{
			retval = new ArrayList<String[]>();
			for(String grain : reportGrainData.getUserList())
			{
				finalSales = stats.getTotal(reportGrainData.getUser(grain).getAttrData(ORDER_AMTS_ATTR));

				retval.add(new String[]{grain, numFormatter.convertToCurrency(finalSales) });
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

		try
		{
			stats = StatisticsFactory.getStatsInstance();
			numFormatter = new NumberFormatter();
		}
		finally
		{
			if(stats != null && numFormatter != null)
			{
				retval = true;
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
		RealtimeSales rs = null;

		System.out.println("Agent Time report");

		try
		{
			rs = new RealtimeSales();

			rs.setParameter(REPORT_TYPE_PARAM, "" + AGENT_TIME_REPORT);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

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
			rs = new RealtimeSales();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

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
			rs = new RealtimeSales();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, CallVolume.AGENT_STACK_REPORT);

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
			rs = new RealtimeSales();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			rs.setParameter(REPORT_TYPE_PARAM, CallVolume.TEAM_STACK_REPORT);

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
