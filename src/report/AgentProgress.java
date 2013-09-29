package report;
/**
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import report.Report;
import report.ReportRunner;
import team.Team;
import util.date.DateParser;
import util.parameter.validation.ReportVisitor;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 *
 */
public class AgentProgress extends Report
{
	private final static String DATE_ATTR = "date";
	private final static String TOTAL_SALES_ATTR = "totalSales";
	private final static String TOTAL_REFUNDS_ATTR = "totalRefunds";
	private final static String CALL_VOL_ATTR = "callVolume";
	private final static String CONV_ATTR = "conversion";
	private final static String SCHEDULE_ADH_ATTR = "schAdh";
	private final static String LATE_DAYS_ATTR = "lateDays";
	private final static String SALES_DOC_RATE_ATTR = "salesDocRate";
	private final static String RPC_ATTR = "rpc";
	private final static String IVR_CSAT_ATTR = "ivrCSAT";
	private final static String AOV_ATTR = "aov";
	
	private RealtimeSales realtimeSalesReport;
	private RefundTotals refundTotalsReport;
	private CallVolume callVolumeReport;
	private Conversion conversionReport;
	private ScheduleAdherence scheduleAdherenceReport;
	private LateDays lateDaysReport;
	private SalesDocumentationRate salesDocRateReport;
	private RevenuePerCall rpcReport;
	private IVRCSAT ivrCSATReport;
	private AverageOrderValue aovReport;
	
	/**
	 * Build the report object.
	 * 
	 * @throws ReportSetupException		If a failure occurs during creation of the report or its resources.
	 */
	public AgentProgress() throws ReportSetupException
	{
		super();

		reportName = "Agent Progress";
		
		logger.info("Building report " +  reportName);
	}

	/* (non-Javadoc)
	 * @see helios.Report#runReport()
	 */
	@Override
	protected ArrayList<String[]> runReport() throws ReportSetupException
	{

		ArrayList<String[]> retval = null;

		String endDate = getParameter(END_DATE_PARAM);

		
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

		
		
		retval = new ArrayList<String[]>();


		Team users = new Team();
		
		ReportRunner runner = new ReportRunner();

		realtimeSalesReport = new RealtimeSales();
		realtimeSalesReport.setChildReport(true);
		realtimeSalesReport.setParameter(REPORT_TYPE_PARAM, RealtimeSales.AGENT_TIME_REPORT);
		realtimeSalesReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		realtimeSalesReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		realtimeSalesReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		realtimeSalesReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		realtimeSalesReport.setParameter(END_DATE_PARAM, endDate);

		refundTotalsReport = new RefundTotals();
		refundTotalsReport.setChildReport(true);
		refundTotalsReport.setParameter(REPORT_TYPE_PARAM, RefundTotals.AGENT_TIME_REPORT);
		refundTotalsReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		refundTotalsReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		refundTotalsReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		refundTotalsReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		refundTotalsReport.setParameter(END_DATE_PARAM, endDate);

		callVolumeReport = new CallVolume();
		callVolumeReport.setChildReport(true);
		callVolumeReport.setParameter(REPORT_TYPE_PARAM, CallVolume.AGENT_TIME_REPORT);
		callVolumeReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		callVolumeReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		callVolumeReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		callVolumeReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		callVolumeReport.setParameter(END_DATE_PARAM, endDate);

		conversionReport = new Conversion();
		conversionReport.setChildReport(true);
		conversionReport.setParameter(REPORT_TYPE_PARAM, Conversion.AGENT_TIME_REPORT);
		conversionReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		conversionReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		conversionReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		conversionReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		conversionReport.setParameter(END_DATE_PARAM, endDate);

		//schedule adherence
		scheduleAdherenceReport = new ScheduleAdherence();
		scheduleAdherenceReport.setChildReport(true);
		scheduleAdherenceReport.setParameter(REPORT_TYPE_PARAM, ScheduleAdherence.AGENT_TIME_REPORT);
		scheduleAdherenceReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		scheduleAdherenceReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		scheduleAdherenceReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		scheduleAdherenceReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		scheduleAdherenceReport.setParameter(END_DATE_PARAM, endDate);
		
		//late days
		lateDaysReport = new LateDays();
		lateDaysReport.setChildReport(true);
		lateDaysReport.setParameter(REPORT_TYPE_PARAM, LateDays.AGENT_TIME_REPORT);
		lateDaysReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		lateDaysReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		lateDaysReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		lateDaysReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		lateDaysReport.setParameter(END_DATE_PARAM, endDate);
		
		//doc rate
		salesDocRateReport = new SalesDocumentationRate();
		salesDocRateReport.setChildReport(true);
		salesDocRateReport.setParameter(REPORT_TYPE_PARAM, SalesDocumentationRate.AGENT_TIME_REPORT);
		salesDocRateReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		salesDocRateReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		salesDocRateReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		salesDocRateReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		salesDocRateReport.setParameter(END_DATE_PARAM, endDate);
		
		//rpc
		rpcReport = new RevenuePerCall();
		rpcReport.setChildReport(true);
		rpcReport.setParameter(REPORT_TYPE_PARAM, RevenuePerCall.AGENT_TIME_REPORT);
		rpcReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		rpcReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		rpcReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		rpcReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		rpcReport.setParameter(END_DATE_PARAM, endDate);
		
		//IVR CSAT
		ivrCSATReport = new IVRCSAT();
		ivrCSATReport.setChildReport(true);
		ivrCSATReport.setParameter(REPORT_TYPE_PARAM, IVRCSAT.AGENT_TIME_REPORT);
		ivrCSATReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		ivrCSATReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		ivrCSATReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		ivrCSATReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		ivrCSATReport.setParameter(END_DATE_PARAM, endDate);
		
		aovReport = new AverageOrderValue();
		aovReport.setChildReport(true);
		aovReport.setParameter(REPORT_TYPE_PARAM, IVRCSAT.AGENT_TIME_REPORT);
		aovReport.setParameter(AGENT_NAME_PARAM, getParameter(AGENT_NAME_PARAM));
		aovReport.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		aovReport.setParameter(TIME_GRAIN_PARAM, getParameter(TIME_GRAIN_PARAM));
		aovReport.setParameter(START_DATE_PARAM, getParameter(START_DATE_PARAM));
		aovReport.setParameter(END_DATE_PARAM, endDate);
		
		runner.addReport(TOTAL_SALES_ATTR, realtimeSalesReport);
		runner.addReport(TOTAL_REFUNDS_ATTR, refundTotalsReport);
		runner.addReport(CALL_VOL_ATTR, callVolumeReport);
		runner.addReport(CONV_ATTR, conversionReport);
		runner.addReport(SCHEDULE_ADH_ATTR, scheduleAdherenceReport);
		runner.addReport(LATE_DAYS_ATTR, lateDaysReport);
		runner.addReport(SALES_DOC_RATE_ATTR, salesDocRateReport);
		runner.addReport(RPC_ATTR, rpcReport);
		runner.addReport(IVR_CSAT_ATTR, ivrCSATReport);
		runner.addReport(AOV_ATTR, aovReport);
		
		if(!runner.runReports())
		{
			throw new ReportSetupException("Running reports failed");
		}
		else
		{
			for(String[] row : runner.getResults(TOTAL_SALES_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(TOTAL_SALES_ATTR );
				users.getUser(row[0]).addData(TOTAL_SALES_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(TOTAL_REFUNDS_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(TOTAL_REFUNDS_ATTR  );
				users.getUser(row[0]).addData(TOTAL_REFUNDS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(CALL_VOL_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(CALL_VOL_ATTR   );
				users.getUser(row[0]).addData(CALL_VOL_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(CONV_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(CONV_ATTR    );
				users.getUser(row[0]).addData(CONV_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(AOV_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(AOV_ATTR    );
				users.getUser(row[0]).addData(AOV_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(SCHEDULE_ADH_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(SCHEDULE_ADH_ATTR    );
				users.getUser(row[0]).addData(SCHEDULE_ADH_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(LATE_DAYS_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(LATE_DAYS_ATTR    );
				users.getUser(row[0]).addData(LATE_DAYS_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(SALES_DOC_RATE_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(SALES_DOC_RATE_ATTR    );
				users.getUser(row[0]).addData(SALES_DOC_RATE_ATTR, row[1]);
			}

			for(String[] row : runner.getResults(RPC_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(RPC_ATTR    );
				users.getUser(row[0]).addData(RPC_ATTR, row[1]);
			}
			
			for(String[] row : runner.getResults(IVR_CSAT_ATTR))
			{
				users.addUser(row[0]);
				users.getUser(row[0]).addAttr(DATE_ATTR );
				users.getUser(row[0]).addData(DATE_ATTR, row[0]);

				users.getUser(row[0]).addAttr(IVR_CSAT_ATTR    );
				users.getUser(row[0]).addData(IVR_CSAT_ATTR, row[1]);
			}
		}

		for(String userName : users.getUserList())
		{
			String name = users.getUser(userName).getAttrData(DATE_ATTR).get(0);

			double rpc = 0;
			int lateDays = 0;

			double salesDocRate = 0;
			
			double totalSales = 0;
			double totalRefunds = 0;

			int callVolume = 0;

			double conversion = 0; 
			double schAdh = 0;
			
			double ivrCSAT =0;
			double aov = 0;

			if(users.getUser(userName).getAttrData(RPC_ATTR) != null )
			{
				rpc = Double.parseDouble(users.getUser(userName).getAttrData(RPC_ATTR).get(0));
			}

			if(users.getUser(userName).getAttrData(TOTAL_SALES_ATTR) != null)
			{
				totalSales = Double.parseDouble(users.getUser(userName).getAttrData(TOTAL_SALES_ATTR).get(0));
			}

			if(users.getUser(userName).getAttrData(TOTAL_REFUNDS_ATTR) != null)
			{
				totalRefunds = Double.parseDouble(users.getUser(userName).getAttrData(TOTAL_REFUNDS_ATTR).get(0));
			}

			if(users.getUser(userName).getAttrData(CALL_VOL_ATTR) != null)
			{
				callVolume = Integer.parseInt(users.getUser(userName).getAttrData(CALL_VOL_ATTR).get(0));
			}
			
			if(users.getUser(userName).getAttrData(AOV_ATTR) != null)
			{
				aov = Double.parseDouble(users.getUser(userName).getAttrData(AOV_ATTR).get(0));
			}
			
			if(users.getUser(userName).getAttrData(LATE_DAYS_ATTR) != null)
			{
				lateDays = Integer.parseInt(users.getUser(userName).getAttrData(LATE_DAYS_ATTR).get(0));
			}

			if(users.getUser(userName).getAttrData(CONV_ATTR) != null)
			{
				conversion = Double.parseDouble(users.getUser(userName).getAttrData(CONV_ATTR).get(0));
			}

			if(users.getUser(userName).getAttrData(SCHEDULE_ADH_ATTR) != null)
			{
				schAdh = Double.parseDouble(users.getUser(userName).getAttrData(SCHEDULE_ADH_ATTR).get(0));
			}
			
			if(users.getUser(userName).getAttrData(SALES_DOC_RATE_ATTR) != null)
			{
				salesDocRate = Double.parseDouble(users.getUser(userName).getAttrData(SALES_DOC_RATE_ATTR).get(0));
			}
			
			if(users.getUser(userName).getAttrData(IVR_CSAT_ATTR) != null)
			{
				ivrCSAT = Double.parseDouble(users.getUser(userName).getAttrData(IVR_CSAT_ATTR).get(0));
			}

			retval.add(new String[]
					{
					name, 
					"" + callVolume,
					"" + totalSales, 
					"" + totalRefunds, 
					"" + conversion,
					"" + rpc, 
					"" + aov,
					"" + schAdh, 
					"" + lateDays,
					"" + salesDocRate,
					"" + ivrCSAT
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
		
		if(scheduleAdherenceReport != null)
		{
			scheduleAdherenceReport.close();
		}
		
		if(lateDaysReport != null)
		{
			lateDaysReport.close();
		}
		
		if(salesDocRateReport != null)
		{
			salesDocRateReport.close();
		}
		
		if(rpcReport != null)
		{
			rpcReport.close();
		}
		
		if(ivrCSATReport != null)
		{
			ivrCSATReport.close();
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
		
		ReportVisitor visitor = new ReportVisitor();
		
		retval = visitor.validate(this);
		
		return retval;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		AgentProgress s = null;
		try
		{
			s = new AgentProgress();
			
			s.setParameter(REPORT_TYPE_PARAM, AGENT_TIME_REPORT);
			s.setParameter(ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
			s.setParameter(START_DATE_PARAM, "2013-01-01 00:00:00");
			s.setParameter(END_DATE_PARAM, "2013-04-30 23:59:59");
			
			s.setParameter(AGENT_NAME_PARAM, "Kumar P, Dinesh");
			s.setParameter(TIME_GRAIN_PARAM, DateParser.WEEKLY_GRANULARITY);

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
