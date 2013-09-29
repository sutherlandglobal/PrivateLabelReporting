<%@ page import="report.Report"
%><%@ page import="util.date.DateParser" 
%><%@ page import="java.util.ArrayList"
%><%@ page import="java.util.Collections"
%><%@ page import="java.util.HashMap"
%><%@ page import="api.FrontEnd"
%><%@ page import="api.formatting.ResultsFormatter"
%><%@ page import="api.formatting.ResultsFormatterFactory"
%><%FrontEnd frontEnd = new FrontEnd();

frontEnd.addJar("/opt/tomcat/apache-tomee-plus-1.5.2/webapps/PrivateLabel/WEB-INF/lib/PrivateLabelReporting.jar");
frontEnd.loadClassNames();

ResultsFormatter formatter = null;

HashMap<String,String> parameters = new HashMap<String, String>();

String reportName = null;

String delimiterParam = null, formatParam = null;

String output = "";

ArrayList<String> reports = new ArrayList<String>(); 

String[] reportParamNames = new String[]
{
	Report.REPORT_TYPE_PARAM,
	Report.ROSTER_TYPE_PARAM,
	Report.START_DATE_PARAM,
	Report.END_DATE_PARAM,
	Report.TIME_GRAIN_PARAM,
	Report.NUM_DRIVERS_PARAM,
	Report.AGENT_NAME_PARAM
};

try
{
	//match the report name to our local list
	reportName = request.getParameter("reportName");
		
	if(reportName != null && !reportName.equals("") )
	{
		if(frontEnd.isValidClassName("report." + reportName))
		{
			formatParam = request.getParameter("format");
			
			if(formatParam != null && !formatParam.equals("")) 
			{
				formatter = ResultsFormatterFactory.buildFormatter(Integer.parseInt(formatParam));
			}
			else
			{
				formatter = ResultsFormatterFactory.buildFormatter(ResultsFormatterFactory.CSV_FORMAT);
			}
	
			for(String paramName : reportParamNames)
			{
				try
				{
					String param = request.getParameter(paramName);

					if(param != null && !param.equals("") )
					{
						parameters.put(paramName, param);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			
			
			if(formatParam.equals("" + ResultsFormatterFactory.CSV_FORMAT))
			{
				String csvEnquoteParam = request.getParameter("enquote");
				String csvDelimParam = request.getParameter("delimiter");
				
				if(csvEnquoteParam != null && !csvEnquoteParam.equals("") )
				{
					formatter.setEnquote("1".equals(csvEnquoteParam));
				}
				
				if(csvDelimParam != null && !csvDelimParam.equals("") )
				{
					int maxLen = csvDelimParam.length();
					
					if(maxLen > 20)
					{
						maxLen= 20;
					}
					
					formatter.setDelim(csvDelimParam.substring(0,maxLen).replaceAll("[^\\x00-\\x7F]", ""));
				}
			}
	
			parameters.put(Report.SOURCE_PARAM, "API");
	
			ArrayList<String[]> results = frontEnd.startReport(reportName, parameters);
		
			for(String row : formatter.formatResults(results))
			{
				output += row + "\n";
			}
		}
		else
		{
			output = "Error: Unknown report.";
		}
	}
	else
	{
		output = "Error: Report not specified.";
	}
}
catch (Exception e)
{
	e.printStackTrace();
	
	
}%><%=output%>