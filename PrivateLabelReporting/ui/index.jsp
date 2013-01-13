<%@include file="/ui/header.jsp"%>

<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.FileOutputStream"%>


<%!public String convertGregorianToMySQL(GregorianCalendar date) {
		String retval = "" + date.get(Calendar.YEAR) + "-";

		if (date.get(Calendar.MONTH) + 1 < 10) {
			retval += "0";
		}

		retval += date.get(Calendar.MONTH) + 1 + "-";

		if (date.get(Calendar.DAY_OF_MONTH) < 10) {
			retval += "0";
		}

		retval += date.get(Calendar.DAY_OF_MONTH) + "+";

		if (date.get(Calendar.HOUR_OF_DAY) < 10) {
			retval += "0";
		}

		retval += date.get(Calendar.HOUR_OF_DAY) + ":";

		if (date.get(Calendar.MINUTE) < 10) {
			retval += "0";
		}

		retval += date.get(Calendar.MINUTE) + ":";

		if (date.get(Calendar.SECOND) < 10) {
			retval += "0";
		}

		retval += date.get(Calendar.SECOND);

		return retval;
	}

%>

<%
	//end date: now
	//start date. 24 hours prior

	GregorianCalendar now = new GregorianCalendar();

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
	//lastWeekStart.add(Calendar.DAY_OF_WEEK, 1 );

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
	//lastMonthEnd.add(Calendar.MONTH, );
	//last day of last month, is current month's first day, minus 1 day
	lastMonthEnd.set(Calendar.DAY_OF_MONTH, 1);
	//lastMonthEnd.add(Calendar.MONTH, -1 );
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

	String javaVersion = System.getProperty("java.version");
	
	String viewerVersion = "3.7.1";
	String engineVersion = "3.7.1";
	
	String tomcatVersion = application.getServerInfo();
	tomcatVersion =  tomcatVersion.substring(  tomcatVersion.indexOf("/") + 1);
%>

<table width="100%" border="1">
	<th>System Info</th>
	<th>Generated Parameters</th>
	<tr>
		<!--  column one -->
		<td width="50%" valign="top"><b>Viewer Version : </b><%=viewerVersion%><br>
		<b>Engine Version: </b><%= engineVersion %><br>
		<b>Tomcat Version: </b><%= tomcatVersion %><br>

		<%
			String javaVersionMessage = javaVersion;

			// check Java version
			String[] versionParts = javaVersion.split("\\.");
			int majorVersion = 0;
			int minorVersion = 0;
			try
			{
				majorVersion = Integer.parseInt(versionParts[0]);
				minorVersion = Integer.parseInt(versionParts[1]);
				if (majorVersion < 1 || (majorVersion == 1 && minorVersion < 5))
				{
					javaVersionMessage = "<span class=\"warningMessage\">"
							+ javaVersion + " (WARNING: BIRT " + viewerVersion
							+ " only supports JRE versions >= 1.5)</span>";
				}
			} catch (NumberFormatException e)
			{

			}
		%> <b>JRE version: </b><%=javaVersionMessage%><br>
		<br>
		</td>
		<!--  column two -->
		<td width="50%" valign="top" align="right">
		<%
			String nowParam = convertGregorianToMySQL(now);
			String todayParam = convertGregorianToMySQL(today);
			String yesterdayStartParam = convertGregorianToMySQL(yesterdayStart);
			String yesterdayEndParam = convertGregorianToMySQL(yesterdayEnd);
			String thisWeekStartParam = convertGregorianToMySQL(thisWeekStart);
			String lastWeekStartParam = convertGregorianToMySQL(lastWeekStart);
			String lastWeekEndParam = convertGregorianToMySQL(lastWeekEnd);
			String thisMonthStartParam = convertGregorianToMySQL(thisMonthStart);
			String lastMonthStartParam = convertGregorianToMySQL(lastMonthStart);
			String lastMonthEndParam = convertGregorianToMySQL(lastMonthEnd);
			String thisYearStartParam = convertGregorianToMySQL(thisYearStart);
			String lastYearStartParam = convertGregorianToMySQL(lastYearStart);
			String lastYearEndParam = convertGregorianToMySQL(lastYearEnd);

			String reportURLPrefix = "/frameset?__report=";
			String AccessReportDir = "MSAccess/";
			String SQLReportDir = "SQL/";
			String rosterPath = reportURLPrefix + SQLReportDir
					+ "Roster.rptdesign";
			String topCaseDriversPath = reportURLPrefix + SQLReportDir
					+ "TopCaseDrivers.rptdesign";
			String pinMinutesUsedPath = reportURLPrefix + SQLReportDir
					+ "PINMinutesUsed.rptdesign";
			String caseUpdatedRatioPath = reportURLPrefix + SQLReportDir
					+ "CaseUpdatedRatio.rptdesign";
			String salesStackRankPath = reportURLPrefix + SQLReportDir
					+ "SalesStackRank.rptdesign";
			String techStackRankPath = reportURLPrefix + SQLReportDir
					+ "TechStackRank.rptdesign";
			String realtimeSalesTeamPath = reportURLPrefix + SQLReportDir
					+ "RealtimeSales.rptdesign";
			String refundCountPath = reportURLPrefix + SQLReportDir
					+ "RefundCount.rptdesign";
			String refundTotalsPath = reportURLPrefix + SQLReportDir
					+ "RefundTotals.rptdesign";
			String topRefundDriversPath = reportURLPrefix + SQLReportDir
					+ "TopRefundDrivers.rptdesign";
			String noSaleDriversPath = reportURLPrefix + SQLReportDir
					+ "NoSaleDrivers.rptdesign";
			String aovPath = reportURLPrefix + SQLReportDir
					+ "AverageOrderValue.rptdesign";
			String IVRCSATPath = reportURLPrefix + SQLReportDir
					+ "IVRCSAT.rptdesign";
			String LMICSATPath = reportURLPrefix + SQLReportDir
					+ "LMICSAT.rptdesign";
			String IVRAgentCSATPath = reportURLPrefix + SQLReportDir
					+ "IVRAgentCSAT.rptdesign";
			String IVRAgentCSATDetailPath = reportURLPrefix + SQLReportDir
					+ "IVRAgentCSATDetail.rptdesign";
			String callVolumePath = reportURLPrefix + SQLReportDir
					+ "CallVolume.rptdesign";
			String conversionPath = reportURLPrefix + SQLReportDir
					+ "Conversion.rptdesign";
			String salesCountPath = reportURLPrefix + SQLReportDir
					+ "SalesCount.rptdesign";
			String schAdherencePath = reportURLPrefix + SQLReportDir
					+ "ScheduleAdherence.rptdesign";
			String schedulePath = reportURLPrefix + SQLReportDir
					+ "Schedule.rptdesign";
			String lateDaysPath = reportURLPrefix + SQLReportDir
					+ "LateDays.rptdesign";
			String minsLatePath = reportURLPrefix + SQLReportDir
					+ "MinutesLate.rptdesign";
			String minsWorkedPath = reportURLPrefix + SQLReportDir
					+ "MinutesWorked.rptdesign";
			String attendanceStackPath = reportURLPrefix + SQLReportDir
					+ "AttendanceStackRank.rptdesign";
			String IVRDSATCasesPath = reportURLPrefix + SQLReportDir
					+ "IVRDSATCases.rptdesign";
			String IVRDSATCaseCountPath = reportURLPrefix + SQLReportDir
					+ "IVRDSATCaseCount.rptdesign";
			String LMIDSATCasesPath = reportURLPrefix + SQLReportDir
					+ "LMIDSATCases.rptdesign";
			String LMIDSATCaseCountPath = reportURLPrefix + SQLReportDir
					+ "LMIDSATCaseCount.rptdesign";
			String AggregateDSATCaseCountPath = reportURLPrefix + SQLReportDir
			+ "AggregateDSATCaseCount.rptdesign";
		%>

		<table width="100%">
			<tr>
				<!-- <td colspan="2" width="100%" align="center" valign="top">
					Current Time is: <b><%=nowParam.replace('+', ' ')%></b>
 				</td>
			</tr>
			<tr>-->
				<td nowrap align="left" valign="top" width="50%">Current Time
				is: <b><%=nowParam.replace('+', ' ')%></b><br>
				Today began: <b><%=todayParam.replace('+', ' ')%></b><br>
				Yesterday began: <b><%=yesterdayStartParam.replace('+', ' ')%></b><br>
				Yesterday ended: <b><%=yesterdayEndParam.replace('+', ' ')%></b><br>

				This Week began: <b><%=thisWeekStartParam.replace('+', ' ')%></b><br>
				Last Week began: <b><%=lastWeekStartParam.replace('+', ' ')%></b><br>
				Last Week ended: <b><%=lastWeekEndParam.replace('+', ' ')%></b><br>
				</td>
				<td nowrap align="right" valign="bottom" width="50%">This Month
				began: <b><%=thisMonthStartParam.replace('+', ' ')%></b><br>
				Last Month began: <b><%=lastMonthStartParam.replace('+', ' ')%></b><br>
				Last Month ended: <b><%=lastMonthEndParam.replace('+', ' ')%></b><br>

				This Year began: <b><%=thisYearStartParam.replace('+', ' ')%></b><br>
				Last Year began: <b><%=lastYearStartParam.replace('+', ' ')%></b><br>
				Last Year ended: <b><%=lastYearEndParam.replace('+', ' ')%></b><br>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>

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

<hr size="5" color="blue">

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
				//reportTypes.put("Agent Time", "Agent Time");
				
				timeIntervals.clear();
				timeIntervals.put("Today","Today");
				timeIntervals.put("Yesterday", "Yesterday");
				timeIntervals.put("This Week", "This Week");
				timeIntervals.put("Last Week", "Last Week");
				timeIntervals.put("This Month", "This Month");
				timeIntervals.put("Last Month", "Last Month");
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
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
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
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
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
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
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
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
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
		<b>Real-Time Sales</b>

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
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
		<b>Average Order Value</b>

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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			%>

		</ul>
		
		<!------------------------------------------------------>
		
		<hr size="5" color="blue">
		<b>Sales Count</b>

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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
		%>
		</ul>
		
		<!------------------------------------------------------>

		<hr size="5" color="blue">
		<b>Call Volume</b>

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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			%>

		</ul>
		
		<hr size="5" color="blue">
		<b>Conversion</b>

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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			%>

		</ul>
		
		
		</td>
		<!------------------------------------------------------>
		<!--  column two -->
		<td width="50%" valign="top">

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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
		<b>Top Refund Reasons</b>

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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
				timeIntervals.put("This Year", "This Year");
				timeIntervals.put("Last Year", "Last Year");
				
				timeGrains.clear();
				timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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

			timeIntervals.put("Today","Today");
			timeIntervals.put("Yesterday", "Yesterday");
			timeIntervals.put("This Week", "This Week");
			timeIntervals.put("Last Week", "Last Week");
			timeIntervals.put("This Month", "This Month");
			timeIntervals.put("Last Month", "Last Month");
			timeIntervals.put("This Year", "This Year");
			timeIntervals.put("Last Year", "Last Year");
			
			timeGrains.clear();
			timeGrains.put("Yearly","Yearly");
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

<%@include file="/ui/footer.jsp"%>