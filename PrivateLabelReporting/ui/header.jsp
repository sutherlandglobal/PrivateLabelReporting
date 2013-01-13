<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<HEAD>
		<TITLE>Helios Reporting Framework</TITLE>
		<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
		<LINK href="styles/iv/index.css" type=text/css rel=stylesheet>
		<LINK href="ui/images/favicon.ico" type=image/x-icon rel="shortcut icon">
		<LINK href="ui/images/favicon.ico" type=image/x-icon rel="icon">
		<STYLE>
			.warningMessage { color:red; }
			
			html body, td 
			{
				font-family: arial,verdana,helvetica;
    			font-size:10pt;
			}
 
		</STYLE>

	</HEAD>
	<BODY  bgcolor="#ededed" link="#0000FF" vlink="#0000FF">
		
		<%@include file="/ui/browserAssist.jsp" %>
		
		<a name="home"></a>
		
		<TABLE cellSpacing=0 border=0 cellpadding="2" width="100%">
			<TBODY>
			<!-- <tr><td colspan="2"><hr size="5" color ="blue"></td></tr>-->
			
				<TR>
					<td width = "50%" align="left">

						<font size="+3">
						<b>Helios Reporting Framework</b><br>
						</font>
						<font size="-1">
						<br>
						<b>Sutherland Global Services - North American Technical Support Division</b><br>
						Developed by Jason Diamond<br>
						</font>
					</td>
					<td width = "50%" align="right">
						<img src="<%= request.getContextPath( ) + "/ui/images/suth_logo.jpg" %>"></img>
					</td>
				</tr>
			</TBODY>
		</TABLE>
		
		<%@include file="/ui/menu.jsp" %>