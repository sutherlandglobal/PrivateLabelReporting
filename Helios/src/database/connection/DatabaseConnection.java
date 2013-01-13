package database.connection;

import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import exceptions.DatabaseConnectionCreationException;
import exceptions.LoggerCreationException;

/**
 * Abstract connection to a datastore. In practice, most likely a database. 
 * Some basic functionality is implemented here, specifically aggregating queries and returning a single resultset.
 * 
 * @author Jason Diamond
 *
 */
/**
 * @author Jason Diamond
 *
 */
public abstract class DatabaseConnection 
{
	protected Logger logger;
	protected String dbType;
	protected String name;
	private final String LOGGER_HANDLE = "database";

	/**
	 * Hollow constructor for a parent database connection.
	 * @throws DatabaseConnectionCreationException 
	 * 
	 * @throws LoggerCreationException 
	 * 
	 */
	protected DatabaseConnection() throws DatabaseConnectionCreationException 
	{
		logger = Logger.getLogger(LOGGER_HANDLE );
	}

	/**
	 *	Close any database connections opened by children.
	 */
	public void close()
	{
		logger.info( "Closing " + dbType + " DB Connection to " + name );
	}

	/**
	 * Run a query and return the results. Implementation will depend on the particular database.
	 * 
	 * @param query	Query to run.
	 * @return	The results of this query.
	 */
	public abstract Vector<String[]> runQuery(String query);


	/**
	 * Run several queries in parallel, and return the results from each query.
	 * 
	 * @param queries	A list of queries to run.
	 * 
	 * @return	Mapping of query -> result set.
	 */
	public abstract Map<String, Vector<String[]>> runParallelQueries(Vector<String> queries);


	/*
	 *
	 */
	public abstract Vector<String> getSchemaInfo(String tableName);

	/**
	 * Attempt to establish a connection to the database. Implementation will depend on the particular database.
	 * 
	 * @return	True if the connection was successful, false otherwise.
	 */
	protected abstract boolean setupConnection();

}
