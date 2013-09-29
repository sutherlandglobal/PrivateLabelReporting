<%@include file="/ui/header.jsp" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="java.util.SortedSet" %>

<%
//name, desc
HashMap<String, String> metricList = new HashMap<String,String>();

metricList.put("Attendance Stack Rank", "A stack ranking of Attendance-based metrics. Final ranking is by Schedule Adherence."); 		
metricList.put("Average Order Value", "Trends average dollar amount of orders.");
metricList.put("Conversion", "Trends percentage of calls where a sale has occured.");		
metricList.put("CSAT IVR", "Trends customer satisfaction rate for IVR surveys.");
metricList.put("CSAT IVR Agent Detail", "Visualizes IVR CSAT survey details in tabular form."); 
metricList.put("CSAT LMI", "Trends customer satisfaction rate for LMI surveys.");
metricList.put("DSAT IVR", "Trends customer dissatisfaction rate for IVR surveys."); 
metricList.put("DSAT IVR Case Count", "Trends the count of DSAT IVR cases.");
metricList.put("DSAT LMI", "Trends customer dissatisfaction rate for LMI surveys.");
metricList.put("DSAT LMI Case Count", "Trends the count of DSAT LMI cases.");
metricList.put("DSAT Aggregate Case Count", "Trends the total count of all DSAT cases.");
metricList.put("Late Days", "Trends the count of days where a agent is more than five minutes late for their scheduled shift.");
metricList.put("Minutes Late", "Trends the count of minutes that a agent is late for their scheduled shift.");
metricList.put("Minutes Worked", "Trends the count of minutes that a agent works. This includes minutes outside of their scheduled shift.");
metricList.put("No Sale Drivers", "Determines the most common no sale categories (i.e. Software -> OS).");
metricList.put("Real-Time Sales", "Trends the total dollar value of sales.");
metricList.put("Refund Totals", "Trends the total dollar value of refunds.");
metricList.put("Refund Count", "Trends the total count of refunds.");
metricList.put("Roster", "The list of staff whose data we have interest in.");
metricList.put("Sales Count", "Trends the total count of sales in a given time period.");
metricList.put("Sales Stack Rank", "A stack ranking of Sales-based metrics. Final ranking is by Net Revenue."); 
metricList.put("Schedules", "Schedule data for agents, as well as any worked shifts within the schedules.");
metricList.put("Schedule Adherence", "A comparison of how well an agent's shifts adhere to their schedules."); 
metricList.put("Top Case Drivers", "Determines the most common case categories (i.e. Software -> OS).");
metricList.put("Top Refund Drivers", "Determines the most common refund categories (i.e. Software -> OS).");


metricList.put("Agent Progress", "Trends an agent's performance in many metrics.");

metricList.put("Calls Per Case", "Trends number of calls received over a case's lifetime.");

metricList.put("Call Volume", "Trends number of calls received.");

metricList.put("Created Cases", "Trends number of cases created.");

metricList.put("Net Revenue", "Trends net revenue (total sales - total refunds).");

metricList.put("Net Sales Count", "Trends net sales count (total sales count - total refund count).");

metricList.put("Opened Cases", "Trends number of cases opened.");

metricList.put("PIN Consumption Rate", "Trends rate of PIN consumption.");

metricList.put("PINs Consumed Count", "Trends count of PIN usage.");

metricList.put("Revenue Per Call", "Trends revenue generated per call received.");

metricList.put("Sales Documentation Count", "Trends count of sales documentations in cases.");

metricList.put("Sales Documentation Rate", "Trends rate of sales documenations.");

metricList.put("Sold Issues", "Trends number of issues sold.");

metricList.put("Teams", "A list of agent teams");

metricList.put("Updated Cases", "Trends number of cases updated.");

metricList.put("Used Issues", "Trends number of issues used.");




%>


<h2>Helios, a Guide.</h2>

<hr size="5" color ="blue">

<a href="#pages">Pages</a><br>
<a href="#reports">Reports</a><br>

<hr size="5" color ="blue">

<a name="pages"></a>
<h3>Pages</h3>
		
<b>Analysis</b> - The landing and index for Private Label's data analysis. All available reports can be launched from here, and many of their respective parameters specified. 
<br></br>
<b>JUnit Testing</b> - A summary of the automated testing effort.
<br></br>
<b>Javadocs</b> - Javadocs for Private Label-specific data analysis.
<br></br>
<b>Help</b> - The page you're looking at; a guide to efficiently using the framework.
<br></br>
<hr size="5" color ="blue">

<a name="reports"></a>
<h3>Metrics</h3>

<%
	SortedSet<String> keys = new TreeSet<String>(metricList.keySet());
	
	for(String metricName : keys)
	{
		out.println("<b>" + metricName + "</b> - " + metricList.get(metricName) + "<br><br>");
	}

%>



<%@include file="ui/footer.jsp"%>