<?php

$dbConnection = mssql_connect("rocjfsdev18", "PrivateLabelHeliosRead", 'privlabr#$1');

if(!$dbConnection)
{
	echo "Initial MS SQL connection failed", PHP_EOL;
}

ini_set('mssql.datetimeconvert', "0");

#$query = "select top 1 Start_Time from LMI_5452038_Collab_Chat_Log ";
#$query = "select convert(varchar(16), (select top 1 Start_Time from LMI_5452038_Collab_Chat_Log), 20) as \"Start_Time\"";
####$query = "select convert(varchar(16), (select top 1 Start_Time from LMI_5452038_Collab_Chat_Log order by \"Start_Time\" desc), 20) as \"Start_Time\"";
$query = "select convert(varchar(19), (select top 1 [Start_Time] from [LMI_5452038_Collab_Chat_Log] order by [Start_Time] desc), 20) as [Start_Time]";
#$query = "select convert(varchar(16), (select top 1 from LMI_5452038_Collab_Chat_Log), 20) as \"Start_Time\"";

echo $query, PHP_EOL;

$sql_result = mssql_query($query ,$dbConnection);

//nothing returned likely empty table
if(!$sql_result)
{
	echo "Sql Error: ". mssql_get_last_message(),PHP_EOL;
}
else if(mssql_num_rows($sql_result) > 0)
{
	$row =  mssql_fetch_array($sql_result);

	echo "Query returned:";
	print_r($row);
}

mssql_close($dbConnection);

?>
