<%@include file="/ui/header.jsp" %>

<%@ page import="java.util.HashMap" %>

<%

HashMap<String,String> questionLookup = new HashMap<String,String>();
Vector<String> sortedQuestions = new Vector<String>();

sortedQuestions.add("Where am I?");
questionLookup.put(sortedQuestions.lastElement(),
		"You've reached a deployment of Helios, the reporting framework originally written for the NATS Private Label program at Sutherland Global Services."
		);

sortedQuestions.add("What does Helios do, and why was it built?");
questionLookup.put(sortedQuestions.lastElement(),
		"Helios was built to provide high-availability, data source-independent, \"realest\"-time visibility into the massive amounts of data accumulated during the " + 
		"course of program operation.  This framework was designed and developed by Jason Diamond; commissioned by Kris Booth and Lou Giordano."
		);

sortedQuestions.add("How do I use Helios?");
questionLookup.put(sortedQuestions.lastElement(),
		"Easy. Navigate to the <a href=\"index.jsp\">Home</a> page and click on a report link for a report you seek. The report will launch and prompt you for any required " + 
		"parameters. Enter prompted parameters and wait for the report generation to complete, which may take several minutes depending on the report's complexity, and " + 
		"machine load. Further information is available on the Help page." 
		);

sortedQuestions.add("What do I enter in an Agent Name field that a report prompts me for?");
questionLookup.put(sortedQuestions.lastElement(),
	"Run a Roster report in another tab or window, and use the value in the \"Name\" field for the agent desired. Generally the form is [lastName],[space][firstName]"
		);

sortedQuestions.add("There is a error/problem with a report, or the results are not coherent with expectations. How can this be remedied?");
questionLookup.put(sortedQuestions.lastElement(),
		"Outstanding, your efforts will contribute to Helios' stability and power. Send an email to <a href=\"mailto:jason.diamond@sutherlandglobal.com\">Jason</a> with as " + 
		"much detail as you possibly can provide. Critical details include the report name, parameters used, time executed, steps to reproduce, barometric pressure, current day's horoscope, blood-alcohol content, etc. " + 
		"Screenshots are helpful too."
		);

sortedQuestions.add("I have a non-bug-related suggestion, idea, or feature request. Would you like to hear it?");
questionLookup.put(sortedQuestions.lastElement(),
		"<a href=\"mailto:jason.diamond@sutherlandglobal.com\">Yes, I would</a>."
		);

sortedQuestions.add("I would like my own reporting dashboard, could you please create one for me?");
questionLookup.put(sortedQuestions.lastElement(),
		"Sure. Send an email to <a href=\"mailto:jason.diamond@sutherlandglobal.com\">Jason</a> detailing which reports you'd like, their respective \n" + 
		"parameters, types, and time granularity. If you are non-management please seek your supervisor's approval first."
		);

sortedQuestions.add("Why isn't reporting done by hand?");
questionLookup.put(sortedQuestions.lastElement(),
		"It was, for a very long time. However it's not realistic, accurate, cost-effective, or even possible to involve the human hand in anything other than a high-level interpretation of " + 
		"the data. " +  
		"The scope and size of the data, as well as the level of accuracy necessitated by business decisions, requires an attention to detail that only software can relabily provide." +
		" Additionally, a report can (and most do) consist of thousands of database queries, which can take a human months to inaccurately finesse down to actionable intelligence. " 
		);

sortedQuestions.add("How big is Helios?");
questionLookup.put(sortedQuestions.lastElement(),
		"In the neighborhood of 30,000 lines, across 7 languages."
		);

sortedQuestions.add("Helios is experiencing major problems, or an outage. What should I do?");
questionLookup.put(sortedQuestions.lastElement(),
		"Call Jason Diamond's Mobile (check Outlook), anytime day or night, urgency permitting."
		);

/*
sortedQuestions.add("");
questionLookup.put(sortedQuestions.lastElement(),
		""
		);
*/

%>
		<h3>Frequently Asked Questions</h3>
<%
			int i = 0;
			for(String question : sortedQuestions)
			{
				out.println("<a href=\"#q" + i +"\"><b>" + question + "</b></a><br>");
				i++;
			}

	i = 0;
	for(String question : sortedQuestions)
	{
		out.println("<hr size=\"5\" color =\"blue\">");
		out.println("<a name = \"q" + i +"\"></a>");
		out.println("<b>" + question + "</b><br><br>");
		
		out.println(questionLookup.get(question));
		
		out.println("<br><br><a href=\"#home\">Return to Top</a><br>");
		
		
		i++;
	}

%>
		
<%@include file="/ui/footer.jsp" %>