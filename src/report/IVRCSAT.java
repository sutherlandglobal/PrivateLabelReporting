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
public class IVRCSAT extends Report 
{
	private RemoteConnection dbConnection;
	private Statistics stats;
	private final static String RAW_SURVEYS_ATTR = "rawSurveys";
	private final static String SAT_SURVEYS_ATTR = "satSurveys";
	private Roster roster;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRCSAT() throws ReportSetupException 
	{
		super();

		reportName = "IVR CSAT";
		
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

		if(roster != null)
		{
			roster.close();
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


		String query = "SELECT " + 
				"tbl_AcerPFSSurveyIVR.RId,CRM_MST_USER.USER_USERID, " +  
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) AS DATE, " + 
				" tbl_AcerPFSSurveyIVR.Survey_Result, " +
				"CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2 " +
				" Then " + 
				" CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,6),1) = 1 " +
				" THEN 'Yes' " + 
				" Else 'No' END " +
				"Else " + 
				"CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,5),1) = 1 "+
				"THEN 'Yes' " +
				" Else 'No' END " +
				"END AS Q6, " +
				"CRM_MST_USER.USER_USERID AS UserID " +
				"FROM " + 
				"(tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID) " +
				"WHERE "+ 
				"(Len(tbl_AcerPFSSurveyIVR.Survey_Result)>=7) AND " + 
				" CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) >= '"+
				parameters.get(START_DATE_PARAM)+"' AND " +
				"CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) <= '"+
				parameters.get(END_DATE_PARAM)+"' ";

		String reportType = parameters.get(REPORT_TYPE_PARAM);

		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			query += " AND CRM_MST_USER.USER_USERID = '"+ roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.USER_ID_ATTR).get(0)+ "'";
		}

		Team reportGrainData = new Team();
		
		//don't assign time grain just yet. in case this is a non-time report, because the timegrain param is not guaranteed to be set 
		int timeGrain;
		
		String q6, reportGrain, rID, userID, date;
		
		for(String[] row:  dbConnection.runQuery(query))
		{
			userID = row[1];
			date = row[2];
			rID = row[0];
			q6 = row[4];

			if(roster.getUser(row[1]) != null )
			{
				roster.getUser(userID).addAttr(RAW_SURVEYS_ATTR);
				roster.getUser(userID).addData(RAW_SURVEYS_ATTR, rID);

				if(q6.equalsIgnoreCase("Yes"))
				{
					roster.getUser(userID).addAttr(SAT_SURVEYS_ATTR);
					roster.getUser(userID).addData(SAT_SURVEYS_ATTR, rID);
				}

				//time grain for time reports
				if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(date));

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(RAW_SURVEYS_ATTR);
					reportGrainData.getUser(reportGrain).addAttr(SAT_SURVEYS_ATTR);

					reportGrainData.getUser(reportGrain).addData(RAW_SURVEYS_ATTR, rID);

					if(q6.equalsIgnoreCase("Yes"))
					{
						reportGrainData.getUser(reportGrain).addData(SAT_SURVEYS_ATTR, rID);
					}
				}
			}
		}

		double csat, numSatCases, numRawCases;
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			HashMap<String, User> stack = new HashMap<String, User>();
			User thisTeam;

			String fullName;
			User user;

			for(String id : roster.getUsers().keySet())
			{
				user = roster.getUser(id);

				if( !user.addAttr(RAW_SURVEYS_ATTR) &&  !user.addAttr(SAT_SURVEYS_ATTR))
				{
					csat = 0;

					if(reportType.equals("" + AGENT_STACK_REPORT ))
					{
						fullName = roster.getFullName(user.getAttrData(Roster.USER_ID_ATTR).get(0));

						stack.put(fullName, user);
					}
					else
					{
						fullName = user.getAttrData(Roster.TEAMNAME_ATTR).get(0);

						if(!stack.containsKey(fullName))
						{
							thisTeam = new User(fullName);
							thisTeam.addAttr(SAT_SURVEYS_ATTR);
							thisTeam.addAttr(RAW_SURVEYS_ATTR);

							stack.put(fullName, thisTeam);
						}

						for(String datum :  user.getAttrData(SAT_SURVEYS_ATTR))
						{
							stack.get(fullName).addData(SAT_SURVEYS_ATTR, datum);
						}

						for(String datum : user.getAttrData(RAW_SURVEYS_ATTR))
						{
							stack.get(fullName).addData(RAW_SURVEYS_ATTR, datum);
						}
					}
				}
			}

			for(Entry<String, User> entry : stack.entrySet())
			{
				numSatCases = entry.getValue().getAttrData(SAT_SURVEYS_ATTR).size();
				numRawCases = entry.getValue().getAttrData(RAW_SURVEYS_ATTR).size();
				csat =  (100 * (numSatCases / numRawCases)) ;

				retval.add(new String[]{entry.getKey(), "" + csat });
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			for(String grain : reportGrainData.getUserList())
			{
				csat = 0;
				numSatCases = 0;
				numRawCases = 0;

				if(!reportGrainData.getUser(grain).addAttr(RAW_SURVEYS_ATTR))
				{
					numRawCases = reportGrainData.getUser(grain).getAttrData(RAW_SURVEYS_ATTR).size();
				}

				if(!reportGrainData.getUser(grain).addAttr(SAT_SURVEYS_ATTR))
				{
					numSatCases = reportGrainData.getUser(grain).getAttrData(SAT_SURVEYS_ATTR).size();
				}

				if(numRawCases != 0)
				{
					csat = 100 * (numSatCases / numRawCases);
				}

				retval.add(new String[]{grain, "" + csat });
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
		IVRCSAT rs = null;

		System.out.println("Agent Time report");

		try
		{
			rs = new IVRCSAT();

			rs.setParameter(REPORT_TYPE_PARAM, "" + AGENT_TIME_REPORT);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Zioto, Andrew");

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
			rs = new IVRCSAT();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
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

		System.out.println("===================\nAgent Stack report");

		try
		{
			rs = new IVRCSAT();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

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
			rs = new IVRCSAT();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

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
