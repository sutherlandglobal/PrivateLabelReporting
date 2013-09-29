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
import team.User;
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
public final class IVRDSATCases extends Report {

	private RemoteConnection dbConnection;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;
	private Roster roster;
	private final String SURVEYS_ATTR = "surveys";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public IVRDSATCases() throws ReportSetupException 
	{
		super();

		reportName = "IVR DSAT Cases";
		
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
		boolean retval = true;

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

		String query = "select * from "+
		"(select tbl_AcerPFSSurveyIVR.rID,tbl_AcerPFSSurveyIVR.CaseID,CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) AS DATE" + 
				",tbl_AcerPFSSurveyIVR.Survey_Result as Survey," + 
		"crm_MST_USER.USER_NTLOGINID as ntlogin " +  
		"from " +  
		"(tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID) " +
		") t " + 
		" where Date >= '"+ getParameter(START_DATE_PARAM) +"' and " +
		" Date <= '" + getParameter(END_DATE_PARAM) + "'  and " +
		" Len(Survey) = 6 "; 
		
		String reportType = parameters.get(REPORT_TYPE_PARAM);
		
		if(reportType.equals("" + AGENT_TIME_REPORT))
		{
			String targetUserID = roster.getUser(parameters.get(AGENT_NAME_PARAM)).getAttrData(Roster.LOGIN_NAME_ATTR).get(0);
			query += " AND ntlogin = '" + targetUserID +"'" ;
		}
	
		String rID,caseID,surveyDate,ntlogin,surveyString;
		
		int timeGrain;
		String reportGrain;
		
		Team reportGrainData = new Team();

		String[] surveyData; 
		
		for(String[] surveyRow : dbConnection.runQuery(query))
		{
			rID  = surveyRow[0];
			caseID = surveyRow[1];
			surveyDate = surveyRow[2];
			ntlogin = surveyRow[4];
			surveyString = surveyRow[3];
			
			//just add the survey data to the roster's user
			if(roster.getUser(ntlogin) != null && surveyString.charAt(5) == '2')
			{
				surveyData = new String[]{rID,caseID,surveyDate,ntlogin,surveyString};
				
				roster.getUser(ntlogin).addObject(SURVEYS_ATTR, surveyData);
				
				//time grain for time reports
				if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
				{
					timeGrain = Integer.parseInt(parameters.get(TIME_GRAIN_PARAM));
					reportGrain = dateParser.getDateGrain(timeGrain, dateParser.convertSQLDateToGregorian(surveyDate));

					reportGrainData.addUser(reportGrain);
					reportGrainData.getUser(reportGrain).addAttr(SURVEYS_ATTR);
					reportGrainData.getUser(reportGrain).addObject(SURVEYS_ATTR, surveyData);
				}
			}			
		}

		String[] rowData;

		String name;
		
		//time grain for time reports
		if(reportType.equals("" + AGENT_STACK_REPORT ) || reportType.equals("" + TEAM_STACK_REPORT ) )
		{
			User user;
			
			retval = new ArrayList<String[]>(roster.getSize());
						
			for(String id : roster.getUserIDList())
			{
				user = roster.getUser(id);

				if(user.hasObjectName(SURVEYS_ATTR))
				{
					for(Object row : user.getUserObjects(SURVEYS_ATTR))
					{
						rowData = (String[]) row;
						
						name = "";
						
						if(reportType.equals("" + Report.AGENT_STACK_REPORT))
						{
							name = roster.getUser(rowData[3]).getAttrData(Roster.FULLNAME_ATTR).get(0);
						}
						else if(reportType.equals("" + Report.TEAM_STACK_REPORT))
						{
							name = roster.getUser(rowData[3]).getAttrData(Roster.TEAMNAME_ATTR).get(0);
						}
						
						retval.add(new String[]{name, rowData[0],rowData[1],rowData[2],rowData[4]});
					}
				}
			}
		}
		else if(reportType.equals("" + AGENT_TIME_REPORT) || reportType.equals("" + TEAM_TIME_REPORT))
		{
			retval = new ArrayList<String[]>(reportGrainData.getSize());
			
			for(String grain : reportGrainData.getUserList())
			{
				for(Object row : reportGrainData.getUser(grain).getUserObjects(SURVEYS_ATTR))
				{
					rowData = (String[]) row;
					
					name = "";
					
					if(reportType.equals("" + Report.AGENT_TIME_REPORT))
					{
						name = roster.getUser(rowData[3]).getAttrData(Roster.FULLNAME_ATTR).get(0);
					}
					else if(reportType.equals("" + Report.TEAM_TIME_REPORT))
					{
						name = roster.getUser(rowData[3]).getAttrData(Roster.TEAMNAME_ATTR).get(0);
					}
					
					retval.add(new String[]{grain, rowData[0],rowData[1], name, rowData[4]});
				}
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
		IVRDSATCases rs = null;

		System.out.println("Agent Time Report");
		
		try
		{
			rs = new IVRDSATCases();

			rs.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(AGENT_NAME_PARAM, "Zioto, Andrew");

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

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
			rs = new IVRDSATCases();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

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
		
		System.out.println("===================\nTeam Time Report");
		
		try
		{
			rs = new IVRDSATCases();

			rs.setParameter(REPORT_TYPE_PARAM, TEAM_TIME_REPORT);
			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);
			rs.setParameter(TIME_GRAIN_PARAM, "" + DateParser.DAILY_GRANULARITY);

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

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
			rs = new IVRDSATCases();

			rs.setParameter(ROSTER_TYPE_PARAM, "" + Roster.SUPPORT_ROSTER);

			rs.setParameter(START_DATE_PARAM, "2012-10-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-10-31 23:59:59");

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
