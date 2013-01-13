<%@include file="/ui/header.jsp"%>

<%@ page import="java.io.File"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.util.Calendar"%>

<%!

public String convertGregorianToMySQL(GregorianCalendar date)
{
	String retval = "" + date.get(Calendar.YEAR) + "-";
	
	if(date.get(Calendar.MONTH) + 1 < 10)
	{
		retval += "0";
	}
	
	retval += date.get(Calendar.MONTH) + 1 + "-";
	
	if(date.get(Calendar.DAY_OF_MONTH) < 10)
	{
		retval += "0";
	}
	
	retval += date.get(Calendar.DAY_OF_MONTH) + "+";
	
	if(date.get(Calendar.HOUR_OF_DAY) < 10)
	{
		retval += "0";
	}
	
	retval += date.get(Calendar.HOUR_OF_DAY) + ":";
	
	if(date.get(Calendar.MINUTE) < 10)
	{
		retval += "0";
	}
	
	retval += date.get(Calendar.MINUTE) + ":";
	
	if(date.get(Calendar.SECOND) < 10)
	{
		retval += "0";
	}
	
	retval += date.get(Calendar.SECOND);
	
	return retval;
}

%>

<h3>Jason Diamond's Objectives</h3>

This is a log of my past and current projects and their respective
statuses, mostly revolving around development of the Helios Reporting
Framework for the Private Label program.
<br>
<br>

Any questions, updates, or priority revisions should be directed to me
via
<a href="mailto:jason.diamond@sutherlandglobal.com">Email</a>
.

<br>
<br>
Upcoming Vacation days:
<br>
<br>

<%
			File thisFile = new File("../../../objectives.jsp");
			
			GregorianCalendar modDate = new GregorianCalendar();
			modDate.setTimeInMillis(thisFile.lastModified());
			
			String modString = convertGregorianToMySQL(modDate);
			//out.println(thisFile.getAbsolutePath());
			//out.println(thisFile.lastModified());
		%>

<h4>This document was last updated on <%= modString.replace('+', ' ') %></h4>


<br>
<a href="#progress"><b>In Progress</b></a>
<br>
<a href="#completed"><b>Completed</b></a>
<br>

<hr size="5" color="blue">

<a name="progress"></a>
<h4>In Progress</h4>
<ul>
	<br>
	<li>Implement and deploy Private Label report backlog in the
	Helios Reporting Framework, using database ROCJFSDBS27 - <b>TOP
	PRIORITY</b></li>
	<ul>
		<li>Roster report - <b>DONE</b></li>
		<li>Realtime Sales reports - <b>DONE</b></li>
		<li>Refund Amount reports - <b>DONE</b></li>
		<li>Refund Totals reports - <b>DONE</b></li>
		<li>Refund Reasons reports - <b>DONE</b></li>
		<li>Case Drivers reports - <b>DONE</b></li>
		<li>Average Order Value reports - <b>DONE</b></li>
		<li>LMI CSAT reports - <b>DONE</b></li>
		<li>IVR CSAT reports - <b>DONE</b></li>
		<!-- 
				<li>Documentation Rate report - <b>DONE</b></li>
				<li>Team Documentation Rate report - <b>DONE</b></li>
				<li>Top Call Drivers report - <b>DONE</b></li>
				<li>PIN Minute Usage report - <b>DONE</b></li>
				<li>Case Updated Ratio report - <b>DONE</b></li>
				-->

		<li>Calls Per Unique Entitlement - <font color="red"><b>STALLED</b></font></li>
		<ul>
			<li>Description: We do not have access to requisite data.</li>
			<li>Root cause: We do not have access to requisite data.</li>
			<li>Action required: James' ticket 1796187 requires resolution.</li>
			<li>Completion Date Estimate: Unknown, depends on ticket
			1796187.</li>
		</ul>
		<li>LMI Chat usage/Conversion rates reports - <font color="red"><b>STALLED</b></font></li>
		<ul>
			<li>Description: Report requirements have not been established.</li>
			<li>Root cause: Report requirements have not been established.</li>
			<li>Action required: Require concrete report requirements from
			Chris Paddon and James Roffe to proceed.</li>
			<li>Completion Date Estimate: Unknown, depends on how soon
			requirements can be established.</li>
		</ul>
	</ul>

	<br>
	<li>Implement Test Plan - <b>SECOND PRIORITY</b></li>
	<ul>
		<li>Implement JUnit test cases for reporting framework classes.</li>
		<ul>
			<li>Constants - <b>DONE</b></li>
			<li>DateParser - <b>DONE</b></li>
			<li>MSAccess.FileConnection - <b>DONE</b></li>
			<li>Roster - <b>DONE</b></li>
			<li>Active Roster - <b>DONE</b></li>
			<li>Documentation Rate - <b>DONE</b></li>
			<li>Team Documentation Rate - <b>DONE</b></li>
			<li>Top Call Drivers - <b>DONE</b></li>
			<li>PIN Minutes Used - <b>DONE</b></li>
			<li>Case Updated Ratio - <b>DONE</b></li>
			<li>Realtime Sales reports - <b>DONE</b></li>
			<li>Refund Amount reports - <b>DONE</b></li>
			<li>Refund Totals reports - <b>DONE</b></li>
			<li>Refund Reasons reports - <b>DONE</b></li>
			<li>Case Drivers reports - <b>DONE</b></li>
			<li>Average Order Value reports - <b>DONE</b></li>
			<li>LMI CSAT reports - <b>DONE</b></li>
			<li>IVR CSAT reports - <b>DONE</b></li>

			<li>Completion Date Estimate: Rolling. This will be an ongoing
			process as new classes requiring testing are developed.</li>
		</ul>
		<li>Implement Profile generation tests.</li>
		<ul>
			<li>Roster - <b>DONE</b></li>
			<li>Documentation Rate - <b>DONE</b></li>
			<li>Team Documentation Rate - <b>DONE</b></li>
			<li>Top Call Drivers - <b>DONE</b></li>
			<li>PIN Minutes Used - <b>DONE</b></li>
			<li>Case Updated Ratio - <b>DONE</b></li>

			<li>Completion Date Estimate: Rolling. This will be an ongoing
			process as new reports requiring testing are developed.</li>
		</ul>
	</ul>

	<br>
	<li>Security Vulnerability Auditing - <b>THIRD PRIORITY</b></li>
	<ul>
		<li>Patched a local file inclusion bug in the dashboard
		component.</li>
		<li>Completion Date Estimate: Rolling. This will be an ongoing
		process as the framework grows, and new auditing tools are used.</li>
	</ul>
</ul>
<a href="#home">Return to Top</a>

<hr size="5" color="blue">
<a name="completed"></a>
<h4>Completed</h4>
<ul>
	<li>Gather department reporting requirements and reconcile with
	pre-existing Proofpoint Executive Reporting project.</li>
	<li>Research methods of connecting to a MS Access database via the
	SUN JDBC drivers.</li>
	<li>Research Java libraries for directly interacting with MS
	Access database files.</li>
	<li>Develop SQL-like interpreter for processing user commands to
	interact with a MS Access database. Support querying fields with
	specified tables, with conditions affecting supplied fields.</li>
	<li>Implement Proof-of-Concepts for interacting with "backend"
	version of existing Private Label database.</li>
	<li>Establish Version Control with SVN.</li>
	<li>Design coherent framework allowing reports to be datasource
	independent. Reports must be tolerant of both user and database error,
	as well as provide functional flexibility to surpass current reporting
	measures. Environmental constraints are the Apache Tomcat application
	server with the BIRT plugin, running on linux.</li>
	<li>Successfully Integrated Reporting Framework with Tomcat and
	the Birt Viewer.</li>
	<li>Implement functionality to arbitrarily aggregate datapoints on
	a per-report basis, be it by team, agent, time granularity, etc.</li>
	<li>Implement Roster report as a standalone report, and with API
	functionality to be used as a sub-report in all other reports requiring
	a list of active support staff.</li>
	<li>Implement logging mechanism for report actions. Supports many
	reports running simultaneously.</li>
	<li>Implement a library to handle conversions between date
	formats.</li>
	<li>Implement a library to handle system call interaction.</li>
	<li>Acquire hardware to use as a staging environment
	(rocjfsdev07).</li>
	<li>Platform configuration migration: Tomcat 7 running as non-root
	user.</li>
	<li>Deployment of staging environment on staging hardware
	(rocjfsdev07).</li>
	<li>Drafted Reporting Framework deployment documentation.</li>
	<li>Implemented Documentation Rate report, qualified by James
	Roffe.</li>
	<li>Implemented Team Documentation Rate report, qualified by James
	Roffe.</li>
	<li>Implemented Top Call Drivers report, qualified by James Roffe.</li>
	<li>Successfully created BIRT Report structure that doesn't run
	all database queries twice.</li>
	<li>Proof-of-Concept for a fixed thread pool in Java.</li>
	<li>Design, develop, and test a multi-threaded implementation of
	MS Access database queries for drastically increased query speed in
	table row traversal, and condition evaluation.</li>
	<li>Platform configuration migration: Tomcat 7 expires/renews
	threads in a disagreeable way for version 2.6.1 of the BIRT Viewer.
	Switched to Tomcat 5 after extensive testing.</li>
	<li>Redesigned Helios landing page. Added reports for most common
	date intervals with generated timestamps.</li>
	<li>Developed Proof-of-Concept perl script to interact with
	LogMeIn reporting mechanism for future reports.</li>
	<li>Framework build automation using Apache Ant. Greatly reduces
	build process duration and complexity.</li>
	<li>Developed perl script to manage Tomcat service restart
	operations.</li>
	<li>Javadoc generation and interfacing with the framework build
	process and Tomcat. Includes internal Java documentation, as well as
	external libraries (Jackcess,JExcel)</li>
	<li>Framework-wide Javadoc tagging.</li>
	<li>Refactoring and expansion of command shell to interact with MS
	Access database files.</li>
	<li>JUnit deployment on development environment.</li>
	<li>Proof-of-Concept JUnit test suite execution and build process
	integration.</li>
	<li>JUnit deployment on staging environment.</li>
	<li>Wrote test results page to aggregate and display results from
	JUnit tests.</li>
	<li>Refactored Helios UI into header,menu,footer, and content script files. Updated the appropriate build targets to reflect the new structure.</li>
	<li>Added the "Powered By" section to the UI page footer.</li>
	<li>Refactored the MS Access Query Threadpool into a generalized utility class.</li>
	<li>Added support for running database queries in parallel, from a single DatabaseConnection object.</li>
	<li>Researched into measures for profiling backend code and garbage collection analysis.</li>
	<li>Began developing test classes to generate meaningful profile data from framework classes.</li>
	<li>Wrote script to process all profile-type tests and generate datafiles for use by respective profiling tools.</li>
	<li>Integrated profile generation into build process.</li>
	<li>Researched methods for generating reports outside of the BIRT Viewer for potential use in periodic email blasts.</li>
	<li>Enforced datasource creation and pre-report setup success requirements on all reports.</li>
	<li>Research methods of interacting with an MS SQL server from Java.</li>
	<li>MySQL and MSSQL database interaction in Java are very similar. Created a parent SQL database connection class to handle user-supplied JDBC drivers to eliminate redundant code.</li>
	<li>Implemented performance tuning for faster MS Access queries.</li>
	<li>Wrote FAQ page.</li>
	<li>Wrote Help page.</li>
	<li>Fixed the UI so that the Header and Menu persist and function	correctly if the body page contains frames.</li>
	<li>Developed personalized reporting dashboards for periodic batch	report generation, supporting output formats of PDF and HTML.</li>
	<li>Developed a web interface to easily navigate users and their dashboard reports.</li>
	<li>Proof of concept for querying an MS SQL database from Java.</li>
	<li>Wrote support for running queries against a single SQL server 	in parallel.</li>
	<li>Switch over completed reports to use the new SQL server.</li>
	<li>Moving logging functionality into its own package.</li>
	<li>Extended logging functionality to allow reports and database connections to write to different logs.</li>
	<li>Completed the PIN Minutes Used report, qualified by James Roffe.</li>
	<li>Completed the Cases Updated Ratio report, qualified by James Roffe.</li>
	<li>Completed the Active Roster report.</li>
	<li>Completed the Case Updated Ratio report, qualified by James Roffe.</li>
	<li>Patched a bug in the Case Updated Ratio report, qualified by Rich Goodenough.</li>
	<li>Reworked the Roster subreport to support wildly varying rosters in response to the Sales Stack Rank report.</li>
	<li>Completed the Sales Roster report.</li>
	<li>Completed the Active Sales Roster report.</li>
	<li>Refactored all database queries to comply with SQL best practices.</li>
	<li>Completed research into using LMI API</li>
	<li>LMI Replication SOAP API proof of concept established for	running reports.</li>
	<li>Established workaround with LMI Technical Account Managers Charles and Justin to address issues of report schema separators appearing in report data.</li>
	<li>Extensive replication testing on entire backlog of LMI data.</li>
	<li>Successful test run of replication of all LMI data.</li>
	<li>Memory analysis-type testing on LMI replication process to track down a brutal leak.</li>
	<li>Refactored replication process into a service.</li>
	<li>LMI Replication established to target tables on ROCJFSDEV18 with frequency of 1 hour.</li>
	<li>Refactored Roster reports to use ROCJFSDBS27.</li>

	<li>Implemented and deployed Realtime Sales reports at team and agent levels.</li>
	<li>Implemented and deployed the Refund Totals report.</li>
	<li>Implemented and deployed the Top Case Drivers report.</li>
	<li>Implemented and deployed the Top Refund Reasons report.</li>
	<li>Implemented and deployed the No Sale Drivers report.</li>
	<li>Added quick-frequency support for dashboard reports.</li>
	<li>Deployed dashboard reports for PL Supervisors and team leads.</li>

	<li>Established requirements for LMI replication for ATT	Connecttech.</li>
	<li>Established LMI replication for ATT	Connecttech.</li>

	<li>Uncoupled LMI Replication code from Helios, and redeployed PL Replication as it's own instance.</li>
	<li>Established source control via SVN onsite at Sutherland for Helios.</li>
	<li>Offloaded generic date validation code to it's own utility class.</li>
	<li>Configured Tomcat to track end-user usage of Helios.</li>
	<li>Revised framework documentation to prep for production deployment.</li>
	
	

</ul>
<a href="#home">Return to Top</a>

<%@include file="/ui/footer.jsp"%>