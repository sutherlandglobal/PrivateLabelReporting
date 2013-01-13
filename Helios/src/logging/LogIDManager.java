/**
 * 
 */
package logging;


/**
 * @author jdiamond
 *
 */
public class LogIDManager 
{
	private LogIDManager(){}
	
	public static String getLogID()
	{
		//avoid the oddly-changing value
		return new Long(System.currentTimeMillis() / 1000L).toString();
	}
}
