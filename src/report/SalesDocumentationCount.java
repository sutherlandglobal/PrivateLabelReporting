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
public final class SalesDocumentationCount extends Report
{
	private RemoteConnection dbConnection;
	private Roster roster;
	private final static String DOC_COUNT_ATTR = "docCount";
	private Statistics stats;

	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public SalesDocumentationCount() throws ReportSetupException
	{
		super();

		reportName = "Sales Documentation Count";
		
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

		String documentationsQuery = "SELECT CRM_TRN_ACTIVITY.ACTIVITY_CREATEDDATE,CRM_MST_USER.USER_USERID " +
				" FROM CRM_TRN_ACTIVITY INNER JOIN CRM_MST_USER ON CRM_TRN_ACTIVITY.ACTIVITY_CREATEDBY = CRM_MST_USER.USER_USERID " +
				" WHERE CRM_TRN_ACTIVITY.ACTIVITY_CREATEDDATE >= '" + parameters.get(START_DATE_PARAM) + "' AND CRM_TRN_ACTIVITY.ACTIVITY_CREATEDDATE <= '" + parameters.get(END_DATE_PARAM) + "' AND CRM_TRN_ACTIVITY.ACTIVITY_PAGESOURCE = 'QUICKADD' " ;
				

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			documentationsQuery += " AND CRM_MST_USER.USER_USERID = '" + targetUserID + "'";
		}
		
		documentationsQuery += " GROUP BY CRM_TRN_ACTIVITY.ACTIVITY_CREATEDDATE,CRM_MST_USER.USER_USERID";

		Team reportGrainData = new Team();

		String userID, reportGrain;
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(String[] row:  dbConnection.runQuery(documentationsQuery))
		{
			userID = row[1];	

			if(roster.getUser(userID) != null )
			{
				roster.getUser(userID).addAttr(DOC_COUNT_ATTR);
				roster.getUser(userID).addData(DOC_COUNT_ATTR, userID);

				//time grain for time reports
				if(isTimeReport())
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[0]));

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(DOC_COUNT_ATTR);
					reportGrainData.getUser(reportGrain).addData(DOC_COUNT_ATTR, userID);
				}
			}
		}
		
		retval = new ArrayList<String[]>();

		User user;
		
		//format the output
		int docCount;
		
		if(isTimeReport())
		{
			for(String grain : reportGrainData.getUserList())
			{
				user = reportGrainData.getUser(grain);
				
				docCount = 0;
				if( user.getAttrData(DOC_COUNT_ATTR) != null)
				{
					docCount = user.getAttrData(DOC_COUNT_ATTR).size();
				}

				retval.add(new String[]{grain, "" + docCount }) ;
			}
		}
		else if(isStackReport() )
		{
			HashMap<String, Integer> stack = new HashMap<String, Integer>();

			String fullName;
			
			for(String id : roster.getUsers().keySet())
			{
				user = roster.getUser(id);

				docCount = 0;
				
				if(reportType.equals("" + AGENT_STACK_REPORT ))
				{
					fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));
				}
				else
				{
					fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);
				}
				
				if( !user.addAttr(DOC_COUNT_ATTR))
				{
					docCount = user.getAttrData(DOC_COUNT_ATTR).size();
				}
				
				if(stack.containsKey(fullName))
				{
					stack.put(fullName, stack.get(fullName) + docCount);
				}
				else
				{
					stack.put(fullName, docCount);
				}
			}

			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
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
		SalesDocumentationCount rs = null;

		System.out.println("Agent Time report");

		try
		{
			rs = new SalesDocumentationCount();

			rs.setParameter(REPORT_TYPE_PARAM, "" + AGENT_TIME_REPORT);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Kumar P, Dinesh");

			rs.setParameter(START_DATE_PARAM, "2013-03-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-03-31 23:59:59");

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
			rs = new SalesDocumentationCount();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-03-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-03-31 23:59:59");

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
			rs = new SalesDocumentationCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-03-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-03-31 23:59:59");

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
			rs = new SalesDocumentationCount();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SALES_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			//rs.setParameter(AGENT_NAME_PARAM, "Carter, Janice");

			rs.setParameter(START_DATE_PARAM, "2013-03-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2013-03-31 23:59:59");

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
