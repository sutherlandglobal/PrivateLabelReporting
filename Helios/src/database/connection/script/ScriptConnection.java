/**
 * 
 */
package database.connection.script;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import util.SystemCallProcessor;
import util.ThreadPool;
import database.connection.DatabaseConnection;
import exceptions.DatabaseConnectionCreationException;

/**
 * Treat the output of an executable like a database query. Accessing of datasources may not always be possible exclusively through java. It is more likely 
 * that if a datasource has no java drivers, it may have a CPAN module or a Gem to provide access to the underlying data. This is especially true of interacting
 * with special-purpose interpreters like R. 
 * 
 * The scriptPath is treated like the database and the supplied query for the runQuery(String) method will be a space-seperated string of it's arguments:
 * 
 * ScriptConnection sc = new ScriptConnection("\/\/home\/user\/script.pl");
 * sc.runQuery("arg1 arg2 arg3")
 *  
 * @author Jason Diamond
 *
 */
public class ScriptConnection extends DatabaseConnection
{

	private String scriptPath;
	private Logger logger;
	
	/**
	 * 
	 */
	public ScriptConnection(String scriptPath) throws DatabaseConnectionCreationException
	{
		super();
		
		this.scriptPath = scriptPath;
		
		
		if(!setupConnection())
		{
			throw new DatabaseConnectionCreationException("Could not open or access scriptFile " + scriptPath);
		}
		
		logger.info(logger.getName() + " " +"Estabished connection to " + scriptPath);
	}

	private boolean validateParameters(String argString)
	{
		boolean retval = true;
		
		String[] args = argString.trim().split(" ");
		
		for(String arg : args)
		{
			if(!arg.matches("[a-zA-Z0-9\\.\\-\\_\\:]+"))
			{
				retval = false;
				
				//logger.log(Level.ERROR, "Invalid character in argument");
				logger.severe("Invalid character in argument");
				
				break;
			}
			
		}
		
		return retval;
	}

	/**
	 *	Close any database connections opened by children.
	 *
	 * @see database.connection.DatabaseConnection#close()
	 */
	@Override
	public void close()
	{
		//nothing
	}

	/**
	 * Run several queries in parallel, and return the results from each query.
	 * 
	 * @param queries	A list of queries to run.
	 * 
	 * @return	Mapping of query -> result set.
	 *
	 * @see database.connection.DatabaseConnection#runParallelQueries(java.util.Vector)
	 */
	@Override
	public Map<String, Vector<String[]>> runParallelQueries(Vector<String> queries)
	{
		ThreadPool tp = new ThreadPool(queries.size());
		
		final List<ScriptConnection> workers = Collections.synchronizedList(new Vector<ScriptConnection>(queries.size()));
		
		for(int i =0; i<queries.size();i++)
		{
			workers.add(null);
		}
		
		final Map<String, Vector<String[]>> retval = Collections.synchronizedMap(new HashMap<String, Vector<String[]>>(queries.size()));
		
		int index = 0;
		for(final String query : queries)
		{
			final int i = index;
			index++;

			tp.runTask
			(
					new Runnable()
					{
						@Override
						public void run() 
						{
							try
							{
								final ScriptConnection workerConnection = new ScriptConnection(scriptPath);
								workers.set(i, workerConnection);

								retval.put(query, workerConnection.runQuery(query) );
							} 
							catch (DatabaseConnectionCreationException e)
							{
								e.printStackTrace();
							}
							finally
							{
								if(workers.get(i) != null)
								{
									workers.get(i).close();
								}
							}
						}
					}
			);
		}
		
		tp.close();
		
		return retval;
	}

	/** 
	 * Pretend this is a database and we query against it. We analagously run a system call and give it parameters, not unlike 
	 * a database query. There is a danger of encasing additional system calls inside parameters or daisy-chaining additional 
	 * system calls together. Arguments are validated to prevent this.
	 * 
	 * @param query The system call to make/database query to run. 
	 * 
	 * (non-Javadoc)
	 * @see database.connection.DatabaseConnection#runQuery(java.lang.String)
	 */
	@Override
	public Vector<String[]> runQuery(String query)
	{
		Vector<String[]> retval = new Vector<String[]>();
		
		
		
		if(validateParameters(query))
		{
			retval.add(new String[]{SystemCallProcessor.runAndGetOutput(scriptPath + " " + query)});
		}
		else
		{
			//logger.log(Level.ERROR, "Could not validate query parameters.");
			logger.severe("Could not validate query parameters.");
		}

		return retval;
	}

	/**
	 * Attempt to establish a connection to the database. Implementation will depend on the particular database.
	 * 
	 * @return	True if the connection was successful, false otherwise.
	 *
	 * @see database.connection.DatabaseConnection#setupConnection()
	 */
	@Override
	protected boolean setupConnection()
	{
		boolean retval = false;
		
		if(scriptPath.matches("[a-zA-Z0-9\\.\\_\\-\\/]+") && !(scriptPath.contains("..") || scriptPath.contains("~")) )
		{
			File scriptFile = null;

			try
			{
				scriptFile = new File(scriptPath);


				if(scriptFile.exists() && scriptFile.canExecute())
				{
					retval = true;
				}
			}
			catch(Exception e)
			{
				//logger.log(Level.ERROR,  e.getMessage());
				logger.severe(e.getMessage());
			}
		}
		else
		{
			//logger.log(Level.ERROR, "Invalid scriptPath");
			logger.severe("Invalid scriptPath");
		}
		
		return retval;
	}

	@Override
	public Vector<String> getSchemaInfo(String tableName)
	{
		return null;
	}

	public static void main(String[] args)
	{
		ScriptConnection sc = null; 

		try
		{
			sc = new ScriptConnection("/usr/bin/perl");
			
			System.out.println(sc.runQuery("-v").get(0)[0]);
		} 
		catch (DatabaseConnectionCreationException e)
		{
			e.printStackTrace();
		} 
		catch (SecurityException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if(sc != null)
			{
				sc.close();
			}
		}
	}
}
