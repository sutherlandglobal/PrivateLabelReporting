<?php

//expect a csv file, an export of the excel schedule

//file has weekly data, will have to figure out the first day of the month's day name
//filename has month and year in file name -- 20110_Tech_Schedules_2013-06-Rev2.xlsx

//for each user
//	iterate through current month's days, starting at day 1
//	determine day's name, and retrieve start and stop times, skip if time is "off"
//	if the user exists in the user's table, insert that user's schedule for that day. existing schedules should be updated. a user will only have one scheduled shift per days
//	load YYYY-MM-DD 00:00:00, user id, start time, stop time into a string array for conversion to an insert statement

include 'Agent.php';
include 'ReplicationSite.php';

ini_set('memory_limit', '512M');

$scheduleFile = null;

$expectedFilePrefix = "20110_Tech_Schedules_";
$expectedFileSuffix = ".csv";

$confFile = "conf/importer.conf";

$scheduleYear = null;
$scheduleMonth = null;

$targetTableDirective = "targetTable";
$databaseDirective = "databaseName";
$dbHostDirective = "host";
$dbPortDirective ="port";
$dbUserDirective ="user";
$dbPassDirective ="pass";

$targetTable = null;
$databaseName = null;
$dbHost = null;
$dbPort = null;
$dbUser = null;
$dbPass = null;

$finalStats = array
(
		'repTime' =>0,
		'repRows' => 0,
		'repAttempts' => 0,
);

//grab the conf file and config directives
if(file_exists($confFile))
{
	echo "Reading login info from $confFile", PHP_EOL;

	foreach( file($confFile) as $line)
	{
		if(!preg_match('/^\s*$/', $line) && !preg_match('/^#/', $line))
		{
			$line = chop($line);

			$lineFields = explode('=', $line);

			if(count($lineFields) > 2)
			{
				throw new Exception("Malformed config line in file $confFile: $line");
			}

			if($lineFields[0] == $targetTableDirective)
			{
				$targetTable = $lineFields[1];
			}
			else if($lineFields[0] == $databaseDirective)
			{
				$databaseName = $lineFields[1];
			}
			else if($lineFields[0] == $dbHostDirective)
			{
				$dbHost = $lineFields[1];
			}
			else if($lineFields[0] == $dbPortDirective)
			{
				$dbPort = $lineFields[1];
			}
			else if($lineFields[0] == $dbUserDirective)
			{
				$dbUser = $lineFields[1];
			}
			else if($lineFields[0] == $dbPassDirective)
			{
				$dbPass = $lineFields[1];
			}
		}
	}
	
	if( !$targetTable ||  !$dbHost || !$dbUser || !$dbPass || !$dbPort)
	{
		var_dump(array($targetTable, $dbHost,  $dbUser,  $dbPass,  $dbPort ));
		throw new Exception("Missing config directives");
	}
}
else
{
	echo "Couldn't read conf file", PHP_EOL;
	exit;
}

//grab the schedule csv file

if(count($argv) > 1)
{
	//first arg is the filename
	
	$scheduleFile = $argv[1];
	$scheduleFilename = basename($scheduleFile);
	
	if( count($argv) >= 3)
	{
		//unexpected, expect the month and date in param 2
	
		$scheduleYear = substr($argv[2], 0, 4);
		$scheduleMonth = substr($argv[2], 4, 2);
	}
	else if( preg_match("/^$expectedFilePrefix/", $scheduleFilename) && preg_match("/$expectedFileSuffix$/", $scheduleFilename) )
	{
		//if the filename matches the expected format, get the month and year from the filename
		
		//expected, we can grab the month and date from the file name
		$dateChunk = substr(array_pop(explode("_", $scheduleFilename)), 0, 7);
		
		$dateFields = explode("-", $dateChunk);
		
		$scheduleYear = $dateFields[0];
		$scheduleMonth = $dateFields[1];
	}
}

if( $scheduleFile == null || !file_exists($scheduleFile))
{
	echo "Schedule File invalid or couldn't be found", PHP_EOL;
	exit;
}
if($scheduleYear == null)
{
	echo "Schedule Year invalid.", PHP_EOL;
	exit;
}
if($scheduleMonth == null) 
{
	echo "Schedule Month invalid.", PHP_EOL;
	exit;
}

//echo $scheduleFile, PHP_EOL, $scheduleYear, PHP_EOL, $scheduleMonth, PHP_EOL;

//get the raw csv schedules
$csvLines = array();
try
{
	//open the file, most of it will be garbage, only import good lines

	foreach( file($scheduleFile) as $line)
	{
		if(!preg_match('/^\s*$/', $line) )
		{
			$csvFields = str_getcsv($line);

			if
			(
				preg_match('/[0-9]+/', $csvFields[1]) &&
				$csvFields[2] != "" &&
				preg_match('/[A-Za-z\-]+/', $csvFields[2]) &&
				$csvFields[3] != "" &&
				preg_match('/[A-Za-z0-9]+/', $csvFields[3]) &&
				$csvFields[4] != "" &&
				preg_match('/[A-Za-z\-\ ]+/', $csvFields[4]) &&
				$csvFields[5] != "" &&
				preg_match('/[0-9]{2}\:[0-9]{2}\ \-\ [0-9]{2}\:[0-9]{2}/', $csvFields[5])
			)
			{
				array_push($csvLines, $line);
			}
		}
	}
}
catch(Exception $e)
{
	echo $e->getMessage() , PHP_EOL;
	echo $e->getLine() , PHP_EOL;
}

//figure out the first day of the month

date_default_timezone_set('America/New_York');

$agents = array();

//build the agents and their respective shifts
foreach( $csvLines as $csvLine)
{
	$csvFields = str_getcsv($csvLine);
	
	$agentShifts = array();
	
	//agent names are agent ids
	$agentName = $csvFields[3];
	
	$agentShifts["sunday"] = $csvFields[5];
	$agentShifts["monday"] = $csvFields[6];
	$agentShifts["tuesday"] = $csvFields[7];
	$agentShifts["wednesday"] = $csvFields[8];
	$agentShifts["thursday"] = $csvFields[9];
	$agentShifts["friday"] = $csvFields[10];
	$agentShifts["saturday"] = $csvFields[11];
	
	//get the day name from yyyy-mm-01, and iterate 
	
	$currentDate = date_create();
	$currentDate = date_date_set($currentDate, $scheduleYear, $scheduleMonth, "1" );
	$currentDate = date_time_set($currentDate, 0,0,0);
	
	$agent = new Agent($agentName);
	
	while(date_format($currentDate, "n") == $scheduleMonth)
	{
		
		$dayShift = $agentShifts[strtolower(date_format($currentDate, "l"))];

		if(preg_match('/[0-9]{2}\:[0-9]{2}\ \-\ [0-9]{2}\:[0-9]{2}/', $dayShift))
		{
			$shiftFields = explode('-', preg_replace('/\ /', '', $dayShift));

			$shiftStartString = $shiftFields[0];
			$shiftEndString = $shiftFields[1];

			//echo $dayShift, ":" ,$shiftStartString, "-", $shiftEndString, PHP_EOL;

			$shiftStart = date_create();
			$shiftStart = date_date_set($shiftStart, $scheduleYear, $scheduleMonth, date_format($currentDate, "j") );
			
			$shiftEnd = date_create();
			$shiftEnd = date_date_set($shiftEnd, $scheduleYear, $scheduleMonth, date_format($currentDate, "j") );
			
			$timeFields = explode(":", $shiftStartString);
			$shiftStartHour = $timeFields[0];
			$shiftStartMin = $timeFields[1];
			
			$timeFields = explode(":", $shiftEndString);
			$shiftEndHour = $timeFields[0];
			$shiftEndMin = $timeFields[1];
			
			$shiftStart = date_time_set($shiftStart, $shiftStartHour, $shiftStartMin, 0 );
			$shiftEnd = date_time_set($shiftEnd, $shiftEndHour, $shiftEndMin, 0 );
			
			//account for shifts that span midnight
			//shiftstart hour is greater than shift end hour, the shift ends in the next day
			if($shiftStartHour > $shiftEndHour)
			{
				$shiftEnd = date_add($shiftEnd,  date_interval_create_from_date_string("1 days"));
			}
					
			//echo date_format($shiftStart, "r"), " => ",  date_format($shiftEnd, "r"), PHP_EOL;
			
			$agent->addShift($shiftStart, $shiftEnd);
		}
		
		$currentDate = date_add($currentDate,  date_interval_create_from_date_string("1 days"));
	}
	
	array_push($agents, $agent);
}

//connect to the database, replicate the agent's schedules
$repSite = new ReplicationSite($dbHost, $dbPort, $dbUser, $dbPass, $targetTable); 

foreach($agents as $agent)
{
	$repSite->replicate($agent->getShifts());
}

?>