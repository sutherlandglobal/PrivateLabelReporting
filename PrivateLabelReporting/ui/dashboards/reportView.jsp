
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>Helios Reporting Framework</TITLE>
<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">

<%
	String reportParam =request.getParameter("report");

	if(reportParam != null )
	{
		out.println("<meta http-equiv=\"refresh\" content=\"10\">");
	}
%>

<LINK href="styles/iv/index.css" type=text/css rel=stylesheet>
<LINK href="http://www.eclipse.org/images/eclipse.ico" type=image/x-icon
	rel="shortcut icon">
<STYLE>
.warningMessage {
	color: red;
}

body {
	font-family: arial, verdana, helvetica;
	font-size: 10pt;
}
</STYLE>

</HEAD>
<BODY bgcolor="#ededed" link="#0000FF" vlink="#0000FF">


<%@ page import="java.io.File"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.FileReader"%>


<%!
	String dashboardUserDir = "../../../dashboards";
	String dumpDir = "";
	String htmlSuffix = ".html";
	String pdfSuffix = ".pdf";
	
%>

<%

//http://mediaanalysis:8080/birt-viewer/dashboards/reportView.jsp?report=jason.diamond/DocumentationRate_DocRate_Interval_Oct_20

	
		
	if(reportParam != null )
	{
		File report = new File(dashboardUserDir + "/"
				+ reportParam);
	
		//out.println(report.getAbsoluteFile());
	
		if (reportParam.indexOf("..") == -1 && !reportParam.startsWith("/") && report.exists() && report.isFile())
		{
			if (report.getName().endsWith(htmlSuffix))
			{
				BufferedReader dataIn = null;
				InputStreamReader ins = null;
				FileReader fin = null;
	
				try
				{
					fin = new FileReader(report);
					dataIn = new BufferedReader(fin);
	
					String line;
					StringBuilder pageOutput = new StringBuilder();
					
					while ((line = dataIn.readLine()) != null)
					{
	
						//correct the report image paths
						
						if(line.trim().startsWith("<img"))
						{
							line = line.replaceAll("src=\"", "src=\"" + "../dashboards/" + new File(report.getParent()).getName() + "/");
						}
						
						
						pageOutput.append(line);
						pageOutput.append("\n");
					}
					
					out.println(pageOutput.toString());
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				} 
				finally
				{
					if (ins != null)
					{
						fin.close();
					}
	
					if (dataIn != null)
					{
						dataIn.close();
					}
				}
			}
			else if(report.getName().endsWith(pdfSuffix))
			{
				//redir to file
				response.sendRedirect( "../dashboards/" + new File(report.getParent()).getName() + "/" +report.getName());
			}
			else
			{
				out.println("<hr>");
			}
		}
		else
		{
			out.println("Error: Could not find report file.");
		}
	}
%>

</BODY>
</HTML>