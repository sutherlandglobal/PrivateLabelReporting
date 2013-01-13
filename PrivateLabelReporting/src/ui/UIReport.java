/**
 * 
 */
package ui;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * @author jdiamond
 *
 */
public class UIReport 
{

	private String reportUrl;
	private String name;
	private String extraVars;
	
	private LinkedHashMap<String, String> reportTypes;
	private LinkedHashMap<String, String> rosterTypes;
	private LinkedHashMap<String, String> timeIntervals;
	private LinkedHashMap<String, String> timeGrains;
	
	private String timeIntervalSelect;
	private String timeGrainSelect;
	private String buttonName;
	private String rosterSelect;
	private String reportSelect;
	
	//JSP requires an arg-less constructor
	public UIReport()
	{
		
	}
	
	public void setReportUrl(String url)
	{
		reportUrl = url;
	}
	
	public void setName(String name)
	{
		this.name = name;
		
		
		

	}
	
	public void setRosterTypeOptions(LinkedHashMap<String, String> options)
	{
		rosterTypes = options;
	}
	
	public void setReportTypeOptions(LinkedHashMap<String, String> options)
	{
		reportTypes = options;
	}
	
	public void setTimeIntervals(LinkedHashMap<String, String> options)
	{
		timeIntervals = options;
	}
	
	public void setTimeGrains(LinkedHashMap<String, String> options)
	{
		timeGrains = options;
	}
	
	public void setExtraVars(String vars)
	{
		extraVars = vars;
	}
	
	public String getReportHtml()
	{
		String retval = "<li><b>" + name + "</b><br>\n"; 
		String reportFunctionCall = "<button name=\"" + buttonName
				+ "\" type=\"submit\"	onclick=\"redirReport('" + reportUrl + "', ";
		
		Vector<String> functionArgs = new Vector<String>();
		
		//set all the element names accordingly
		
		buttonName = name.replaceAll("\\s", "" ) + "RunButton";
				
		//redirReport(reportUrl, reportTypes, rosterTypes, timeIntervals, timeGrains, extraVars)
		
		if(reportTypes != null && !reportTypes.isEmpty())
		{			
			reportSelect = name.replaceAll("\\s", "" ) + "ReportType";
			
			retval += "<select id=\""
					+ reportSelect + "\" name=\"" + reportSelect
					+ "\">\n";
			retval += "<option selected=\"selected\" value=\"Type\">Type</option>\n";
			
			for(Entry<String, String> reportType  : reportTypes.entrySet())
			{
				retval += "<option value=\"" + reportType.getValue() + "\">" + reportType.getKey() + "</option>\n";
			}
			
			retval += "</select>\n";
			
			functionArgs.add(reportSelect);
		}
		else
		{
			functionArgs.add("");
		}
		
		if(rosterTypes != null && !rosterTypes.isEmpty())
		{
			rosterSelect = name.replaceAll("\\s", "" ) + "RosterType";
			
			retval += "<select id=\""
					+ rosterSelect + "\" name=\"" + rosterSelect
					+ "\">\n";
			retval += "<option selected=\"selected\" value=\"Roster\">Roster</option>\n";
			
			for(Entry<String, String> rosterType  : rosterTypes.entrySet())
			{
				retval += "<option value=\"" + rosterType.getValue() + "\">" + rosterType.getKey() + "</option>\n";
			}
			
			retval += "</select>\n";
			
			//reportFunctionCall += ", getElementById('" + rosterSelect + "').value";
			functionArgs.add(rosterSelect);
		}
		else
		{
			functionArgs.add("");
		}
		
		if(timeIntervals != null && !timeIntervals.isEmpty())
		{
			timeIntervalSelect = name.replaceAll("\\s", "" ) + "TimeInterval";
			
			retval += "<select id=\""
					+ timeIntervalSelect + "\" name=\"" + timeIntervalSelect
					+ "\">\n";
			retval += "<option selected=\"selected\" value=\"Interval\">Interval</option>\n";
			
			for(Entry<String, String> timeInterval  : timeIntervals.entrySet())
			{
				retval += "<option value=\"" + timeInterval.getValue() + "\">" + timeInterval.getKey() + "</option>\n";
			}
			
			retval += "</select>\n";
			
			functionArgs.add(timeIntervalSelect);
		}
		else
		{
			functionArgs.add("");
		}
		
		if(timeGrains != null && !timeGrains.isEmpty())
		{
			timeGrainSelect = name.replaceAll("\\s", "" ) + "TimeGrain";
			
			retval += "<select id=\""
					+ timeGrainSelect + "\" name=\"" + timeGrainSelect
					+ "\">\n";
			retval += "<option selected=\"selected\" value=\"Grain\">Grain</option>\n";
			
			for(Entry<String, String> timeGrain  : timeGrains.entrySet())
			{
				retval += "<option value=\"" + timeGrain.getValue() + "\">" + timeGrain.getKey() + "</option>\n";
			}
			
			retval += "</select>\n";
			
			functionArgs.add(timeGrainSelect);
		}
		else
		{
			functionArgs.add("");
		}
		
		for(String arg : functionArgs)
		{
			if(!arg.equals(""))
			{
				reportFunctionCall += "getElementById('" + arg + "').value, ";
			}
			else
			{
				reportFunctionCall += "'',";
			}
		}
		
		if(extraVars != "")
		{
			reportFunctionCall += "'" + extraVars + "'";
		}
		else
		{
			reportFunctionCall += "''";
		}
		
		
		retval += reportFunctionCall;

		
		//button

		
		retval += ") ";
		

		retval += "\">Run</button>\n";
		retval += "</li>\n";
		
		return retval;
	}

	public void clearOptions()
	{
		extraVars = "";
		rosterTypes = new LinkedHashMap<String, String>();
		timeIntervals = new LinkedHashMap<String, String>();
		reportTypes = new LinkedHashMap<String, String>();
		timeGrains = new LinkedHashMap<String, String>();
	}
	
	public void setDefaults()
	{
		extraVars = "";
		
		//default options
		rosterTypes = new LinkedHashMap<String, String>();
		rosterTypes.put("Support", "Support");
		rosterTypes.put("Active Support", "Active Support");
		rosterTypes.put("Sales", "Sales");
		rosterTypes.put("Active Sales", "Active Sales");
		rosterTypes.put("All", "All");
		rosterTypes.put("Active", "Active");
		
		reportTypes = new LinkedHashMap<String, String>();
		reportTypes.put("Agent Stack", "1");
		reportTypes.put("Agent Time", "2");
		reportTypes.put("Team Stack", "3");
		reportTypes.put("Team Time", "4");
		
		timeIntervals = new LinkedHashMap<String, String>();
		timeIntervals.put("Today", "Today");
		timeIntervals.put("Yesterday", "Yesterday");
		timeIntervals.put("This Week", "This Week");
		timeIntervals.put("Last Week", "Last Week");
		timeIntervals.put("This Month", "This Month");
		timeIntervals.put("Last Month", "Last Month");
		timeIntervals.put("This Year", "This Year");
		timeIntervals.put("Last Year", "Last Year");
		
		timeGrains = new LinkedHashMap<String, String>();
		timeGrains.put("Yearly", "Yearly");
		timeGrains.put("Monthly", "Monthly");
		timeGrains.put("Weekly", "Weekly");
		timeGrains.put("Daily", "Daily");
		timeGrains.put("Hourly", "Hourly");
		
	}
	
	public void reset()
	{
		setDefaults();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		UIReport rpt = new UIReport();
		
		rpt.setName("Test report");
		rpt.setReportUrl("www.google.com");
		rpt.setExtraVars("derp=1");
		
		System.out.println(rpt.getReportHtml());
		
	}

}
