/**
 * 
 */
package com.sutherland.privatelabel.report.test;

import java.util.ArrayList;

import org.junit.Test;

import com.sutherland.helios.data.granularity.time.TimeGrains;
import com.sutherland.helios.data.granularity.user.UserGrains;
import com.sutherland.helios.exceptions.ReportSetupException;
import com.sutherland.privatelabel.report.AverageOrderValue;


/**
 * @author Jason Diamond
 *
 */
public class AverageOrderValueTest extends ReportOutputTest
{
	private AverageOrderValue report;
	private String startDate;
	private String endDate;
	private String timeGrain;
	private String userGrain;

	public void setUp()
	{
		startDate = "2011-10-01 00:00:00";
		endDate =  "2011-10-31 23:59:59";
		timeGrain = "" + TimeGrains.DAILY_GRANULARITY;
		userGrain = "" + UserGrains.TEAM_GRANULARITY;
		
		try 
		{
			report = new AverageOrderValue();
			report.getParameters().setStartDate(startDate);
			report.getParameters().setEndDate( endDate );
		} 
		catch (ReportSetupException e) 
		{
			assertFalse("Could not build AOV report", true);

			e.printStackTrace();
		}
	}

	public void tearDown()
	{
		if(report != null)
		{
			report.close();
		}
	}
	
	@Test
	public void testTimeTrend()
	{
		report.getParameters().setTimeGrain( timeGrain);
		report.getParameters().setReportType( "" + AverageOrderValue.TIME_TREND_REPORT);
		
		report.getParameters().addTeamName("ROCJFS Sales Team");
		report.getParameters().addTeamName("CHNDLF Sales Team");
		
		ArrayList<String[]> actualResults = report.startReport();
		
		assertTrue("Non-empty resultset", actualResults.size() > 0);
		
		assertTrue("Resultset size is one-per-timegrain", actualResults.size() == 31);
	}
	
	@Test
	public void testStack()
	{
		report.getParameters().setUserGrain( userGrain);
		report.getParameters().setReportType( "" + AverageOrderValue.STACK_REPORT);

		report.getParameters().addTeamName("ROCJFS Sales Team");
		report.getParameters().addTeamName("CHNDLF Sales Team");
		
		ArrayList<String[]> actualResults = report.startReport();
		
		assertTrue("Non-empty resultset", actualResults.size() > 0);
		assertTrue("Resultset size is one-per-usergrain", actualResults.size() == 2);
		
	}
}
