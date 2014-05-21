<?php 

//an agent is a collection of shift start and end times by date

//a shift

class Agent
{
	private $agentName;
	private $shifts;
	
	public function __construct( $agentName)
	{
		$this->agentName= $agentName;
		
		$this->shifts = array();
	}
	
	public function addShift(DateTime $startDate, DateTime $endDate)
	{
		//convert both to string, startdates are unique, so use them as keys
		
		$startDateString = date_format($startDate, 'Y-m-d H:i:s');
		$endDateString = date_format($endDate, 'Y-m-d H:i:s');
		
		$this->shifts[$startDateString] = $endDateString;
	}
	
	public function getAgentName()
	{
		return $this->agentName;
	}
	
	public function getShifts()
	{
		$retval = array();
		
		//return key val pairs of shifts
		foreach( $this->shifts as $key => $val)
		{
			$shift = array();
			array_push($shift, (substr($key, 0, 10) . " 00:00:00"));
			array_push($shift, $this->agentName);
			array_push($shift, $key);
			array_push($shift, $val);
			
			array_push($retval, $shift);
		}
		
		return $retval;
	}
}

?>