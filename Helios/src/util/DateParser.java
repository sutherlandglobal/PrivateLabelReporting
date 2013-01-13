/**
 * 
 */
package util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Library for any date manipulation, conversion, and calculation. Any database work with a lot of reporting on date granularity will have plenty of this.
 * 
 * @author Jason Diamond
 */
public class DateParser 
{

	private HashMap<String,Integer> monthNameToValue;
	private HashMap<Integer,String> valueToMonthName;

	private HashMap<Integer,String> valueToDOW;

	public static final int YEARLY_GRANULARITY = 0;
	public static final int MONTHLY_GRANULARITY = 1;
	public static final int WEEKLY_GRANULARITY = 2;
	public static final int DAILY_GRANULARITY = 3;
	public static final int HOURLY_GRANULARITY = 4;

	//private final int MONTH_START = 1;
	//private final int MONTH_END = 12;

	/**
	 * 	Build the date parser, and load up hashes for the easy Month <-> Month number associations.
	 */
	public DateParser() 
	{
		monthNameToValue = new HashMap<String,Integer>();
		valueToMonthName = new HashMap<Integer,String>();
		valueToDOW = new HashMap<Integer,String>();

		monthNameToValue.put("Jan", 1);
		monthNameToValue.put("Feb", 2);
		monthNameToValue.put("Mar", 3);
		monthNameToValue.put("Apr", 4);
		monthNameToValue.put("May", 5);
		monthNameToValue.put("Jun", 6);
		monthNameToValue.put("Jul", 7);
		monthNameToValue.put("Aug", 8);
		monthNameToValue.put("Sep", 9);
		monthNameToValue.put("Oct", 10);
		monthNameToValue.put("Nov", 11);
		monthNameToValue.put("Dec", 12);

		valueToDOW.put(1, "Sun");
		valueToDOW.put(2, "Mon");
		valueToDOW.put(3, "Tue");
		valueToDOW.put(4, "Wed");
		valueToDOW.put(5, "Thu");
		valueToDOW.put(6, "Fri");
		valueToDOW.put(7, "Sat");

		for(String monthName : monthNameToValue.keySet())
		{
			valueToMonthName.put(monthNameToValue.get(monthName), monthName);
		}

	}

	/**
	 * Determine the date grain for the provided date's day, in the form of YYYY-MM-DD. 
	 * 
	 * @param date	The date to determine the grain for.
	 * 
	 * @return	The day granularity.
	 */
	private String getDayGrain(GregorianCalendar date)
	{
		//YYYY-MM
		String dayGrain = getMonthGrain(date) + "-";

		if(date.get(Calendar.DAY_OF_MONTH) < 10)
		{
			dayGrain += "0";
		}

		dayGrain += date.get(Calendar.DAY_OF_MONTH);

		return dayGrain;
	}

	/**
	 * Determine the date grain for the provided date's month, in the form of YYYY-MM. 
	 * 
	 * @param date	The date to determine the grain for.
	 * 
	 * @return	The month granularity.
	 */
	private String getMonthGrain(GregorianCalendar date)
	{
		//YYYY-MM
		String monthGrain = ""+ date.get(Calendar.YEAR) + "-";

		if(date.get(Calendar.MONTH)+1 < 10)
		{
			monthGrain += "0";
		}
		monthGrain += (date.get(Calendar.MONTH)+1);

		return monthGrain;
	}

	/**
	 * Determine the date grain for the provided date's week, in the form of YYYY-MM-WW. 
	 * 
	 * @param date	The date to determine the grain for.
	 * 
	 * @return	The week granularity.
	 */
	private String getWeekGrain(GregorianCalendar date)
	{
		//YYYY-MM-W?W

		//probably never a week number >= 10, but w/e
		String weekGrain = getMonthGrain(date) + "-";
		if( date.get(Calendar.WEEK_OF_MONTH) < 10)
		{
			weekGrain += "0";

		}
		weekGrain += date.get(Calendar.WEEK_OF_MONTH); 

		return weekGrain;
	}

	/**
	 * Determine the date grain for the provided date's year, in the form of YYYY. 
	 * 
	 * @param date	The date to determine the grain for.
	 * 
	 * @return	The year granularity.
	 */
	private String getYearGrain(GregorianCalendar date)
	{
		//YYYY-MM-W?W

		//probably never a year number >= 10, but w/e
		String yearGrain = getMonthGrain(date) + "-";
		if( date.get(Calendar.YEAR) < 10)
		{
			yearGrain += "0";

		}
		yearGrain += date.get(Calendar.YEAR); 

		return yearGrain;
	}
	
	/**
	 * Determine the date grain for the provided date's Hour, in the form of YYYY-MM-DD_HH:00:00. 
	 * 
	 * @param date	The date to determine the grain for.
	 * 
	 * @return	The Hour granularity.
	 */
	private String getHourGrain(GregorianCalendar date)
	{
		//YYYY-MM-W?W

		String hourGrain = getDayGrain(date) + "_";
		if( date.get(Calendar.HOUR_OF_DAY) < 10)
		{
			hourGrain += "0";

		}
		hourGrain += date.get(Calendar.HOUR_OF_DAY) + ":00:00"; 

		return hourGrain;
	}

	public String getDateGrain(int dateGrain, GregorianCalendar date)
	{
		String retval = null;

		if(dateGrain == YEARLY_GRANULARITY )
		{
			retval = getYearGrain(date);
		}
		else if(dateGrain == WEEKLY_GRANULARITY )
		{
			retval = getWeekGrain(date);
		}
		else if(dateGrain == DAILY_GRANULARITY)
		{
			retval = getDayGrain(date);
		}
		else if(dateGrain == HOURLY_GRANULARITY)
		{
			retval = getHourGrain(date);
		}
		else 
		{
			retval = getMonthGrain(date);
		}


		return retval;
	}

	/**
	 * Retrieve the Month name <-> Month index bindings.
	 * 
	 * @param month	Either an integer to lookup the month with, or a string to lookup the index with.
	 * 
	 * @return	The month index corresponding to the month name, or vice versa depending on the actual type of the month parameter. 
	 */
	public String monthLookup(String month)
	{
		String retval = "";
		try
		{
			retval = valueToMonthName.get(Integer.parseInt(month));
		}
		catch (NumberFormatException e)
		{
			retval = monthNameToValue.get(month).toString();
		}
		finally
		{
			if (retval.equals(""))
			{
				System.err.println("Error looking up " + month + " in month to index mappings");
			}
		}

		return retval;
	}

	/**
	 * Determine the distance in minutes between two dates. Start and end are interchangable since this is essentially a distance measurement.	
	 * 
	 * @param startDate	Demarcation 1 of the interval.
	 * @param endDate		Demarcation 2 of the interval.
	 * 
	 * @return 	The distance in minutes between two dates.	
	 */
	public double  getMinutesBetween(String startDate, String endDate)
	{
		return  getMinutesBetween(convertMSAccessDateToGregorian(startDate), convertMSAccessDateToGregorian( endDate));
	}

	/**
	 * Determine the distance in minutes between two dates. Start and end are interchangable since this is essentially a distance measurement.	
	 * 
	 * @param startDate	Demarcation 1 of the interval.
	 * @param endDate		Demarcation 2 of the interval.
	 * 
	 * @return 	The distance in minutes between two dates.	
	 */
	public double getMinutesBetween(GregorianCalendar startDate, GregorianCalendar endDate)
	{

		double retval = -1L;

		if(startDate != null && endDate != null)
		{
			if(startDate.equals(endDate))
			{
				retval = 0L;
			}
			else if(startDate.before(endDate))
			{
				retval = (endDate.getTime().getTime() - startDate.getTime().getTime());

				//retval = Math.abs(retval);
			}
			else
			{
				//				GregorianCalendar g1 = new GregorianCalendar();
				//				GregorianCalendar g2 = new GregorianCalendar();
				//				g1.setTimeInMillis(startDate.getTime().getTime());
				//				g2.setTimeInMillis(endDate.getTime().getTime());
				//				System.out.println
				//				(
				//						startDate + ": " +startDate.getTime().getTime() + " " + g1.toString()
				//				);
				//				System.out.println
				//				(
				//						endDate + ": " +endDate.getTime().getTime() + " " + g2.toString()
				//				);

				retval = ( startDate.getTime().getTime() - endDate.getTime().getTime() );

			}
			retval /= 60000; //ms -> s, s-> m
		}


		return retval;
	}

	/**
	 * Format a GregorianCalendar into something more readable, specifically in the form "YYYY-MM-DD HH:MM:SS".
	 * 
	 * @param date	The date to convert.
	 * 
	 * @return	A string representing a GregorianCalendar.
	 */
	public String readableGregorian(GregorianCalendar date)
	{
		StringBuilder retval = new StringBuilder();
		//		System.out.print("year: " +retval.get(Calendar.YEAR));
		//		System.out.print(", mon: "+retval.get(Calendar.MONTH));
		//		System.out.print(", day: " +retval.get(Calendar.DAY_OF_MONTH));
		//		System.out.print(", hour: "+retval.get(Calendar.HOUR_OF_DAY));
		//		System.out.print(", min: " +retval.get(Calendar.MINUTE));
		//		System.out.println(", sec: "+retval.get(Calendar.SECOND));

		retval.append(date.get(Calendar.YEAR));
		retval.append("-");

		if(date.get(Calendar.MONTH)+1 < 10)
		{
			retval.append("0");
		}
		retval.append(date.get(Calendar.MONTH)+1);
		retval.append("-");

		if(date.get(Calendar.DAY_OF_MONTH) < 10) 
		{
			retval.append("0");
		}
		retval.append(date.get(Calendar.DAY_OF_MONTH));
		retval.append(" ");

		if(date.get(Calendar.HOUR_OF_DAY) < 10) 
		{
			retval.append("0");
		}
		retval.append(date.get(Calendar.HOUR_OF_DAY));
		retval.append(":");

		if(date.get(Calendar.MINUTE) < 10) 
		{
			retval.append("0");
		}
		retval.append(date.get(Calendar.MINUTE));
		retval.append(":");


		if(date.get(Calendar.SECOND) < 10) 
		{
			retval.append("0");
		}
		retval.append(date.get(Calendar.SECOND));

		return retval.toString();
	}

	/**
	 * Convert an MS Access date (Mon Oct 25 14:36:00 EDT 2010) to a GregorianCalendar date.
	 * 
	 * @param date	The date string to convert.
	 * 
	 * @return	The converted date, or null if the conversion failed.
	 */
	public GregorianCalendar convertMSAccessDateToGregorian(String date)
	{

		//dates in YYYY-MM-DD HH:MM:SS
		//Wed Nov 03 21:35:00 EDT 2010
		//
		//date is [1]-[2]-[5] [3] from splt

		//System.out.println("Converting " + date); 

		String[] dateComponents= null;
		String[] dateFields = null;
		String[] timeFields= null;

		GregorianCalendar retval = null;

		try
		{
			dateComponents = date.split(" ");
			dateFields = new String[]{dateComponents[5], monthLookup(dateComponents[1]), dateComponents[2]};
			timeFields = dateComponents[3].split(":");

			//			System.out.println("===========");
			//			
			//			System.out.print("year: " +dateFields[0]);
			//			System.out.print(", mon: " +dateFields[1]);
			//			System.out.print(", day: " +dateFields[2]);
			//			System.out.print(", hour: " +timeFields[0]);
			//			System.out.print(", min: " +timeFields[1]);
			//			System.out.println(", sec: " +timeFields[2]);


			//cheers to dishonest javadocs
			//0-based stupid


			retval = new GregorianCalendar
			(
					Integer.parseInt(dateFields[0]),
					Integer.parseInt(dateFields[1]) -1,
					Integer.parseInt(dateFields[2]),
					Integer.parseInt(timeFields[0]),
					Integer.parseInt(timeFields[1]),
					(int) Double.parseDouble(timeFields[2])	//truncate tenths of a second in mysql timestamps
			);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Error converting " + date);
		}
		catch(NullPointerException e)
		{
			System.err.println("Error converting " + date);
		}


		return retval;

		//		int year = Integer.parseInt(dateFields[0]);
		//int mon = Integer.parseInt(dateFields[1]) -1;
		//		int day = Integer.parseInt(dateFields[2]);
		//		int hour = Integer.parseInt(timeFields[0]);
		//		int min = Integer.parseInt(timeFields[1]);
		//		int sec =  (int) Double.parseDouble(timeFields[2]);
		//		
		//		System.out.print("year: " +year);
		//		System.out.print(", mon: " +mon);
		//		System.out.print(", day: " +day);
		//		System.out.print(", hour: " +hour);
		//		System.out.print(", min: " +min);
		//		System.out.println(", sec: " +sec);

		//cheers to dishonest javadocs
		//0-based stupid
		//		GregorianCalendar retval = new GregorianCalendar();
		//		retval.set(Calendar.YEAR, Integer.parseInt(dateFields[0]));
		//		retval.set(Calendar.MONTH, mon);
		//		retval.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateFields[2]));
		//		retval.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeFields[0]));
		//		retval.set(Calendar.MINUTE, Integer.parseInt(timeFields[1]));
		//		retval.set(Calendar.SECOND, (int) Double.parseDouble(timeFields[2]));

		//		retval.set(Calendar.YEAR, year);
		//		retval.set(Calendar.MONTH, mon);
		//		retval.set(Calendar.DAY_OF_MONTH, day);
		//		retval.set(Calendar.HOUR_OF_DAY, hour);
		//		retval.set(Calendar.MINUTE, min);
		//		retval.set(Calendar.SECOND, sec);

		//		System.out.print("year: " +retval.get(Calendar.YEAR));
		//		System.out.print(", mon: "+retval.get(Calendar.MONTH));
		//		System.out.print(", day: " +retval.get(Calendar.DAY_OF_MONTH));
		//		System.out.print(", hour: "+retval.get(Calendar.HOUR_OF_DAY));
		//		System.out.print(", min: " +retval.get(Calendar.MINUTE));
		//		System.out.println(", sec: "+retval.get(Calendar.SECOND));
		//		
		//		
		//		System.out.println("===========");

		//return retval;
	}

	/**
	 * Convert an SQL date (2010-06-27 00:00:00) to a GregorianCalendar date.
	 * 
	 * @param date	The date string to convert.
	 * 
	 * @return	The converted date, or null if the conversion failed.
	 */
	public GregorianCalendar convertSQLDateToGregorian(String date)
	{
		//dates in YYYY-MM-DD HH:MM:SS

		GregorianCalendar retval = null;

		if(!date.equalsIgnoreCase("now"))
		{
				String[] dateComponents = date.split(" ");
				String[] dateFields = dateComponents[0].split("-");
				String[] timeFields = dateComponents[1].split(":");

				retval = new GregorianCalendar
				(
						Integer.parseInt(dateFields[0]),
						Integer.parseInt(dateFields[1]) -1,
						Integer.parseInt(dateFields[2]),
						Integer.parseInt(timeFields[0]),
						Integer.parseInt(timeFields[1]),
						(int) Double.parseDouble(timeFields[2])	//truncate tenths of a second in mysql timestamps
				);
		}
		else
		{
			retval = new GregorianCalendar();
		}

		//		System.out.println("===========");
		//		
		//		System.out.print("year: " +dateFields[0]);
		//		System.out.print(", mon: " +dateFields[1]);
		//		System.out.print(", day: " +dateFields[2]);
		//		System.out.print(", hour: " +timeFields[0]);
		//		System.out.print(", min: " +timeFields[1]);
		//		System.out.println(", sec: " +timeFields[2]);

		//cheers to dishonest javadocs
		//0-based stupid
		return retval;
	}

	/**
	 * Convert an Excel date to an MSAccess date.
	 * 
	 * @param date	The date string to convert.
	 * 
	 * @return	The converted date, or null if the conversion failed.
	 */
	public String convertExcelDateToMSAccessDate(String date)
	{
		//convert formats: 11/10/2010 12:01:34 AM to Sat Jan 08 23:39:00 EST 2011

		String retval = null;
		try
		{
			//System.out.println(date);

			String[] fields = date.split("\\s");
			String fullDate = fields[0];
			String fullTime = fields[1];
			String[] dateFields = fullDate.split("\\/");
			String[] timeFields = fullTime.split("\\:");

			int hour = Integer.parseInt(timeFields[0]);
			String min = timeFields[1];
			String sec = timeFields[2];

			String mon = dateFields[0];
			String day = dateFields[1];
			String year = dateFields[2];

			GregorianCalendar shortDate = new GregorianCalendar();

			//System.out.println(readableGregorian(shortDate));

			shortDate.set(Calendar.YEAR, Integer.parseInt(year));
			shortDate.set(Calendar.MONTH, Integer.parseInt(mon)-1);
			shortDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

			String dow = valueToDOW.get(shortDate.get(Calendar.DAY_OF_WEEK));

			//System.out.println(readableGregorian(shortDate));

			//int ampm= Calendar.PM;

			if(fields[2].equals("AM"))
			{
				hour -= 12;
				//ampm = Calendar.AM;
			}
			else if(hour < 12)
			{
				hour += 12;
				hour %= 24;
			}
			//shortDate.set(Calendar.HOUR, hour);
			//shortDate.set(Calendar.AM_PM, ampm);
			//shortDate.set(Calendar.MINUTE, Integer.parseInt(min));
			//shortDate.set(Calendar.SECOND, Integer.parseInt(sec));

			//System.out.println(readableGregorian(shortDate));
			//			
			//			hour ="" + shortDate.get(Calendar.HOUR_OF_DAY);
			//			
			//			
			//			
			String hourStr = "" + hour;
			if(hour < 10)
			{
				hourStr = "0" + hour;
			}
			//			
			if(day.length() == 1)
			{
				day = "0" + day;
			}

			if(sec.length() == 1)
			{
				sec = "0" + sec;
			}

			if(min.length() == 1)
			{
				min = "0" + min;
			}

			retval  =  dow + " " +
			//monthLookup(""+shortDate.get(Calendar.MONTH)) + " " +
			monthLookup(mon) + " " +
			day + " " +
			hourStr + ":" + 
			min + ":" + 
			sec + " " +
			shortDate.getTimeZone().getDisplayName(false, TimeZone.SHORT) + " " +
			year;

		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}

		return retval;
	}

	/**
	 * Convert an Excel date to an GregorianCalendar date.
	 * 
	 * @param date	The date string to convert.
	 * 
	 * @return	The converted date, or null if the conversion failed.
	 */
	public GregorianCalendar convertExcelDateToGregorian(String date)
	{
		String[] fields = date.split("\\s");
		String fullDate = fields[0];
		String fullTime = fields[1];
		String[] dateFields = fullDate.split("\\/");
		String[] timeFields = fullTime.split("\\:");

		//System.out.println(date);

		int hour = Integer.parseInt(timeFields[0]);
		int min = Integer.parseInt(timeFields[1]);
		int sec =Integer.parseInt(timeFields[2]);

		int mon = Integer.parseInt(dateFields[0]);
		int day = Integer.parseInt(dateFields[1]);
		int year = Integer.parseInt(dateFields[2]);

		if(fields[2].equals("AM"))
		{
			hour -= 12;
			//ampm = Calendar.AM;
		}
		else if(hour < 12)
		{
			hour += 12;
			hour %= 24;
		}

		GregorianCalendar retval = new GregorianCalendar();

		retval.set(year, mon - 1, day, hour, min, sec);

		return retval;
	}


	public static void main(String[] args) 
	{
		DateParser d = new DateParser();
		//		System.out.println(
		//				d.readableGregorian(d.convertMSAccessDateToGregorian("Mon Oct 25 14:36:00 EDT 2010"))
		//		);
		//		System.out.println();
		//		System.out.println
		//		(
		//			d.readableGregorian(d.convertMySQLDateToGregorian("2010-06-27 00:00:00"))	
		//		);
		//		System.out.println();
		//		System.out.println(d.convertExcelDateToMSAccessDate("11/10/2011 12:01:34 AM"));
		//		System.out.println();
		//		System.out.println(d.convertExcelDateToMSAccessDate("11/10/2011 12:01:34 PM"));
		//		System.out.println();
		//		System.out.println(d.convertExcelDateToMSAccessDate("12/10/2011 12:01:34 PM"));
		//		System.out.println();
		//		System.out.println(d.convertExcelDateToMSAccessDate("01/10/2011 1:01:34 PM"));
		//		System.out.println();
		//		System.out.println(d.convertExcelDateToMSAccessDate("04/10/2011 11:01:34 PM"));
		//		System.out.println();
		System.out.println(d.readableGregorian(d.convertExcelDateToGregorian("11/10/2011 12:01:34 AM")));
		System.out.println();
		System.out.println(d.readableGregorian(d.convertExcelDateToGregorian("11/10/2011 12:01:34 PM")));
		System.out.println();
		System.out.println(d.readableGregorian(d.convertExcelDateToGregorian("12/10/2011 12:01:34 PM")));
		System.out.println();
		System.out.println(d.readableGregorian(d.convertExcelDateToGregorian("01/10/2011 1:01:34 PM")));
		System.out.println();
		System.out.println(d.readableGregorian(d.convertExcelDateToGregorian("04/10/2011 11:01:34 PM")));
		
		System.out.println(d.getDateGrain(HOURLY_GRANULARITY, new GregorianCalendar()));
		
		System.out.println(d.getMinutesBetween(d.convertExcelDateToGregorian("04/10/2011 11:01:34 PM"), d.convertExcelDateToGregorian("11/10/2011 12:01:14 AM")));
	}

}
