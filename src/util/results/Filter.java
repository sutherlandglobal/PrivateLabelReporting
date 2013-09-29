package util.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import team.User;

public class Filter 
{

	/**
	 * Putting the "Top" in "Top X Drivers." Reduce the total Top X Drivers data to the top n for each date grain.
	 * 
	 * @param aggPoint	The greater result set to reduce.
	 * @param	numDrivers	The max driver size
	 * 
	 * @return	The final result set.
	 */
	public static ArrayList<String[]> filterTopDrivers(User aggPoint, int numDrivers)
	{
		//retval is the numdrivers-sized list of the most popular drivers contained in aggPoint's data
		ArrayList<String[]> retval = new ArrayList<String[]>(numDrivers);
		//descending, for use in graphs

		int driverCount;
		
		/*
		 * Hardware-Network/Communications-NIC: [1085]
Hardware-Video-Video Card: [1085, 1085]
Hardware-Input Device-Mouse: [1085]
Hardware-Network/Communications-Router: [1085, 1085]
Hardware-Network/Communications-Broadband Modem: [1085, 1085]
Software-Other: [1085, 1085, 1085]
Software-Browser: [1085, 1085, 1085]
Software-OS: [1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085, 1085]
Software-Malware Protection: [1085, 1085]
Hardware-Storage-HDD: [1085, 1085, 1085]
Hardware-Motherboard-Motherboard: [1085]
name: [2010-10]
Hardware-Other-Other: [1085]
Hardware-Printer-Printer: [1085]
		 * 
		 */
		
		for(String category : aggPoint.getAttrList())
		{
			if(!category.equals("name"))
			{
				driverCount = aggPoint.getAttrData(category).size();

				//loop through the list of drivers to find a less popular driver
				//list may have empty indexes
				for(int i =0; i< numDrivers; i++)
				{
					if(retval.size() < numDrivers)
					{
						retval.add(new String[]{category, "" + driverCount});
						break;
					}
					else 
					{
						
						if(retval.get(i) == null)
						{
							//empty index really shouldn't happen
							//retval.set(i, new String[]{category, "" + driverCount});
						}
						else if(Integer.parseInt(retval.get(i)[1]) <= driverCount)
						{
							//found a spot to insert 
							
							//insert at position i
							retval.add(i, new String[]{category, "" + driverCount});
							
							//remove the last element
							retval.remove(retval.size()-1);	
							break;
						}

					}
				}
			}
		}
		
		Collections.sort(retval, new Comparator<String[]>()
		{

			@Override
			public int compare(String[] arg0, String[] arg1) 
			{
				//for 2 elements, compare their driver counts:
				//
				//[2010-10, Software-Microsoft(non-os), 61]
				//[2010-10, Hardware-Printer-Other, 4]
				
				double result = Double.parseDouble(arg0[1]) - (Double.parseDouble(arg1[1]));
				
				if( result == 0 )
				{
					return 0;
				}
				else if(result < 0)
				{
					return 1;
				}
				else
				{
					return -1;
				}
			}
			
		}
);
		return retval;
	}
}
