
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<HEAD>
		<TITLE>LinkTest</TITLE>
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
		
		
		
		
		<!--<link rel="start" type="text/xml" href="http://ezclmacerpfs.suth.com/Data.xml" />-->
		<xml id="cdcat" src="http://ezclmacerpfs.suth.com/common/js/../../../Data.xml"></xml>
		
		<script  type="text/javascript" src="http://ezclmacerpfs.suth.com/View/../../../common/js/commonNew.js"></script>
		
	<script type="text/javascript">
		window.parent.PutHdnValue('hdnUSERID', '4');
		window.parent.parent.PutHdnValue('hdnUSERID', '4');
		
		document.write(window.parent.GetHdnValue);
		document.write(window.parent.parent.GetHdnValue);
	</script>

	</HEAD>
	<body>
	
	<br>
	
	<form id="frmSearch" name="frmSearch">
	
	<input class="textbox_medium"  name="txtCaseID" type="TextBox" id="txtCaseID" value="101" ></input>
	</form>
	
	<script type="text/javascript">


		pageinit('http://ezclmacerpfs.suth.com/View/Pages/HTML/TicketSearch.html','http://ezclmacerpfs.suth.com/View/Pages/DS/TicketSearch.xml','frmSearch',null);

		
	</script>
	
	</body>
	</HTML>
	
	
	
	
	
	