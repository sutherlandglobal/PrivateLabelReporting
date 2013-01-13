<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<HEAD>
		<TITLE>Helios Reporting Framework - Dashboards</TITLE>
		<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
		<LINK href="styles/iv/index.css" type=text/css rel=stylesheet>
		<LINK href="ui/images/favicon.ico" type=image/x-icon rel="shortcut icon">
		<LINK href="ui/images/favicon.ico" type=image/x-icon rel="icon">
		<STYLE>
			.warningMessage { color:red; }
			body
			{
     			font-family: arial,verdana,helvetica;
     			font-size: 10pt;
			}
 
		</STYLE>

	</HEAD>


<frameset rows="20%,80%">
	<frame name="dashboardheader" noresize scrolling ="no" frameborder="0" src= "<%= request.getContextPath( )  %>/ui/header.jsp"></frame>


	<frameset cols="15%,85%">

 	
		<frameset rows="30%,70%">
			<frame src = "<%= request.getContextPath( )  %>/dashboards/userList.jsp" name = "userList" ></frame>
			<frame src = "<%= request.getContextPath( )  %>/dashboards/userDashboard.jsp" name="userDash" ></frame>
		</frameset>
	
		<frame src = "<%= request.getContextPath( )  %>/dashboards/reportView.jsp" name ="reportView"></frame>

	</frameset>
</frameset>

<%@include file="/ui/footer.jsp" %> 
