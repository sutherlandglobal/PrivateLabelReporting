/**
 * 
 */
package report;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;

import constants.Constants;

import report.Report;
import team.User;
import util.parameter.validation.ReportVisitor;
import database.connection.DatabaseConnection;
import database.connection.SQL.ConnectionFactory;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * The roster containing the agents for whom we care about performance. This backend is not only used by the Roster report, but is used by other reports requiring 
 * an end-all list of users to report on. AgentName to CMS Name mappings are also loaded and can be referenced by implementing reports without additional database queries.
 * 
 * @author Jason Diamond
 *
 */
public final class Roster extends Report 
{
	private DatabaseConnection dbConnection;
	private HashMap<String, User> userList;

	//these are the most likely lookup mechanisms
	private HashMap<String, String> fullNameMap;
	private HashMap<String, String> loginNameMap;
	private HashMap<String, String> employeeIDMap;
	private HashMap<String, String> userIDMap;
	private HashMap<String, String> ntloginMap;

	public static final int SUPPORT_ROSTER = 1;
	public static final int ACTIVE_SUPPORT_ROSTER = 2;
	public static final int SALES_ROSTER = 3;
	public static final int ACTIVE_SALES_ROSTER = 4;
	public static final int ALL_ROSTER = 5;
	public static final int ACTIVE_ROSTER = 6;

	public final static String USER_ID_ATTR = "userID";
	public final static String LOGIN_NAME_ATTR = "loginName";
	public final static String TEAMNAME_ATTR = "teamName";
	public final static String FIRSTNAME_ATTR = "firstName";
	public final static String LASTNAME_ATTR = "lastName";
	public final static String EXTENSION_ATTR = "extension";
	public final static String SUPPORT_TYPE_ID_ATTR = "supportTypeID";
	public final static String EMP_ID_ATTR = "empID";
	public final static String EMAIL_ID_ATTR = "emailID";
	public final static String LMI_LOGIN_NAME_ATTR = "lmiLogin";
	public final static String LMI_LOGIN_NODE_ID_ATTR = "lmiNodeID";
	public final static String FULLNAME_ATTR = "fullName";
	public final static String NTLOGIN_ATTR = "ntlogin";
	
	public static final String SCHEDULE_ATTR = "schedule";

	//private final int USER_SUPPORT_TYPE_ID_SYSTEM = 142;
	private final static int USER_SUPPORT_TYPE_ID_SUPPORT = 151;
	private final static int USER_SUPPORT_TYPE_ID_SALES = 159;

	private String queryFields;
	
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the Roster report.
	 *
	 * @throws ReportSetupException	If a connection to the database could not be established.
	 */
	public Roster() throws ReportSetupException 
	{
		super();

		reportName = "Roster";

		queryFields = 
				"SELECT CRM_MST_USER.USER_USERID,CRM_MST_USER.USER_LOGINNAME,CRM_MST_USERTEAM.UTEAM_TEAMNAME,CRM_MST_USER.USER_FIRSTNAME," + 
						"CRM_MST_USER.USER_LASTNAME, CRM_MST_USER.USER_EXTENSION, CRM_MST_USER.USER_SUPPORTTYPEID, CRM_MST_USER.USER_EMPID," +
						"CRM_MST_USER.USER_EMAILID, CRM_MST_USER.USER_LOGMEINLOGINNAME, CRM_MST_USER.USER_LOGMEINNODEID,CRM_MST_USER.USER_NTLOGINID";
		
		logger.info("Building report " +  reportName);
	}


	/** 
	 * Attempt to fufill the prerequisites of the report. A common one will be building the report's roster.
	 * 
	 * @return	True if the setup was run successfully, false otherwise.
	 */
	@Override
	protected boolean setupReport()
	{
		boolean retval = true;

		return retval;
	}

	/** 
	 * Attempt to establish connections to all required datasources. A report by definition has at least one, and possibly many.
	 * 
	 * @return	True if the connection was established, false otherwise.
	 */
	protected boolean setupDataSourceConnections()
	{
		boolean retval = false;

		try 
		{
			ConnectionFactory factory = new ConnectionFactory();
			
			factory.load(dbPropFile);
			
			dbConnection = factory.getConnection();
		}
		catch (DatabaseConnectionCreationException e) 
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

	/**
	 * Close the report, any sub reports, and any database connections.
	 * 
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

	/**
	 * Build the roster from the database. Also build the PARAM -> USER mappings for other reports to reference.
	 * 
	 */
	public void load()
	{
		logger.log(Level.INFO, "Loading Roster for likely a parent report: " + toString());


		userList = new HashMap<String, User>();

		loginNameMap = new HashMap<String,String>();
		employeeIDMap = new HashMap<String,String>();
		userIDMap = new HashMap<String,String>();
		fullNameMap = new HashMap<String,String>();
		ntloginMap = new HashMap<String,String>();

		//new HashMap<String, User>();

		//			CRM_MST_USER.USER_USERID
		//			CRM_MST_USER.USER_LOGINNAME
		//			CRM_MST_USERTEAM.UTEAM_TEAMNAME
		//			CRM_MST_USER.USER_FIRSTNAME
		//			CRM_MST_USER.USER_LASTNAME
		//			CRM_MST_USER.USER_EXTENSION
		//			CRM_MST_USER.USER_SUPPORTTYPEID
		//			CRM_MST_USER.USER_EMPID
		//			CRM_MST_USER.USER_EMAILID
		//			CRM_MST_USER.USER_LOGMEINLOGINNAME
		//			CRM_MST_USER.USER_LOGMEINNODEID

		String userID;
		String loginName;
		String teamName;
		String firstName;
		String lastName;
		String extension;
		String supportTypeID;
		String empID;
		String emailID;
		String lmiLogin;
		String lmiLoginNodeID;
		String fullName;
		String ntlogin;

		User newUser = null;

		ArrayList<String[]> rawRoster = startReport();

		boolean duplicateUser;

		for(String[] line : rawRoster)
		{
			duplicateUser = false; 
			int oldSize = userList.size();
			try
			{					
				//System.out.println(Arrays.asList(line).toString());

				userID = line[0].trim();
				loginName = line[1].trim();
				teamName = line[2].trim();
				firstName = line[3].trim();
				lastName = line[4].trim();
				fullName = lastName + ", " + firstName;

				if(line[5] != null)
				{
					extension = line[5].trim();
				}
				else
				{
					extension="";
				}

				supportTypeID = line[6].trim();
				empID = line[7].trim().toUpperCase();
				emailID = line[8].trim();

				if(line[9] != null)
				{
					lmiLogin = line[9].trim();
				}
				else
				{
					lmiLogin ="";
				}

				if(line[10] != null)
				{
					lmiLoginNodeID = line[10].trim();
				}
				else
				{
					lmiLoginNodeID ="";
				}
				
				if(line[11] != null)
				{
					ntlogin = line[11].trim();
				}
				else
				{
					ntlogin ="";
				}

				if
				(
						!(
								//loginNameMap.containsKey(loginName) || 
								fullNameMap.containsKey(fullName) ||
								employeeIDMap.containsKey(empID)  ||
								userIDMap.containsKey(userID) ||
								ntloginMap.containsKey(ntlogin)
								)
						)
				{
					//logger.info("Roster: Found user " + line[0]);

					newUser = new User(userID);

					newUser.addAttr(USER_ID_ATTR);
					newUser.addAttr(LOGIN_NAME_ATTR);
					newUser.addAttr(TEAMNAME_ATTR);
					newUser.addAttr(FIRSTNAME_ATTR);
					newUser.addAttr(LASTNAME_ATTR);
					newUser.addAttr(EXTENSION_ATTR); 
					newUser.addAttr(SUPPORT_TYPE_ID_ATTR);
					newUser.addAttr(EMP_ID_ATTR);
					newUser.addAttr(EMAIL_ID_ATTR );
					newUser.addAttr(LMI_LOGIN_NAME_ATTR); 
					newUser.addAttr(LMI_LOGIN_NODE_ID_ATTR);
					newUser.addAttr(FULLNAME_ATTR);
					newUser.addAttr(NTLOGIN_ATTR);

					newUser.addData(USER_ID_ATTR, userID);
					newUser.addData(LOGIN_NAME_ATTR, loginName);
					newUser.addData(TEAMNAME_ATTR, teamName);
					newUser.addData(FIRSTNAME_ATTR, firstName);
					newUser.addData(LASTNAME_ATTR, lastName);
					newUser.addData(EXTENSION_ATTR, extension);
					newUser.addData(SUPPORT_TYPE_ID_ATTR, supportTypeID);
					newUser.addData(EMP_ID_ATTR, empID);
					newUser.addData(EMAIL_ID_ATTR, emailID);
					newUser.addData(LMI_LOGIN_NAME_ATTR, lmiLogin);
					newUser.addData(LMI_LOGIN_NODE_ID_ATTR, lmiLoginNodeID);
					newUser.addData(FULLNAME_ATTR, fullName);
					newUser.addData(NTLOGIN_ATTR, ntlogin);

					userList.put(userID, newUser);

					//loginNameMap.put(loginName, newUser);
					fullNameMap.put(fullName, userID);
					employeeIDMap.put(empID, userID);
					userIDMap.put(userID, userID);
					ntloginMap.put(ntlogin, userID);
					

					//System.out.println("Adding user for " + empID + "," + loginName + "," + extension + "," + fullName);
				}
				else
				{
					duplicateUser = true;
				}
			}
			catch(NullPointerException e)
			{
				logger.log(Level.ERROR,  "Error adding user for line beginning with " + line[0]);
				e.printStackTrace();
			}
			finally
			{
				if(newUser != null && duplicateUser == false && oldSize + 1 != userList.size() )
				{
					logger.log(Level.ERROR,  "Error adding user " + line[0]);
				}
			}
		}
	}
	
	/**
	 * Load the roster with Schedule data. This requires START_DATE_PARAM and END_DATE_PARAM to be defined for the Schedule subreport. This is going to wipe the existing roster by importing the userlist from it's child Schedule report, which maintains its own roster.
	 */
	public void loadSchedule()
	{		
		Schedules s = null;
		try 
		{
			s = new Schedules();
			s.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
			s.setParameter(END_DATE_PARAM, getParameter(END_DATE_PARAM));
			s.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
			s.setParameter(REPORT_TYPE_PARAM, getParameter(REPORT_TYPE_PARAM));
			s.setParameter(ROSTER_TYPE_PARAM, getParameter(ROSTER_TYPE_PARAM));
			
			s.setChildReport(true);
			
			userList = s.load();
			
			loginNameMap = new HashMap<String,String>();
			employeeIDMap = new HashMap<String,String>();
			userIDMap = new HashMap<String,String>();
			fullNameMap = new HashMap<String,String>();
			userList = new HashMap<String, User>();
			
			String fullName, empID, userID;
			
			for(Entry<String, User> name : userList.entrySet())
			{
				fullName = name.getValue().getAttrData(FULLNAME_ATTR).get(0);
				empID = name.getValue().getAttrData(EMP_ID_ATTR).get(0);
				userID = name.getValue().getAttrData(USER_ID_ATTR).get(0);
				
				if
				(
						!(
								//loginNameMap.containsKey(loginName) || 
								fullNameMap.containsKey(fullName) ||
								employeeIDMap.containsKey(empID)  ||
								userIDMap.containsKey(userID)

						)
				)
				{
				
					fullNameMap.put(fullName, userID);
					employeeIDMap.put(empID, userID);
					userIDMap.put(userID, userID);
				}
			}
		} 
		catch (ReportSetupException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(s != null)
			{
				s.close();
			}
		}
	}



	/**
	 * Retrieve the combined active rosters for support and sales teams. 
	 * 
	 * @return 	The roster fields.
	 */
	private ArrayList<String[]> getActiveSupportAndSalesRoster()
	{
		ArrayList<String[]> retval = getActiveSupportRoster();

		retval.addAll(getActiveSalesRoster());

		return retval;
	}

	/**
	 * Retrieve the combined historical roster for support and sales teams. 
	 * 
	 * @return	The roster fields.
	 */
	private ArrayList<String[]> getSupportAndSalesRoster()
	{
		ArrayList<String[]> retval = getSupportRoster();

		retval.addAll(getSalesRoster());

		return retval;

	}

	/**
	 * Accessor for the list of User IDs stored.
	 * 
	 * @return	The list of User IDs stored.
	 */
	public Set<String> getUserIDList()
	{
		return userIDMap.keySet();
	}

	/**
	 * Accessor for the number of users stored.
	 * 
	 * @return	The number of users stored.
	 */
	public int getSize()
	{
		return userIDMap.size();
	}

	/**
	 * Accessor for a specified User's name, for human readability.
	 * 
	 * @param userParameter	String to query the Users by. Can be Employee ID or User ID.
	 * 
	 * @return	The User discovered.
	 */
	public String getFullName(String userParameter)
	{
		User user = getUser(userParameter);

		String fullName = null;

		try
		{
			fullName = user.getAttrData(LASTNAME_ATTR).get(0) + ", " + user.getAttrData(FIRSTNAME_ATTR).get(0);
		}
		catch(NullPointerException e)
		{
			logger.log(Level.ERROR, "Could not determine full name for parameter: " + userParameter);
		}

		return fullName;
	}

	/**
	 * Accessor for a specified User.
	 * 
	 * @param userParameter	String to query the Users by. Can be Employee ID or User ID.
	 * 
	 * @return	The User discovered.
	 */
	public User getUser(String userParameter)
	{
		User retval = null;
		String userID;

		if(userParameter != null && userParameter.length() > 0)
		{
			userID = userIDMap.get(userParameter);

			if(userID == null)
			{
				userID = loginNameMap.get(userParameter);

				if(userID == null)
				{
					userID = employeeIDMap.get(userParameter.toUpperCase());

					if(userID == null)
					{
						userID = fullNameMap.get(userParameter);

						if(userID == null)
						{
							userID = ntloginMap.get(userParameter);
							
							if(userID == null)
							{
								//not really an error
								//logger.severe( " Could not find User for parameter: " + userParameter);
							}
						}
						else
						{
							retval = userList.get(userID);
						}
					}
					else
					{
						retval = userList.get(userID);
					}
				}
				else
				{
					retval = userList.get(userID);
				}
			}
			else
			{
				retval = userList.get(userID);
			}
		}

		//System.out.println("User for parameter: " + userParameter + "\n " + retval);

		return retval;
	}

	/**
	 * Process the report. We are not concerned with active staff members, since it's useless to compare the list of current employees against last year's data.
	 * 
	 * @return	The roster. 
	 * 
	 * @see report.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() 
	{
		logger.log(Level.INFO, "Running report " + reportName);

		//coherent roster type confirmed in startReport
		switch(Integer.parseInt(parameters.get(ROSTER_TYPE_PARAM)))
		{
		case ACTIVE_SUPPORT_ROSTER:
			return getActiveSupportRoster();
		case SUPPORT_ROSTER:
			return getSupportRoster();
		case SALES_ROSTER:
			return getSalesRoster();
		case ACTIVE_SALES_ROSTER:
			return getActiveSalesRoster();
		case ACTIVE_ROSTER:
			return getSupportAndSalesRoster();
		case ALL_ROSTER:
			return getActiveSupportAndSalesRoster();
		default:
			return null;
		}
	}

	/**
	 * Process the report for active support members.
	 * 
	 * @return	The roster of active support. 
	 * 
	 */
	private ArrayList<String[]> getActiveSupportRoster() 
	{
		return dbConnection.runQuery
				(
						queryFields +
						" FROM CRM_MST_USER INNER JOIN CRM_MST_USERTEAM ON CRM_MST_USER.USER_TEAMID = CRM_MST_USERTEAM.UTEAM_TEAMID " +
						" WHERE (CRM_MST_USER.USER_RECORDSTATUS=1 AND CRM_MST_USER.USER_SUPPORTTYPEID = " + USER_SUPPORT_TYPE_ID_SUPPORT + 
						") AND CRM_MST_USER.USER_LOGINNAME NOT IN ('CHATUSER', 'WEBUSER', 'USER11')" +
						" AND CRM_MST_USERTEAM.UTEAM_TEAMNAME NOT IN ('Management', 'SGS') " +
						" ORDER BY CRM_MST_USERTEAM.UTEAM_TEAMNAME, CRM_MST_USER.USER_LASTNAME, CRM_MST_USER.USER_LOGMEINNODEID;"
						);
	}

	/**
	 * Process the report for support members.
	 * 
	 * @return	The roster of support. 
	 * 
	 */
	private ArrayList<String[]> getSupportRoster()
	{
		return dbConnection.runQuery
				(

						queryFields +
						" FROM CRM_MST_USER INNER JOIN CRM_MST_USERTEAM ON CRM_MST_USER.USER_TEAMID = CRM_MST_USERTEAM.UTEAM_TEAMID " +
						" WHERE ( CRM_MST_USER.USER_SUPPORTTYPEID = " + USER_SUPPORT_TYPE_ID_SUPPORT  + 
						") AND CRM_MST_USER.USER_LOGINNAME NOT IN ('CHATUSER', 'WEBUSER', 'USER11')" +
						" AND CRM_MST_USERTEAM.UTEAM_TEAMNAME NOT IN ('Management', 'SGS') " +
						" ORDER BY CRM_MST_USERTEAM.UTEAM_TEAMNAME, CRM_MST_USER.USER_LASTNAME, CRM_MST_USER.USER_LOGMEINNODEID;"

						);
	}

	/**
	 * Process the report for sales members.
	 * 
	 * @return	The roster of sales. 
	 * 
	 */
	private ArrayList<String[]> getSalesRoster() 
	{
		return dbConnection.runQuery
				(
						queryFields +
						" FROM CRM_MST_USER INNER JOIN CRM_MST_USERTEAM ON CRM_MST_USER.USER_TEAMID = CRM_MST_USERTEAM.UTEAM_TEAMID " +
						" WHERE ( CRM_MST_USER.USER_SUPPORTTYPEID = " + USER_SUPPORT_TYPE_ID_SALES  + 
						") AND CRM_MST_USER.USER_LOGINNAME NOT IN ('CHATUSER', 'WEBUSER', 'USER11')" +
						" AND CRM_MST_USERTEAM.UTEAM_TEAMNAME NOT IN ('Management', 'SGS') " +
						" ORDER BY CRM_MST_USERTEAM.UTEAM_TEAMNAME, CRM_MST_USER.USER_LASTNAME, CRM_MST_USER.USER_LOGMEINNODEID;"
						);
	} 

	/**
	 * Process the report for active sales members.
	 * 
	 * @return	The roster of active sales. 
	 * 
	 */
	private ArrayList<String[]> getActiveSalesRoster() 
	{
		return dbConnection.runQuery
				(
						queryFields +
						" FROM CRM_MST_USER INNER JOIN CRM_MST_USERTEAM ON CRM_MST_USER.USER_TEAMID = CRM_MST_USERTEAM.UTEAM_TEAMID " +
						" WHERE (CRM_MST_USER.USER_RECORDSTATUS=1 AND CRM_MST_USER.USER_SUPPORTTYPEID = " + USER_SUPPORT_TYPE_ID_SALES + 
						") AND CRM_MST_USER.USER_LOGINNAME NOT IN ('CHATUSER', 'WEBUSER', 'USER11')" +
						" AND CRM_MST_USERTEAM.UTEAM_TEAMNAME NOT IN ('Management', 'SGS') " +
						" ORDER BY CRM_MST_USERTEAM.UTEAM_TEAMNAME, CRM_MST_USER.USER_LASTNAME, CRM_MST_USER.USER_LOGMEINNODEID;"
						);
	} 

	/**
	 * Retrieve the collection of Users stored by the Roster.
	 * 
	 * @return	Collection of Users stored by the Roster. 
	 * 
	 */
	public HashMap<String, User> getUsers()
	{
		return userList;
	}

	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	public boolean validateParameters() 
	{
		return new ReportVisitor().validate(this);
	}

	public static void main(String[] args) 
	{
		Roster r = null;

		try 
		{
			r =  new Roster();

			r.setParameter(ROSTER_TYPE_PARAM, Roster.SALES_ROSTER);

			for(String[] line : r.startReport())
			{
				System.out.println(Arrays.asList(line).toString());
			}

			r.load();

//
//			//"1056","GBray","Retention Team","George","Bray","46919","151","US003563","George.Bray@sutherlandglobal.com","null","null",
//			//String userID = "1056";
//			//String empID = "US003563";
//
			//"1092","JCarter","ROCJFS Sales Team","Janice","Carter","469903","159","US002315","Janice.Carter@sutherlandglobal.com","GWSS149","6895375",
			String userID = "1092";
			String fullName = "Carter, Janice";
//
//			//System.out.println("loginName " + loginName + " maps to " + r.getUser(loginName).getAttrData(Roster.LMI_LOGIN_NAME_ATTR).firstElement());
			System.out.println("userID " + userID + " maps to\n" + r.getUser(fullName));
		} 
		catch(NullPointerException e)
		{
			e.printStackTrace();
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(r!=null)
			{
				r.close();
			}
		}
		
		System.out.println("====================\nSchedule Test");
		
		try 
		{
			r =  new Roster();

			r.setParameter(ROSTER_TYPE_PARAM, Roster.SUPPORT_ROSTER);

			r.setParameter(START_DATE_PARAM, "2012-07-01 00:00:00");
			r.setParameter(END_DATE_PARAM, "2012-07-19 23:59:59");
			
			r.loadSchedule();
			
//			for(Shift s : r.getUser("Justice, Kevin").getSchedule("2012-07-18 18:00:00").getSortedShifts())
//			{
//				System.out.println(s.toString());
//			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(r!=null)
			{
				r.close();
			}
		}
	}



}
