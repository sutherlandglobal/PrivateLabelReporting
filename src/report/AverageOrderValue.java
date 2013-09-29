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
public class AverageOrderValue extends Report 
{
	private RemoteConnection dbConnection;
	private Roster roster;
	private Statistics stats;
	private NumberFormatter numFormatter;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;
	
	private final static String ORDER_AMTS_ATTR = "orderAmounts";

	/** 
	 * Build the Average Order Value report.
	 * 
	 */
	public AverageOrderValue() throws ReportSetupException 
	{
		super();
		
		reportName = "Average Order Value";
		
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

		try
		{
			stats = StatisticsFactory.getStatsInstance();
			numFormatter = new NumberFormatter();
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

		if(roster != null)
		{
			roster.close();
		}

		super.close();
	}
	
	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	protected ArrayList<String[]> runReport()
	{
		ArrayList<String[]> retval = null;

		String query = 
			"SELECT ORDDET_CREATEDBY,ORDDET_CREATEDDATE,ORDDET_AMOUNT " + " FROM CRM_TRN_ORDERDETAILS " +  
			" WHERE ORDDET_CREATEDDATE >= '" + parameters.get(START_DATE_PARAM) + "' AND ORDDET_CREATEDDATE <= '" +  parameters.get(END_DATE_PARAM) + "'";

		String reportType = parameters.get(REPORT_TYPE_PARAM);
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			String targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			query += " AND ORDDET_CREATEDBY = '" + targetUserID +"'" ;
		}

		Team reportGrainData = new Team();

		String userID, reportGrain, orderAmount;
		
		//don't assign time grain. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(String[] row:  dbConnection.runQuery(query))
		{
			userID = row[0];
			orderAmount = row[2];

			if(roster.getUser(userID) != null)
			{
				roster.getUser(userID).addAttr(ORDER_AMTS_ATTR);
				roster.getUser(userID).addData(ORDER_AMTS_ATTR, orderAmount);

				//time grain for time reports
				if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[1]));

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(ORDER_AMTS_ATTR);
					reportGrainData.getUser(reportGrain).addData(ORDER_AMTS_ATTR, orderAmount);
				}
			}
		}
		
		/////////////////
		//processing the buckets

		double aov;
		User user;
		
		retval = new ArrayList<String[]>(roster.getSize());
		
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			retval = new ArrayList<String[]>(roster.getSize());
			String fullName;
			
			HashMap<String, Double> stack = new HashMap<String, Double>();
			
			for(String id : roster.getUserIDList())
			{
				user = roster.getUser(id);

				if(!user.addAttr(ORDER_AMTS_ATTR))
				{
					aov = stats.getAverage(user.getAttrData(ORDER_AMTS_ATTR));

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
						stack.put(fullName, stack.get(fullName) + aov);
					}
					else
					{
						stack.put(fullName, aov);
					}
				}
			}
			
			for(Entry<String, Double> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), numFormatter.convertToCurrency(entry.getValue()) });
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			retval = new ArrayList<String[]>();
			for(String grain : reportGrainData.getUserList())
			{
				aov = stats.getAverage(reportGrainData.getUser(grain).getAttrData(ORDER_AMTS_ATTR));

				retval.add(new String[]{grain, numFormatter.convertToCurrency(aov) });
			}
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
		
		roster = visitor.getRoster();
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		AverageOrderValue rs = null;

		System.out.println("Agent Time Report");
			
		try
		{
			rs = new AverageOrderValue();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

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
		
		System.out.println("===================\nTeam Time report");
		
		try
		{
			rs = new AverageOrderValue();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

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
		
		System.out.println("===================\nAgent Stack report");
		
		try
		{
			rs = new AverageOrderValue();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

			rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

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
			rs = new AverageOrderValue();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

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
