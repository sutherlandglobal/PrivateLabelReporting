package database.connection.MSAccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import util.IOLocker;
import util.ThreadPool;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

import database.connection.DatabaseConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.InvalidSyntaxException;


/**
 * Manage a database-like connection to an MS Access database file using Jackcess. Supports running select queries only, the underlying database is 
 * opened in read-only mode. The connection setup will wait and retry on the presence of locks, in the form of .lck files in the database file's directory. 
 * 
 * As with most databases without a coherent frontend or socket to connect to, respect the open file handles to the underlying file with lsof, and on
 * any modification put a lock file in the same directory as the database file. 
 * 
 * No password authentication is done. MS Access implements this at the client level in windows (LOL).
 * 
 * Supports queries in the format SELECT fields FROM table WHERE conditions.  
 * 
 * @author Jason Diamond
 *
 */
public class FileConnection extends DatabaseConnection
{
	private File databaseFile;
	private Database database;
	private static final int MAX_ATTEMPTS =20;	//don't lock on read, tailor to expected runtimes of writes
	private static final long SLEEP_INT = 5000; //3 * 1000ms
	private static final String SELECT_DELIMITER = "SELECT";
	private static final String FROM_DELIMITER="FROM";
	private static final String WHERE_DELIMITER="WHERE";
	private static final String FIELD_SEPERATOR=",";
	private static final String STAR = "*";
	private IOLocker locker;
	private static final int QUERY_POOL_SIZE = 50;


	/**
	 * Attempt to establish a connection to a specified MS Access database file.
	 * 
	 * @param filename	Location of database to connect to.
	 * 
	 * @throws DatabaseConnectionCreationException		If a connection cannot be established.
	 */
	public FileConnection(String filename) throws DatabaseConnectionCreationException
	{
		super();
		
		//this is the local databaseFile
		this.databaseFile = new File( filename );



		if(!setupConnection())
		{
			throw new DatabaseConnectionCreationException("Could not open databaseFile " + filename);
		}

		logger.log(Level.INFO, "Estabished connection to " + filename);

	}

	/**
	 * Return the table names in the database.
	 * 
	 * @return 	A newline-seperated list of the retrieved table names.
	 */
	public String showTables()
	{
		StringBuilder retval = new StringBuilder();

		if(database != null)
		{
			for(String name : database.getTableNames())
			{
				retval.append(name + "\n");
			}
		}

		return retval.toString();
	}

	/**
	 * Retrieve column names in the specified table.
	 * 
	 * @param tableName		The table to retrieve column names for.
	 * @return	A comma-seperated list of column names.
	 */
	public String describe(String tableName)
	{
		StringBuilder output = new StringBuilder();

		if(database != null)
		{
			try 
			{
				for(Column col : database.getTable(tableName).getColumns())
				{
					output.append(col.getName() + ",");
				}
			} 
			catch (IOException e) 
			{
				//logger.log(Level.ERROR, "IOException running describe on table \"" + tableName + "\" -- " +  e.getMessage());
				
			}
			catch (NullPointerException e)
			{
				//logger.log(Level.ERROR, "Unknown Table \"" + tableName + "\" " + e.getMessage());
				
			}
		}


		return output.toString();
	}


	/**
	 * Retrieve schema information for the specified table. 
	 * 
	 * @param tableName		The table to retrieve schema information for.
	 * 
	 * @return	A string of the table's columns and their respective types.
	 */
	public String getSchema(String tableName)
	{
		StringBuilder output = new StringBuilder();

		if(database != null)
		{
			try 
			{
				for(Column col : database.getTable(tableName).getColumns())
				{
					output.append(col.getName());
					output.append(": ");
					output.append(col.getType());
					output.append("\n");
				}
			} 
			catch (IOException e) 
			{
				//logger.log(Level.ERROR, "IOException running describe on table \"" + tableName + "\" -- " +  e.getMessage());
				logger.log(Level.ERROR, "IOException running describe on table \"" + tableName + "\" -- " +  e.getMessage());
			}
			catch (NullPointerException e)
			{
				//logger.log(Level.ERROR, "Unknown Table \"" + tableName + "\" " + e.getMessage());
				logger.log(Level.ERROR,  "Unknown Table \"" + tableName + "\" " + e.getMessage());
			}
		}


		return output.toString();
	}

	/**
	 * Retrieve schema information for the specified table. 
	 * 
	 * @param tableName		The table to retrieve schema information for.
	 * 
	 * @return	A string of the table's columns and their respective types.
	 */
	public Vector<String> getSchemaInfo(String tableName)
	{
		Vector<String> schemaInfo = null;

		if(database != null)
		{
			try 
			{
				Table table = database.getTable(tableName);
				schemaInfo = new Vector<String>(table.getColumnCount());
				for(Column col : table.getColumns())
				{
					schemaInfo.add(col.getType().toString());
				}
			} 
			catch (IOException e) 
			{
				//logger.log(Level.ERROR, "IOException running describe on table \"" + tableName + "\" -- " +  e.getMessage());
				logger.log(Level.ERROR,  "IOException running describe on table \"" + tableName + "\" -- " +  e.getMessage());
			}
			catch (NullPointerException e)
			{
				//logger.log(Level.ERROR, "Unknown Table \"" + tableName + "\" " + e.getMessage());
				logger.log(Level.ERROR,  "Unknown Table \"" + tableName + "\" " + e.getMessage());
			}
		}


		return schemaInfo;
	}

	/**
	 * Determine row count for a specified table.
	 * 
	 * @param tableName		Table to determine row count for.
	 * @return	Discovered row count.
	 */
	public int getRowCount(String tableName)
	{
		int retval = 0;

		if(database != null)
		{
			try 
			{
				retval = database.getTable(tableName).getRowCount();
			} 
			catch (IOException e) 
			{
				//logger.log(Level.ERROR, "IOException running row count on table \"" + tableName + "\" -- " +  e.getMessage());
				logger.log(Level.ERROR, "IOException running row count on table \"" + tableName + "\" -- " +  e.getMessage());
			}
			catch (NullPointerException e)
			{
				//logger.log(Level.ERROR, "Unknown Table \"" + tableName + "\" " + e.getMessage());
				logger.log(Level.ERROR, "Unknown Table \"" + tableName + "\" " + e.getMessage());
			}
		}

		return retval;
	}

	/**
	 * Print out a readable form of the specified table.
	 * 
	 * @param tableName Table to print.
	 */
	public String printTable(String tableName)
	{
		String retval = "";
		if(database != null )
		{
			//Table thisTable = null;
			try 
			{
				if(database.getTable(tableName) != null)
				{

					//print the table header. same output as the "describe" command
					StringBuilder output = new StringBuilder(); 

					output.append(describe(tableName) + "\n");

					output.append("----------------------------------------------------------------------------------------\n");

					//print the row,col grid of data that comprises the table
					for(String[] rows : runQuery("SELECT * FROM " + tableName))
					{
						for(String col : rows)
						{
							output.append(col + FIELD_SEPERATOR);
						}

						output.append("\n");
					}


					retval = output.toString().trim();
				}
				else
				{
					//logger.log(Level.ERROR, "Table not found: " + tableName);
					logger.log(Level.ERROR, "Table not found: " + tableName);
				}
			} 
			catch (IOException e) 
			{
				//logger.log(Level.ERROR, "IOException running printTable on table \"" + tableName + "\" -- " +  e.getMessage());
				logger.log(Level.ERROR, "IOException running printTable on table \"" + tableName + "\" -- " +  e.getMessage());
			}
		}

		return retval;
	}

	/**
	 * Run a query and return the results. Implementation will depend on the particular database. No limit on the size of the result set.
	 * 
	 * @param query	Query to run.
	 * @return	The results of this query.
	 * 
	 * @see database.connection.DatabaseConnection#runQuery(java.lang.String)
	 */
	@Override
	public Vector<String[]> runQuery(String query) 
	{
		return runQuery( query, -1); 
	}

	/**
	 * Run a query and return the results. No limit on the size of the result set.
	 * 
	 * @param limit 		The maximum size of the result set
	 * @param query		The query to run.
	 * @return				The results of this query.
	 * 
	 * @see database.connection.DatabaseConnection#runQuery(java.lang.String)
	 */
	public Vector<String[]> runQuery(String query, int limit) 
	{
		long startTime = System.currentTimeMillis();

		logger.log(Level.INFO, "Running query: " + query + " limit " + limit);

		Vector<String[]> retval = new Vector<String[]>();
		final QueryResults queryResults = new  QueryResults();
		final Query thisQuery;

		final Table table;

		//can't have a queryresults object for the connection, since a connection must support many queries
		try 
		{
			thisQuery = new Query(query, queryResults, logger);

			table = database.getTable(thisQuery.getTables()[0],true);

		} 
		catch (InvalidSyntaxException e) 
		{
			//logger.log(Level.ERROR, "Syntax Error: " + query);
			logger.log(Level.ERROR, "Syntax Error: " + query);
			return retval;
		} 
		catch (IOException e)
		{
			//logger.log(Level.ERROR, "IOException retrieving table for: " + query);
			logger.log(Level.ERROR,  "IOException retrieving table for: " + query);
			e.printStackTrace();

			return retval;
		}

		//jackcess doesn't support query execution
		//however it allows navigation to data
		//this is the hilarious task of accepting sql-like queries and manually processing them

		//enforce simple statements, leave off wheres for now
		//each field must be in each table -> unless specifying table in field
		//select FIELD_1,FIELD_2,FIELD_3 from TABLE_A,TABLE_B,TABLE_C where COND_1 == true,COND_2 == true
		//select Users.UserId,Boats.BoatId from Users,Boats

		//each query has it's own results, so local results object

		ThreadPool tp = new ThreadPool(QUERY_POOL_SIZE );


		//synchronized list for multiple writes from multiple threads
		final ArrayList<String[]> queryOutput = new ArrayList<String[]>(Collections.synchronizedList(new ArrayList<String[]>(table.getRowCount())));

		//build a hollow result set so rows can be added regardless of their neighbors. nulls will be removed later.
		for(int i = 0; i<table.getRowCount(); i ++)
		{
			queryOutput.add(null);
		}

		//using multiple Cursors is not threadsafe
		final Cursor c = Cursor.createCursor(table);

		try 
		{
			final String[] queryRowFields = thisQuery.getFields();

			for(int i = 0; i<table.getRowCount(); i++)
			{
				//since cursor is not threadsafe, we advance to the target row in a non-threaded way, then each row is processed in a threaded way.
				//turns out simply advancing a cursor serially is very fast, and the row parsing and condition eval is the slow part, so the latter is threaded
				final int index = i;
				//final Map<String, Object> rowData = c.getNextRow();
				final HashMap<String,Object> rowData = new  HashMap<String,Object>(c.getNextRow());

				tp.runTask
				(
						new Runnable()
						{
							@Override
							public void run() 
							{

								String[] rowFields = new String[queryRowFields.length];
								String data;
								String fieldName;

								for(int j = 0 ; j< rowFields.length; j++)
								{
									fieldName = queryRowFields[j];

									try
									{
										//get the value at this cursor position
										data = rowData.get(fieldName).toString().replace(',',' ').trim();
									}
									catch(NullPointerException e)
									{
										data = "";
										//System.out.print("");
									}

									//if fieldName isn't applicable to a condition, it returns true, as if it passed since no comparison can be made
									if(queryResults.applyConditions(fieldName, data))
									{
										//System.out.println("keeping " + data);
										rowFields[j] = data;
									}
									else
									{
										rowFields = null;
										break;
									}
								}

								if(rowFields != null)
								{
									queryOutput.set(index, rowFields);
								}
							}
						}
						);
			}

			tp.close();

			//System.out.println(queryOutput.size());

			//remove nulls
			//iterates over all rows in the table
			for(int i =0; i< queryOutput.size(); i++)
			{
				if(queryOutput.get(i) == null )
				{
					queryOutput.remove(i);
					i--;
				}
			}


			long endTime = System.currentTimeMillis();
			logger.log(Level.INFO,  "Result building Time Elapsed (ms): " + (((endTime)) -  ((startTime)) ));


			//convert the raw output list into a vector
			//			Vector<String[]> resultSet = new Vector<String[]>();
			//			resultSet.addAll(queryOutput);
			//			
			//			queryResults.setResults(resultSet);
			//
			//			retval =  queryResults.getResults();

			retval = new Vector<String[]>(queryOutput);

			//limit the size of the result set if necessary
			if(limit > 0 )
			{
				int totalRows = retval.size();
				int start = totalRows - limit;
				ArrayList<String[]> window = new ArrayList<String[]>();

				if(start > 0 && start < totalRows)
				{
					window.addAll(retval.subList( start, totalRows));

					retval.removeAllElements();
					retval.addAll(window);
				}
			}

			logger.log(Level.INFO, "Query returned rows: " + retval.size());
		} 
		catch (IOException e) 
		{
			//logger.log(Level.ERROR, "IOException: " + e.getMessage());
			logger.log(Level.ERROR,  "IOException: " + e.getMessage());
		}
		catch(OutOfMemoryError e)
		{
			Runtime.getRuntime().gc();

			//logger.log(Level.ERROR, "OutOfMemoryError: " + e.getMessage());
			logger.log(Level.ERROR, "OutOfMemoryError: " + e.getMessage());
		}

		return retval;
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
	public Map<String, Vector<String[]>> runParallelQueries(Vector<String> queries)
	{
		ThreadPool tp = new ThreadPool(queries.size());

		final List<FileConnection> workers = Collections.synchronizedList(new Vector<FileConnection>(queries.size()));

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
								final FileConnection workerConnection = new FileConnection(databaseFile.getAbsolutePath());
								workers.set(i, workerConnection);

								//								Vector<String[]> results = workerConnection.runQuery(query);
								//
								//								if(!retval.containsKey(query))
								//								{
								//									retval.put(query, results );
								//								}
								//								else
								//								{
								//									//not sure why'd you aggregately run the same query more than once, maybe for easy stress testing.
								//
								//									Vector<String[]> newResults = retval.get(query);
								//
								//									if(newResults.addAll(results))
								//									{
								//										retval.put(query, newResults);
								//									}
								//								}
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
	 * Attempt to establish a connection to the database. 
	 * 
	 * @return	True if the connection was established, false otherwise.
	 * 
	 * @see database.connection.DatabaseConnection#setupConnection()
	 */
	@Override
	protected boolean setupConnection() 
	{
		boolean retval = false;

		locker = null; 

		//spin on lsof of the databaseFile
		database = null;

		try 
		{
			locker = IOLocker.getInstance(databaseFile.getParent());

			int attempts = 0;
			boolean done = false;

			logger.log(Level.INFO, "Checking for locks.");

			while(attempts < MAX_ATTEMPTS && !done)
			{
				try 
				{					
					//if we can safely read from the database, try to create a connection, trying once
					if (!locker.isLocked(databaseFile.getName())) 
					{			
						try
						{
							//lsof is live here
							logger.log(Level.INFO, "Opening connection to database.");
							database = Database.open(databaseFile, true);
						} 
						catch (IOException e) 
						{
							//logger.log(Level.ERROR, e.getMessage());
							logger.log(Level.ERROR, e.getMessage());
						}
						catch (ArrayIndexOutOfBoundsException e)
						{
							//logger.log(Level.ERROR, e.getMessage());
							logger.log(Level.ERROR,  e.getMessage());
						}
						finally
						{
							if(database != null)
							{
								//just need one correct unlocked access
								retval = true;
								done = true;
								logger.log(Level.INFO, "Database.open() call successful");
							}
							else
							{
								//logger.log(Level.ERROR, "Database.open() call failed");
								logger.log(Level.ERROR,   "Database.open() call failed");

								//retry in next iteration, maybe it was a fluke. worst that can happen is it fails again and the loop terminates for max attempts.
							}
						}
					}
					else
					{
						logger.log(Level.INFO, "Database locked, sleeping");

						Thread.sleep(SLEEP_INT);
					}

					attempts++;
				} 
				catch (InterruptedException e) 
				{
					//logger.log(Level.ERROR, e.getMessage());
					logger.log(Level.ERROR, e.getMessage());
				} 
			}

			if(attempts == MAX_ATTEMPTS)
			{
				//logger.log(Level.ERROR, "Timeout due to existing DB lock");
				logger.log(Level.ERROR, "Timeout due to existing DB lock");
			}
		}
		finally
		{
			if(database == null)
			{
				//final check
				//logger.log(Level.ERROR, "Database RemoteConnection creation failure in final check");
				logger.log(Level.ERROR, "Database RemoteConnection creation failure in final check");
				
				retval = false;
			}
		}


		return retval;
	}

	/**
	 *	Close the database connection.
	 *
	 * @see database.connection.DatabaseConnection#close()
	 */
	public void close() 
	{
		super.close();

		try 
		{

			logger.log(Level.INFO, "CLOSING database connection");

			//LogManager.shutdown();
			
			database.close();

		} 
		catch (IOException e) 
		{
			//logger.log(Level.ERROR, "Error closing database connection: " + e.getMessage());
			logger.log(Level.ERROR,  "Error closing database connection: " + e.getMessage());
		}
	}

	/**
	 * An SQL-like query for querying an MS Access database via FileConnection.
	 * 
	 * @author Jason Diamond
	 *
	 */
	class Query
	{
		private String[] fields;
		private String[] tables;
		private QueryResults results;
		private Logger logger;
		private HashMap<String,String> fieldTableMapping;

		/**
		 * Build a query object, given an SQL-like statement
		 * 
		 * @param query		The query statement.
		 * @param queryResults		The results of the query.
		 * @param loggerObj		Logger object.
		 * @throws InvalidSyntaxException		If the query statement fails syntax constraints.
		 */
		public Query(String query, QueryResults queryResults, Logger loggerObj) throws InvalidSyntaxException
		{
			this.results = queryResults;
			this.logger = loggerObj;

			//need to support spaces in fields and tables
			String fieldsString = "";
			String tableString = "";
			String conditionsString = "";

			try
			{				
				fieldsString = query.substring	(	query.indexOf(SELECT_DELIMITER) + SELECT_DELIMITER.length() + 1,	query.indexOf(FROM_DELIMITER)	);

				if(query.contains(WHERE_DELIMITER))
				{
					tableString =	query.substring(query.indexOf(	FROM_DELIMITER) +FROM_DELIMITER.length() + 1 , query.indexOf(WHERE_DELIMITER) );

					conditionsString = query.substring	(	query.indexOf(WHERE_DELIMITER) +WHERE_DELIMITER.length() +  1 );
				}
				else
				{
					tableString = 	query.substring(query.indexOf(FROM_DELIMITER) +FROM_DELIMITER.length() + 1);
				}

			} 
			catch (ArrayIndexOutOfBoundsException e)
			{
				//if we have a coherent fields and tables string, no conditions are made
				//else bad syntax

				//nothing, no conditions made
				logger.log(Level.INFO, "no conditions made " + e.getMessage());
			} 
			finally
			{
				fieldsString = fieldsString.trim();
				tableString = tableString.trim();
				conditionsString = conditionsString.trim();



				//				System.out.println("FieldsString: " + fieldsString);
				//				System.out.println("TableString: " + tableString);
				//				System.out.println("ConditionsString: " + conditionsString);

				if(fieldsString.length() == 0 || tableString.length() == 0)
				{
					throw new InvalidSyntaxException("Malformed query: " + query);
				}
			}
			//String fieldsString = queryFields[1];
			//String tableString = queryFields[3];

			String[] conditionFields =null;


			//probably should switch to and/ors rather than assume and/or
			if(conditionsString.length() > 0)
			{
				conditionFields = conditionsString.split(FIELD_SEPERATOR);

				for(String s : conditionFields)
				{
					if(!results.addCondition(s))
					{
						throw new InvalidSyntaxException("Failed adding condition " + s);
					}
					else
					{
						logger.log(Level.INFO,  "Adding condition: " + s);
					}
				}
			}


			tables = tableString.split(FIELD_SEPERATOR);



			//if columnString eq * then all columns from describe table 1 -> each field
			if(fieldsString.equals(STAR))
			{
				fieldsString = describe(tables[0]);
			}


			fields = fieldsString.split(FIELD_SEPERATOR);


			fieldTableMapping = new HashMap<String,String>();

			for(String fieldName : fields)
			{
				//if f contains a period like in field.Table then update the mapping
				if(fieldName.contains("."))
				{
					String tableName = fieldName.substring(0, fieldName.indexOf("."));
					//fieldName = fieldName.substring(fieldName.indexOf(".") + 1);

					//field -> table
					fieldTableMapping.put	( fieldName, tableName);

					logger.log(Level.INFO, "Mapping " + fieldName + " to " +  fieldTableMapping.get(fieldName) );
				}
				else if(getTables().length == 1)
				{
					//specifying no tables or no explicit mappings can only mean
					//SELECT a,b,c FROM t


					fieldTableMapping.put(fieldName, getTables()[0]);

					//logger.log(Level.INFO, "Mapping " + fieldName + " to " + fieldTableMapping.get(fieldName));
				}
				else
				{
					//logger.log(Level.ERROR, "Error mapping field " + fieldName + " to appropriate table");
					logger.log(Level.ERROR,  "Error mapping field " + fieldName + " to appropriate table");
				}


				results.addColumn(fieldName);

			}

		}

		/**
		 * Accessor to retrieve the number of field to table mappings.
		 * 
		 * @return  Number of field to table mappings.
		 */
		public int getNumTableMappings()
		{
			return fieldTableMapping.size();
		}

		/**
		 * Retrieve the table name mapped to the supplied field.
		 * 
		 * @param field 	The field to retrieve the table name for.
		 * @return	The table name corresponding to the field parameter.
		 */
		public String getMapping(String field)
		{
			return fieldTableMapping.get(field);
		}

		/**
		 * Retrieve fields for mapped to the specified table.
		 * 
		 * @param tableName		Table name to retrieve fields for.
		 * @return	List of fields mapped to the specified table.
		 */
		public String[] getFieldsForTable(String tableName)
		{
			Vector<String> retval = new Vector<String>();
			for(String field : getFields())
			{
				if(getMapping(field).equals(tableName))
				{
					retval.add(field);
				}
			}

			return retval.toArray(new String[retval.size()]);
		}

		/**
		 * Accessor for fields referenced in the query.
		 * 
		 * @return	Fields referenced in the query.
		 */
		public String[] getFields()
		{
			return fields;
		}

		/**
		 * Accessor for tables referenced in the query.
		 * 
		 * @return	Tables referenced in the query.
		 */
		public String[] getTables()
		{
			return tables;
		}
	}
}
