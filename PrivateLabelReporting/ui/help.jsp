<%@include file="/ui/header.jsp" %>


<h2>Helios, a Guide.</h2>

<hr size="5" color ="blue">

<a href="#pages">Pages</a><br>
<a href="#reports">Reports</a><br>

<hr size="5" color ="blue">

<a name="pages"></a>
<h3>Pages</h3>
		
<b>Helios Home</b> - The landing and report index for the framework. All available reports can be launched from here. Some reports have options to use the 
generated date parameters for common intervals. The top option allows you to specify arbitrary date intervals.
<br></br>
<b>Dashboards</b> - The dashboard interface for personalized batch reporting.
<br></br>
<b>Jason's Objectives</b> - My objectives and goals for expansion and improvement of Helios, as outlined by Kris and Lou.
<br></br>
<b>JUnit Testing</b> - A summary of the automated testing effort.
<br></br>
<b>FAQ</b> - Addresses common questions about the framework.
<br></br>
<b>Help</b> - This page you're looking at, a guide to efficiently using the framework.
<br></br>
<b>API Documentation</b> - Javadocs for the Helios backend.
 

<hr size="5" color ="blue">

<a name="reports"></a>
<h3>Reports</h3>

<b>Roster</b> - The list of staff whose data we have interest in.
<br></br>

<b>Active Roster</b> - The list of currently active staff.
<br></br>
 
<b>Documentation Rate</b> - Details the Documentation Rate metric on an individual in weekly and monthly granularity. For a given time period, 
Documentation Rate is the percentage described by the quotient of Documentations Recorded and Cases Taken.
 <br></br>
 
<b>Team Documentation Rate</b> - Details the Team Documentation Rate metric at the team level. For a given time period, 
Documentation Rate is the percentage described by the quotient of Documentations Recorded and Cases Taken.
<br></br>

<b>Top Call Drivers</b> - Calculates the most common case categories (i.e. Software -> OS) for a given time period.
<br></br>

<b>PIN Minutes Used</b> - Calculates the number of PIN Minutes used over time.
<br></br>

<b>Case Updated Ratio</b> - Calculates the ratio of new to old cases over time.
<br></br>

<%@include file="/ui/footer.jsp" %>