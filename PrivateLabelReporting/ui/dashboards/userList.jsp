
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

<%!

String dashboardUserDir = "../../../dashboards";
String dumpDir = "";
String htmlSuffix = ".html";
String pdfSuffix = ".pdf";
%>


<!-- check the dashboard dir for user dirs -->
<!--  for each user dir,  -->

<h4>User List</h4>

<%

File dir = new File(dashboardUserDir);

//out.println(dir.getAbsoluteFile());

if(dir.exists() && dir.isDirectory())
{
	for(File userDir : dir.listFiles())
	{
		//out.println(userDir.getAbsoluteFile());
		
		if(userDir.exists() &&  userDir.isDirectory())
		{	
			out.println("<a target =\"userDash\" href=\"./userDashboard.jsp?user=" + userDir.getName() + "\">" + userDir.getName() +"</a><br>");
		}
	}
	
}
			
%>

	</BODY>
</HTML>