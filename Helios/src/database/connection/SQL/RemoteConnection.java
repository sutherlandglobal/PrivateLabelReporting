/**
 * 
 */
package database.connection.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Level;

import util.ThreadPool;
import database.connection.DatabaseConnection;
import exceptions.DatabaseConnectionCreationException;

/**
 * Connect to an SQL database, be it MySQL, MS SQL, etc. Just make sure you specify the driver to use.
 * 
 * @author Jason Diamond
 *
 */
public class RemoteConnection extends DatabaseConnection
{

	private String url;
	private String password;
	private String userName;
	private String driverClassName;

	private Connection con;
	//private final int QUERY_POOL_SIZE = 20;

	/**
	 * Build a connection to the database with the supplied url, credentials and driver.
	 * 
	 * @param url	URL of the database.
	 * @param userName	Username to authenticate with.
	 * @param password	Password to authenticate with.
	 * @param driverClassName	Driver to use to build the connection.
	 * 
	 * @throws DatabaseConnectionCreationException	If a connection to the database could not be established.
	 */
	public RemoteConnection(String url, String userName, String password, String driverClassName) throws DatabaseConnectionCreationException
	{
		super();

		this.url = url;
		this.userName = userName;
		this.password = password;
		this.driverClassName = driverClassName;

		dbType = "SQL";
		name = url;

		//guaranteed we want to setup the connection automatically
		if(!setupConnection())
		{
			throw new DatabaseConnectionCreationException("Could not build a DB connection");
		}

		logger.log(Level.INFO,  "Connected to " + name + " of type " + dbType );
	}

	/**
	 *	Close any database connections opened by children.
	 *
	 * @see database.connection.DatabaseConnection#close()
	 */
	@Override
	public void close()
	{
		super.close();

		try
		{
			if(!con.isClosed())
			{
				con.close();
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Run a query and return the results. Implementation will depend on the particular database.
	 * 
	 * @param query	Query to run.
	 *
	 * @return	The results of this query.
	 *
	 * @see database.connection.DatabaseConnection#runQuery(java.lang.String)
	 */
	@Override
	public Vector<String[]> runQuery(String query)
	{
		//final List<String[]> queryResults = Collections.synchronizedList(new Vector<String[]>());
		Vector<String[]> queryResults = null; 

		logger.log(Level.INFO,  "Running SQL query: " + query );

		Statement s = null;

		try
		{
			//these values get wiped if we assign the resultset directly from the query
			s = con.createStatement
					(				
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE
							);

			long queryStart = System.currentTimeMillis();
			s.executeQuery(query);
			long queryEnd = System.currentTimeMillis();

			long resultsStart =  queryEnd;
			//final ResultSet rs = s.executeQuery(query);


			final ResultSet rs = s.getResultSet();

			rs.last();
			queryResults = new Vector<String[]>(rs.getRow());
			rs.beforeFirst();

			try
			{					
				while (rs.next())
				{

					final String[] temp = new String[rs.getMetaData().getColumnCount()];

					//resultset is not parallel so we can't populate the rows in parallel
					for(int j=1; j<= temp.length; j++)
					{
						temp[j-1] = rs.getString(j);
					}

					queryResults.add(temp);
				}

				long resultsEnd =  System.currentTimeMillis();

				logger.log(Level.INFO,  "Query returned rows: " + queryResults.size() );
				logger.log(Level.INFO,  "Query time: " + (queryEnd - queryStart) + " ms");
				logger.log(Level.INFO,  "Results time: " + (resultsEnd - resultsStart) + " ms");
			}
			catch (SQLException e)
			{
				throw e;
			}
			finally
			{
				try
				{
					rs.close();
				} 
				catch (SQLException e)
				{
					e.printStackTrace();
				}

				try
				{
					s.close();
				} 
				catch (SQLException e)
				{
					e.printStackTrace();
				}

				//leave the connection open because we might have more queries to do
			}
		} 
		catch (SQLException e1)
		{
			//logger.log(Level.ERROR, "SQL Exception running query " + query + ": " + e1.getMessage());
			logger.log(Level.ERROR, "SQL Exception running query " + query + ": " + e1.getMessage());

			for(StackTraceElement st : e1.getStackTrace())
			{
				//logger.log(Level.ERROR, st.toString());
				logger.log(Level.ERROR,  st.toString());
			}

			queryResults = new Vector<String[]>();
		}

		return queryResults;
	}

	/**
	 * Run several queries in parallel, and return the results from each query.
	 * 
	 * @param queries	A list of queries to run.
	 * 
	 * @return	Mapping of query -> result set.
	 */
	@Override
	public Map<String, Vector<String[]>> runParallelQueries(Vector<String> queries)
	{

		ThreadPool tp = new ThreadPool(queries.size());

		final List<RemoteConnection> workers = Collections.synchronizedList(new Vector<RemoteConnection>(queries.size()));

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
								final RemoteConnection workerConnection = new RemoteConnection(url, userName, password, driverClassName);
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
		try
		{
			Class.forName(driverClassName);
			con = DriverManager.getConnection(url, userName, password);

			retval = true;
		} 
		catch (ClassNotFoundException e)
		{
			//logger.log(Level.ERROR, "ClassNotFoundException establishing database connection: "+ e.getMessage());
			logger.log(Level.ERROR, "ClassNotFoundException establishing database connection: "+ e.getMessage());
		} 
		catch (SQLException e)
		{
			//logger.log(Level.ERROR, "SQLException establishing database connection: "+ e.getMessage());
			logger.log(Level.ERROR, "SQLException establishing database connection: "+ e.getMessage());
		}

		return retval;
	}


	/** 
	 * Return a remote SQL database's schema information.
	 * 
	 * @param tableName		The table to retrieve schema information for.
	 * 
	 * @return The schema information for the given table.
	 * 
	 * @see database.connection.DatabaseConnection#getSchemaInfo(java.lang.String)
	 */
	public Vector<String> getSchemaInfo(String tableName)
	{
		Vector<String> schemaInfo = null; 

		//get a row, read its metadata
		if(con != null)
		{
			logger.log(Level.INFO,  "Retriveing schema info for SQL table: " + tableName );

			Statement s = null;
			ResultSet rs = null;
			ResultSetMetaData metaData = null;
			try
			{
				//these values get wiped if we assign the resultset directly from the query
				s = con.createStatement
						(				
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_UPDATABLE
								);

				String query = "SELECT * FROM " + tableName + " limit 1";

				long queryStart = System.currentTimeMillis();
				s.executeQuery(query);
				long queryEnd = System.currentTimeMillis();

				rs = s.getResultSet();
				metaData = rs.getMetaData();

				schemaInfo = new Vector<String>();

				for(int j=1; j<= metaData.getColumnCount(); j++)
				{
					schemaInfo.add(metaData.getColumnTypeName(j));
				}

				logger.log(Level.INFO,  "Query time: " + (queryEnd - queryStart) + " ms");

			}
			catch (SQLException e)
			{
				e.printStackTrace();

				schemaInfo = new Vector<String>();
			}
			finally
			{
				if(rs != null) 
				{
					try
					{
						rs.close();
					} 
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}

				if(s != null) 
				{
					try
					{
						s.close();
					} 
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}

				//leave the connection open because we might have more queries to do
			}

		}
		else
		{
			//logger.log(Level.ERROR,  "Attempted to query a null SQL connection");
			logger.log(Level.ERROR, "Attempted to query a null SQL connection");
		}


		return schemaInfo;
	}

	public static void main(String[] args)
	{
		//jdbc:jtds:sqlserver://<hostname>[:<1433>]/<dbname>/catalogname]

		//String user = "PrivateLabelUser";
		//String pass = "plabel*1";
		//String url = "jdbc:jtds:sqlserver://ROCJFSDBS35:1433/PrivateLabel/PrivateLabel";

		String user = "PrivateLabelHeliosRead";
		String pass = "privlabr#$1";
		String url = "jdbc:jtds:sqlserver://ROCJFSDEV18:1433/";

		String driver = "net.sourceforge.jtds.jdbc.Driver";
		//"jdbc:jtds:sqlserver://localhost:1433/tempdb"

		String query = "SELECT * FROM Employees";

		RemoteConnection rc = null;
		try
		{

			rc = new RemoteConnection(url, user, pass, driver);
			if(rc.setupConnection())
			{
				Vector<String[]> results =  rc.runQuery(query);
				for(String[] rows : results)
				{
					for(String col : rows)
					{
						System.out.print(col + ",");
					}
					System.out.println();
				}
				//				
				//				System.out.println("Got rows: " + results.size());
				//				System.out.println("Got cols: " + results.get(0).length);

				//				long serTimeStart = System.currentTimeMillis();
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				rc.runQuery(query);
				//				long serTimeEnd = System.currentTimeMillis();
				//
				//				Vector<String> queries = new Vector<String>();
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);
				//				queries.add(query);

				//				long parTimeStart = System.currentTimeMillis(); 
				//				rc.runParallelQueries(queries);
				//				long parTimeEnd = System.currentTimeMillis();
				//
				//				System.out.println("Serial time: " + (serTimeEnd-serTimeStart) + " ms");
				//				System.out.println("Parallel time: " + (parTimeEnd-parTimeStart) + " ms" );

				//				for(String q : parResults.ketSet())
				//				{
				//					System.out.println(parResults.get(q).size());
				//				}
			}
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
			if(rc != null)
			{
				rc.close();
			}
		}

	}
}
