<?php

class LMICase extends CaseTicket
{
	const SLA_PROX_1_THRES = 0;
	const SLA_PROX_2_THRES = 5;
	const SLA_PROX_3_THRES = 15;
	const SLA_PROX_4_THRES = 30;
	const SLA_PROX_5_THRES = 45;
	
	const DATE_FIELD = 'Date';
	
	private $idFieldName = "Session_ID";

	public function __construct($data)
	{
		parent::__construct($data);

		$this->calcSLADeadline();
	}

	public function toHTML()
	{
		$htmlString = "<tr>";

		$minsLeft = parent::getSLAMinutesLeft(parent::getSLADeadline());

		$color = parent::CELL_COLOR;
		
		if($minsLeft < IVRCase::SLA_PROX_1_THRES)
		{
			$color = parent::SLA_PROX_1_COLOR;
		}
		else if($minsLeft < IVRCase::SLA_PROX_2_THRES)
		{
			$color = parent::SLA_PROX_2_COLOR;
		}
		else if($minsLeft < IVRCase::SLA_PROX_3_THRES)
		{
			$color = parent::SLA_PROX_3_COLOR;
		}
		else if($minsLeft < IVRCase::SLA_PROX_4_THRES)
		{
			$color = parent::SLA_PROX_4_COLOR;	
		}
		else if($minsLeft < IVRCase::SLA_PROX_5_THRES)
		{
			$color = parent::SLA_PROX_5_COLOR;
		}

		$htmlString .= "<td bgcolor=\"$color\" nowrap><font size=\"-1\">" . $minsLeft ."</font></td>" . PHP_EOL;
		$htmlString .= "<td bgcolor=\"$color\" nowrap><font size=\"-1\">" . parent::getSLADeadline()->format("Y-m-d H:i:s") ."</font></td>". PHP_EOL;

		foreach(parent::getFields() as $field)
		{

			$htmlString .= "<td bgcolor=\"$color\" nowrap><font size=\"-1\">";
				
			if(parent::getField($field))
			{

				$htmlString .= parent::getField($field);
				
			}
			else
			{
				$htmlString .= "&nbsp;";
			}
				
			$htmlString .= "</font></td>";
		}
		$htmlString .= "</tr>" . PHP_EOL;

		return $htmlString;

	}

	public function equals($otherCase)
	{
		#compare case ids
		$retval = false;

		#var_dump($this->getFields());
		#echo $this->getCaseField($this->idFieldName), " vs ", $otherCase->getCaseField($this->idFieldName), PHP_EOL;

		if($this->getField($this->idFieldName) == $otherCase->getField($this->idFieldName))
		{
			$retval = true;
		}

		return $retval;
	}

	protected function calcSLADeadline()
	{
		$date = date_create();

		$dateFields = date_parse_from_format( "Y-m-d H:i:s",  parent::getField(LMICase::DATE_FIELD));

		$date = date_date_set($date, $dateFields['year'], $dateFields['month'], $dateFields['day'] );
		$date = date_time_set($date, $dateFields['hour'], $dateFields['minute'], $dateFields['second']);

		$date = date_add($date, date_interval_create_from_date_string("12 Hours"));

		echo "SLA Deadline for LMICase ", parent::getField($this->idFieldName), " is ", $date->format("Y-m-d H:i:s"), PHP_EOL;
		
		parent::setSLADeadline($date);
	}

}

?>