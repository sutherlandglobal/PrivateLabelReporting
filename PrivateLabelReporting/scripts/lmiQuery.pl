#!/usr/bin/perl -w

#query logmein for chatlogs

use WWW::Mechanize;
use HTTP::Cookies;

#my $user = 'jason.diamond@sutherlandglobal.com';
my $user = 'xyz123@sutherlandglobal.com';
my $pass = 'jasonSutherland!';

my $url = 'https://secure.logmeinrescue.com/';
my $backendUrl = 'https://secure.logmeinrescue.com/AdminCenter/Backend.aspx';
my $reportUrl = 'https://secure.logmeinrescue.com/AdminCenter/Report.aspx';
$logoutUrl = "https://secure.logmeinrescue.com/HelpDesk/Logout.aspx";

#https://secure.logmeinrescue.com/AdminCenter/Report.aspx?report_hidden_node=5452038&report_hidden_selectedtype=node&report_hidden_area=5&report_hidden_type=1&report_hidden_startdate=2011%2F1%2F17+0%3A0%3A00.000&report_hidden_enddate=2011%2F1%2F18+0%3A0%3A00.000&report_hidden_starttime=0&report_hidden_endtime=1440&report_hidden_timezone_offset=-300&report_hidden_dateformat=M%2Fd%2Fyyyy&report_hidden_target=3

#./lmiQuery.pl RochesterGroup Chatlog 2011-01-10_00-00 2011-01-17_00-00

my %nodeTable =
(
	'RochesterGroup' => '5452038',
	'DLFGroup' => '6971911',
	'SalesGroup' =>'6894856',
	'SSMGroup' => '6373240',
	'TestGroup' => '6664024',
);

my %areaTable =
(
	'CustomerSurvey' => '1',
	'Performance' => '2',
	'Login' => '3',
	'Session' => '4',
	'Chatlog' => '5',
	'CollaborationChatlog' => '6',
	'CustomFields' => '7',
	'MissedSessions' => '8',
	'TransferredSessions' => '9',
	"TechnicianSurvey" => '10',
	'FailedSessions' => '11',
	'ConcurrentUsage' => '12',
);

my($node, $area, $startDate, $endDate) = @ARGV;

$startDate = &encodeDate($startDate);
$endDate = &encodeDate($endDate);

die "Invalid parameters\n" unless
$nodeTable{$node} &&
$areaTable{$area} &&
$startDate &&
$endDate
;


my $finalUrl =  $reportUrl . 
'?report_hidden_node= ' . $nodeTable{$node}  .
'&report_hidden_selectedtype=node' .
'&report_hidden_area=' . $areaTable{$area} .
'&report_hidden_type=' . 1 . 
'&report_hidden_startdate=' . $startDate .
'&report_hidden_enddate=' . $endDate . 
'&report_hidden_starttime=' . 0 .
'&report_hidden_endtime=' . 1440 .
'&report_hidden_timezone_offset=' . -300 .
'&report_hidden_dateformat=' . 'M%2Fd%2Fyyyy' . 
'&report_hidden_target=3'
;

print $finalUrl . "\n";
print "=========\n";



my $mech = WWW::Mechanize->new();

$mech->get($url);

#<input name='email' type='text' id='email' class=' tbox' style='width:164px;' size='26' maxlength='128'  value='Email'  tabindex="1" />

#<input type="password" size="26" id="password" name="password" maxlength="70" class=" tbox" style="width:164px" tabindex="2" />
#<input id="password_wmark" name="password_wmark" type="text" style="margin-left: -164px; width: 164px;" readonly="readonly" class="wmark hasjs" value="Password" />

#<button id="loginButton" name="loginButton"  tabindex="3"type="submit" >Log Me In</button>
$mech->field('email' => $user);
$mech->field('password_wmark' => $pass);
$mech->field('password' => $pass);

$mech->click_button('name' => 'loginButton');

print $mech->content() . "\n";

$mech->get($finalUrl);



#print "" . &convertXMLToCSV( $mech->content()) . "\n";

$mech->get($logoutUrl);

sub convertXMLToCSV
{
	my( $text ) = $_[0];
	
	chomp $text;
	
	#my $parser = new XML::( Style => 'Tree' );
	#$parser->parse($text);
	
	use HTML::Scrubber;
	
	my $s = HTML::Scrubber->new('allow' => ['Row', 'Cell']);
	
	$text = $s->scrub($text);
	$text =~ s/\<\/row\>/\n/g;
	$text =~ s/\<cell\>/\"/g;
	$text =~ s/\<\/cell\>/\"\,/g;
	$text =~ s/\<row\>//g;
	$text =~ s/\n\s*\n/\n/g;
	
	#these might be newlines in a chatlog
	$text =~ s/\&\#10\;/ /g;

	#^M chars
	$text =~ s/\cM//g;

	#remove first two lines; title and blank
	$text =~ s/^.*\n//;   

	

	return $text;
}

sub encodeDate
{
	my($date ) = @_;
	my $retval = "";
	
	if($date =~ /\d\d\d\d\-\d\d-\d\d_\d\d\-\d\d/)
	{
		my ($datePart,$timePart) =split('_', $date);
		
		
		#2011%2F1%2F17+0%3A0%3A00.000
		#report.aspx requires single digits for <10 dates
		if($datePart && $timePart)
		{
			my ($year,$mon,$day) = split('-', $datePart);
			
			if(length $mon == 2 && $mon < 10)
			{
				$mon = substr($mon,1);
			}
			
			if(length $day == 2 && $day < 10)
			{
				$day = substr($day,1);
			}
			
			my ($hour,$min) = split('-', $timePart);
			
			if(length $hour == 2 && $hour < 10)
			{
				$hour = substr($hour,1);
			}
			
			if(length $min == 2 && $min < 10)
			{
				$min = substr($min,1);
			}
			
			
			if( $year eq "" || $mon eq "" || $day eq "" || $hour eq ""  || $min eq "")
			{
				print "Null date fields in formatting: \n" . 
				"year: $year\n".
				"mon: $mon\n". 
				"day: $day\n". 
				"hour: $hour\n". 
				"min: $min\n";  
			}
			else
			{
				$retval = $year . '%2F' . $mon . '%2F' . $day . '+' . $hour . '%3A' . $min . '%3A00.000';
			} 
		} 
		else
		{
			print "Invalid Date Parameters\n";
		}
	}
	else
	{
		print "Invalid Date $date\n";
	}
		
	
	return $retval;
}

#############################################################
##javascript garbage comprises entire page body
##https://secure.logmeinrescue.com/AdminCenter/Scripts/report.js
#
##that script just collects crap from form elements and submits for an xml form
##we will likely need to convert xml to csv or use an xml parser to get some useful db updates out of it
#
##xmlhttp.js handes the processing
##reporting.js -> xmlhttp.js
#
##reporting.js compiles the "message" and passes it off
#
##rescue.js
##tree_selected_id = getCookie("idSelected");
#
#
##helpers.js
##function trimNodeId( id )
##{
##	if (id != null && id.length>9) return id.substr(9, id.length);
##	else return id;
##}
#
#my $nodeID = "5452038";
#
##	var selectedType = "node";
##	if (isChannelNode(tree_selected_id)) selectedType = "channel";
#
##helpers.js
##function isChannelNode( id )
##{
##	if (id != null && id.length>9) return (id.substr(7,2) == 'c_');
##	else return false;
##}
#
#my $selectedType = "";
#
##startdate
##	    var bdth = parseInt(document.getElementById("report_begin_date_hour").value);
##	    var bdtm = parseInt(document.getElementById("report_begin_date_minutes").value);
##	    var bdtampm = parseInt(document.getElementById("report_begin_date_ampm").value);
##	    if (bdth == 12) bdth = 0;
##	    if (bdtampm == 1) bdth += 12;
##
##	    var startdate = document.getElementById("report_begin_date_year").value + "/" +
##			document.getElementById("report_begin_date_month").value + "/" +
##			document.getElementById("report_begin_date_day").value + " " + bdth + ":" + bdtm + ":00.000";
#
#my $startDate = '2011/01/01 00:00:00.000';
#my $endDate = '2011/01/02 23:59:59.000';
#
##startTime
#
##		var bh = parseInt(document.getElementById("report_begin_time_hour").value);
##		var bm = parseInt(document.getElementById("report_begin_time_minutes").value);
##		var bampm = parseInt(document.getElementById("report_begin_time_ampm").value);
##		if (bh == 12) bh = 0;
##		if (bampm == 1) bh += 12;
#
##var starttime = bh * 60 + bm;
#
##start and end time seem to be a number of minutes from start of day, to end of day
#my $startTime = 0;
#my $endTime = (23 * 60) + 59;
#
#
#my $message = 
##node id is the ID on mouseover of groups, i think
#			 "<msg>" .
#			 "<node>" . $nodeID . "</node>" .
#			"<selectedtype>" . "node". "</selectedtype>" .
#			#"<area>" . "Chatlog" . "</area>" .
#			#"<type>" . "List All" . "</type>" .
#			"<area>" . "5" . "</area>" .
#			"<type>" . "1" . "</type>" .
#			"<startdate>" . $startDate . "</startdate>" .
#			"<enddate>" . $endDate . "</enddate>" .
#
##maybe unnecessary for date reports
#			"<starttime>" . $startTime . "</starttime>" .
#			"<endtime>" . $endTime . "</endtime>" .
#
#			"<timezone>" . "-300" . "</timezone>" .
#			"<dateformat>" . 'm/d/yyyy' . "</dateformat>" . 
#			"</msg>";
#
##xml message
##
##		var message = "<msg><node>" + trimNodeId(tree_selected_id) + "</node>" +
##			"<selectedtype>" + selectedType + "</selectedtype>" +
##			"<area>" + document.getElementById("report_area").value + "</area>" +
##			"<type>" + document.getElementById("report_type").value + "</type>" +
##			"<startdate>" + startdate + "</startdate>" +
##			"<enddate>" + enddate + "</enddate>" +
##			"<starttime>" + parseInt(starttime) + "</starttime>" +
##			"<endtime>" + parseInt(endtime) + "</endtime>" +
##			"<timezone>" + timezone + "</timezone>" +
##			"<dateformat>" + document.getElementById("report_date_format").value + "</dateformat>" +
##			"</msg>";
#
##function xmlhttp_post(event_name, event_param, response_function)
#
##https://secure.logmeinrescue.com/AdminCenter/Backend.aspx
##xmlhttp.js posts to this script
##xmlhttp.open('POST', 'Backend.aspx', true);
#
#
#
#
#
#########################
#
#my $mech = WWW::Mechanize->new();
#
#$mech->get($url);
#
##<input name='email' type='text' id='email' class=' tbox' style='width:164px;' size='26' maxlength='128'  value='Email'  tabindex="1" />
#
##<input type="password" size="26" id="password" name="password" maxlength="70" class=" tbox" style="width:164px" tabindex="2" />
##<input id="password_wmark" name="password_wmark" type="text" style="margin-left: -164px; width: 164px;" readonly="readonly" class="wmark hasjs" value="Password" />
#
##<button id="loginButton" name="loginButton"  tabindex="3"type="submit" >Log Me In</button>
#$mech->field('email' => $user);
#$mech->field('password_wmark' => $pass);
#$mech->field('password' => $pass);
#
#$mech->click_button('name' => 'loginButton');
#
##		xmlhttp_post('on_get_report', message, 
##			function(xmlhttp_response, error_code, error_desc) 
##			{
##			    if (error_code != 0) alert(jsMessages["textJavascriptMessageError"] + error_desc);
##				button_obj.disabled = false;
##				updateRightPane( xmlhttp_response, error_code, error_desc );
##
##				// Show report
##				document.getElementById("report_data_context").style.display = "block";
##                setFooterPosition();
##			}
##		);
#
##function xmlhttp_post(event_name, event_param, response_function)
#
##my @form = 
##(
##	"on_get_report",
##	$message,
##			'function(xmlhttp_response, error_code, error_desc) 
##			{
##			    if (error_code != 0) alert(jsMessages["textJavascriptMessageError"] + error_desc);
##				button_obj.disabled = false;
##				document.write( xmlhttp_response + "\n");
##				document.write(error_code + "\n"  );
##				document.write(error_desc + "\n" );
##
##				// Show report
##				document.getElementById("report_data_context").style.display = "block";
##                setFooterPosition();
##			});'
##);
##
##my $response =  $mech->post( $backendUrl, \@form );
#
##############
#
##my %form = 
##(
##	"report_hidden_node" => '5452038',
##	"report_hidden_selectedtype" => 'node',
##	"report_hidden_area" => '5',
##	"report_hidden_type" => '1',
##	"report_hidden_startdate" => '2011%2F1%2F17+0%3A0%3A00.000',
##	"report_hidden_enddate" => '2011%2F1%2F18+0%3A0%3A00.000',
##	"report_hidden_starttime" => '0',
##	"report_hidden_endtime" => '1440',
##	"report_hidden_timezone_offset" => '-300',
##	"report_hidden_type" => '1',
##	"report_hidden_dateformat" => 'M%2Fd%2Fyyyy',
##	"report_hidden_target" => '3',
##);
##
##my $response =  $mech->post( $backendUrl, \%form );
#
###############
#
#use HTTP::Request::Common;
#$ua = LWP::UserAgent->new;
#
#my $response =  $ua->request
#(
#POST $reportUrl,
#[
##	'Host' => 'secure.logmeinrescue.com',
##	'User-Agent' => 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.16) Gecko/20110107 Iceweasel/3.5.16 (like Firefox/3.5.16)',
##	'Accept' => 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
##	'Accept-Language' => 'en-us,en;q=0.5',
##	'Accept-Encoding' => 'gzip,deflate',
##	'Accept-Charset' => 'UTF-8,*',
##	'Keep-Alive' => '300',
##	'Connection' => 'keep-alive',
##	'Referer' => 'https://secure.logmeinrescue.com/US/AdminCenter/Console.aspx',
##	'Cookie' => 'WT_FPC=id=227f752e82e17ebe9e81295305186539:lv=1295323284687:ss=1295323284687; rescue_language=en-US; ASP.NET_SessionId=imjxvfra1xkcr0qssrne52m4',
##	'X-Behavioral-Ad-Opt-Out' => '1',
##	'X-Do-Not-Track' => '1',
##	'Content-Type' => 'application/x-www-form-urlencoded',
#	#'Content-Length' => '353',
#	#POSTDATA=report_hidden_node=5452038&report_hidden_selectedtype=node&report_hidden_area=5&report_hidden_type=1&report_hidden_startdate=2011%2F1%2F17+0%3A0%3A00.000&report_hidden_enddate=2011%2F1%2F18+0%3A0%3A00.000&report_hidden_starttime=0&report_hidden_endtime=1440&report_hidden_timezone_offset=-300&report_hidden_dateformat=M%2Fd%2Fyyyy&report_hidden_target=3
#	
#
#	"report_hidden_node" => '5452038',
#	"report_hidden_selectedtype" => 'node',
#	"report_hidden_area" => '5',
#	"report_hidden_type" => '1',
#	"report_hidden_startdate" => '2011%2F1%2F17+0%3A0%3A00.000',
#	"report_hidden_enddate" => '2011%2F1%2F18+0%3A0%3A00.000',
#	"report_hidden_starttime" => '0',
#	"report_hidden_endtime" => '1440',
#	"report_hidden_timezone_offset" => '-300',
#	"report_hidden_type" => '1',
#	"report_hidden_dateformat" => 'M%2Fd%2Fyyyy',
#	"report_hidden_target" => '3',
#	]
#);
#
##my $response = $mech->post($backendUrl,
##
##[
##	"report_hidden_node" => '5452038',
##	"report_hidden_selectedtype" => 'node',
##	"report_hidden_area" => '5',
##	"report_hidden_type" => '1',
##	"report_hidden_startdate" => '2011%2F1%2F17+0%3A0%3A00.000',
##	"report_hidden_enddate" => '2011%2F1%2F18+0%3A0%3A00.000',
##	"report_hidden_starttime" => '0',
##	"report_hidden_endtime" => '1440',
##	"report_hidden_timezone_offset" => '-300',
##	"report_hidden_type" => '1',
##	"report_hidden_dateformat" => 'M%2Fd%2Fyyyy',
##	"report_hidden_target" => '3',
##]
##
##);
##
##print $response->code() . "\n";
##print $response->message()  ."\n";
###print $response->content() . "\n";
##print $response->decoded_content() . "\n";
##
###$response = $mech->reload();
#
#print $response->code() . "\n";
#print $response->message()  ."\n";
##print $response->content() . "\n";
#print $response->decoded_content() . "\n";
#
##############



