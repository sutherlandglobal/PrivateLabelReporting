<?php

require 'CaseTicket.php';
#require 'LMICase.php';
#require 'IVRCase.php';

abstract class Queue
{
	protected $confFile;

	protected $host;
	protected $port;
	protected $user;
	protected $pass;
	protected $query;
	protected $updateDate;

	protected $queueFile;

	protected $caseList;
	protected $oldCases;

	protected $htmlFile;
	protected $title;

	const HOST_DIRECTIVE = "host";
	const PORT_DIRECTIVE = "port";
	const USER_DIRECTIVE = "user";
	const PASS_DIRECTIVE = "pass";
	const UPDATE_DIRECTIVE = "lastUpdated";
	const QUEUE_FILE_DIRECTIVE = "queueFile";
	const TITLE_DIRECTIVE ="title";
	const HTML_FILE_DIRECTIVE = "htmlFile";
	const QUERY_DIRECTIVE = "query";

	public function __construct($confFile)
	{
		$this->confFile = $confFile;

		$this->host = "";
		$this->port = "";
		$this->user = "";
		$this->pass = "";
		$this->updateDate = "";

		$this->loadConf();

		#don't really validate config, just count on a db connection to fail
		if(!$this->connect())
		{
			throw new Exception ("Could not connect to database at $host:$port");
		}

		$this->loadCases();

		$this->caseList = array();
	}

	private function loadConf()
	{
		$lastUpdatedString = "";

		if( file_exists($this->confFile) )
		{
			foreach( file($this->confFile) as $line)
			{
				if(!preg_match('/^\s*$/', $line) && !preg_match('/^#/', $line))
				{
					$line = chop($line);

					$directive = substr($line, 0, strpos($line,"="));
					$data = substr($line, strpos($line,"=")+1);
						
					if($directive == Queue::HOST_DIRECTIVE)
					{
						$this->host = $data;
					}
					else if($directive == Queue::PORT_DIRECTIVE)
					{
						$this->port = $data;
					}
					else if($directive == Queue::USER_DIRECTIVE)
					{
						$this->user = $data;
					}
					else if($directive == Queue::PASS_DIRECTIVE)
					{
						$this->pass =$data;
					}
					else if($directive == Queue::QUEUE_FILE_DIRECTIVE)
					{
						$this->queueFile =$data;
					}
					else if($directive == Queue::UPDATE_DIRECTIVE)
					{
						$this->updateDate =$data;
					}
					else if($directive == Queue::TITLE_DIRECTIVE)
					{
						$this->title = $data;
					}
					else if($directive == Queue::HTML_FILE_DIRECTIVE)
					{
						$this->htmlFile = $data;
					}
					else if($directive == Queue::QUERY_DIRECTIVE)
					{
						$this->query = $data;
					}
				}
			}
				
			if( !$this->host || !$this->port || !$this->user|| !$this->pass )
			{
				echo $this->host, PHP_EOL;
				echo $this->port, PHP_EOL;
				echo $this->user, PHP_EOL;
				echo $this->pass, PHP_EOL;

				throw new Exception("Missing config directives");
			}
				
			date_default_timezone_set('America/New_York');

			$lastUpdatedDate = date_create();

			if($this->updateDate)
			{
				$dateFields = date_parse_from_format( "Y-m-d H:i:s", $this->updateDate);

				$lastUpdatedDate = date_date_set($lastUpdatedDate, $dateFields['year'], $dateFields['month'], $dateFields['day'] );
				$lastUpdatedDate = date_time_set($lastUpdatedDate, $dateFields['hour'], $dateFields['minute'], $dateFields['second']);
			}

			//echo $lastUpdatedDate->format("Y-m-d H:i:s"), PHP_EOL;
			$this->updateDate = $lastUpdatedDate->format("Y-m-d H:i:s");

		}
		else
		{
			echo "no conf file", PHP_EOL;
		}

	}

	protected function writeLastUpdateDate($date)
	{
		$lastUpdatedDate = date_create();

		$dateFields = date_parse_from_format( "Y-m-d H:i:s", $date);

		$lastUpdatedDate = date_date_set($lastUpdatedDate, $dateFields['year'], $dateFields['month'], $dateFields['day'] );
		$lastUpdatedDate = date_time_set($lastUpdatedDate, $dateFields['hour'], $dateFields['minute'], $dateFields['second']);

		$lastUpdatedDateString = $lastUpdatedDate->format("Y-m-d H:i:s");

		//		$outputString =
		//			Updater::HOST_DIRECTIVE . "=" . $this->host . PHP_EOL .
		//			Updater::PORT_DIRECTIVE . "=" . $this->port . PHP_EOL .
		//			Updater::USER_DIRECTIVE . "=" . $this->user . PHP_EOL .
		//			Updater::PASS_DIRECTIVE . "=" . $this->pass . PHP_EOL .
		//			Updater::UPDATE_DIRECTIVE . "=" . $lastUpdatedDateString . PHP_EOL;
			

		//can't lock at the os level
		$confFileContents = file_get_contents($this->confFile);

		file_put_contents($this->confFile, str_replace(
		Queue::UPDATE_DIRECTIVE . "=" . $this->updateDate,
		Queue::UPDATE_DIRECTIVE . "=" . $lastUpdatedDateString .PHP_EOL, $confFileContents)
		);
	}

	abstract protected function connect();

	abstract public function runUpdate();
	abstract protected function shouldAddCase($potentialCase);

	protected function loadCases()
	{
		$this->oldCases = array();

		if(file_exists($this->queueFile))
		{
			$schemaFields = 0;

			foreach( explode("\n", file_get_contents($this->queueFile)) as $line)
			{
				if(!preg_match('/^\s*$/', $line) && !preg_match('/^#/', $line))
				{
					#first line is schema of tickets
					if(!$schemaFields)
					{
						$schemaFields = str_getcsv($line, ",", '"');
					}
					else
					{
						$case = 0;

						$caseFields = array();
						$caseData = str_getcsv($line, ",", '"');

						for($i = 0; $i<count($schemaFields); $i++)
						{
							$caseFields[$schemaFields[$i]] = $caseData[$i];
						}

						if($this->title == "IVR")
						{
							$case = new IVRCase($caseFields);
						}
						else if($this->title == "LMI_Level_1" || $this->title == "LMI_Level_2")
						{
							$case = new LMICase($caseFields);
						}
						else
						{
							echo "unknown case type, skipping loading of case $line", PHP_EOL;
						}

						if($case)
						{
							echo "Loading case ", $case->toString(), PHP_EOL;
							array_push($this->oldCases, $case);
						}
					}
				}
			}
		}
		else
		{
			touch($this->queueFile);
		}
	}

	public function saveCases()
	{
		$output = "";

		$schemaString = "";

		foreach($this->caseList as $case)
		{
			$line = $case->toString();
			if($line != "")
			{
				if(!$schemaString)
				{
					$schemaString = $case->getSchemaString();
				}

				$output .= $line . "\n";
			}
		}

		file_put_contents($this->queueFile, $schemaString . "\n" . $output);
	}

	protected function getHost()
	{
		return $this->host;
	}

	protected function getPort()
	{
		return $this->port;
	}

	public function getTitle()
	{
		return $this->title;
	}

	public function getHTMLFile()
	{
		return $this->htmlFile;
	}

	protected function isNewCase($case)
	{
		$retval = true;

		foreach($this->caseList as $confirmedCase)
		{
			if($confirmedCase->equals($case))
			{
				$retval = false;
				break;
			}
		}

		return $retval;
	}

	protected function isOldCase($case)
	{
		$retval = false;

		foreach($this->oldCases as $oldCase)
		{
			if($oldCase->equals($case))
			{
				$retval = true;
				break;
			}
		}

		return $retval;
	}

	protected function addCase($newCase)
	{
		$retval = false;
		if($this->isNewCase($newCase))
		{
			if($this->title == "IVR")
			{
				echo "Adding IVR case: ", $newCase->toString(), PHP_EOL;
				array_push($this->caseList, $newCase);

				$retval = true;
			}
			else if($this->title == "LMI_Level_1" || $this->title == "LMI_Level_2" )
			{
				echo "Adding LMI case: ", $newCase->toString(), PHP_EOL;
				array_push($this->caseList, $newCase);

				$retval = true;
			}
			else
			{
				echo "Add failed ";
				print_r($caseFields);
			}
		}

		return $retval;
	}

	public function getCaseList()
	{
		return $this->caseList;
	}

	public function getOldCaseList()
	{
		return $this->oldCases;
	}

	protected function getUser()
	{
		return $this->user;
	}

	protected function getPass()
	{
		return $this->pass;
	}

	protected function getUpdateDate()
	{
		$this->updateDate = "2011-09-08 00:00:00";

		return $this->updateDate;
	}


	protected function setUpdateDate($newDate)
	{
		$dateFields = date_parse_from_format( "Y-m-d H:i:s", $newDate);

		$lastUpdatedDate = date_date_set($lastUpdatedDate, $dateFields['year'], $dateFields['month'], $dateFields['day'] );
		$lastUpdatedDate = date_time_set($lastUpdatedDate, $dateFields['hour'], $dateFields['minute'], $dateFields['second']);

		$this->updateDate = $lastUpdatedDate->format("Y-m-d H:i:s");
	}
}
?>