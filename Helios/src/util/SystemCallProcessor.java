package util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * System calls in java aren't as neat as in a lot of scripting languages. This class manages the Framework <-> System interface and makes system calls a lot cleaner.
 * 
 * @author Jason Diamond
 *
 */
public class SystemCallProcessor 
{
	/**
	 * 
	 */
	private SystemCallProcessor()
	{}
		
	/**
	 * Run a system call and retrieve it's output from STDOUT. I recommend using full paths, and redirecting STDERR to STDOUT if necessary.
	 * 
	 * @param call		The system call to run.
	 * @return	The output of the system call.
	 */
	public static String runAndGetOutput(String call)
	{
		String retval = null;
		
		//xls2csv of file
		Process p;
		InputStreamReader inputStreamReader = null;
		BufferedInputStream bufferedInputStream = null;
		BufferedReader bufferedReader = null;
		try 
		{
			p = Runtime.getRuntime().exec(call);
			
			bufferedInputStream = new BufferedInputStream(p.getInputStream());
			inputStreamReader = new InputStreamReader(bufferedInputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			// Read the output
			String line;
			StringBuilder output = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) 
			{
				output.append(line);
				output.append("\n");
			}
			
			//only good if we read all of the file
			retval = output.toString().trim();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if(bufferedInputStream != null)
				{
					bufferedInputStream.close();
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			try 
			{
				if(inputStreamReader != null)
				{
					inputStreamReader.close();
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			try 
			{
				if(bufferedReader != null)
				{
					bufferedReader.close();
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		return retval;
	}
	
	
	/**
	 *  Run a system call and retrieve it's exit value. A bit subjective, but nice to have and easy to implement. 
	 * 
	 * @param call		The system call to run.
	 * 
	 * @return	The return value of the system call.
	 */
	public static int runAndGetExitValue(String call)
	{
		int retval = -99;
		
		Process p;
		try 
		{
			p = Runtime.getRuntime().exec(call);
			retval = p.waitFor();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		return retval;
	}
	



}
