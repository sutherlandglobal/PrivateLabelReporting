<?php

abstract class CaseTicket 
{
	const CELL_COLOR = "white";
	
	const SLA_PROX_1_COLOR = "#FF0000";
	const SLA_PROX_2_COLOR = "#FF3300";
	const SLA_PROX_3_COLOR = "#FF6600";
	const SLA_PROX_4_COLOR = "#FF9900";
	const SLA_PROX_5_COLOR = "#FFCC00";
	
	protected $data;
	protected $type;
	protected $slaDeadline;
	
	public function __construct($rawData)
	{
		$this->data = $rawData;
		
		#print_r($this->data);
	}
	
	public function setType($type)
	{
		$this->type = $type;
	}
	
	public function getFields()
	{
		return array_keys($this->data);
	}
	
	public function getField($key)
	{		
		return $this->data[$key];
	}
	
	public function toString()
	{
		$retval = ""; //\"";
		
		foreach(array_values($this->data) as $data)
//		for($i = 0; $i< count($this->data); $i++)
		{
//			$data = $this->data[$i];
			
		
		
			$retval .= "\"";
			
			
			
			$data = str_replace("'", "", $data);
			$data = str_replace(",", "", $data);
			$data = str_replace("\"", "", $data);
			$data = preg_replace("/\n/", " ", $data);
			
			$retval .= $data . "\",";
		}
		
		if(strlen($retval) > 0)
		{ 

			$retval = rtrim($retval,",");
			#$retval .= "\"";
		}
		
		return $retval;
	}
	
	public function getSchemaString()
	{
		$retval = "";
		foreach($this->getFields()  as $field)
		{
			$retval .= "\"";
			
			$field = str_replace("'", "", $field);
			$field = str_replace(",", "", $field);
			$field = str_replace("\"", "", $field);
			$field = preg_replace("/\n/", " ", $field);
			
			$retval .= $field . "\",";
		}
		
		if(strlen($retval)  > 0 )
		{ 
			$retval = chop($retval,",");
			#$retval .= "\"";
		}
		
		return $retval;
	}
	
	public function getType()
	{
		return $this->type;
	}
	
	public function getSLADeadline()
	{
		
		return $this->slaDeadline;
	}
	
	public function isSLADeadlineBefore($date)
	{
		return $this->getSLADeadline() < $date;
	}
	
	protected function setSLADeadline($date)
	{
		$this->slaDeadline = clone $date;
	}
	
	public function getSLAMinutesLeft()
	{
		return round((strtotime($this->slaDeadline->format("Y-m-d H:i:s")) - strtotime(date_create()->format("Y-m-d H:i:s"))) / 60,0);
	}
	
	abstract public function toHTML();
	abstract public function equals($otherCase);
	abstract protected function calcSLADeadline();
}
?>