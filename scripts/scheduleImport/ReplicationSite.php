<?php

class ReplicationSite
{
	private $targetTable;

	private $host;
	private $port;
	private $user;
	private $pass;
	private $databaseName;

	private $repTime = 0;
	private $repRows = 0;
	private $repTries = 0;

	public function __construct( $host, $port, $user, $pass, $targetTable, $databaseName = "")
	{
		ini_set('mssql.secure_connection', 1);

		$this->host = $host;
		$this->user = $user;
		$this->port = $port;
		$this->pass = $pass;
		$this->databaseName = $databaseName;

		$this->targetTable = $targetTable;

		if(!$this->canConnect())
		{
			throw new Exception("Error establishing replication link");
		}

		$dbNameString = $this->databaseName;

		if(!$dbNameString)
		{
			$dbNameString = "Default";
		}

		echo "Built replication site for database server: $this->host, database: $dbNameString, table: $this->targetTable", PHP_EOL;
	}

	function __destruct()
	{
		echo "Destructing replication site, memory usage: ", memory_get_peak_usage(true)/1000,"kb", PHP_EOL;
	}
	
	private function isValidUser($userID)
	{
		//web request into the helios roster api :)
		$retval = false;
		
		$request = 'http://rocjfsdev08.corp.suth.com:8080/birt/api/reporting.jsp?reportName=Roster&rosterType=2&format=1&enquote=1';
		$session = null;
		
		try 
		{
			$session = curl_init($request);
			
			curl_setopt($session, CURLOPT_RETURNTRANSFER, 1);
			curl_setopt($session, CURLOPT_HEADER, 0);
			
			$rawdata = curl_exec($session);
			$array = json_decode($rawdata,true);
			
			foreach( explode("\n", $rawdata) as $agentString)
			{				
				$scheduleFields = str_getcsv($agentString,",","\"");
				
				if($userID == $scheduleFields[7])
				{
					$retval = true;
					break;
				}
			}
		}
		catch (Exception $e)
		{
			echo $e->getMessage() , PHP_EOL;
			echo $e->getLine() , PHP_EOL;
		}
		
		if($session != null)
		{
			curl_close($session);
		}
		
		return $retval;
	}

	private function canConnect()
	{
		$retval = false;
		try
		{
			$dbConnection = $this->getDatabaseConnection();

			if($dbConnection )
			{
				if($this->checkTarget($dbConnection))
				{
					$retval = true;
				}

				mssql_close($dbConnection);

				unset($dbConnection);
				$dbConnection = null;
			}
			else
			{
				echo "connection test failed for table " . $this->targetTable , PHP_EOL ;
			}
		}
		catch (Exception $e)
		{
			echo $e->getMessage() , PHP_EOL;
			echo $e->getLine() , PHP_EOL;
		}

		return $retval;
	}

	private function getDatabaseConnection()
	{
		$dbConnection = mssql_connect($this->host, $this->user, $this->pass);

		if($dbConnection)
		{
			if($this->databaseName != "")
			{
				if(!mssql_select_db($this->databaseName))
				{
					echo "Could not find databaseName ", $this->databaseName, " on server ", $this->host, PHP_EOL;

					mssql_close($dbConnection);

					unset($dbConnection);
					$dbConnection = null;
				}
			}
		}
		else
		{
			echo "Initial MS SQL connection failed", PHP_EOL;
		}

		return $dbConnection;
	}

	public function replicate($formattedData)
	{
		//missing table fails construction, don't worry about it here

		$i = 0;

		echo "Replication started with rows: " , count($formattedData),", memory usage: ", memory_get_peak_usage(true)/1000,"kb", PHP_EOL;

		$start = microTime(true);

		//re-create the connection here, doesn't make sense to leave it open while 50 other reports are replicating
		//although fuck mssql for timing it out anyway, i could have ini_set the timeout to a billion if i wanted to
		$dbConnection =  $this->getDatabaseConnection();

		$insertRowPrefix = "insert into $this->targetTable Values (";
		foreach($formattedData as $row)
		{
			//table is date,userid,start,end --> check if the user is valid
			
			if($this->isValidUser($row[1]))
			{
				$insertValues = "";
	
				foreach($row as $col)
				{
					//remove non-ascii chars
					$col = preg_replace('/[^(\x20-\x7F)\x0A]*/','', $col);
	
					//remove commas, apostrophes, quotes, newlines, and slashes
					$col = str_replace("'", "", $col);
					$col = str_replace(",", "", $col);
					$col = str_replace('"', "", $col);
					$col = str_replace("\\", "", $col);
					//$col = str_replace("/", "", $col);
					$col = str_replace("\n", " ", $col);
	
					$insertValues .= "'$col',";
	
					//I'm lazy
					if($col == $row[1])
					{
						$userid = $col;
					}
					else if($col == $row[2])
					{
						$startTime = $col;
					}
				}
				try
				{
					//drop any similar rows, start_times should be unique
					//delete from pl_schedule where userid = 'blahuser' and start_time = 'derptime' 
					$deleteQuery= "delete from $this->targetTable where empid = '" . $userid . "' and start_time='" . $startTime . "'";
					echo "Attempted Query: ",$deleteQuery, PHP_EOL;
					if($dbConnection && mssql_query( $deleteQuery ,$dbConnection))// or die("Sql Error: ". mssql_get_last_message() .  PHP_EOL );)
					{
 						$i++;
					}
					else
					{
						echo "Original Row: ",$deleteQuery, PHP_EOL;
						var_dump($row);
						throw new Exception("Failure running query: $insertQuery --> " . mssql_get_last_message());
					}
					
					
					//run the insert, since the delete succeeded
					$insertQuery = $insertRowPrefix . chop($insertValues,",") . ")";
					echo "Attempted Query: ",$insertQuery, PHP_EOL;
					if($dbConnection && mssql_query( $insertQuery ,$dbConnection))// or die("Sql Error: ". mssql_get_last_message() .  PHP_EOL );)
					{
 						$i++;
					}
					else
					{
						echo "Original Row: ",$insertQuery, PHP_EOL;
						var_dump($row);
						throw new Exception("Failure running query: $insertQuery --> " . mssql_get_last_message());
					}
				}
				catch (Exception $e)
				{
					echo $e->getMessage(), " at line: ", $e->getLine() , PHP_EOL;
	
					//break, if we re-run it, it'll start at the last failure point
					break;
				}
			}
			else
			{
				echo "Invalid user: ", $row[1], PHP_EOL;
			}
		}

		$this->repTime = (microTime(true) - $start)*1000;
		$this->repRows = $i;
		$this->repTries = count($formattedData);

		unset($formattedData);
		$formattedData = null;

		mssql_close($dbConnection);

		unset($dbConnection);
		$dbConnection = null;

		echo "Replication ended, memory usage: ", memory_get_usage(true)/1000,"kb", PHP_EOL;
	}

	private function checkTarget($dbConnection)
	{
		$retval = false;

		try
		{
			if(!mssql_query( "select top 1 * from " . $this->targetTable, $dbConnection))
			{
				throw new Exception( "Sql Error determining table existence: " . mssql_get_last_message() );
			}

			$retval = true;
		}
		catch (Exception $e)
		{
			echo $e->getMessage(), " at line: ", $e->getLine() , PHP_EOL;
		}

		return $retval;
	}

	public function getReplicationStats()
	{
		$stats = array
		(
				'repTime' => $this->repTime,
				'repRows' => $this->repRows,
				'repTries' => $this->repTries,
		);

		return $stats;
	}
}

?>