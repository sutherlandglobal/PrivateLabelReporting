<?php

$loggedIn = false;
//$soapClient = new SoapClient("https://secure.logmeinrescue.com/api/api.asmx?wsdl");

#att 
#$user = 'web.service@sutherlandglobal.com';
#$pass =  'u)d#SkM,q^x7';

#pl
$user='jason.diamond@sutherlandglobal.com';
$pass='jasonSutherland!';

$url = "https://secure.logmeinrescue.com/api/api.asmx?wsdl";

$startDate = "12/20/2010";
//$startDate = "5/1/2011";
//$startDate = "8/1/2011";
$startTime = "00:00:00";

$endDate = "12/21/2010";
$endTime = "15:30:59";

//$endDate = "8/18/2011";
//$endTime = "04:00:00";

//schema breaking bug
//$startDate = "4/12/2011";
//$startTime = "03:00:00";
//
//$endDate = "4/12/2011";
//$endTime = "04:00:00";

//rtg
$nodeID = "6373240";

$nodeRef = "NODE";

//cochin
//$nodeID = "6372463";


if($argv[1])
{
	$reportArea = $argv[1];
}
else
{
	$reportArea = "TECHNICIAN_SURVEY";
	#$reportArea = "CUSTOMER_SURVEY";
	#$reportArea = "PERFORMANCE";
	#$reportArea = "CHAT_LOG";
	#$reportArea = "FAILED_SESSION";
	#$reportArea = "COLLABORATION_CHAT_LOG";
}

try
{
	echo "Running report $reportArea on $nodeID", PHP_EOL;

	$soapClient = new SoapClient
	(
		$url,
		array
		(
			'trace' => 1,
			'exceptions' => true,
			'compression' => SOAP_COMPRESSION_ACCEPT | SOAP_COMPRESSION_GZIP
		)
	);

	var_dump ($soapClient) ;
	#var_dump( $soapClient->__getFunctions());

	//define parameters
	$loginParams = array
	(
		'sEmail' => $user,
		'sPassword' => $pass
	);

	if($soapClient->login($loginParams)->loginResult == "login_OK" )
	{
		$request = $soapClient->requestAuthCode($loginParams);
		if($request->requestAuthCodeResult != "requestAuthCode_OK" )
		{
			throw new Exception("Failed to request LMI Auth Code");
		}

		$authCode = $request->sAuthCode;

		$loggedIn = true;
			
		echo "got authcode " . $authCode , PHP_EOL;
	}
	else
	{
		throw new Exception("Login Failed");
	}

	//set the type of report
	$reportAreaParams = array (
			'eReportArea' => $reportArea,
	);

	$reportDateParams= array(
		'sBeginDate' => $startDate,
		'sEndDate' => $endDate,
	);

	//set the report time
	$reportTimeParams = array (
		'sBeginTime' => $startTime,
		'sEndTime' => $endTime
	);

	$getReportParams = array(
		'iNodeID' => $nodeID,
		'eNodeRef' => $nodeRef,
	);

	$delimiterParams = array(
		'sDelimiter' => 'Ω',
	);

	$timezone = -300;  //UTC -5 hours = = -300 minutes (CST during Daylight Savings)

	date_default_timezone_set('America/New_York');

	$time = localtime(time(), true);

	if(!$time['tm_isdst'])
	{
		$timezone -= 60;
	}

	$timeZoneParams = array(
			'sTimezone' => $timezone,
			'sAuthCode' => $authCode
	);
	
	$reportAreaParams['sAuthCode'] = $authCode;
	$reportDateParams['sAuthCode'] = $authCode;
	$reportTimeParams['sAuthCode'] = $authCode;
	$getReportParams['sAuthCode'] = $authCode;
	$delimiterParams['sAuthCode'] = $authCode;

	$setReportAreaResponse = $soapClient->setReportArea($reportAreaParams);
	$setReportTimeResponse = $soapClient->setReportTime($reportTimeParams);
	$setReportDateResponse = $soapClient->setReportDate($reportDateParams);
	$setReportDelimiterResponse = $soapClient->setDelimiter($delimiterParams);
	$setReportTimeZoneResponse= $soapClient->setTimeZone($timeZoneParams);
	
	var_dump($setReportAreaResponse);
	var_dump($setReportTimeResponse);
	var_dump($setReportDateResponse);
	var_dump($setReportDelimiterResponse);
	var_dump($setReportTimeZoneResponse);
	var_dump($soapClient);
	
	$getReportResponse = $soapClient->getReport($getReportParams);

	$reportData =  $getReportResponse->sReport;

	echo $reportData , PHP_EOL;

	//	echo $soapClient->__getLastRequest, PHP_EOL;
	//	echo $soapClient->__getLastRequestHeaders, PHP_EOL;
	//	echo $soapClient->__getLastResponse, PHP_EOL;
	//	echo $soapClient->__getLastResponseHeaders, PHP_EOL;

	var_dump($getReportResponse);

	echo "===================", PHP_EOL;
}
catch (Exception $e)
{
	echo $e->getMessage() , PHP_EOL;
	echo $e->getLine() , PHP_EOL;
}

if($loggedIn)
{
	echo "logging out\n";
	$logoutResult = $soapClient->logout();

	//var_dump($logoutResult);
	if($logoutResult->logoutResult == "logout_OK")
	{
		echo "logout good\n";
	}
	else
	{
		echo "logout bad\n";
	}
}
else
{
	echo "not logged in, so not logging out\n";
}

?>