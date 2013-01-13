package report.SQL;
/**
 * 
 */

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.log4j.Level;

import report.Report;
import team.Team;
import util.ReportParameterValidator;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class SalesStackRank extends Report
{
	private final static String NAME_ATTR = "userName";
	private final static String NUM_SALES_ATTR = "grossSales";
	private final static String NUM_REFUNDS_ATTR = "numRefunds";
	private final static String TOTAL_SALES_ATTR = "totalSales";
	private final static String TOTAL_REFUNDS_ATTR = "totalRefunds";
	private final static String AOV_ATTR = "aov";
	private final static String CALL_VOL_ATTR = "callVolume";
	private final static String CONV_ATTR = "conversion";
	private SalesCount salesCountReport;
	private RefundCount refundCountReport;
	private RealtimeSales realtimeSalesReport;
	private RefundTotals refundTotalsReport;
	private CallVolume callVolumeReport;
	private Conversion conversionReport;
	private AverageOrderValue aovReport;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public SalesStackRank() throws ReportSetupException
	{
		super();

		reportName = "Sales Stack Rank";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected Vector<String[]> runReport() throws ReportSetupException
	{

		Vector<String[]> retval = null;

		String endDate = parameters.get(END_DATE_PARAM);

		
		//call stats update only once per day because stupid reasons. only valid to have report current to previous day.
		GregorianCalendar previousDay = new GregorianCalendar();
		previousDay.add(Calendar.DAY_OF_MONTH, -1);
		previousDay.set(Calendar.HOUR_OF_DAY, 23);
		previousDay.set(Calendar.MINUTE, 59);
		previousDay.set(Calendar.SECOND, 59);
		
		if(!dateParser.convertSQLDateToGregorian(endDate).before(previousDay))
		{
			endDate = dateParser.readableGregorian(previousDay);
		}

		retval = new Vector<String[]>();


		Team users = new Team();

		salesCountReport = new SalesCount();
		salesCountReport.setChildReport(true);
		salesCountReport.setParameter(REPORT_TYPE_PARAM, SalesCount.AGENT_STACK_REPORT);
		salesCountReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		salesCountReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		salesCountReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		salesCountReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : salesCountReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(NUM_SALES_ATTR );
			users.getUser(row[0]).addData(NUM_SALES_ATTR, row[1]);
		}

		refundCountReport = new RefundCount();
		refundCountReport.setChildReport(true);
		refundCountReport.setParameter(REPORT_TYPE_PARAM, RefundCount.AGENT_STACK_REPORT);
		refundCountReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		refundCountReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		refundCountReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		refundCountReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : refundCountReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(NUM_REFUNDS_ATTR );
			users.getUser(row[0]).addData(NUM_REFUNDS_ATTR, row[1]);
		}

		realtimeSalesReport = new RealtimeSales();
		realtimeSalesReport.setChildReport(true);
		realtimeSalesReport.setParameter(REPORT_TYPE_PARAM, RealtimeSales.AGENT_STACK_REPORT);
		realtimeSalesReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		realtimeSalesReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		realtimeSalesReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		realtimeSalesReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : realtimeSalesReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(TOTAL_SALES_ATTR );
			users.getUser(row[0]).addData(TOTAL_SALES_ATTR, row[1]);
		}

		refundTotalsReport = new RefundTotals();
		refundTotalsReport.setChildReport(true);
		refundTotalsReport.setParameter(REPORT_TYPE_PARAM, RefundTotals.AGENT_STACK_REPORT);
		refundTotalsReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		refundTotalsReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		refundTotalsReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		refundTotalsReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : refundTotalsReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(TOTAL_REFUNDS_ATTR  );
			users.getUser(row[0]).addData(TOTAL_REFUNDS_ATTR, row[1]);
		}

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameter(REPORT_TYPE_PARAM, CallVolume.AGENT_STACK_REPORT);
		callVolumeReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		callVolumeReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		callVolumeReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		callVolumeReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : callVolumeReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(CALL_VOL_ATTR   );
			users.getUser(row[0]).addData(CALL_VOL_ATTR, row[1]);
		}

		conversionReport = new Conversion();
		conversionReport.setChildReport(true);
		conversionReport.setParameter(REPORT_TYPE_PARAM, Conversion.AGENT_STACK_REPORT);
		conversionReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		conversionReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		conversionReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		conversionReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : conversionReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(CONV_ATTR    );
			users.getUser(row[0]).addData(CONV_ATTR, row[1]);
		}

		aovReport = new AverageOrderValue();
		aovReport.setChildReport(true);
		aovReport.setParameter(REPORT_TYPE_PARAM, AverageOrderValue.AGENT_STACK_REPORT);
		aovReport.setParameter(ROSTER_TYPE_PARAM, parameters.get(ROSTER_TYPE_PARAM));
		aovReport.setParameter(TIME_GRAIN_PARAM, parameters.get(TIME_GRAIN_PARAM));
		aovReport.setParameter(START_DATE_PARAM, parameters.get(START_DATE_PARAM));
		aovReport.setParameter(END_DATE_PARAM, endDate);

		for(String[] row : aovReport.startReport())
		{
			users.addUser(row[0]);
			users.getUser(row[0]).addAttr(NAME_ATTR );
			users.getUser(row[0]).addData(NAME_ATTR, row[0]);

			users.getUser(row[0]).addAttr(AOV_ATTR  );
			users.getUser(row[0]).addData(AOV_ATTR, row[1]);
		}


		for(String userName : users.getUserList())
		{
			String name = users.getUser(userName).getAttrData(NAME_ATTR).firstElement();

			int numSales = 0;
			int numRefunds = 0;

			double totalSales = 0;
			double totalRefunds = 0;

			int callVolume = 0;

			double conversion = 0; 
			double aov = 0;

			if(users.getUser(userName).getAttrData(NUM_SALES_ATTR) != null )
			{
				numSales = Integer.parseInt(users.getUser(userName).getAttrData(NUM_SALES_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(NUM_REFUNDS_ATTR) != null)
			{
				numRefunds = Integer.parseInt(users.getUser(userName).getAttrData(NUM_REFUNDS_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(TOTAL_SALES_ATTR) != null)
			{
				totalSales = Double.parseDouble(users.getUser(userName).getAttrData(TOTAL_SALES_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(TOTAL_REFUNDS_ATTR) != null)
			{
				totalRefunds = Double.parseDouble(users.getUser(userName).getAttrData(TOTAL_REFUNDS_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(CALL_VOL_ATTR) != null)
			{
				callVolume = Integer.parseInt(users.getUser(userName).getAttrData(CALL_VOL_ATTR).firstElement());
			}

			if(users.getUser(userName).getAttrData(CONV_ATTR) != null)
			{
				conversion = Double.parseDouble(users.getUser(userName).getAttrData(CONV_ATTR).firstElement());
			}


			if(users.getUser(userName).getAttrData(AOV_ATTR) != null)
			{
				aov = Double.parseDouble(users.getUser(userName).getAttrData(AOV_ATTR).firstElement());
			}

			retval.add(new String[]
					{
					name, 
					"" + numSales, 
					"" + numRefunds, 
					"" + (numSales - numRefunds), 
					"" + totalSales, 
					"" + totalRefunds, 
					"" + (totalSales - totalRefunds),
					"" + callVolume,
					"" + conversion,
					"" + aov, 
					});
		}

		return retval;
	}

	/* (non-Javadoc)
	 * @see report.Report#close()
	 */
	@Override
	public void close()
	{
		if(salesCountReport != null)
		{
			salesCountReport.close();
		}

		if(refundCountReport != null)
		{
			refundCountReport.close();
		}

		if(realtimeSalesReport != null)
		{
			realtimeSalesReport.close();
		}

		if(refundTotalsReport != null)
		{
			refundTotalsReport.close();
		}

		if(callVolumeReport != null)
		{
			callVolumeReport.close();
		}

		if(conversionReport != null)
		{
			conversionReport.close();
		}

		if(aovReport != null)
		{
			aovReport.close();
		}
		
		super.close();
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupDataSourceConnections()
	 */
	@Override
	protected boolean setupDataSourceConnections()
	{
		boolean retval = true;

		//connectivity tests handled by subreports

		return retval;
	}

	/* (non-Javadoc)
	 * @see helios.Report#setupReport()
	 */
	@Override
	protected boolean setupReport()
	{
		boolean retval = true;


		return retval;
	}
	
	/* (non-Javadoc)
	 * @see helios.Report#validateParameters()
	 */
	@Override
	protected boolean validateParameters() 
	{
		boolean retval = false;			
		
		try
		{
			String rosterType = getParameter(ROSTER_TYPE_PARAM);
			if
			(
					hasValidDateInterval() && 
					ReportParameterValidator.validateRosterType(rosterType) && 
					isValidReportType(new int[]{TEAM_STACK_REPORT, AGENT_STACK_REPORT})
			)
			{
				retval = true;
			}			
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR,  "Exception: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		finally
		{

		}

		return retval;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SalesStackRank s = null;
		try
		{
			s = new SalesStackRank();
			
			s.setParameter(REPORT_TYPE_PARAM, AGENT_STACK_REPORT);
			s.setParameter(ROSTER_TYPE_PARAM, Roster.SALES_ROSTER);
			s.setParameter(START_DATE_PARAM, "2011-10-01 00:00:00");
			s.setParameter(END_DATE_PARAM, "2011-10-31 23:59:59");

			for(String[] row : s.startReport())
			{
				System.out.println(Arrays.asList(row).toString());
			}
		} 
		catch (ReportSetupException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(s!= null)
			{
				s.close();
			}
		}
	}
}
