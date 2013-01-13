/**
 * 
 */
package forecast;

import java.util.Vector;


/**
 * @author Jason Diamond
 *
 */
public abstract class Regression
{
	private Vector<String[]> reportData;
	private final static String HELIOS_CLASSPATH = "helios.SQL.";
	private Class<?> reportClass;
	
	/**
	 * Accept a report and parameters to run, then apply an extrapolation method
	 * @throws ClassNotFoundException 
	 */
	public Regression(String reportName) throws ClassNotFoundException
	{
		
		//take a class name, find it and initialize it, then find a way to pass it the right params
		
		reportClass = Class.forName(HELIOS_CLASSPATH + reportName );
		
		//reportClass.getMethod("runReport", parameterTypes)
		
		reportClass.toString();
		
	}
	
	public abstract Vector<String[]> runForcast();

}
