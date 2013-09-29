package test.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import ui.date.DateUtil;
import util.date.DateParser;
import junit.framework.TestCase;

/**
 * @author Jason Diamond
 *
 */
public class DateUtilTest extends TestCase 
{
	private DateUtil dateUtil;
	private int[]  dateFlags;
	private DateParser dateParser;

	public void setUp()
	{
		dateParser = new DateParser();

		dateFlags = new int[]
				{
				DateUtil.TODAY_START,
				DateUtil.YESTERDAY_START,
				DateUtil.YESTERDAY_END,
				DateUtil.THIS_WEEK_START,
				DateUtil.LAST_WEEK_START,
				DateUtil.LAST_WEEK_END,
				DateUtil.THIS_MONTH_START,
				DateUtil.LAST_MONTH_START,
				DateUtil.LAST_MONTH_END,
				DateUtil.THIS_YEAR_START,
				DateUtil.LAST_YEAR_START,
				DateUtil.LAST_YEAR_END,
				DateUtil.THIS_FISCAL_QUARTER_START,
				DateUtil.LAST_FISCAL_QUARTER_START,
				DateUtil.LAST_FISCAL_QUARTER_END,
				DateUtil.THIS_FISCAL_YEAR_START,
				DateUtil.LAST_FISCAL_YEAR_START,
				DateUtil.LAST_FISCAL_YEAR_END
				};
	}

	public void tearDown()
	{

	}

	/*
	 * For each second for the next 4 years (leap year test), make sure viable dates are returned. we are not checking the dates for accuracy, just that they exist.
	 */
	@Test
	public void testAllDatesViability()
	{
		
		GregorianCalendar now = new GregorianCalendar();
		
		//seconds in 4 years = ~126m 
		for(int i = 0; i< 4*60*60*24*365; i++)
		{
			now.add(Calendar.SECOND, 1);
			dateUtil = new DateUtil(now);
			
			for(int index : dateFlags)
			{
				assertTrue("Test parameter generation at time now + " +  i + " seconds: " + index, isValidDate(dateUtil.getDateByName(index)));
			}
		}
	}
	
	/*
	 * Pick dates in Fiscal Quarter 1 and verify against known dates
	 */
	@Test
	public void testQ1IntervalGeneration()
	{
		//literal q1 start
		dateUtil = new DateUtil("2012-07-01 00:00:00");
		
		assertEquals("", "","");
		
		//random q1 date
		
		//literal q1 end
	}

	private boolean isValidDate(String date)
	{
		boolean retval = false;

		try
		{
			if(date.equals(dateParser.readableGregorian(dateParser.convertSQLDateToGregorian(date))))
			{
				retval = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return retval;
	}

	private boolean isValidDate(GregorianCalendar date)
	{
		return isValidDate(dateParser.readableGregorian(date)); 
	}
}
