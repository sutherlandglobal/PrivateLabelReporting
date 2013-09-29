/**
 * 
 */
package report;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.log4j.Level;

import constants.Constants;

import database.connection.DatabaseConnection;
import database.connection.SQL.ConnectionFactory;

import report.Report;
import team.User;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * The list of teams within the PrivateLabel support desk
 * 
 * @author Jason Diamond
 *
 */
public final class Teams extends Report 
{	
	private Roster roster;
	private DatabaseConnection dbConnection;
	private final String dbPropFile = Constants.PRIVATE_LABEL_PROD_DB;

	/**
	 * Build the Roster report.
	 *
	 * @throws ReportSetupException	If a connection to the database could not be established.
	 */
	public Teams() throws ReportSetupException 
	{
		super();

		reportName = "Teams";
		
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

	@Override
	protected ArrayList<String[]> runReport() throws Exception 
	{
		ArrayList<String[]> retval = new ArrayList<String[]>();

		roster = new Roster();
		roster.setChildReport(true);
		roster.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);

		roster.load();

	
		TreeSet<String> teamSet = new TreeSet<String>();
		
		for(Entry<String, User> userObject : roster.getUsers().entrySet())
		{			
			teamSet.add(userObject.getValue().getAttrData(Roster.TEAMNAME_ATTR).get(0));
		}
		
		String[] teamCreatorRow;
		
		String teamCreator;
		for(String teamName : teamSet)
		{
			//for each teamname, query crm_mst_userteam table for more info
			
			for(String[] row : dbConnection.runQuery("Select uteam_description,uteam_createddate, uteam_createdby from crm_mst_userteam where uteam_teamname ='" + teamName + "'"))
			{
				try
				{
					//explicit query, since management creates a lot of users, and probably isn't in the roster
					teamCreatorRow = dbConnection.runQuery("select user_firstname, user_lastname from crm_mst_user where user_userid = '" + row[2] + "'").get(0);
					teamCreator = teamCreatorRow[0] + " " + teamCreatorRow[1];
				}
				catch(Exception e)
				{
					e.printStackTrace();
					teamCreator = "";
				}
				
				retval.add(new String[]{teamName, row[0], row[1], teamCreator});
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
		return true;
	}

	public static void main(String[] args) 
	{
		Teams teamReport = null;
		
		try
		{
			teamReport = new Teams();
			
			for(String[] row : teamReport.startReport())
			{
				System.out.println(Arrays.asList(row).toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(teamReport != null)
			{
				teamReport.close();
			}
		}
	}



}
