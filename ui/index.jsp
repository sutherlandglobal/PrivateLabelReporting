<%@include file="/ui/header.jsp"%>
<%@ page import="ui.date.DateUtil"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.FileOutputStream"%>

<%
	GregorianCalendar now = new GregorianCalendar();

	DateUtil dateUtil = new DateUtil(now);

	String nowParam = dateUtil.convertGregorianToMySQL(now);
	
	String todayParam = dateUtil.getDateByName(DateUtil.TODAY_START);
	String yesterdayStartParam = dateUtil.getDateByName(DateUtil.YESTERDAY_START);
	String yesterdayEndParam = dateUtil.getDateByName(DateUtil.YESTERDAY_END);
	String thisWeekStartParam = dateUtil.getDateByName(DateUtil.THIS_WEEK_START);
	String lastWeekStartParam = dateUtil.getDateByName(DateUtil.LAST_WEEK_START);
	String lastWeekEndParam = dateUtil.getDateByName(DateUtil.LAST_WEEK_END);
	String thisMonthStartParam = dateUtil.getDateByName(DateUtil.THIS_MONTH_START);
	String lastMonthStartParam = dateUtil.getDateByName(DateUtil.LAST_MONTH_START);
	String lastMonthEndParam = dateUtil.getDateByName(DateUtil.LAST_MONTH_END);
	String thisFiscalQuarterStartParam = dateUtil.getDateByName(DateUtil.THIS_FISCAL_QUARTER_START);
	String lastFiscalQuarterStartParam = dateUtil.getDateByName(DateUtil.LAST_FISCAL_QUARTER_START);
	String lastFiscalQuarterEndParam = dateUtil.getDateByName(DateUtil.LAST_FISCAL_QUARTER_END);
	String thisFiscalYearStartParam = dateUtil.getDateByName(DateUtil.THIS_FISCAL_YEAR_START);
	String lastFiscalYearStartParam = dateUtil.getDateByName(DateUtil.LAST_FISCAL_YEAR_START);
	String lastFiscalYearEndParam = dateUtil.getDateByName(DateUtil.LAST_FISCAL_YEAR_END);
	String thisYearStartParam = dateUtil.getDateByName(DateUtil.THIS_YEAR_START);
	String lastYearStartParam = dateUtil.getDateByName(DateUtil.LAST_YEAR_START);
	String lastYearEndParam = dateUtil.getDateByName(DateUtil.LAST_YEAR_END);

	
	
	String reportURLPrefix = "/frameset?__report=reports/";

	String rosterPath = reportURLPrefix + "Roster.rptdesign";
	String topCaseDriversPath = reportURLPrefix + "TopCaseDrivers.rptdesign";
	String pinMinutesUsedPath = reportURLPrefix + "PINMinutesUsed.rptdesign";
	String caseUpdatedRatioPath = reportURLPrefix + "CaseUpdatedRatio.rptdesign";
	String salesStackRankPath = reportURLPrefix + "SalesStackRank.rptdesign";
	String techStackRankPath = reportURLPrefix + "TechStackRank.rptdesign";
	String realtimeSalesTeamPath = reportURLPrefix + "RealtimeSales.rptdesign";
	String refundCountPath = reportURLPrefix + "RefundCount.rptdesign";
	String refundTotalsPath = reportURLPrefix + "RefundTotals.rptdesign";
	String topRefundDriversPath = reportURLPrefix + "TopRefundDrivers.rptdesign";
	String noSaleDriversPath = reportURLPrefix + "NoSaleDrivers.rptdesign";
	String aovPath = reportURLPrefix + "AverageOrderValue.rptdesign";
	String IVRCSATPath = reportURLPrefix + "IVRCSAT.rptdesign";
	String LMICSATPath = reportURLPrefix + "LMICSAT.rptdesign";
	String IVRAgentCSATPath = reportURLPrefix + "IVRAgentCSAT.rptdesign";
	String IVRAgentCSATDetailPath = reportURLPrefix + "IVRAgentCSATDetail.rptdesign";
	String callVolumePath = reportURLPrefix + "CallVolume.rptdesign";
	String conversionPath = reportURLPrefix + "Conversion.rptdesign";
	String salesCountPath = reportURLPrefix + "SalesCount.rptdesign";
	String schAdherencePath = reportURLPrefix	+ "ScheduleAdherence.rptdesign";
	String schedulePath = reportURLPrefix + "Schedule.rptdesign";
	String lateDaysPath = reportURLPrefix + "LateDays.rptdesign";
	String minsLatePath = reportURLPrefix + "MinutesLate.rptdesign";
	String minsWorkedPath = reportURLPrefix + "MinutesWorked.rptdesign";
	String attendanceStackPath = reportURLPrefix + "AttendanceStackRank.rptdesign";
	String IVRDSATCasesPath = reportURLPrefix + "IVRDSATCases.rptdesign";
	String IVRDSATCaseCountPath = reportURLPrefix + "IVRDSATCaseCount.rptdesign";
	String LMIDSATCasesPath = reportURLPrefix + "LMIDSATCases.rptdesign";
	String LMIDSATCaseCountPath = reportURLPrefix 	+ "LMIDSATCaseCount.rptdesign";
	String AggregateDSATCaseCountPath = reportURLPrefix	+ "AggregateDSATCaseCount.rptdesign";
	String salesDocumentationCountPath = reportURLPrefix	+ "SalesDocumentationCount.rptdesign";
	String salesDocumentationRatePath = reportURLPrefix	+ "SalesDocumentationRate.rptdesign";
	String PINConsumptionRatePath = reportURLPrefix	+ "PINConsumptionRate.rptdesign";
	String PINsConsumedCountPath = reportURLPrefix+ "PINsConsumedCount.rptdesign";
	String openedCasesPath = reportURLPrefix + "OpenedCases.rptdesign";
	String rpcPath = reportURLPrefix + "RevenuePerCall.rptdesign";
	String teamsPath = reportURLPrefix + "Teams.rptdesign";
	String agentProgressPath = reportURLPrefix	+ "AgentProgress.rptdesign";
	String netRevenuePath = reportURLPrefix + "NetRevenue.rptdesign";
	String netSalesCountPath = reportURLPrefix + "NetSalesCount.rptdesign";
	
	String usedIssuesPath = reportURLPrefix + "UsedIssues.rptdesign";
	String soldIssuesPath = reportURLPrefix + "SoldIssues.rptdesign";
	String updatedCasesPath = reportURLPrefix + "UpdatedCases.rptdesign";
	String createdCasesPath = reportURLPrefix + "CreatedCases.rptdesign";
	String redemptionRatePath = reportURLPrefix + "RedemptionRate.rptdesign";
	String callsPerCasePath = reportURLPrefix + "CallsPerCase.rptdesign";
	
	
%>

<script type="text/javascript">

function redirReport(reportURL, reportType, rosterType,  timeInterval, timeGrain, otherVars)
{
	
	//collect info from the html elements and generate a url for the desired report


	if(reportType == "Agent Stack")
	{
		reportURL += "&reportType=1"; 
		otherVars += "&agentName=null";
	}
	else 	if(reportType == "Agent Time")
	{
		reportURL += "&reportType=2"; 
	}
	else 	if(reportType == "Team Stack")
	{
		reportURL += "&reportType=3"; 
		otherVars += "&agentName=null";
	}
	else 	if(reportType == "Team Time")
	{
		reportURL += "&reportType=4"; 
		otherVars += "&agentName=null";
	}

	if(rosterType == "Support")
	{
		reportURL += "&rosterType=1"; 
	}
	else 	if(rosterType == "Active Support")
	{
		reportURL += "&rosterType=2"; 
	}
	else 	if(rosterType == "Sales")
	{
		reportURL += "&rosterType=3"; 
	}
	else 	if(rosterType == "Active Sales")
	{
		reportURL += "&rosterType=4"; 
	}
	else 	if(rosterType == "All")
	{
		reportURL += "&rosterType=5"; 
	}
	else 	if(rosterType == "Active")
	{
		reportURL += "&rosterType=6"; 
	}
	
	if(timeInterval == "Today")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=todayParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=nowParam%>";
		reportURL += "now";
	}
	else if(timeInterval == "Yesterday")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=yesterdayStartParam%>";
		reportURL += "&endDate=";
		reportURL += "<%=yesterdayEndParam%>";
	}
	else if(timeInterval == "This Week")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=thisWeekStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=nowParam%>";
		reportURL += "now";
	}
	else if(timeInterval == "Last Week")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=lastWeekStartParam%>";
		reportURL += "&endDate=";
		reportURL += "<%=lastWeekEndParam%>";
	}
	else if(timeInterval == "This Month")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=thisMonthStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=nowParam%>";
		reportURL += "now";
		
	}
	else if(timeInterval == "Last Month")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=lastMonthStartParam%>";
		reportURL += "&endDate=";
		reportURL += "<%=lastMonthEndParam%>";
	}
	
	else if(timeInterval == "This Fiscal Quarter")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=thisFiscalQuarterStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=nowParam%>";
		reportURL += "now";
	}
	else if(timeInterval == "Last Fiscal Quarter")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=lastFiscalQuarterStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=lastFiscalQuarterEndParam%>";
		reportURL += "now";
	}
	
	else if(timeInterval == "This Fiscal Year")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=thisFiscalYearStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=nowParam%>";
		reportURL += "now";
	}
	
	else if(timeInterval == "Last Fiscal Year")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=lastFiscalYearStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=lastFiscalYearEndParam%>";
		reportURL += "now";
	}
	
	else if(timeInterval == "This Year")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=thisYearStartParam%>";
		reportURL += "&endDate=";
		//reportURL += "<%=nowParam%>";
		reportURL += "now";
	}
	else if(timeInterval == "Last Year")
	{
		reportURL += "&startDate="; 
		reportURL += "<%=lastYearStartParam%>";
		reportURL += "&endDate=";
		reportURL += "<%=lastYearEndParam%>";
	}
	
	if(timeGrain == "Yearly")
	{
		reportURL += "&timeGrain=0"; 
	}
	else if(timeGrain == "Quarterly")
	{
		reportURL += "&timeGrain=5"; 
	}
	else if(timeGrain == "Monthly")
	{
		reportURL += "&timeGrain=1"; 
	}
	else if(timeGrain == "Weekly")
	{
		reportURL += "&timeGrain=2"; 
	}
	else if(timeGrain == "Daily")
	{
		reportURL += "&timeGrain=3"; 
	}
	else if(timeGrain == "Hourly")
	{
		reportURL += "&timeGrain=4"; 
	}
	
	if(otherVars != "")
	{
		reportURL += otherVars;
	}

	window.open(reportURL);
}
</script>

<%
	String reportUrl, buttonName, selectName;
%>

<jsp:useBean id="ui" class="ui.UIReport" scope="session"/>
<jsp:setProperty name="ui" property="*"/> 

<table width="100%" border="1">
	<th colspan="2">Reports</th>
	<tr>
		<!--  column one -->

		<%
		LinkedHashMap<String,String> rosterTypes = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> reportTypes = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> timeIntervals = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> timeGrains = new LinkedHashMap<String, String>();
		%>

		<td width="50%" valign="top">
		<b>Roster</b>
		<ul>				
			
			<% 
				//ui.reset();
				
				ui.clearOptions();
			
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active Sales");
				
				
				ui.setName("Roster");
				ui.setReportUrl(request.getContextPath() + rosterPath);
				ui.setRosterTypeOptions(rosterTypes);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
								
				ui.setName("Teams");
				ui.setReportUrl(request.getContextPath() + teamsPath);
				out.println(ui.getReportHtml());
			%>
		</ul>

		<!------------------------------------------------------>
		<hr size="5" color="blue">
		<b>Schedule</b>
		<ul>				
			
			<% 
				ui.clearOptions();
			
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");

				
				ui.setName("Schedule");
				ui.setReportUrl(request.getContextPath() + schedulePath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
				
				out.println("<br>");
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Schedule Adherence");
				ui.setReportUrl(request.getContextPath() + schAdherencePath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeGrains(timeGrains);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
				
				out.println("<br>");
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Late Days");
				ui.setReportUrl(request.getContextPath() + lateDaysPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeGrains(timeGrains);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
				
				out.println("<br>");
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Minutes Late");
				ui.setReportUrl(request.getContextPath() + minsLatePath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeGrains(timeGrains);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
				
				out.println("<br>");
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Minutes Worked");
				ui.setReportUrl(request.getContextPath() + minsWorkedPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeGrains(timeGrains);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
			%>
		</ul>

		<!------------------------------------------------------>
		<hr size="5" color="blue">
		<b>Revenue</b>

		<ul>
				<%
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");
				
				ui.setName("Real-Time Sales");
				ui.setReportUrl(request.getContextPath() + realtimeSalesTeamPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeGrains(timeGrains);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");
				
				ui.setName("Revenue Per Call");
				ui.setReportUrl(request.getContextPath() + rpcPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeGrains(timeGrains);
				ui.setTimeIntervals(timeIntervals);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Average Order Value");
				ui.setReportUrl(request.getContextPath() + aovPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeIntervals(timeIntervals);
				ui.setTimeGrains(timeGrains);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Sales Count");
				ui.setReportUrl(request.getContextPath() + salesCountPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeIntervals(timeIntervals);
				ui.setTimeGrains(timeGrains);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Conversion");
				ui.setReportUrl(request.getContextPath() + conversionPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeIntervals(timeIntervals);
				ui.setTimeGrains(timeGrains);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Net Revenue");
				ui.setReportUrl(request.getContextPath() + netRevenuePath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeIntervals(timeIntervals);
				ui.setTimeGrains(timeGrains);
				out.println(ui.getReportHtml());
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Stack", "Agent Stack");
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Stack", "Team Stack");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("Net Sales Count");
				ui.setReportUrl(request.getContextPath() + netSalesCountPath);
				ui.setRosterTypeOptions(rosterTypes);
				ui.setReportTypeOptions(reportTypes);
				ui.setTimeIntervals(timeIntervals);
				ui.setTimeGrains(timeGrains);
				out.println(ui.getReportHtml());
			%>

		</ul>
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>Refunds</b>

		<ul>
			<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Refund Totals");
			ui.setReportUrl(request.getContextPath() + refundTotalsPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Refund Count");
			ui.setReportUrl(request.getContextPath() + refundCountPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			%>
		</ul>
		
		<!------------------------------------------------------>
		<hr size="5" color="blue">
		<b>Top Case Drivers</b> 

		<ul>
		
			<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Top Case Drivers");
			ui.setReportUrl(request.getContextPath() + topCaseDriversPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());

			out.println("<br>");
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			ui.setExtraVars("&numDrivers=5");
			
			ui.setName("Top 5 Case Drivers");
			ui.setReportUrl(request.getContextPath() + topCaseDriversPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			%>


		</ul>
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>Top Refund Drivers</b>

		<ul>
			
			<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Top Refund Drivers");
			ui.setReportUrl(request.getContextPath() + topRefundDriversPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());

			out.println("<br>");
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			ui.setExtraVars("&numDrivers=5");
			
			ui.setName("Top 5 Refund Drivers");
			ui.setReportUrl(request.getContextPath() + topRefundDriversPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			%>
		</ul>
		
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>No Sale Drivers</b> 
			<ul>
			
			<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Top No Sale Drivers");
			ui.setReportUrl(request.getContextPath() + noSaleDriversPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());

			out.println("<br>");
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			ui.setExtraVars("&numDrivers=5");
			
			ui.setName("Top 5 No Sale Drivers");
			ui.setReportUrl(request.getContextPath() + noSaleDriversPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			%>
		</ul>
		
		</td>
		<!------------------------------------------------------>
		<!--  column two -->
		<td width="50%" valign="top">
		
		<!------------------------------------------------------>

		<b>Volume</b>

		<ul>
		
			<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Call Volume");
			ui.setReportUrl(request.getContextPath() + callVolumePath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Opened Cases");
			ui.setReportUrl(request.getContextPath() + openedCasesPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Created Cases");
			ui.setReportUrl(request.getContextPath() + createdCasesPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Updated Cases");
			ui.setReportUrl(request.getContextPath() + updatedCasesPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Used Issues");
			ui.setReportUrl(request.getContextPath() + usedIssuesPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Sold Issues");
			ui.setReportUrl(request.getContextPath() + soldIssuesPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Redemption Rate");
			ui.setReportUrl(request.getContextPath() + redemptionRatePath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Calls Per Case");
			ui.setReportUrl(request.getContextPath() + callsPerCasePath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			

			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("PINs Consumed Count");
			ui.setReportUrl(request.getContextPath() + PINsConsumedCountPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("PIN Consumption Rate");
			ui.setReportUrl(request.getContextPath() + PINConsumptionRatePath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			%>

		</ul>

		<!------------------------------------------------------>
		<hr size="5" color="blue">
		<b>Documentation</b> 

		<ul>
		
			<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Sales Documentation Count");
			ui.setReportUrl(request.getContextPath() + salesDocumentationCountPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());

			out.println("<br>");
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");
			
			ui.setName("Sales Documentation Rate");
			ui.setReportUrl(request.getContextPath() + salesDocumentationRatePath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			%>


		</ul>

		
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>CSAT</b> 
		<ul>
		<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("IVR");
			ui.setReportUrl(request.getContextPath() + IVRCSATPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
				
				out.println("<br>");
				
				ui.clearOptions();
				
				rosterTypes.clear();
				rosterTypes.put("Support", "Support");
				rosterTypes.put("Active Support", "Active Support");
				rosterTypes.put("Sales", "Sales");
				rosterTypes.put("Active Sales", "Active Sales");
				rosterTypes.put("All", "All");
				rosterTypes.put("Active", "Active");	
				
				reportTypes.clear();
				reportTypes.put("Agent Time", "Agent Time");
				reportTypes.put("Team Time", "Team Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();

				
				ui.setName("IVR Agent CSAT Detail");
				ui.setReportUrl(request.getContextPath() + IVRAgentCSATDetailPath);
				ui.setTimeIntervals(timeIntervals);
				ui.setReportTypeOptions(reportTypes);
				ui.setRosterTypeOptions(rosterTypes);

				out.println(ui.getReportHtml());
				
				
				out.println("<br>");
				
				ui.clearOptions();
				
				rosterTypes.clear();
				
				reportTypes.clear();
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
				timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
				timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
				timeIntervals.put("This Fiscal Year", "This Fiscal Year");
				timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
				timeGrains.put("Quarterly", "Quarterly");
				timeGrains.put("Monthly", "Monthly");
				timeGrains.put("Weekly", "Weekly");
				timeGrains.put("Daily", "Daily");
				timeGrains.put("Hourly", "Hourly");

				
				ui.setName("LMI CSAT");
				ui.setReportUrl(request.getContextPath() + LMICSATPath);
				ui.setTimeIntervals(timeIntervals);
				ui.setTimeGrains(timeGrains);
				out.println(ui.getReportHtml());
			%>

		</ul>
				<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>DSAT</b> 
		<ul>
		<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();

			
			ui.setName("IVR DSAT Cases");
			ui.setReportUrl(request.getContextPath() + IVRDSATCasesPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			out.println(ui.getReportHtml());
			
				
			out.println("<br>");
				
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Agent Time", "Agent Time");
			reportTypes.put("Team Stack", "Team Stack");
			reportTypes.put("Team Time", "Team Time");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("IVR DSAT Case Count");
			ui.setReportUrl(request.getContextPath() + IVRDSATCaseCountPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
				
			out.println("<br>");
			
			ui.clearOptions();
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");
			
			ui.setName("LMI DSAT Cases");
			ui.setReportUrl(request.getContextPath() + LMIDSATCasesPath);
			ui.setTimeIntervals(timeIntervals);
			out.println(ui.getReportHtml());
			
				
			out.println("<br>");
				
			ui.clearOptions();
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("LMI DSAT Case Count");
			ui.setReportUrl(request.getContextPath() + LMIDSATCaseCountPath);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
				
			out.println("<br>");
			
			ui.clearOptions();
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
			timeGrains.put("Quarterly", "Quarterly");
			timeGrains.put("Monthly", "Monthly");
			timeGrains.put("Weekly", "Weekly");
			timeGrains.put("Daily", "Daily");
			timeGrains.put("Hourly", "Hourly");

			
			ui.setName("Aggregate DSAT Case Count");
			ui.setReportUrl(request.getContextPath() + AggregateDSATCaseCountPath);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
				
			out.println("<br>");
			%>

		</ul>
		
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>Coaching</b> 
		<ul>
		<%
			ui.clearOptions();
			
			rosterTypes.clear();
			
			reportTypes.clear();
			
			timeIntervals.clear();
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
	
			ui.setName("Agent Progress");
			ui.setReportUrl(request.getContextPath() + agentProgressPath);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			out.println("<br>");
			%>

		</ul>
		
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>Stack Ranks</b> 
		<ul>
		<%
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active ");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Team Stack", "Team Stack");
			
			timeIntervals.clear();
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
	
			ui.setName("Sales");
			ui.setReportUrl(request.getContextPath() + salesStackRankPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			ui.setTimeGrains(timeGrains);
			out.println(ui.getReportHtml());
			
			out.println("<br>");
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			reportTypes.put("Team Stack", "Team Stack");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			
			ui.setName("Attendance");
			ui.setReportUrl(request.getContextPath() + attendanceStackPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			out.println(ui.getReportHtml());
			
			out.println("<br>");
			
			ui.clearOptions();
			
			rosterTypes.clear();
			rosterTypes.put("Support", "Support");
			rosterTypes.put("Active Support", "Active Support");
			rosterTypes.put("Sales", "Sales");
			rosterTypes.put("Active Sales", "Active Sales");
			rosterTypes.put("All", "All");
			rosterTypes.put("Active", "Active");	
			
			reportTypes.clear();
			reportTypes.put("Agent Stack", "Agent Stack");
			//reportTypes.put("Team Stack", "Team Stack");
			
			timeIntervals.clear();
			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Fiscal Quarter", "This Fiscal Quarter");
			timeIntervals.put("Last Fiscal Quarter", "Last Fiscal Quarter");
			timeIntervals.put("This Fiscal Year", "This Fiscal Year");
			timeIntervals.put("Last Fiscal Year", "Last Fiscal Year");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();

			
			ui.setName("Tech");
			ui.setReportUrl(request.getContextPath() + techStackRankPath);
			ui.setRosterTypeOptions(rosterTypes);
			ui.setReportTypeOptions(reportTypes);
			ui.setTimeIntervals(timeIntervals);
			//out.println(ui.getReportHtml());
			
			out.println("<br>");

			%>

		</ul>

		</td>
	</tr>
</table>

<%@include file="ui/footer.jsp"%>

