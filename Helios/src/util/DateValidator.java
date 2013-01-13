/**
 * 
 */
package util;

import java.util.GregorianCalendar;

/**
 * @author Jason Diamond
 *
 */
public class DateValidator
{

	private DateParser dateParser;

	/**
	 * 
	 */
	public DateValidator()
	{
		dateParser = new DateParser();
	}
	
	public boolean validateTimeInterval(String startDate, String endDate)
	{
		boolean retval = false;
		
		GregorianCalendar gregorianStartDate = null;
		GregorianCalendar gregorianEndDate= null;
		
		try
		{
			gregorianStartDate = dateParser.convertSQLDateToGregorian(startDate);
			gregorianEndDate = dateParser.convertSQLDateToGregorian(endDate);
		}
		finally
		{
			if( !(gregorianStartDate == null || gregorianEndDate == null ) && gregorianEndDate.after(gregorianStartDate))
			{
				retval = true;
			}
		}
		
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

}
