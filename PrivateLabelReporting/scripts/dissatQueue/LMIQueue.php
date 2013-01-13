<?php

require 'LMICase.php';

class LMIQueue extends Queue
{
	private $privateLabelDB;
	private $LMIDB;

	public function __construct($confFile)
	{
		parent::__construct($confFile);
	}

	public function __destruct()
	{
		if($this->privateLabelDB)
		{
			mssql_close($this->privateLabelDB);
			unset($this->privateLabelDB);
		}
		
		
		if($this->LMIDB)
		{
			mssql_close($this->LMIDB);
			unset($this->LMIDB);
		}

	}

	protected function connect()
	{
		$retval = false;
		try
		{
			$this->privateLabelDB = mssql_connect(parent::getHost() . ":" . parent::getPort() , parent::getUser(), parent::getPass());

			$lmiHost = "rocjfsdev18";
			$lmiPort = "1433";
			$lmiUser = "PrivateLabelHeliosRead";
			$lmiPass = "privlabr#$1";

			$this->LMIDB = mssql_connect($lmiHost . ":" . $lmiPort , $lmiUser, $lmiPass);

			$retval = $this->privateLabelDB != false && $this->LMIDB != false;
		}
		catch(Exception $e)
		{
			echo $e->getMessage() , PHP_EOL;
			echo $e->getLine() , PHP_EOL;
		}

		return $retval;
	}

	public function runUpdate()
	{
		echo "Running Update", PHP_EOL;

		$resultSet = array();

		//		$activitiesQuery = "Select ACTIVITY_CreatedDate,ACTIVITY_CreatedBy,ACTIVITY_ProspectID " .
		//		" FROM CRM_TRN_ACTIVITY " .
		//		" WHERE " .
		//		"      (ACTIVITY_YEARMONTH = CONVERT(VARCHAR(4),YEAR(getdate()-1))+CONVERT(VARCHAR(2),MONTH(getdate()-1)) OR " .
		//        "       ACTIVITY_YEARMONTH = CONVERT(VARCHAR(4),YEAR(getdate()))+CONVERT(VARCHAR(2),MONTH(getdate()))) AND" .
		//        " ACTIVITY_CreatedDate >= getdate()-1.2 AND ";

		$activitiesQuery = "Select ACTIVITY_CreatedDate,CRM_MST_USER.USER_LOGMEINNODEID,ACTIVITY_ProspectID " .
		" FROM CRM_TRN_ACTIVITY LEFT JOIN CRM_MST_USER ON CRM_TRN_ACTIVITY.ACTIVITY_CREATEDBY = CRM_MST_USER.USER_USERID " .
		" WHERE " .  
               	" (ACTIVITY_YEARMONTH = CONVERT(VARCHAR(4),YEAR(getdate()-1))+CONVERT(VARCHAR(2),MONTH(getdate()-1)) OR " . 
                " ACTIVITY_YEARMONTH = CONVERT(VARCHAR(4),YEAR(getdate()))+CONVERT(VARCHAR(2),MONTH(getdate())))  AND " . 
				"ACTIVITY_CreatedDate >= getdate()-1.2 ";
        		
		$activitiesQueryLMICondition = " AND CRM_MST_USER.USER_LOGMEINNODEID IS NOT NULL AND CRM_MST_USER.USER_LOGMEINNODEID = ";
		$activitiesQueryDateStartConditionPrefix = " AND ACTIVITY_CREATEDDATE >= (convert(datetime,'";
		$activitiesQueryDateStartConditionSuffix ="') - .021)";
		$activitiesQueryDateEndConditionPrefix = " AND ACTIVITY_CREATEDDATE <= (convert(datetime,'";
		$activitiesQueryDateEndConditionSuffix ="') + .021) ";

		$lmi_sql_result = "";

		try
		{
			$query = $this->query;

			ini_set('mssql.datetimeconvert', "0");

			#echo $query, PHP_EOL;

			$lmi_sql_result = mssql_query( $query ,$this->LMIDB) or die("Sql Error: ". mssql_get_last_message() );

			//$oldestDate = date_create();

			$addedCases = 0;

			if($lmi_sql_result && mssql_num_rows($lmi_sql_result) > 0)
			{
				echo "LMI query returning rows: " ,  mssql_num_rows($lmi_sql_result), PHP_EOL;

				while ($lmi_row =mssql_fetch_assoc($lmi_sql_result))
				{
					//is it new? then add it

					#print_r($lmi_row);

					$techID = $lmi_row['Technician_ID'];
					$date = $lmi_row['Date'];

					$fullActivitiesQuery =
					$activitiesQuery . $activitiesQueryDateStartConditionPrefix . $date . $activitiesQueryDateStartConditionSuffix .
					$activitiesQueryDateEndConditionPrefix . $date .  $activitiesQueryDateEndConditionSuffix . $activitiesQueryLMICondition . $techID;

					#echo $fullActivitiesQuery, PHP_EOL;
					
					try
					{
						$pl_sql_result = mssql_query($fullActivitiesQuery, $this->privateLabelDB) or die("Sql Error: ". mssql_get_last_message() );
							
						if($pl_sql_result && mssql_num_rows($pl_sql_result) > 0)
						{
							$replicatedRow = array
							(
									'Session_ID' => $lmi_row['Session_ID'],
									'Date' => $lmi_row['Date'],
									'Customer_Name' => $lmi_row['Customer_Name'],
									'Technician_ID' => $lmi_row['Technician_ID'],
									'Q6' => $lmi_row['Q6'],
									'ACTIVITY_ProspectID' => "Suggested: ",
							);
							
							echo "PL query returning rows: " ,  mssql_num_rows($pl_sql_result), PHP_EOL;
							
							while ($pl_row =mssql_fetch_assoc($pl_sql_result))
							{
								$replicatedRow['ACTIVITY_ProspectID'] .= $pl_row['ACTIVITY_ProspectID'] . ":";
							}
								
							$case = new LMICase($replicatedRow);

							if(parent::addCase($case))
							{
								$addedCases++;

								if(!parent::isOldCase($case))
								{
									echo "Found new case: ", $case->toString(), PHP_EOL;
								}
							}
							else
							{
								echo "NOT Adding new case: ", $case->toString(), PHP_EOL;
							}
						}
						else
						{
							echo "Could not find matching ezCLM case for: ",$lmi_row['Session_ID'],",",$lmi_row['Date'],",",$lmi_row['Customer_Name'],",",$lmi_row['Technician_ID'], PHP_EOL;
							echo "PL Query: ", $fullActivitiesQuery, PHP_EOL;
						}
					}
					catch(Exception $e)
					{
						echo $e->getMessage() , PHP_EOL;
						echo $e->getLine() , PHP_EOL;
					}

					if($pl_sql_result)
					{
						mssql_free_result($pl_sql_result);
					}
				}
			}

			echo "Found $addedCases cases from update", PHP_EOL;

			//sort out the cases that fell off the list
			//which of the old cases aren't in the new case list?
			foreach(parent::getOldCaseList() as $case)
			{
				$hasBeenRemoved = 1;

				foreach(parent::getCaseList() as $newCase)
				{
					if($case->equals($newCase))
					{
						$hasBeenRemoved = 0;
						break;
					}
				}

				if($hasBeenRemoved)
				{
					echo "Removed case ", $case->toString(), PHP_EOL;
				}
				else
				{
					parent::addCase($case);
				}
			}
		}
		catch(Exception $e)
		{
			echo $e->getMessage() , PHP_EOL;
			echo $e->getLine() , PHP_EOL;
		}

		if($lmi_sql_result)
		{
			mssql_free_result($lmi_sql_result);
		}
	}

	protected function shouldAddCase($potentialCase)
	{
		#survey logic here

		#handled in query, so nothing to do here

		return true;
	}
}

?>