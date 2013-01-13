package constants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import exceptions.InvalidConstantException;


/**
 * A singleton for managing and accessing constants global to the Helios Framework.
 * 
 * @author Jason Diamond
 *
 */
public final class Constants 
{
	private final static String constantsFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/conf/helios.conf";
	private static Constants thisInstance;
	private final HashMap<String,String> constants;
	private final static String commentDelim = "#";
	private final static String fieldSeperator = "=";
	
	/**
	 * Hash up key -> value pairings from a config file.
	 * @throws FileNotFoundException 
	 * 
	 */
	private Constants() throws FileNotFoundException
	{
		//load from file
		constants = new HashMap<String,String>();
		
		BufferedReader dataIn = null;
		FileInputStream fin = null;
		InputStreamReader inReader = null;
		
		
		try 
		{
			fin = new FileInputStream(constantsFile);
			inReader = new InputStreamReader(fin);
			dataIn = new BufferedReader(inReader);
			
			String line; 
			while((line = dataIn.readLine() ) != null)
			{
				if(!line.startsWith(commentDelim) && !line.trim().equals(""))
				{
					//System.out.println(line);
					
					//split string 
					String[] fields = line.split(fieldSeperator);
					
					//need to tolerate greater variety in values over keys
					//fields[1..last] is value
					//fields[0] is key
					
					String key = fields[0];
					StringBuilder val = new StringBuilder();
					
					for(int i = 1; i<fields.length; i++)
					{
						val.append(fields[i]);
					}
					
					//System.out.println("key: " + key + " val: " + val);
					
					constants.put(key, val.toString());
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			
			throw e;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(fin != null)
				{
					fin.close();
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try 
			{
				if(inReader != null)
				{
					inReader.close();
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
	};
	
	
	
	/**
	 * Return the Constants object, initializing if necessary.
	 * 
	 * @return	Constants object to access hash.
	 */
	public static Constants getInstance()
	{
		if(thisInstance == null)
		{
			try
			{
				thisInstance = new Constants();
			} 
			catch (FileNotFoundException e)
			{
				//nothing, and return null
			}
		}
		
		return thisInstance;
	}
	
	/**
	 * Query the known constants and return the discovered value.
	 * 
	 * @param key		Key value to use in hash lookup.
	 * @return	Value bound to key.
	 * @throws InvalidConstantException		If key does not exist as a known constant.
	 */
	public String get(String key) throws InvalidConstantException
	{
		if(!constants.containsKey(key))
		{
			throw new InvalidConstantException("Invalid constant lookup: " + key);
		}
		
		return constants.get(key);
	}
	
}
