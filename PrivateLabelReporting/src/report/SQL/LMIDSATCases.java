/**
 * 
 */
package report.SQL;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import database.connection.SQL.ConnectionFactory;
import database.connection.SQL.RemoteConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * @author jdiamond
 *
 */
public final class LMIDSATCases extends Report {

	private RemoteConnection dbConnection;
	
	private final static String[] FLAGGED_COMMENT_WORDS = {"unhappy","awful","worthless","fuck","crap","bitch","shit","suck","stupid","terrible","horrible", "asshole","jerk","unsatisfied","refund", "hate", "insult", "unprofessional","sarcastic", "angry", "disappointed","wors","rude"}; 
	private final static String[] FLAGGED_COMMENT_PHRASES = {"not satisfied", "money back", "bad service", "not happy", "not resolved","better business bureau","not recommend"}; 
	
	private final String dbPropFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/database/rocjfsdev18.properties";

	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public LMIDSATCases() throws ReportSetupException 
	{
		super();

		reportName = "LMI DSAT Cases";
		
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
	protected Vector<String[]> runReport() 
	{
		Vector<String[]> retval = new Vector<String[]>();

		String L1query = "SELECT Date,Session_ID,Customer_Name,Technician_Name,Q6,Comments FROM LMI_6971911_Customer_Survey WHERE Date >= '" + 
				parameters.get(START_DATE_PARAM) + 
				"' AND Date <= '" + parameters.get(END_DATE_PARAM)+ "'"; 
		
		String L2query = "SELECT Date,Session_ID,Customer_Name,Technician_Name,Q6,Comments FROM LMI_5452038_Customer_Survey WHERE Date >= '" + 
				parameters.get(START_DATE_PARAM) + 
				"' AND Date <= '" + parameters.get(END_DATE_PARAM) + "'";
		
		Vector<String> queries = new Vector<String>();
		queries.add(L1query);
		queries.add(L2query);

		Map<String, Vector<String[]>> aggregateResults = dbConnection.runParallelQueries(queries);

		String surveyComments;
		String q6;
		
		for(Entry<String, Vector<String[]>> query :aggregateResults.entrySet()  )
		{
			for(String[] row : query.getValue())
			{
				surveyComments = row[5];
				q6 = row[4];
				
				if(q6 != null && (q6.equals("1") || q6.equals("2") || q6.equals("3") ))
				{
					retval.add(row);
				}
				else if(surveyComments != null)
				{
					boolean isDSAT = false;
					for(String word : surveyComments.split("\\s+"))
					{
						for(String flaggedWord : FLAGGED_COMMENT_WORDS)
						{
							if(word.toLowerCase().equals(flaggedWord.toLowerCase()))
							{
								isDSAT = true;
								break;
							}
						}
						
						if(!isDSAT)
						{
							String whitespaceCleanedComments = surveyComments.replaceAll("\\s+", " ").toLowerCase();
							
							for(String flaggedPhrase : FLAGGED_COMMENT_PHRASES)
							{
								if(whitespaceCleanedComments.contains(flaggedPhrase.toLowerCase()))
								{
									isDSAT = true;
									break;
								}
							}
						}
						
						if(isDSAT)
						{
							retval.add(row);
							break;
						}
					}
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
		
		if
		(
				hasValidDateInterval()
		)
		{
			retval = true;
		}
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		LMIDSATCases rs = null;

		try
		{
			rs = new LMIDSATCases();

			rs.setParameter(START_DATE_PARAM, "2012-09-01 00:00:00");
			rs.setParameter(END_DATE_PARAM, "2012-09-30 23:59:59");

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
