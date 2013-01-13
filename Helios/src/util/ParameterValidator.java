/**
 * 
 */
package util;

import java.util.GregorianCalendar;

/**
 * @author jdiamond
 *
 */
public class ParameterValidator
{
	public static boolean validateTimeGrain(int timeGrain, int[] validTypes)
	{
		boolean retval = false;
		
		for(int type : validTypes)
		{
			if(type == timeGrain)
			{
				retval = true;
				break;
			}
		}
		
		return retval;
	}

	public static boolean validateTimeInterval(String startDate, String endDate)
	{
		boolean retval = false;

		DateParser dateParser = new DateParser();

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
	
	public static boolean validateReportType(int reportType, int[] validTypes)
	{
		boolean retval = false;
		
		for(int type : validTypes)
		{
			if(type == reportType)
			{
				retval = true;
				break;
			}
		}
		
		return retval;
	}

	public static boolean validateDateInterval(String startDate, String endDate)
	{
		DateValidator dateValidator = new DateValidator();
		DateParser dateParser = new DateParser();
		
		return
		(
			dateValidator.validateTimeInterval(startDate, endDate) && 
			(startDate = dateParser.readableGregorian(dateParser.convertSQLDateToGregorian(startDate))) != null &&
			(endDate = dateParser.readableGregorian(dateParser.convertSQLDateToGregorian(endDate))) != null
		);

	}

}
