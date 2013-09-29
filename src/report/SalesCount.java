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
public final class SalesCount extends Report
{
	private static final String SALES_AMTS_ATTR = "salesAmounts";
	private RemoteConnection dbConnection;
	private Roster roster;
	private Statistics stats;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public SalesCount() throws ReportSetupException
	{
		super();

		reportName = "Sales Count";
		
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
		String salesQuery = "SELECT " +
				" CRM_MST_USER.USER_USERID AS USER_USERID,CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE AS ORDER_DATE, " +
				" CRM_TRN_ORDERDETAILS.ORDDET_AMOUNT AS ORDER_AMOUNT " + 
				" FROM CRM_MST_USER INNER JOIN CRM_TRN_ORDERDETAILS ON CRM_MST_USER.USER_USERID = CRM_TRN_ORDERDETAILS.ORDDET_CREATEDBY " + 
				" WHERE CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE >= '" + parameters.get(START_DATE_PARAM) + 
				"' AND CRM_TRN_ORDERDETAILS.ORDDET_CREATEDDATE <= '" + parameters.get(END_DATE_PARAM) + "' ";

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			salesQuery += " AND CRM_MST_USER.USER_USERID = '" + targetUserID + "'";
		}

		Team reportGrainData = new Team();

		String userID, salesAmount, reportGrain;

		int timeGrain ;

		for(String[] row:  dbConnection.runQuery(salesQuery))
		{
			userID = row[0];

			if(roster.getUser(userID) != null)
			{
				salesAmount = row[2];

				roster.getUser(userID).addAttr(SALES_AMTS_ATTR);
				roster.getUser(userID).addData(SALES_AMTS_ATTR, salesAmount);

				//time grain for time reports
				if(isTimeReport())
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[1]));

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(SALES_AMTS_ATTR);
					reportGrainData.getUser(reportGrain).addData(SALES_AMTS_ATTR, salesAmount);
				}
			}
		}

		/////////////////
		//processing the buckets
		//want to put this into it's own method, can't think of a way without introducing terrible coupling

		retval =  new ArrayList<String[]>();

		int salesCount;
		if(isStackReport() )
		{
			HashMap<String, Integer> stack = new HashMap<String, Integer>();

			String fullName;
			User user;
			for(String id : roster.getUsers().keySet())
			{
				user = roster.getUser(id);

				salesCount = 0;
				
				if(reportType.equals("" + AGENT_STACK_REPORT ))
				{
					fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));
				}
				else
				{
					fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);
				}
				
				if( !user.addAttr(SALES_AMTS_ATTR))
				{
					salesCount = user.getAttrData(SALES_AMTS_ATTR).size();
				}
				
				if(stack.containsKey(fullName))
				{
					stack.put(fullName, stack.get(fullName) + salesCount);
				}
				else
				{
					stack.put(fullName, salesCount);
				}
			}

			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
			}
		}
		else if(isTimeReport())
		{
			for(String user : reportGrainData.getUserList())
			{
				//not all users will have refunds
				salesCount = 0;
				if( reportGrainData.getUser(user).getAttrData(SALES_AMTS_ATTR) != null)
				{
					salesCount = reportGrainData.getUser(user).getAttrData(SALES_AMTS_ATTR).size();
				}

				retval.add(new String[]{user, "" + salesCount }) ;
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
		SalesCount rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new SalesCount();

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
			rs = new SalesCount();

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
			rs = new SalesCount();

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
			rs = new SalesCount();

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
