package database.connection.MSAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Vector;

import exceptions.DatabaseConnectionCreationException;
import exceptions.ReportSetupException;

/**
 * A command shell for read-interaction with an MS Access database. Commands are described by the "help" command. Supports session command history,
 * recalling past commands for execution, and specifying the database file to connect to. 
 * 
 * @author Jason Diamond
 *
 */
public class Shell
{
	private Vector<String> history;
	private FileConnection databaseConnection;
	private final static String DEFAULT_ACCESS_DB = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/ezCLM_Database_current.accdb";
	
	/**
	 * Connect to the database considered to be the default. Mostly for legacy reasons.
	 * 
	 * @throws ReportSetupException	If a database connection could not be established.
	 */
	public Shell() throws ReportSetupException
	{
		this(DEFAULT_ACCESS_DB);
	}
	
	/**
	 * Open a shell to the specified database file.
	 * 
	 * @param dbFile		The path of the database file to connect to.
	 * 
	 * @throws ReportSetupException	If a database connection could not be established.
	 */
	public Shell(String dbFile) throws ReportSetupException
	{
		history = new Vector<String>();
		databaseConnection = null;
		
		try
		{
			databaseConnection = new FileConnection(new File(dbFile).getAbsolutePath());
		} 
		catch (SecurityException e)
		{
			e.printStackTrace();
		} 
		catch (DatabaseConnectionCreationException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(databaseConnection == null)
			{
				throw new ReportSetupException("Database connection was null in final check.");
			}
		}
	}

	/**
	 * Close the shell and release its I/O resources.
	 */
	public void close()
	{		
		//logger is closed by databaseConnection
		if(databaseConnection != null)
		{
			databaseConnection.close();
		}
	}
	
	/**
	 * Run the command to determine the number of rows in a given table.
	 * 
	 * @param tableName		The table to count the rows for.
	 * 
	 * @return	A string representing the number of rows in a given table.
	 */
	private String rowsCommand(String tableName)
	{
		String output = "";
		
		if(tableName.equals(""))
		{
			output = "Invalid rows command";
		}
		else
		{
			output = "" +databaseConnection.getRowCount(tableName);
		}
		
		return output;
	}
	
	/**
	 * Run the help command to display command options.
	 * 
	 * @return	The help command output.
	 */
	private String helpCommand()
	{
		return "FailSQL query language\n" +
		"Command\tDescription\n" +
		"help\tPrint this help info\n" +
		"exit\tExit program\n" + 
		"show tables\tDisplay all table names in this database\n" + 
		"describe [tableName]\tPrint columns of tableName\n" +
		"rows [tableName]\t Print the row count of tableName\n" + 
		"schema [tableName]\t Print the columns and respective types of tableName\n" + 
		"dump [tableName\t Print the table contents\n" + 
		"SELECT <fields> FROM <tables> WHERE [conditions]\tRun specified query\n" + 
		"test (outputFile)\tFor each found table, select * from it and dump output to outputFile. Diskspace and time intensive\n";
	}
	
	/**
	 * Run the history command to display previously run commands. Recalled commands are stored by the actual recalled command.
	 * 
	 * @return	The shell command history.
	 */
	private String historyCommand()
	{
		StringBuilder output = new StringBuilder(history.size());
		
		for(int i = 0; i < history.size(); i ++)
		{
			output.append(i);
			output.append(":\t");
			output.append(history.get(i));
			output.append("\n");
		}
		
		return output.toString().trim();
	}
	
	/**
	 * Run the describe command to display the columns in a table.
	 * 
	 * @param tableName		The table name to retrieve columns from.
	 * 
	 * @return	The describe command output.
	 */
	private String describeCommand(String tableName)
	{
		String output = "";
		
		if(tableName.equals(""))
		{
			output = "Invalid describe command";
		}
		else
		{
			output = databaseConnection.describe(tableName);
		}
		
		return output;
	}

	/**
	 * Run a select command for queries. Commands are in the format SELECT field1,field2,... FROM table WHERE [conditions].
	 * 
	 * @param command	The command to run.
	 * 
	 * @return	The query's output.
	 */
	private String selectCommand(String command)
	{
		StringBuilder output = new StringBuilder();
		
		try
		{
			
			for(String[] s : databaseConnection.runQuery(command))
			{
				for(String t : s)
				{
					output.append(t+ ",");
				}
				output.append("\n");
			}
		}
		catch (NullPointerException e)
		{
			System.err.println("Query failed " + e.getMessage());
		}
	
		return output.toString().trim();
	}
	
	/**
	 * Run the schema command to display the columns in a table, and their respective types.
	 * 
	 * @param tableName		The table name to retrieve schema information for.
	 * 
	 * @return	The schema command output.
	 */
	private String schemaCommand(String tableName)
	{
		String output = "";
		
		if(tableName.equals(""))
		{
			output = "Invalid schema command";
		}
		else
		{
			output = databaseConnection.getSchema(tableName);
		}
		
		return output;
	}

	/**
	 * Run the test command to print out each table to a text file.
	 * 
	 * @param outputFile	The output file to print to.
	 */
	private void testCommand(String outputFile)
	{
		try
		{
			File file = new File(outputFile);
						
			if( !file.exists() && !file.createNewFile())
			{
				System.err.println("Could not create target file");
			}
			else if( file.canWrite())
			{
				System.out.println("I hope you have a lot of free disk space");

				String[] tables = databaseConnection.showTables().split("\n");



				FileOutputStream fout = null;
				PrintStream pstr = null;

				try
				{
					fout = new FileOutputStream(file);
					pstr = new PrintStream(fout);

					StringBuilder buffer = new StringBuilder();
					
					
					for(String table : tables)
					{

						buffer.append("TABLE NAME: " + table + 
								"\nTABLE COLUMNS: " + databaseConnection.describe(table) + 
						"\n------------------------------------------------\nOUTPUT:" );
						//System.out.println(buffer);

						for(String[] s : databaseConnection.runQuery("SELECT * FROM " + table))
						{
							for(String t : s)
							{
								buffer.append(t+ ",");
							}
							//tables can be huge, need to print each line

							buffer.append("\n");
						}

						buffer.append("===================\n");	
						pstr.println(	buffer.toString());
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(fout != null)
					{
						fout.close();
					}
					
					if(pstr != null)
					{
						pstr.close();
					}
				}
			}
			else
			{
				System.err.println("Cannot access databaseFile " + outputFile);
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Specify output databaseFile name");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Recall a command from the command history and run it. Store the recalled command in the command history.
	 * 
	 * @param command	The recall command itself in the form: ![number].
	 * 
	 * @return	The output of the called command.
	 */
	private String recallCommand(String command)
	{
		String output= "";
		
		int index = -1;
		try
		{
			String stringIndex = command.substring(1);
			
			if(stringIndex.equals("!"))
			{
				index = history.size() -1;
			}
			else
			{
				index = Integer.parseInt(stringIndex);
			}

			if(index > 0 && index < history.size())
			{
				System.out.println(history.get(index));
				output = runCommand(history.get(index));
			}
			else
			{
				System.err.println("Error: Recall index does not exist");
			}
		}
		catch(NumberFormatException e)
		{
			System.err.println("Error:  Invalid recall index");
		}
		
		return output;
	}
	
	
	
	/**
	 * Return the full table name in a command, which may contain arbitrary number of spaces.
	 * 
	 * @param commandString		The full command. 
	 * 
	 * @return	The full table name.
	 */
	private String getFullTableName(String commandString)
	{
		//StringBuilder tableNameBuilder = new StringBuilder();
		
		//ignore the rows word, eat the rest of the table
//		for(String word : commandString.substring(command[0].length()).split("\\s+"))
//		{
//			tableNameBuilder.append(word);
//			tableNameBuilder.append(" ");
//		}
		
		
	
		//return tableNameBuilder.toString().trim();
		
		String retval = "";
		
		try
		{
			String [] command = commandString.split("\\s+");
			
			retval = commandString.substring(command[0].length() + 1);
		}
		catch(StringIndexOutOfBoundsException e)
		{
			//do nothing, will return empty string
		}
		
		return retval;
	}
	
	/**
	 * Run a command and return it's output. Stores the command in the session's command history.
	 * 
	 * @param commandString		The command to run.
	 * 
	 * @return The output of the command.
	 */
	public String runCommand(String commandString)
	{
		String[] command = null;

		command = commandString.split("\\s+");

		String output = "";
		
		if(command[0].equals("help"))
		{
			output = helpCommand();
		}
		else if(command[0].equals("show") && command[1].equals("tables"))
		{
			output = databaseConnection.showTables();
		}
		else if(command[0].equals("describe") )
		{
			output = describeCommand(getFullTableName(commandString));
		}
		else if(command[0].equals("rows"))
		{
			output = rowsCommand(getFullTableName(commandString));
		}
		else if(command[0].equals("schema"))
		{
			output = schemaCommand(getFullTableName(commandString));
		}
		else if(command[0].equals("test") )
		{
			try
			{
				//no print, this writes to a file
				testCommand(command[1]);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.err.println("Error: test output file omitted");
			}
		}
		else if(command[0].equals("dump"))
		{
			output = databaseConnection.printTable(command[1]);
		}
		else if(command[0].equals("SELECT"))
		{
			output = selectCommand(commandString);
		}
		else if(commandString.equals("history"))
		{
			output = historyCommand();
		}
		else if(commandString.startsWith("!"))
		{
			output = recallCommand(commandString);
		}
		else if( !command[0].equals(""))
		{
			output =  "Error: Unrecognized command" ;
		}

		
		if(!commandString.equals("") && !commandString.startsWith("!"))
		{
			history.add(commandString);
		}
		
		return output;
	}	
	
	public static void main(String[] args)
	{
		Shell shell = null;
		InputStreamReader stream = null;
		BufferedReader dataIn =  null;
		
		try 
		{
			
			
			stream = new InputStreamReader(System.in);
			dataIn = new BufferedReader(stream);
			
			if(args.length >= 1 &&  args[0].equals("-e"))
			{
				
				//execute stuff
				
//				for(String[] s :database.connection.runQuery("SELECT * FROM Employees"))
//				{
//					for(String t : s)
//					{
//						System.out.print(t+ ",");
//					}
//					System.out.println();
//				}
			}
			else
			{
				
				if(args.length >= 1 && new File(args[0]).exists() )
				{
					shell = new Shell(args[0]);
				}
				else
				{
					shell = new Shell();
				}
			
				boolean done = false;
				String commandString;
				while(!done)
				{
					System.out.print("% ");

					try
					{
						commandString = dataIn.readLine();
						if(commandString.equals("exit") || commandString.equals("q")  )
						{
							done = true;
						}
						else if(!commandString.equals(""))
						{
							System.out.println(shell.runCommand(commandString));
						}
					}
					catch(NullPointerException e)
					{
						//probably malformed or something
						continue;
					}
				}
			}
		} 
		catch (SecurityException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Exiting");
			
			if(shell != null)
			{
				shell.close();
			}
			
			try
			{
				if(stream != null)
				{
					stream.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				if(dataIn != null)
				{
					dataIn.close();
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
