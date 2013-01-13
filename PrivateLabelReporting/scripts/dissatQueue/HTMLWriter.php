<?php

class HTMLWriter
{
	private $caseList;
	private $targetFile;
	private $title;

	const BG_COLOR = "#ededed";

	public function __construct($caseList, $targetFile, $title)
	{
		$this->caseList = $caseList;
		$this->targetFile = $targetFile;
		$this->title = $title;
	}

	public function getTargetFile()
	{
		return $this->targetFile;
	}

	protected function printHeader()
	{
		return
		"<html>". PHP_EOL . 
		"<head>" .  PHP_EOL . 
		"<title>$this->title Queue</title>" . PHP_EOL . 
		"<META http-equiv=Content-Type content=\"text/html; charset=iso-8859-1\">" .  PHP_EOL . 
		"<meta http-equiv=\"refresh\" content=\"8\">" .  PHP_EOL . 
		"</head>" .  PHP_EOL . 
		"<body bgcolor=\"". HTMLWriter::BG_COLOR ."\" link=\"#0000FF\" vlink=\"#0000FF\">" .  PHP_EOL . 
		"$this->title Survey Queue<br><hr size=\"5\" color =\"blue\">" . PHP_EOL ;
	}

	protected function printFooter()
	{
		return "<hr size=\"5\" color =\"blue\">" . PHP_EOL . "</body>" . PHP_EOL . "</html>" . PHP_EOL ;
	}

	protected function printQueueTable()
	{
		$output = "<b>Total Cases: " . count($this->caseList) . "</b>" . PHP_EOL;
		$output .= "<table  width=\"100\%\" border =\"1\" >". PHP_EOL;

		if(count($this->caseList) > 0)
		{
			#sort cases earliest deadline first
			#find youngest case, swap it with sortedIndex
			for($i = 0; $i <count($this->caseList); $i++)
			{
				$youngestIndex = $i;
				$youngestDate = $this->caseList[$i]->getSLADeadline();
				
				for($j = $i; $j <count($this->caseList); $j++)
				{				
					#echo $this->caseList[$i]->getSLADeadline()->format("Y-m-d H:i:s"), " vs ". $this->caseList[$j]->getSLADeadline()->format("Y-m-d H:i:s"), PHP_EOL;
						
					if($youngestDate > $this->caseList[$j]->getSLADeadline())
					//if($this->caseList[$i]->getSLAMinutesLeft() > $this->caseList[$j]->getSLAMinutesLeft())
					{
						$youngestDate = $this->caseList[$j]->getSLADeadline();
						#echo $this->caseList[$i]->getSLADeadline()->format("Y-m-d H:i:s") , " is before ", $youngestDate->format("Y-m-d H:i:s"), PHP_EOL;

						$youngestIndex = $j;
					}
				}

				if($i != $youngestIndex)
				{
					$temp = $this->caseList[$i];
					$this->caseList[$i] = $this->caseList[$youngestIndex];
					$this->caseList[$youngestIndex] = $temp;

					#echo "swapping $i and $youngestIndex", PHP_EOL;
				}
			}
				
			$output .= "<th nowrap><font size=\"-1\">SLA Prox</font></th>" . PHP_EOL;
			$output .= "<th nowrap><font size=\"-1\">Deadline</font></th>" . PHP_EOL;
			
			foreach($this->caseList[0]->getFields() as $colHead)
			{
				$output .= "<th nowrap><font size=\"-1\">$colHead</font></th>" . PHP_EOL;
			}

			foreach($this->caseList as $case)
			{	
				$output .= $case->toHTML();
			}
		}

		$output .= "</table>" . PHP_EOL ;

		return $output;
	}

	public function writeHTML()
	{
		file_put_contents($this->targetFile, $this->printHeader() . $this->printQueueTable() . $this->printFooter());
	}
}

?>