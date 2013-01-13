/**
 * 
 */
package util;

import report.SQL.Roster;

/**
 * @author jdiamond
 *
 */
public class ReportParameterValidator
{
	public static boolean validateAgentName(String agentName, Roster roster)
	{
		boolean retval = false;
		
		if(agentName != null && roster != null)
		{
			retval = roster.getUser(agentName) != null;
		}
		
		return retval;
	}

	public static boolean validateRosterType(int rosterType)
	{
		return 
		(
			rosterType == Roster.SUPPORT_ROSTER ||
			rosterType == Roster.ACTIVE_SALES_ROSTER ||
			rosterType == Roster.ACTIVE_ROSTER ||
			rosterType == Roster.ACTIVE_SUPPORT_ROSTER ||
			rosterType == Roster.SALES_ROSTER ||
			rosterType == Roster.ALL_ROSTER 
		);
	}
	
	public static boolean validateRosterType(String rosterType)
	{
		boolean retval = false;
		try
		{
			retval = validateRosterType(Integer.parseInt(rosterType));
		}
		catch (Exception e){}
		
		return retval;
	}
}
