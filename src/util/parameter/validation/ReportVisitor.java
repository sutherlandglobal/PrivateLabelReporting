/**
 * 
 */
package util.parameter.validation;

import java.util.GregorianCalendar;

import report.Report;
import report.Roster;
import util.date.DateParser;
import util.parameter.Validation;
import exceptions.ParameterValidationException;
import exceptions.ReportSetupException;

/**
 * @author Jason Diamond
 * 
 * Validate a Helios report's parameters. Some parameters override others, so there is little we can do to avoid the massive, inherent side effects. Supplying an
 * agentName will override any roster parameters. Supplying a date grain to a stack report should go ignored. Start and end dates need to 
 * be converted from "now" or bad dates like Feb 31st
 * 
 * Similar overrides need to be supported for future validations.
 * 
 * The validator.validate() function should return a new set of parameters, or null, or throw an exception if validation failed.
 *
 */
public class ReportVisitor
{		
	private DateParser dateParser;
	private Roster roster;

	public ReportVisitor()
	{
		dateParser = new DateParser();
	}
	
	public boolean validate(report.AverageOrderValue averageOrderValue)
	{
		return validateBasicMetricReport(averageOrderValue);
	}
	
	public boolean validate(report.RealtimeSales realtimeSales)
	{
		return validateBasicMetricReport(realtimeSales);
	}
	
	public boolean validate(report.TopCaseDrivers topCaseDrivers)
	{
		return validateBasicDriversReport(topCaseDrivers);
	}
	
	public boolean validate(Roster rosterReport)
	{
		return validateRosterType(rosterReport);
	}
	
	public boolean validate(report.AggregateDSATCaseCount aggregateDSATCaseCount) 
	{
		return validateTimeInterval(aggregateDSATCaseCount) && isValidTimeGrain(aggregateDSATCaseCount); 
	}
	
	public boolean validate(report.AttendanceStackRank attendanceStackRank) 
	{
		return validateBasicStackReport(attendanceStackRank); 
	}
	
	public boolean validate(report.CallVolume callVolume) 
	{
		return validateBasicMetricReport(callVolume);
	}
	
	public boolean validate(report.Conversion conversion) 
	{
		return validateBasicMetricReport(conversion);
	}
	
	public boolean validate(report.IVRAgentCSATDetail ivrAgentCSATDetail) 
	{
		return validateBasicMetricReport(ivrAgentCSATDetail);
	}
	
	public boolean validate(report.IVRCSAT ivrcsat) 
	{
		return validateBasicMetricReport(ivrcsat);
	}
	
	public boolean validate(report.IVRDSATCaseCount ivrdsatCaseCount) 
	{
		return validateBasicMetricReport(ivrdsatCaseCount);
	}
	
	public boolean validate(report.IVRDSATCases ivrdsatCases) 
	{
		return validateBasicMetricReport(ivrdsatCases);
	}
	
	public boolean validate(report.LateDays lateDays) 
	{
		return validateBasicMetricReport(lateDays);
	}
	
	public boolean validate(report.LMICSAT lmicsat) 
	{
		return validateTimeInterval(lmicsat) && isValidTimeGrain(lmicsat); 
	}
	
	public boolean validate(report.LMIDSATCaseCount lmidsatCaseCount) 
	{
		return validateTimeInterval(lmidsatCaseCount) && isValidTimeGrain(lmidsatCaseCount); 
	}
	
	public boolean validate(report.LMIDSATCases lmidsatCases) 
	{
		return validateTimeInterval(lmidsatCases);
	}
	
	public boolean validate(report.MinutesLate minutesLate) 
	{
		return validateBasicMetricReport(minutesLate);
	}
	
	public boolean validate(report.MinutesWorked minutesWorked) 
	{
		return validateBasicMetricReport(minutesWorked);
	}
	
	public boolean validate(report.NoSaleDrivers noSaleDrivers) 
	{
		return validateBasicDriversReport(noSaleDrivers);
	}
	
	public boolean validate(report.OpenedCases openedCases) 
	{
		return validateBasicMetricReport(openedCases);
	}
	
	public boolean validate(report.PINConsumptionRate pinConsumption) 
	{
		return validateBasicMetricReport(pinConsumption);
	}
	
	public boolean validate(report.PINsConsumedCount piNsConsumed) 
	{
		return validateBasicMetricReport(piNsConsumed);
	}
	
	public boolean validate(report.RefundCount refundCount) 
	{
		return validateBasicMetricReport(refundCount);
	}
	
	public boolean validate(report.RefundTotals refundTotals) 
	{
		return validateBasicMetricReport(refundTotals);
	}
	
	public boolean validate(report.SalesCount salesCount) 
	{
		return validateBasicMetricReport(salesCount);
	}
	
	public boolean validate(report.SalesDocumentationCount salesDocumentationCount) 
	{
		return validateBasicMetricReport(salesDocumentationCount);
	}
	
	public boolean validate(report.SalesDocumentationRate salesDocumentationRate) 
	{
		return validateBasicMetricReport(salesDocumentationRate);
	}

	public boolean validate(report.SalesStackRank salesStackRank) 
	{
		return validateBasicStackReport(salesStackRank); 
	}
	
	public boolean validate(report.ScheduleAdherence scheduleAdherence) 
	{
		return validateBasicMetricReport(scheduleAdherence);
	}
	
	public boolean validate(report.Schedules schedules) 
	{
		return validateScheduleReport(schedules); 
	}
	
	public boolean validate(report.TopRefundDrivers topRefundDrivers) 
	{
		return validateBasicDriversReport(topRefundDrivers);
	}
	
	public boolean validate(report.RevenuePerCall revenuePerCall) 
	{
		return validateBasicMetricReport(revenuePerCall);
	}
	
	public boolean validate(report.UpdatedCases updatedCases) 
	{
		return validateBasicMetricReport(updatedCases);
	}

	
	public boolean validate(report.AgentProgress agentProgress) 
	{
		agentProgress.setParameter(Report.REPORT_TYPE_PARAM, Report.AGENT_TIME_REPORT);
		agentProgress.setParameter(Report.ROSTER_TYPE_PARAM, Roster.ALL_ROSTER);
		return validateBasicMetricReport(agentProgress);
	}
	
	public boolean validate(report.NetRevenue netRevenue) 
	{
		return validateBasicMetricReport(netRevenue);
	}
	
	public boolean validate(report.NetSalesCount netSales) 
	{
		return validateBasicMetricReport(netSales);
	}
	
	public boolean validate(report.SoldIssues casesUpdated) 
	{
		return validateBasicMetricReport(casesUpdated);
	}
	
	public boolean validate(report.UsedIssues usedIssues) 
	{
		return validateBasicMetricReport(usedIssues);
	}
	
	public boolean validate(report.RedemptionRate redemptionRate) 
	{
		return validateBasicMetricReport(redemptionRate);
	}
	
	public boolean validate(report.CallsPerCase callsPerCase) 
	{
		return validateBasicMetricReport(callsPerCase);
	}
	
	public boolean validate(report.CreatedCases createdCases) 
	{
		return validateBasicMetricReport(createdCases);
	}
	
	private boolean validateAgentName(String agentName)
	{
		boolean retval = false;
		
		if(agentName != null && roster != null)
		{
			retval = roster.getUser(agentName) != null;
		}
		
		return retval;
	}

	private boolean validateRosterType(Report report)
	{
		int rosterType;
		boolean retval = false;
		try
		{
			rosterType =  Integer.parseInt(report.getParameter(Report.ROSTER_TYPE_PARAM));

			retval = 
					(rosterType == Roster.SUPPORT_ROSTER ||
					rosterType == Roster.ACTIVE_SALES_ROSTER ||
					rosterType == Roster.ACTIVE_ROSTER ||
					rosterType == Roster.ACTIVE_SUPPORT_ROSTER ||
					rosterType == Roster.SALES_ROSTER ||
					rosterType == Roster.ALL_ROSTER); 
		}
		finally	{}
		
		return retval;
	}
	
	private boolean validateBasicDriversReport(Report report)
	{
		boolean retval = false;
		boolean validateAgentName = false;
		boolean validateTeamName = false;
		boolean validateTimeGrain = false;
		boolean validateTimeInterval = false;

		try
		{
			int reportType = Integer.parseInt(report.getParameter(Report.REPORT_TYPE_PARAM));
			
			if(Validation.hasValidReportType(reportType, new int[]{Report.AGENT_TIME_REPORT, Report.ALL_STAFF_TIME_REPORT, Report.TEAM_TIME_REPORT}))
			{
				switch(reportType)
				{
				case Report.AGENT_TIME_REPORT:
					validateAgentName = true;
				case Report.ALL_STAFF_TIME_REPORT:
					validateTimeGrain = true;
					report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.ALL_ROSTER);
					validateTimeInterval = true;
					break;
				case Report.TEAM_TIME_REPORT:
					validateTimeGrain = true;
					validateTimeInterval = true;
					validateTeamName = true;
					break;
				default:
					//nothing, verified valid by isValidReportType
					break;
				}
				
				if(!hasValidNumDrivers(report))
				{
					throw new ParameterValidationException("Invalid drivers parameter");
				}
			
				if(validateTimeGrain)
				{
					int timeGrain = Integer.parseInt(report.getParameter(Report.TIME_GRAIN_PARAM));
					
					if(!Validation.hasValidTimeGrain(timeGrain))
					{
						throw new ParameterValidationException("Invalid time grain." );
					}
				}

				if(validateTimeInterval)
				{
					if(!validateTimeInterval(report))
					{
						throw new ParameterValidationException("Invalid Time Interval parameter");
					}
				}

				if(validateTeamName)
				{

				}


				if(validateRosterType(report))
				{
					roster = new Roster();
					roster.setChildReport(true);

					roster.setParameter(Report.ROSTER_TYPE_PARAM, report.getParameter(Report.ROSTER_TYPE_PARAM));
					roster.load();

					if(validateAgentName)
					{
						String agentName = report.getParameter(Report.AGENT_NAME_PARAM);

						if(!validateAgentName(agentName))
						{
							throw new ParameterValidationException("Agent name not found in roster" );
						}
					}
					
					retval = true;
				}
				else
				{
					throw new ParameterValidationException("Invalid roster type" );
				}
			}
			else
			{
				throw new ParameterValidationException("Invalid report type" );
			}
		}
		catch (ReportSetupException e) 
		{
			report.setErrorMsg("ReportSetupException: " + e.getMessage() +" Failed running roster subreport");
			retval = false;
		}
		catch (ParameterValidationException e)
		{
			report.setErrorMsg("ParameterValidationException: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		catch (Exception e)
		{
			report.setErrorMsg("Exception: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		finally
		{
			//only close the roster if validation failed, otherwise the caller report will use it via getRoster()
			if(retval == false && roster != null)
			{
				roster.close();
			}
		}

		return retval;
	}
	
	private boolean validateBasicMetricReport(Report report)
	{
		boolean retval = false;
		boolean validateAgentName = false;
		boolean validateTeamName = false;
		boolean validateTimeGrain = false;
		boolean validateTimeInterval = false;

		try
		{
			int reportType = Integer.parseInt(report.getParameter(Report.REPORT_TYPE_PARAM));
			
			if(	Validation.hasValidReportType(reportType, new int[]{Report.ALL_STAFF_STACK_REPORT,Report.AGENT_STACK_REPORT, Report.TEAM_STACK_REPORT, Report.AGENT_TIME_REPORT, Report.ALL_STAFF_TIME_REPORT, Report.TEAM_TIME_REPORT}))
			{
				switch(reportType)
				{
				case Report.AGENT_TIME_REPORT:
					validateAgentName = true;
				case Report.ALL_STAFF_TIME_REPORT:
					validateTimeGrain = true;
				case Report.ALL_STAFF_STACK_REPORT:
					report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.ALL_ROSTER);
				case Report.AGENT_STACK_REPORT:
					validateTimeInterval = true;
					break;
				case Report.TEAM_TIME_REPORT:
					validateTimeGrain = true;
				case Report.TEAM_STACK_REPORT:
					validateTimeInterval = true;
					validateTeamName = true;
					break;
				default:
					//nothing, verified valid by isValidReportType
					break;
				}
			
				if(validateTimeGrain)
				{
					int timeGrain = Integer.parseInt(report.getParameter(Report.TIME_GRAIN_PARAM));
					
					if(!Validation.hasValidTimeGrain(timeGrain))
					{
						throw new ParameterValidationException("Invalid time grain." );
					}
				}

				if(validateTimeInterval)
				{
					if(!validateTimeInterval(report))
					{
						throw new ParameterValidationException("Invalid Time Interval parameter.");
					}
				}

				if(validateTeamName)
				{

				}


				if(validateRosterType(report))
				{
					roster = new Roster();
					roster.setChildReport(true);

					roster.setParameter(Report.ROSTER_TYPE_PARAM, report.getParameter(Report.ROSTER_TYPE_PARAM));
					roster.load();

					if(validateAgentName)
					{
						String agentName = report.getParameter(Report.AGENT_NAME_PARAM);

						if(!validateAgentName(agentName))
						{
							throw new ParameterValidationException("Agent name not found in roster" );
						}
					}
					
					retval = true;
				}
				else
				{
					throw new ParameterValidationException("Invalid roster type" );
				}
			}
			else
			{
				throw new ParameterValidationException("Invalid report type" );
			}
		}
		catch (ReportSetupException e) 
		{
			report.setErrorMsg("ReportSetupException: " + e.getMessage() +" Failed running roster subreport");
			retval = false;
		}
		catch (ParameterValidationException e)
		{
			report.setErrorMsg("ParameterValidationException: " + e.getMessage());	
			retval = false;
		}
		catch (Exception e)
		{
			report.setErrorMsg("Exception: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		finally
		{
			//only close the roster if validation failed, otherwise the caller report will use it via getRoster()
			if(retval == false && roster != null)
			{
				roster.close();
			}
		}

		return retval;
	}
	
	private boolean validateBasicStackReport(Report report)
	{
		boolean retval = false;
		boolean validateTeamName = false;
		boolean validateTimeInterval = false;

		try
		{
			int reportType = Integer.parseInt(report.getParameter(Report.REPORT_TYPE_PARAM));
			
			if(	Validation.hasValidReportType(reportType, new int[]{Report.ALL_STAFF_STACK_REPORT,Report.AGENT_STACK_REPORT, Report.TEAM_STACK_REPORT}))
			{
				switch(reportType)
				{
				case Report.ALL_STAFF_STACK_REPORT:
					report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.ALL_ROSTER);
				case Report.AGENT_STACK_REPORT:
					validateTimeInterval = true;
					break;
				case Report.TEAM_STACK_REPORT:
					validateTimeInterval = true;
					validateTeamName = true;
					break;
				default:
					//nothing, verified valid by isValidReportType
					break;
				}

				if(validateTimeInterval)
				{
					if(!validateTimeInterval(report))
					{
						throw new ParameterValidationException("Invalid Time Interval parameter.");
					}
				}

				if(validateTeamName)
				{

				}


				if(validateRosterType(report))
				{
					roster = new Roster();
					roster.setChildReport(true);

					roster.setParameter(Report.ROSTER_TYPE_PARAM, report.getParameter(Report.ROSTER_TYPE_PARAM));
					roster.load();
					
					retval = true;
				}
				else
				{
					throw new ParameterValidationException("Invalid roster type" );
				}
			}
			else
			{
				throw new ParameterValidationException("Invalid report type" );
			}
		}
		catch (ReportSetupException e) 
		{
			report.setErrorMsg("ReportSetupException: " + e.getMessage() +" Failed running roster subreport");
			retval = false;
		}
		catch (ParameterValidationException e)
		{
			report.setErrorMsg("ParameterValidationException: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		catch (Exception e)
		{
			report.setErrorMsg("Exception: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		finally
		{
			//only close the roster if validation failed, otherwise the caller report will use it via getRoster()
			if(retval == false && roster != null)
			{
				roster.close();
			}
		}
		
		return retval;
	}
	
	private boolean validateScheduleReport(Report report)
	{
		boolean retval = false;
		boolean validateTeamName = false;
		boolean validateTimeInterval = false;

		try
		{
			int reportType = Integer.parseInt(report.getParameter(Report.REPORT_TYPE_PARAM));
			
			if(	Validation.hasValidReportType(reportType, new int[]{Report.AGENT_TIME_REPORT, Report.ALL_STAFF_STACK_REPORT,Report.AGENT_STACK_REPORT, Report.TEAM_STACK_REPORT}))
			{
				switch(reportType)
				{
				case Report.AGENT_TIME_REPORT:
				case Report.ALL_STAFF_STACK_REPORT:
					report.setParameter(Report.ROSTER_TYPE_PARAM, "" + Roster.ALL_ROSTER);
				case Report.AGENT_STACK_REPORT:
					validateTimeInterval = true;
					break;
				case Report.TEAM_STACK_REPORT:
					validateTimeInterval = true;
					validateTeamName = true;
					break;
				default:
					//nothing, verified valid by isValidReportType
					break;
				}

				if(validateTimeInterval)
				{
					if(!validateTimeInterval(report))
					{
						throw new ParameterValidationException("Invalid Time Interval parameter.");
					}
				}

				if(validateTeamName)
				{

				}


				if(validateRosterType(report))
				{
					roster = new Roster();
					roster.setChildReport(true);

					roster.setParameter(Report.ROSTER_TYPE_PARAM, report.getParameter(Report.ROSTER_TYPE_PARAM));
					roster.load();
					
					retval = true;
				}
				else
				{
					throw new ParameterValidationException("Invalid roster type" );
				}
			}
			else
			{
				throw new ParameterValidationException("Invalid report type" );
			}
		}
		catch (ReportSetupException e) 
		{
			report.setErrorMsg("ReportSetupException: " + e.getMessage() +" Failed running roster subreport");
			retval = false;
		}
		catch (ParameterValidationException e)
		{
			report.setErrorMsg("ParameterValidationException: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		catch (Exception e)
		{
			report.setErrorMsg("Exception: " + e.getMessage() + " processing report parameters");	
			retval = false;
		}
		finally
		{
			//only close the roster if validation failed, otherwise the caller report will use it via getRoster()
			if(retval == false && roster != null)
			{
				roster.close();
			}
		}
		
		return retval;
	}
	
	/**
	 * Validate the report's number-of-drivers parameter.
	 *  
	 * @return	True if the number-of-drivers parameter are valid and viable to run the report. False otherwise.
	 */
	private boolean hasValidNumDrivers(Report report)
	{
		boolean retval = false;

		try
		{
			int numDrivers = Integer.parseInt(report.getParameter(Report.NUM_DRIVERS_PARAM));
			if(	numDrivers > 0)
			{
				retval = true;
			}
		}
		catch(NullPointerException e)
		{
			report.setErrorMsg( "Num Drivers parameter missing." );
		}
//		catch (NumberFormatException e)	
//		{
//			report.setErrorMsg( "Invalid Num Drivers parameter." );
//		}
		catch(Exception e)
		{
			report.setErrorMsg( "Invalid Num Drivers parameter." );
		}
		
		return retval;
	}
	
	private boolean isValidTimeGrain(Report report)
	{
		boolean retval = false;
		try
		{
			int timeGrain = Integer.parseInt(report.getParameter(Report.TIME_GRAIN_PARAM));
			retval = Validation.hasValidTimeGrain(timeGrain);
		}
		finally
		{}
		
		return retval;
	}

	/**
	 * Validate the report's date interval parameters.
	 *  
	 * @return	True if the date interval parameters are valid and viable to run the report. False otherwise.
	 */
	private boolean validateTimeInterval(Report report)
	{
		boolean retval = false;

		GregorianCalendar gregorianStartDate = null;
		GregorianCalendar gregorianEndDate= null;

		String startDate = null , endDate = null;
		
		try
		{
			startDate = Validation.validateDate(report.getParameter(Report.START_DATE_PARAM));
			endDate= Validation.validateDate(report.getParameter(Report.END_DATE_PARAM));
			
			gregorianStartDate = dateParser.convertSQLDateToGregorian(startDate);
			gregorianEndDate = dateParser.convertSQLDateToGregorian(endDate);
			
			if(gregorianStartDate.after(gregorianEndDate))
			{
				//correct the interval
				report.setParameter(Report.START_DATE_PARAM, endDate);
				report.setParameter(Report.END_DATE_PARAM, startDate);
			}
			else
			{
				report.setParameter(Report.START_DATE_PARAM, startDate);
				report.setParameter(Report.END_DATE_PARAM, endDate);
			}

			retval = true;
		}
		finally
		{}

		return retval;
	}
	
	public void setRoster(Roster roster)
	{
		this.roster = roster;
	}
	
	public Roster getRoster()
	{
		return roster;
	}



	


























































}
