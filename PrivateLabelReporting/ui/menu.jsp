
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Vector" %>

<hr size="5" color ="blue">

<table width="100%">
	<tr>
			
			<%
				String url = request.getRequestURL().toString();
			
				HashMap<String, String> navTable = new HashMap<String,String>();
				Vector<String> menu = new Vector<String>();

				menu.add("Helios Home");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/index.jsp");
				
				menu.add("Dashboards");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/dashboards.jsp");
				
				menu.add("Survey Queues");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/surveyQueues.jsp");
				
				menu.add("Jason's Objectives");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/objectives.jsp");
				
				menu.add("JUnit Testing");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/testResults.jsp");
				
				menu.add("FAQ");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/faq.jsp");
				
				menu.add("API Documentation");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/doc.jsp");
				
				menu.add("Help");
				navTable.put(menu.lastElement(), request.getContextPath( ) + "/help.jsp");
				

				
				for(String pageTitle : menu)
				{
					//out.println(navTable.get(pageTitle) + "<br>");
					//out.println(url + "<br>");
					//out.println(request.getServletPath() +"<br>");
					
					out.print("<td nowrap align=\"center\" valign=\"center\" width=\"" + 100/menu.size() + "%\" " );
					
					if(url.endsWith(navTable.get(pageTitle)) || (url+ request.getServletPath().substring(1)).endsWith( navTable.get(pageTitle) ) )
					{
						out.println(" bgcolor=\"#bbbbbb\">");
					}
					else
					{
						out.println(" bgcolor=\"#ededed\">");
					}
					
					out.println("<a target=\"_top\" href=\"" + navTable.get(pageTitle) + "\"><b>" + pageTitle + "</b></a>");
					
					out.println("</td>");
				}
				

			%>

	</tr>
</table>

<hr size="5" color ="blue">