/**
 * 
 */
package report.SQL;


import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import team.Team;
import util.DateParser;
import util.ReportParameterValidator;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class NoSaleDrivers extends Report
{
	private RemoteConnection dbConnection;
	private Roster roster;
	private final String dbPropFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdbs27.properties";
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public NoSaleDrivers() throws ReportSetupException
	{
		super();

		reportName = "No Sale Drivers";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected Vector<String[]> runReport()
	{
		Vector<String[]> retval = new Vector<String[]>();
		
		String targetUserID;
		
		String query = "SELECT  CRM_MST_USER.USER_USERID,CRM_TRN_PROSPECT.PROSPECT_CREATEDDATE,CRM_MST_REFVALUES.REFVAL_DISPLAYVALUE " +
				" FROM (CRM_TRN_PROSPECT LEFT JOIN CRM_MST_REFVALUES ON CRM_TRN_PROSPECT.PROSPECT_REASONFORNOSALE = CRM_MST_REFVALUES.REFVAL_REFVALID) " + 
				" LEFT JOIN CRM_MST_USER ON CRM_TRN_PROSPECT.PROSPECT_CREATEDBY = CRM_MST_USER.USER_USERID " +
				"WHERE PROSPECT_CREATEDDATE >= '" + 
				parameters.get(START_DATE_PARAM)  + "' AND PROSPECT_CREATEDDATE <= '" + 
				parameters.get(END_DATE_PARAM) + "'  AND REFVAL_DISPLAYVALUE is not null ";
		
		String reportType = parameters.get(REPORT_TYPE_PARAM);
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).firstElement();
			query += " AND CRM_MST_USER.USER_USERID = '" + targetUserID + "'";
		}

		Team reportGrainData = new Team();
		
		String reason;
		String userID;
		String reportGrain; 
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		for(String[] row : dbConnection.runQuery(query))
		{
			userID = row[0];
			reason = row[2];

			if(roster.getUser(userID) != null)
			{
				timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
				reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(row[1]));

				reportGrainData.addUser(reportGrain);
				reportGrainData.getUser(reportGrain).addAttr(reason);
				reportGrainData.getUser(reportGrain).addData(reason, userID);	
			}
		}

//		//have to stringsort the month grains
//		List<String> sortedGrains = Arrays.asList();
//		
//		 Collections.reverse(sortedGrains);
//		 Collections.sort(sortedGrains);

		for(String grain : reportGrainData.getUserList())
		{
//			System.out.println(grain);
			for(String[] row : filterTopDrivers(grain, reportGrainData))
			{
				retval.add(new String[]{grain, row[0], row[1] });
			}
		}
		
		return retval;
	}

	/**
	 * Putting the "Top" in "Top X Drivers." Reduce the total Top X Drivers data to the top n for each date grain.
	 * 
	 * @param team	The greater result set to reduce.
	 * 
	 * @return	The final result set.
	 */
	private Vector<String[]> filterTopDrivers(String grain, Team team)
	{
		Vector<String[]> retval = new Vector<String[]>();
		//descending, for use in graphs

		int thisSize;
		int position;

		for(String category : team.getUser(grain).getAttrList())
		{
			if(!category.equals("name"))
			{
				position = 0;
				thisSize = team.getUser(grain).getAttrData(category).size();

				while
				(
							position < retval.size() && 
							(
									thisSize < Integer.parseInt(retval.get(position)[1])
									//thisSize > Integer.parseInt(retval.get(position-1)[1]) &&
									//thisSize <= Integer.parseInt(retval.get(position)[1])
									)
							)
				{

					position++;
				}

				retval.insertElementAt(new String[]{category, ""+ thisSize}, position);

				if(retval.size() > Integer.parseInt(parameters.get(NUM_DRIVERS_PARAM)))
				{
					retval.remove(retval.size()-1);	
				}
			}
		}

		logger.info( "Returning from FilterTopCallDrivers with rows: " + retval.size());

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
		return true;
	}

	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = false;
		boolean validateAgentName = false;

		isTimeReport = true;
		
		if
		(
				isValidReportType(new int[]{AGENT_TIME_REPORT,TEAM_TIME_REPORT}) &&
				hasValidDateInterval() && 
				isValidNumDrivers()
		)
		{
			try
			{
				switch(Integer.parseInt(parameters.get(REPORT_TYPE_PARAM)))
				{
				case AGENT_TIME_REPORT:
					setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
					validateAgentName = true;
					retval = hasValidTimeGrain();
					break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////
				case TEAM_TIME_REPORT:
					retval = hasValidTimeGrain();
					break;
				default:
					//nothing, verified valid by isValidReportType
					break;
				}
				
				if(retval)
				{					
					String rosterType = getParameter(ROSTER_TYPE_PARAM);
					
					if(ReportParameterValidator.validateRosterType(rosterType))
					{
						roster = new Roster();
						roster.setChildReport(true);
						
						roster.setParameter(ROSTER_TYPE_PARAM, getParameter(ROSTER_TYPE_PARAM));
						roster.load();
	
						logger.log(Level.INFO, "Confirmed coherent report roster type: " + rosterType);
						
						if(validateAgentName)
						{
							String agentName = getParameter(AGENT_NAME_PARAM);
							if(ReportParameterValidator.validateAgentName(agentName, roster))
							{
								logger.log(Level.INFO, "Confirmed coherent agentName: " + agentName);
							}
							else
							{
								logger.log(Level.ERROR,  "Agent name not found in report's roster, aborting report" );
							}
						}
					}
					else
					{
						logger.log(Level.ERROR,  "Unexpected report roster type: " + rosterType );
					}
				}
			} 
			catch (ReportSetupException e) 
			{
				logger.log(Level.ERROR,  "Failed running roster subreport");
				retval = false;
			}
			catch (Exception e)
			{
				logger.log(Level.ERROR,  "Exception: " + e.getMessage() + " processing report parameters");	
				retval = false;
			}
			finally
			{
				if(retval == false && roster != null)
				{
					roster.close();
				}
			}
		}

		return retval;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		NoSaleDrivers rs = null;

		System.out.println("Agent Time report");
		
		try
		{
			rs = new NoSaleDrivers();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(NUM_DRIVERS_PARAM, 4);
			
			rs.setParameter(AGENT_NAME_PARAM, "Zioto, Andrew");
			
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
		
		System.out.println("===================\nTeam Stack report");
		
		try
		{
			rs = new NoSaleDrivers();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(NUM_DRIVERS_PARAM, 4);
			
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
