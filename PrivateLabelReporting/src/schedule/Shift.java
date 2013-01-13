package schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;

import util.DateParser;

/**
 * @author Jason Diamond
 *
 */
public class Shift 
{
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;
	private DateParser dp;
	
	/**
	 * Build an empty shift.
	 */
	protected Shift(){};
	
	/**
	 * 
	 * Build a shift with a supplied interval.
	 * 
	 * @param intervalStart	Date of interval's beginning.
	 * @param intervalEnd	Date of interval's end.
	 */
	public Shift(GregorianCalendar intervalStart, GregorianCalendar intervalEnd)
	{
		dp = new DateParser();
		
		if(intervalStart.before(intervalEnd))
		{
				this.startDate = intervalStart;
				this.endDate = intervalEnd;
		}
		else
		{
			this.startDate = intervalEnd;
			this.endDate = intervalStart;
		}
	}
	
	/**
	 * 
	 * Build a shift with a supplied interval.
	 * 
	 * @param intervalStart	Date of interval's beginning.
	 * @param intervalEnd	Date of interval's end.
	 */
	public Shift(String intervalStart, String intervalEnd)
	{
		dp = new DateParser();
		
		GregorianCalendar intervalStartDate =  dp.convertSQLDateToGregorian((intervalStart));
		GregorianCalendar intervalEndDate =  dp.convertSQLDateToGregorian((intervalEnd));
		
		if(intervalStartDate.before(intervalEndDate))
		{
				this.startDate = intervalStartDate;
				this.endDate = intervalEndDate;
		}
		else
		{
			this.startDate = intervalEndDate;
			this.endDate = intervalStartDate;
		}
	}
	
	/**
	 * 
	 * Accessor for the interval's start date.
	 * 
	 * @return	The interval's start date.
	 */
	public GregorianCalendar getStartDate() 
	{
		return startDate;
	}

	/**
	 * 
	 * Accessor for itnerval's end date.
	 * 
	 * @return	The interval's end date.
	 */
	public GregorianCalendar getEndDate() 
	{
		return endDate;
	}
	

	/* 
	 * Stringify this shift
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Shift StartDate: " + dp.readableGregorian(startDate));
		sb.append(" => EndDate: " + dp.readableGregorian(endDate));
		
		return sb.toString();
	}
	
	/* 
	 * Comparator for Shifts. Equality is determined if both shifts have the same start and end dates.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object s1)
	{
		if ( !(s1 instanceof Shift) ) return false;
		
		Shift otherShift = (Shift)s1;
		
		return getStartDate().getTimeInMillis() == otherShift.startDate.getTimeInMillis() && getEndDate().getTimeInMillis() == otherShift.getEndDate().getTimeInMillis();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		GregorianCalendar x = new GregorianCalendar();
		GregorianCalendar y = new GregorianCalendar();
		
		y.add(Calendar.DAY_OF_MONTH, 1);
		
		Shift s = new Shift(x, y);
		System.out.println(s);
		
		Shift t = new Shift(y, x);
		System.out.println(t);
		
	}

}
