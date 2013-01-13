/**
 * 
 */
package test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;

import database.connection.MSAccess.FileConnection;
import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * Proof of concept class for testing database modification with Jackcess.
 * 
 * @author Jason Diamond
 *
 */
public class MSAccessDBWriteTester
{
	private FileConnection dbConnection;
	private Database db;

	/**
	 * Build the database that we will write to later.
	 * 
	 * @param dbName		The location of the MS Access database.
	 * 
	 * @throws ReportSetupException	If a database connection could not be established.
	 * @throws DatabaseConnectionCreationException 
	 */
	public MSAccessDBWriteTester(String dbName ) throws DatabaseConnectionCreationException
	{
		dbConnection = new FileConnection(dbName);
		try
		{
			db = Database.open(new File(dbName));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Close the database connection.
	 */
	public void close()
	{
		dbConnection.close();
		
		try
		{
			db.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Accessor for the FileConnection built over the Jackcess Database.
	 * 
	 * @return 	The FileConnection object built over the database.
	 */
	public FileConnection get()
	{
		return dbConnection;
	}
	
	/**
	 * Accessor for the Jackcess Database directly.
	 * 
	 * @return	The Jackcess Database.
	 */
	public Database getDB()
	{
		return db;
	}
	
	public static void main(String[] args)
	{
		
		String fileName = "ezCLM_Database_current.accdb";
		
		MSAccessDBWriteTester w = null;
		try
		{
			w = new MSAccessDBWriteTester(fileName);
			
			File f = new File(fileName);
			
			System.out.println("Last modified: " + f.lastModified());
			
			System.out.println(w.get().showTables());
			
			String tableName = "Cases Version 2";
			
			System.out.println(w.get().describe(tableName));
			for(Column c : w.getDB().getTable(tableName).getColumns())
			{
				System.out.print(c.getSQLType() + ",");
			}
			System.out.println();
			
			for(Column c : w.getDB().getTable(tableName).getColumns())
			{
				System.out.print(c.getType().name() + ",");
			}
			System.out.println();
			
			System.out.println("rows before: " + w.get().getRowCount(tableName));
			
//			Skillset,Description,Type,VDN,
//			4,12,12,12,
//			LONG,TEXT,TEXT,TEXT,
//			1247,Clearwire Tech,N/A,462292,
			//Object newRow = new Object[]{ Long.parseLong("125"), "Herp Derp Tech", "N/a", "13337"};
			
			//when parsing, for each row create new Object[colcount of table] and loop init each element
			//add to list, then call addRows here
			
			Object newRow = new Object[]{};
			
			w.getDB().getTable(tableName).addRow(newRow);
			//w.getDB().getTable(tableName).addRow("125", "Herp Derp Tech", "N/a", "13337");
			
			System.out.println("rows after: " + w.get().getRowCount(tableName));
			
			for(String[] row : w.get().runQuery("SELECT * FROM " + tableName))
			{
				for(String col : row)
				{
					System.out.print(col + ",");
				}
				System.out.println();
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		} 
		catch (DatabaseConnectionCreationException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if(w != null)
			{
				w.close();
			}
		}
	}

}
