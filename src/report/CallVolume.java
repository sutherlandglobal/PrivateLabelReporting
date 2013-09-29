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
public final class CallVolume extends Report 
{
	private RemoteConnection dbConnection;
	private Statistics stats;
	private final static String CALL_VOL_ATTR = "callVol";
	private Roster roster;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public CallVolume() throws ReportSetupException
	{
		super();

		reportName = "Call Volume";
		
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
	@Override
	protected ArrayList<String[]> runReport() 
	{		
		ArrayList<String[]> retval = null;

		String targetUserID;
		
		String query = "SELECT CRM_MST_USER.USER_USERID AS UserID,tbl_PFS_CMS_Hagent.row_date as Date,Sum(tbl_PFS_CMS_Hagent.acdcalls) AS ACDCalls " + 
				"FROM tbl_PFS_CMS_Hagent INNER JOIN CRM_MST_USER ON tbl_PFS_CMS_Hagent.logid = CRM_MST_USER.USER_EXTENSION " +
				"WHERE tbl_PFS_CMS_Hagent.row_date >= '" +  
				parameters.get(START_DATE_PARAM) + "' AND tbl_PFS_CMS_Hagent.row_date <= '" +   parameters.get(END_DATE_PARAM) + "'" ;

		String reportType = parameters.get(REPORT_TYPE_PARAM);
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0);
			query += " AND CRM_MST_USER.USER_USERID = '" + targetUserID + "'";
		}

		query += " AND ACDCalls > 0 ";
		
		//required
		query += " GROUP BY CRM_MST_USER.USER_USERID,tbl_PFS_CMS_Hagent.row_date, tbl_PFS_CMS_Hagent.split ";
		
		Team reportGrainData = new Team();

		String userID, reportGrain, numCalls;
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(String[] row:  dbConnection.runQuery(query))
		{
			userID = row[0];	
			numCalls = row[2];

			if(roster.getUser(userID) != null )
			{
				roster.getUser(userID).addAttr(CALL_VOL_ATTR);
				roster.getUser(userID).addData(CALL_VOL_ATTR, numCalls);
				
				//time grain for time reports
				if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[1]));
					
					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(CALL_VOL_ATTR);
					reportGrainData.getUser(reportGrain).addData(CALL_VOL_ATTR, numCalls);
				}
			}
		}
		
		/////////////////
		//processing the buckets
		//want to put this into it's own method, can't think of a way without introducing terrible coupling
		
		retval = new ArrayList<String[]>();
		
		int finalCallVolume;
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			HashMap<String, Integer> stack = new HashMap<String, Integer>();
			
			
			String fullName;
			User user;
			for(String id : roster.getUsers().keySet())
			{
				user = roster.getUser(id);
				finalCallVolume = 0 ;
				
				if(!user.addAttr(CALL_VOL_ATTR))
				{
					finalCallVolume = (int)(stats.getTotal(user.getAttrData(CALL_VOL_ATTR)));
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
					stack.put(fullName, stack.get(fullName) + finalCallVolume);
				}
				else
				{
					stack.put(fullName, finalCallVolume);
				}
			}
			
			for(Entry<String, Integer> entry : stack.entrySet())
			{
				retval.add(new String[]{entry.getKey(), "" + entry.getValue() });
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			logger.log(Level.INFO, "TimeGrainData size: " + reportGrainData.getUserList().length);
			
			retval = new ArrayList<String[]>();
			for(String grain : reportGrainData.getUserList())
			{
				finalCallVolume = (int)(stats.getTotal(reportGrainData.getUser(grain).getAttrData(CALL_VOL_ATTR)));

				retval.add(new String[]{grain, "" + finalCallVolume });
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
		CallVolume rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new CallVolume();

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
			rs = new CallVolume();

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
			rs = new CallVolume();

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
			rs = new CallVolume();

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
