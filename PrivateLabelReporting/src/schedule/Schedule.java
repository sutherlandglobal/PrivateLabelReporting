package schedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Vector;

import util.DateParser;

/**
 * @author Jason Diamond
 *
 */
public class Schedule 
{
	//a schedule has a start and end time, and is composed of shifts
	//a schedule has a name, typically an agent that it belongs to
	
	private Shift interval;
	
	private Vector<Shift> shifts;

	private DateParser dp;

	/**
	 * Build a Schedule, with a start and end date.
	 * 
	 * @param intervalStart		The schedule's start date.
	 * @param intervalEnd		The schedule's end date.
	 */
	public Schedule(String intervalStart, String intervalEnd)
	{
		shifts = new Vector<Shift>();
		
		dp = new DateParser();
		
		interval = new Shift(dp.convertSQLDateToGregorian(intervalStart), dp.convertSQLDateToGregorian(intervalEnd));
	}
	
	/**
	 * Build a Schedule, with a start and end date.
	 * 
	 * @param intervalStart		The schedule's start date.
	 * @param intervalEnd		The schedule's end date.
	 */
	public Schedule(GregorianCalendar intervalStart, GregorianCalendar intervalEnd)
	{
		
		interval = new Shift(intervalStart, intervalEnd);
		shifts = new Vector<Shift>();
		
		dp = new DateParser();
	}
	
	/**
	 * Add a shift to the schedule.
	 * 
	 * @param newShift		The shift to add.
	 */
	public void addShift(Shift newShift)
	{
		shifts.add(newShift);
	}
	
	/**
	 * Add a shift to this schedule.
	 * 
	 * @param startShiftDate	The shift's start date.
	 * @param endShiftDate		The shift's end date.
	 */
	public void addShift(GregorianCalendar startShift, GregorianCalendar endShift)
	{
		addShift(new Shift(startShift, endShift));
	}
	
	/**
	 * Add a shift to this schedule.
	 * 
	 * @param startShiftDate	The shift's start date.
	 * @param endShiftDate		The shift's end date.
	 */
	public void addShift(String startShiftDate, String endShiftDate)
	{
		addShift(dp.convertSQLDateToGregorian(startShiftDate), dp.convertSQLDateToGregorian(endShiftDate));
	}
	
	/**
	 * Retrieve a list of shifts within this schedule, sorted by shift start date.
	 * 
	 * @return	The sorted list of shifts.
	 */
	public Vector<Shift> getSortedShifts()
	{
		Vector<Shift> unsorted = shifts;
		
		Collections.sort(unsorted, new Comparator<Shift>() 
		{
			@Override
			public int compare(Shift arg0, Shift arg1) 
			{
				int retval = 0;
				
				if(arg0.getStartDate().after(arg1.getStartDate()))
				{
					retval = 1;
				}
				
				return retval;
			}
		});
		
		return unsorted;
		
	}
	
	/**
	 * 
	 * Accessor for itnerval's end date.
	 * 
	 * @return	The interval's end date.
	 */
	public Vector<Shift> getShifts()
	{
		return shifts;
	}
	
	/* 
	 * Stringify the schedule. Contains the schedule start and end dates, as well as any shifts and their intervals.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Schedules: " + dp.readableGregorian(interval.getStartDate()) + " => " + dp.readableGregorian(interval.getEndDate()));
		sb.append("\n");
		
		for(Shift s : getSortedShifts() )
		{
			sb.append(s);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * Accessor for interval's start date.
	 * 
	 * @return	The interval's start date.
	 */
	public String getStartDate()
	{
		return dp.readableGregorian(interval.getStartDate());
	}
	
	/**
	 * 
	 * Accessor for interval's end date.
	 * 
	 * @return	The interval's end date.
	 */
	public String getEndDate()
	{
		return dp.readableGregorian(interval.getEndDate());
	}
	
	/**
	 * Accessor for the schedule's interval.
	 * 
	 * @return	A shift describing the start and end dates of the schedule.
	 */
	public Shift getInterval()
	{
		return interval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		DateParser dp = new DateParser();
		
		Schedule s = new Schedule( dp.convertSQLDateToGregorian("2012-07-04 00:00:00"), dp.convertSQLDateToGregorian("2012-07-04 08:00:00"));
		
		s.addShift("2012-07-04 00:01:00", "2012-07-04 00:01:00");
		s.addShift("2012-07-04 00:02:00", "2012-07-04 00:05:00");
		s.addShift("2012-07-04 00:10:00", "2012-07-04 04:01:00");
		s.addShift("2012-07-04 04:05:00", "2012-07-04 08:01:00");
		
		System.out.println(s.toString());
	}



}
