
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<HEAD>
		<TITLE>Helios Reporting Framework - Survey Queue</TITLE>
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
	<frame name="surveyHeader" scrolling ="no" frameborder="0" src= "<%= request.getContextPath( )  %>/ui/header.jsp"></frame>

	<frameset cols="15%,85%">
 	
		<frame src = "<%= request.getContextPath( )  %>/surveyQueues/surveyList.jsp" name = "surveyList" ></frame>

		<frame  src = "" name="surveyView"></frame>
	</frameset>
</frameset>

<%@include file="/ui/footer.jsp" %> 