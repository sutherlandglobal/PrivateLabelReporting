<?php

require 'Queue.php';
require 'IVRQueue.php';
require 'LMIQueue.php';
require 'HTMLWriter.php';

#for each updater file, load info and build an updater

$path = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/scripts/dissatQueue/";
$htmlTargetDir = "/opt/tomcat/webapps/birt-viewer/surveyQueues/";

$confDir = $path . "../../conf/dissatQueue/";

$caseListFile = $confDir . 'cases.dat';

$LOCKFILE_NAME = $confDir . "lockfile";

//check for lockfiles in the localdir, only once.
if(file_exists($LOCKFILE_NAME))
{
	echo "Aborting...lockfile found.", PHP_EOL;
	exit(0);
}
else if( !file_put_contents($LOCKFILE_NAME, getmypid(), LOCK_EX) )
{
	echo "Aborting...could not create lockfile.", PHP_EOL;
	exit(1);
}

$iterator = new RecursiveIteratorIterator(new RecursiveDirectoryIterator($confDir), RecursiveIteratorIterator::CHILD_FIRST);

date_default_timezone_set('America/New_York');

#for each updater, run and accumulate new cases
foreach($iterator as $file)
{
	if(preg_match('/\.conf$/', $file))
	{
		try
		{
			echo "===============================", PHP_EOL;
			
			$date = date_create();
			echo $date->format("Y-m-d H:i:s"), ": ",$file,  PHP_EOL;
			
			
			$thisQueue = 0;
			
			if($file ==  $confDir . "ivr.conf")
			{
				$thisQueue = new IVRQueue($file);
			}
			else if($file == $confDir . "lmi5452038.conf" || $file == $confDir . "lmi6971911.conf" )
			{
				$thisQueue = new LMIQueue($file);
			}
			
			if($thisQueue)
			{
				$thisQueue->runUpdate();
				
				$thisQueue->saveCases();
				
				$writer = new HTMLWriter($thisQueue->getCaseList(), $thisQueue->getHTMLFile(), $thisQueue->getTitle() );
				
				$writer->writeHTML();
				
				if(!is_dir($htmlTargetDir))
				{
					echo "Creating target directory $htmlTargetDir", PHP_EOL;
					if(mkdir($htmlTargetDir))
					{
						copy($thisQueue->getHTMLFile(), $htmlTargetDir . basename($thisQueue->getHTMLFile()));
					}
					else
					{
						echo "Error deploying HTML file: $thisQueue->getHTMLFile(), could not create directory: $htmlTargetDir",PHP_EOL;
					}
				}
				else
				{
					copy($thisQueue->getHTMLFile(), $htmlTargetDir . basename($thisQueue->getHTMLFile()));
				}
			}
		}
		catch(Exception $e)
		{
			echo $e->getMessage() , PHP_EOL;
			echo $e->getLine() , PHP_EOL;
		}
	}
}

unlink($LOCKFILE_NAME);

########################


?>