/**
 * 
 */
package database.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author jdiamond
 *
 */
public abstract class DatabaseConnectionFactory 
{

	//hash of db params
	protected HashMap<String, String> parameters;
	protected String[] requiredParameters = null;
	
	/**
	 * 
	 */
	public DatabaseConnectionFactory() 
	{
		parameters = new HashMap<String, String>();
	}
	
	public void load(String propertiesFile)
	{
		Properties dbProperties = new Properties();
		
		FileInputStream fin = null;
		
		//fail sort of silently, the dbconnection owner can handle the error, parent logger can handle the output
		try
		{
			fin = new FileInputStream(propertiesFile);
			
			dbProperties.load(fin);
		
			load(dbProperties);
		}
		catch (FileNotFoundException e) 
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			System.err.println(e.getMessage());
			e.printStackTrace();	
		}
		finally
		{
			if(fin != null)
			{
				try 
				{
					fin.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void load(Properties properties)
	{
		for(Entry<Object, Object> property  : properties.entrySet())
		{
			parameters.put(property.getKey().toString(), property.getValue().toString());
		}
	}
	
	public void setParameter(String key, String val)
	{
		parameters.put(key, val);
	}
	
	protected boolean hasValidParams()
	{
		boolean retval = true;
		
		for(String param : requiredParameters)
		{
			if(!parameters.containsKey(param))
			{
				retval = false;
				break;
			}
		}
		
		return retval;
	}
	
	protected abstract DatabaseConnection getConnection() throws Exception;

	
	
}
