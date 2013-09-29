package ui.date;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import util.date.DateParser;

public class DateUtil 
{
	private DateParser dateParser;
	//fiscal year starts 7/1/thisYear every year
	private final int FISCAL_YEAR_START_MON =6;
	private final int FISCAL_YEAR_START_DAY = 1;
	
	//THIS*END is always NOW. we can't have data for the future.
	
	public final static int TODAY_START = 1;
	public final static int YESTERDAY_START = 2;
	public final static int YESTERDAY_END = 3;
	public final static int THIS_WEEK_START = 4;
	public final static int LAST_WEEK_START = 5;
	public final static int LAST_WEEK_END = 6;
	public final static int THIS_MONTH_START = 7;
	public final static int LAST_MONTH_START = 8;
	public final static int LAST_MONTH_END = 9;
	public final static int THIS_YEAR_START = 10;
	public final static int LAST_YEAR_START = 11;
	public final static int LAST_YEAR_END = 12;
	public final static int THIS_FISCAL_QUARTER_START = 13;
	public final static int LAST_FISCAL_QUARTER_START = 14;
	public final static int LAST_FISCAL_QUARTER_END = 15;
	public final static int THIS_FISCAL_YEAR_START = 16;
	public final static int LAST_FISCAL_YEAR_START = 17;
	public final static int LAST_FISCAL_YEAR_END = 18;
	
	private LinkedHashMap<Integer, String> dates;

	public DateUtil()
	{
		this(new DateParser().readableGregorian(new GregorianCalendar()));
	}
	
	public DateUtil(GregorianCalendar nowDate)
	{
		this(new DateParser().readableGregorian(nowDate));
	}
	
	public DateUtil(String nowDate)
	{
		dateParser = new DateParser();
		
		GregorianCalendar now = new GregorianCalendar();

		dates = new LinkedHashMap<Integer,String>();
		
		//there appears to be no easy way to copy construct gc's

		//current day's interval
		// current y-m-d 00:00:00 --> now
		GregorianCalendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		
		//yesterday's interval
		//today minus 1 day, at 00:00:00 --> today minus 1 day at 23:59:59
		GregorianCalendar yesterdayStart = new GregorianCalendar();
		yesterdayStart.set(Calendar.HOUR_OF_DAY, 0);
		yesterdayStart.set(Calendar.MINUTE, 0);
		yesterdayStart.set(Calendar.SECOND, 0);
		yesterdayStart.add(Calendar.DAY_OF_MONTH, -1);
		
		GregorianCalendar yesterdayEnd = new GregorianCalendar();
		yesterdayEnd.set(Calendar.HOUR_OF_DAY, 23);
		yesterdayEnd.set(Calendar.MINUTE, 59);
		yesterdayEnd.set(Calendar.SECOND, 59);
		yesterdayEnd.add(Calendar.DAY_OF_MONTH, -1);
		
		//weeks decreed to run mon-sun
		//this week's interval
		GregorianCalendar thisWeekStart = new GregorianCalendar();
		thisWeekStart.set(Calendar.HOUR_OF_DAY, 0);
		thisWeekStart.set(Calendar.MINUTE, 0);
		thisWeekStart.set(Calendar.SECOND, 0);
		thisWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		//last week's interval
		GregorianCalendar lastWeekStart = new GregorianCalendar();
		lastWeekStart.set(Calendar.HOUR_OF_DAY, 0);
		lastWeekStart.set(Calendar.MINUTE, 0);
		lastWeekStart.set(Calendar.SECOND, 0);
		lastWeekStart.add(Calendar.WEEK_OF_MONTH, -1);
		lastWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		GregorianCalendar lastWeekEnd = new GregorianCalendar();
		lastWeekEnd.set(Calendar.HOUR_OF_DAY, 23);
		lastWeekEnd.set(Calendar.MINUTE, 59);
		lastWeekEnd.set(Calendar.SECOND, 59);
		lastWeekEnd.add(Calendar.WEEK_OF_MONTH, -1);
		lastWeekEnd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		lastWeekEnd.add(Calendar.DAY_OF_WEEK, 6);
		
		//current month's interval
		GregorianCalendar thisMonthStart = new GregorianCalendar();
		thisMonthStart.set(Calendar.DAY_OF_MONTH, 1);
		thisMonthStart.set(Calendar.HOUR_OF_DAY, 0);
		thisMonthStart.set(Calendar.MINUTE, 0);
		thisMonthStart.set(Calendar.SECOND, 0);
		
		//last month's interval
		GregorianCalendar lastMonthStart = new GregorianCalendar();
		lastMonthStart.add(Calendar.MONTH, -1);
		lastMonthStart.set(Calendar.DAY_OF_MONTH, 1);
		lastMonthStart.set(Calendar.HOUR_OF_DAY, 0);
		lastMonthStart.set(Calendar.MINUTE, 0);
		lastMonthStart.set(Calendar.SECOND, 0);
		
		GregorianCalendar lastMonthEnd = new GregorianCalendar();
		lastMonthEnd.set(Calendar.DAY_OF_MONTH, 1);
		lastMonthEnd.add(Calendar.DAY_OF_MONTH, -1);
		lastMonthEnd.set(Calendar.HOUR_OF_DAY, 23);
		lastMonthEnd.set(Calendar.MINUTE, 59);
		lastMonthEnd.set(Calendar.SECOND, 59);
		
		GregorianCalendar thisYearStart = new GregorianCalendar();
		thisYearStart.set(Calendar.MONTH, 0);
		thisYearStart.set(Calendar.DAY_OF_MONTH, 1);
		thisYearStart.set(Calendar.HOUR_OF_DAY, 0);
		thisYearStart.set(Calendar.MINUTE, 0);
		thisYearStart.set(Calendar.SECOND, 0);
		
		GregorianCalendar lastYearStart = new GregorianCalendar();
		lastYearStart.add(Calendar.YEAR, -1);
		lastYearStart.set(Calendar.MONTH, 0);
		lastYearStart.set(Calendar.DAY_OF_MONTH, 1);
		lastYearStart.set(Calendar.HOUR_OF_DAY, 0);
		lastYearStart.set(Calendar.MINUTE, 0);
		lastYearStart.set(Calendar.SECOND, 0);
		
		GregorianCalendar lastYearEnd = new GregorianCalendar();
		lastYearEnd.add(Calendar.YEAR, -1);
		lastYearEnd.set(Calendar.MONTH, 11);
		lastYearEnd.set(Calendar.DAY_OF_MONTH, 31);
		lastYearEnd.set(Calendar.HOUR_OF_DAY, 23);
		lastYearEnd.set(Calendar.MINUTE, 59);
		lastYearEnd.set(Calendar.SECOND, 59);
		
		dates.put(TODAY_START, convertGregorianToMySQL(today));
		dates.put(YESTERDAY_START, convertGregorianToMySQL(yesterdayStart));
		dates.put(YESTERDAY_END, convertGregorianToMySQL(yesterdayEnd));
		dates.put(THIS_WEEK_START, convertGregorianToMySQL(thisWeekStart));
		dates.put(LAST_WEEK_START, convertGregorianToMySQL(lastWeekStart));
		dates.put(LAST_WEEK_END, convertGregorianToMySQL(lastWeekEnd));
		dates.put(THIS_MONTH_START, convertGregorianToMySQL(thisMonthStart));
		dates.put(LAST_MONTH_START, convertGregorianToMySQL(lastMonthStart));
		dates.put(LAST_MONTH_END, convertGregorianToMySQL(lastMonthEnd));
		dates.put(THIS_YEAR_START, convertGregorianToMySQL(thisYearStart));
		dates.put(LAST_YEAR_START, convertGregorianToMySQL(lastYearStart));
		dates.put(LAST_YEAR_END, convertGregorianToMySQL(lastYearEnd));
		
		generateFiscalIntervals(now);
	}

	private void generateFiscalIntervals(GregorianCalendar now) 
	{
		//XXXX-07-01 -> (XXXX+1)-06-30
		
		//get the current year from the now date
		GregorianCalendar thisFiscalYearStart = new GregorianCalendar();
		thisFiscalYearStart.set(Calendar.DAY_OF_MONTH, FISCAL_YEAR_START_DAY);
		thisFiscalYearStart.set(Calendar.MONTH, FISCAL_YEAR_START_MON);
		thisFiscalYearStart.set(Calendar.HOUR_OF_DAY, 0);
		thisFiscalYearStart.set(Calendar.MINUTE, 0);
		thisFiscalYearStart.set(Calendar.SECOND, 0);
		thisFiscalYearStart.set(Calendar.YEAR, now.get(Calendar.YEAR));
		
		if(now.get(Calendar.MONTH) < FISCAL_YEAR_START_MON)
		{
			thisFiscalYearStart.add(Calendar.YEAR, -1);
		}
		
		GregorianCalendar lastFiscalYearStart = new GregorianCalendar();
		lastFiscalYearStart.set(Calendar.DAY_OF_MONTH, FISCAL_YEAR_START_DAY);
		lastFiscalYearStart.set(Calendar.MONTH, FISCAL_YEAR_START_MON);
		lastFiscalYearStart.set(Calendar.HOUR_OF_DAY, 0);
		lastFiscalYearStart.set(Calendar.MINUTE, 0);
		lastFiscalYearStart.set(Calendar.SECOND, 0);
		lastFiscalYearStart.set(Calendar.YEAR, thisFiscalYearStart.get(Calendar.YEAR));
		lastFiscalYearStart.add(Calendar.YEAR, -1);
		
		GregorianCalendar lastFiscalYearEnd = new GregorianCalendar();
		lastFiscalYearEnd.set(Calendar.DAY_OF_MONTH, thisFiscalYearStart.get(Calendar.DAY_OF_MONTH));
		lastFiscalYearEnd.set(Calendar.MONTH, thisFiscalYearStart.get(Calendar.MONTH));
		lastFiscalYearEnd.set(Calendar.YEAR, thisFiscalYearStart.get(Calendar.YEAR));
		lastFiscalYearEnd.set(Calendar.HOUR_OF_DAY, 0);
		lastFiscalYearEnd.set(Calendar.MINUTE, 0);
		lastFiscalYearEnd.set(Calendar.SECOND, 0);
		lastFiscalYearEnd.add(Calendar.SECOND, -1);
		
		//generate the fiscal quarter intervals, sort out this/last with gregcal.before/after
		//next fq is +3 months, -1 second
		//this fiscal quarter is within this fiscal year
		GregorianCalendar nextFiscalQEnd = new GregorianCalendar();
		nextFiscalQEnd.set(Calendar.YEAR, thisFiscalYearStart.get(Calendar.YEAR));
		nextFiscalQEnd.set(Calendar.MONTH, thisFiscalYearStart.get(Calendar.MONTH));
		nextFiscalQEnd.set(Calendar.DAY_OF_MONTH, thisFiscalYearStart.get(Calendar.DAY_OF_MONTH));
		nextFiscalQEnd.set(Calendar.HOUR_OF_DAY, 0);
		nextFiscalQEnd.set(Calendar.MINUTE, 0);
		nextFiscalQEnd.set(Calendar.SECOND, 0);
		

		do 
		{
			//12 months/ 4 quarters
			nextFiscalQEnd.add(Calendar.MONTH, 3);
		} while (now.after(nextFiscalQEnd));
		
		
		GregorianCalendar thisFiscalQStart = new GregorianCalendar();
		thisFiscalQStart.set(Calendar.DAY_OF_MONTH, 1);
		thisFiscalQStart.set(Calendar.MONTH, nextFiscalQEnd.get(Calendar.MONTH)-3);
		thisFiscalQStart.set(Calendar.HOUR_OF_DAY, 0);
		thisFiscalQStart.set(Calendar.MINUTE, 0);
		thisFiscalQStart.set(Calendar.SECOND, 0);

		GregorianCalendar lastFiscalQStart = new GregorianCalendar();
		lastFiscalQStart.set(Calendar.DAY_OF_MONTH, nextFiscalQEnd.get(Calendar.DAY_OF_MONTH));
		lastFiscalQStart.set(Calendar.MONTH, nextFiscalQEnd.get(Calendar.MONTH));
		lastFiscalQStart.set(Calendar.HOUR_OF_DAY, 0);
		lastFiscalQStart.set(Calendar.MINUTE, 0);
		lastFiscalQStart.set(Calendar.SECOND, 0);
		
		GregorianCalendar lastFiscalQEnd = new GregorianCalendar();
		lastFiscalQEnd.set(Calendar.DAY_OF_MONTH, thisFiscalQStart.get(Calendar.DAY_OF_MONTH));
		lastFiscalQEnd.set(Calendar.YEAR, thisFiscalQStart.get(Calendar.YEAR));
		lastFiscalQEnd.set(Calendar.MONTH, thisFiscalQStart.get(Calendar.MONTH));
		lastFiscalQEnd.set(Calendar.HOUR_OF_DAY, thisFiscalQStart.get(Calendar.HOUR_OF_DAY));
		lastFiscalQEnd.set(Calendar.MINUTE, thisFiscalQStart.get(Calendar.MINUTE));
		lastFiscalQEnd.set(Calendar.SECOND, thisFiscalQStart.get(Calendar.SECOND));
		lastFiscalQEnd.add(Calendar.SECOND, -1);
		
		dates.put(THIS_FISCAL_QUARTER_START, convertGregorianToMySQL(thisFiscalQStart));
		dates.put(LAST_FISCAL_QUARTER_START, convertGregorianToMySQL(lastFiscalQStart));
		dates.put(LAST_FISCAL_QUARTER_END, convertGregorianToMySQL(lastFiscalQEnd));
		dates.put(THIS_FISCAL_YEAR_START, convertGregorianToMySQL(thisFiscalYearStart));
		dates.put(LAST_FISCAL_YEAR_START, convertGregorianToMySQL(lastFiscalYearStart));
		dates.put(LAST_FISCAL_YEAR_END, convertGregorianToMySQL(lastFiscalYearEnd));
	}
	
	public String getDateByName(int dateName)
	{
		return dates.get(dateName);
	}
	
	public String convertGregorianToMySQL(GregorianCalendar date) 
	{
		return dateParser.readableGregorian(date);
	}
	
	public String dumpGeneratedDates()
	{
		StringBuilder retval = new StringBuilder();
		
		for(Entry<Integer,String> date  : dates.entrySet())
		{
			retval.append(date.getKey());
			retval.append(" => ");
			retval.append(getDateByName(date.getKey()));
			retval.append("\n");
		}
		
		return retval.toString();
	}
	
	public static void main(String[] args)
	{
		System.out.println(new DateUtil().dumpGeneratedDates());
	}
}
