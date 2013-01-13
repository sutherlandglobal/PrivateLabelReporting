
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<HEAD>
		<TITLE>Helios Reporting Framework</TITLE>
		<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
		<LINK href="styles/iv/index.css" type=text/css rel=stylesheet>
		<LINK href="http://www.eclipse.org/images/eclipse.ico" type=image/x-icon rel="shortcut icon">
		<STYLE>
			.warningMessage { color:red; }
			body
			{
     			font-family: arial,verdana,helvetica;
     			font-size: 10pt;
			}
 
		</STYLE>

	</HEAD>
	<BODY  bgcolor="#ededed" link="#0000FF" vlink="#0000FF">
	
<%@ page import="java.io.File"%>


<%
	String dashboardUserDir = "../../../dashboards";
	String dumpDir = "";
	String htmlSuffix = ".html";
	String pdfSuffix = ".pdf";

	String user = request.getParameter("user");
%>

<%!
	public String getReportFileName(String fileName)
	{
		String[] fields = fileName.split("_");

		String reportName = "";
		for (int i = 1; i < fields.length - 1; i++)
		{
			reportName += fields[i] + " ";
		}
		
		reportName += fields[fields.length - 1].substring(0,fields[fields.length - 1].lastIndexOf('.'));
		
		return reportName;
	}
	%>

	<h4>User Dashboard</h4>
<!-- check the dashboard dir for user dirs -->
<!--  for each user dir,  -->

<%
	File userDir = new File(dashboardUserDir + "/" + user);

	if (userDir.exists() && userDir.isDirectory())
	{

		for (File reportFile : userDir.listFiles())
		{
			if(reportFile.getName().endsWith(htmlSuffix) || reportFile.getName().endsWith(pdfSuffix))
			{
				out.println("<a target =\"reportView\" href=\"./reportView.jsp?report="
						+ user + "/" + reportFile.getName() + "\">" + getReportFileName(reportFile.getName())
						+ "</a><br>");
			}
		}
	}
%>

	</BODY>
</HTML>