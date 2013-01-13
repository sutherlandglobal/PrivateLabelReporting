package report.SQL;


import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import statistics.StatisticsFactory;
import exceptions.ReportSetupException;

/**
 * Basic report proof-of-concept. Calculate and return the average of numbers returned by a simulated database query.
 * 
 * @author Jason Diamond
 *
 */
public final class Average extends Report 
{
	/**
	 * Build the report object. This particular one is empty because this report is a proof-of-concept. 
	 * 
	 * @throws LoggerCreationException		If the logger cannot be attached to the report.
	 * @throws ReportSetupException	If a database connection cannot be established.
	 */
	protected Average() throws ReportSetupException { }
	
	/** 
	 * Attempt to establish connections to all required datasources. A report by definition has at least one, and possibly many.
	 * 
	 * @return	True if the connection was established, false otherwise.
	 */
	@Override
	protected boolean setupDataSourceConnections()
	{
		//since this is just a test report that simulates a query, no worries.
		return true;
	}
	
	protected boolean setupReport()
	{
		return true;
	}
	
	/**
	 * Retrieve the aggregate results from a list of database queries.
	 * 
	 * @return	The aggregate results from the queries.  
	 * 
	 * @see report.Report#runReport()
	 */
	@Override
	protected Vector<String[]> runReport() 
	{
		Vector<String[]> retval = new Vector<String[]>();
		
		Vector<String> dataSet = new Vector<String>();
		
		for(double i =0; i< 10; i++)
		{
			dataSet.add("" + i);
		}
		
		logger.log(Level.INFO, "Running db query");
		
		//simulated db query results
		retval.add(new String[]{"1","16","2"});
		retval.add(new String[]{"2","31","3"});
		retval.add(new String[]{"3","123","12"});
		retval.add(new String[]{"4","214","20"});
		retval.add(new String[]{"5","671","64"});
		retval.add(new String[]{ ""+ StatisticsFactory.getStatsInstance().getAverage(dataSet),"00","00"});
			
		logger.log(Level.INFO,"Running db query");
		
		return retval;
	}
	
	public void close()
	{
		//closing any db connections would be good too
		
		super.close();
	}
	
	@Override
	public boolean validateParameters() 
	{
		//no params for this one
		return true;
	}
	
	public static void main(String[] args) 
	{
		
		Average a = null;
		try 
		{
			a = new Average();
			
			StringBuilder line;
			for(String [] x : a.startReport())
			{
				line = new StringBuilder();
				for(String y : x)
				{
					line.append(y);
					line.append(","); 
				}
				System.out.println(line.toString());
			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
	}
}
