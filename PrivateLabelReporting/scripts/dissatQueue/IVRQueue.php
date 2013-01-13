<?php

require 'IVRCase.php';

class IVRQueue extends Queue
{
	private $dbConnection;

	public function __construct($confFile)
	{
		parent::__construct($confFile);
	}

	public function __destruct()
	{
		if($this->dbConnection)
		{
			mssql_close($this->dbConnection);
			unset($this->dbConnection);
		}
	}

	protected function connect()
	{
		$retval = false;
		try
		{
			$this->dbConnection = mssql_connect(parent::getHost() . ":" . parent::getPort() , parent::getUser(), parent::getPass());

			$retval = $this->dbConnection != false;
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

		$sql_result = "";

		try
		{
			$query = " SELECT tbl_AcerPFSSurveyIVR.RId,CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112) AS DATE," .
        		" tbl_AcerPFSSurveyIVR.Survey_Result," .
        		" CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2" . 
                " Then " .
                "        CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,6),1) = 1" . 
                "                THEN 'Yes'" . 
                "                Else 'No' END" .
                " Else" . 
                "        CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,5),1) = 1" . 
                "                THEN 'Yes'" . 
                "                Else 'No' END" .
                " END AS Q6," . 
        " tbl_AcerPFSSurveyIVR.CaseId AS Case_ID," .  
        " CRM_MST_USER.USER_USERID AS UserID," .  
        " tbl_AcerPFSSurveyIVR.Customer_Lastname AS Customer_LastName," .
        " tbl_AcerPFSSurveyIVR.Customer_Firstname AS Customer_FirstName," . 
        " tbl_AcerPFSSurveyIVR.ContactNumber AS Customer_Phone," . 
        " tbl_AcerPFSSurveyIVR.[E-MailID] AS Customer_eMail" .

		" FROM " . 
        " (tbl_AcerPFSSurveyIVR LEFT JOIN crm_MST_USER ON tbl_AcerPFSSurveyIVR.NTLogin = crm_MST_USER.USER_NTLOGINID) LEFT JOIN CRM_TRN_PROSPECT ON tbl_AcerPFSSurveyIVR.CaseId = CONVERT(varchar(10),CRM_TRN_PROSPECT.PROSPECT_PROSPECTID)" .

		" WHERE " .  
        " (Len(tbl_AcerPFSSurveyIVR.Survey_Result)>=5)  AND" .
        " ((CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112)) + '0:10:00' > CRM_TRN_PROSPECT.PROSPECT_UPDATEDDATE)" .
        " AND" .                                                                                                                                                      
        " (CONVERT(DATETIME,LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,14),4)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,16),2)+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,18),2)+' '+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,9),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,7),2)+':'+LEFT(RIGHT(tbl_AcerPFSSurveyIVR.UID,5),2),112)) >= (getdate() -1)" .      
        " AND" .                                                                                                                                                      
        " (CASE WHEN Right(left(tbl_AcerPFSSurveyIVR.Survey_Result,2),1) = 2" .
        "        Then" .  
        "                CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,6),1) = 1" . 
        "                        THEN 'Yes'" .
        "                        Else 'No' END" .
        "        Else" . 
        "                CASE WHEN RIGHT(LEFT(tbl_AcerPFSSurveyIVR.Survey_Result,5),1) = 1" .
        "                        THEN 'Yes'".
        "                        Else 'No' END" . 
        "        END) = 'No';";

			ini_set('mssql.datetimeconvert', "0");

			#echo $query, PHP_EOL;

			$sql_result = mssql_query( $query ,$this->dbConnection) or die("Sql Error: ". mssql_get_last_message() );

			//$oldestDate = date_create();

			$addedCases = 0;
			
			if($sql_result && mssql_num_rows($sql_result) > 0)
			{
				echo "Query returning rows: " ,  mssql_num_rows($sql_result), PHP_EOL;
				
				while ($row =mssql_fetch_assoc($sql_result))
				{
					//is it new? then add it

					//$potentialNewCase = new IVRCase($row);
					
					//if($this->shouldAddCase($potentialNewCase))
					//{
						//parent queue enforces uniqueness
					
						$case = new IVRCase($row);
					
						if(parent::addCase($case))
						{
							$addedCases++;
							
							if(!parent::isOldCase($case))
							{
								echo "Found new case: ", $case->toString(), PHP_EOL; 
							}
						}
					//}

//					if(parent::isNewCase($potentialNewCase) && $this->shouldAddCase($potentialNewCase))
//					{
//
//						echo "Adding new case: ", $potentialNewCase->toString(), PHP_EOL;

						//parent::addCase($potentialNewCase);

						//array_push($currentCaseList, $potentialNewCase);

						//						$surveyDate = date_create();
						//
						//						$dateFields = date_parse_from_format( "Y-m-d H:i:s", $row['DATE']);
						//
						//						$surveyDate = date_date_set($surveyDate, $dateFields['year'], $dateFields['month'], $dateFields['day'] );
						//						$surveyDate = date_time_set($surveyDate, $dateFields['hour'], $dateFields['minute'], $dateFields['second']);
						//
						//						if($surveyDate < $oldestDate)
						//						{
						//							$oldestDate = clone $surveyDate;
//					}
//					else
//					{
//						echo "not adding ", $potentialNewCase->toString(), " because not new", PHP_EOL;
//						#print_r($potentialNewCase->getFields());
//					}
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


			//echo "oldestDate: ", $oldestDate->format("Y-m-d H:i:s"), PHP_EOL;

			//parent::writeLastUpdateDate($oldestDate->format("Y-m-d H:i:s"));

		}
		catch(Exception $e)
		{
			echo $e->getMessage() , PHP_EOL;
			echo $e->getLine() , PHP_EOL;
		}

		if($sql_result)
		{
			mssql_free_result($sql_result);
		}
	}


	protected function shouldAddCase($potentialCase)
	{
		#survey logic here

		return true;
	}

}

?>