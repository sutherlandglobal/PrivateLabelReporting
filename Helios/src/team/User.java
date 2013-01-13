package team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * A user and their aggregated data. A user can be any aggregation point, like a date or even a team, not just a human. A user aggregates a collection of attributes, which in
 * turn aggregate data.
 * 
 * @author Jason Diamond
 *
 */
public class User 
{
	private HashMap<String, Vector<String>> data;
	private HashMap<String, Vector<Object>> userObjects;
	
	/**
	 * Build a user, with the provided Borg designation. We are the Borg. Lower your shields and surrender your ships. We will add your biological and technological distinctiveness to our own. Resistance is futile.
	 * 
	 * @param name		The name to refer to this user by.
	 */
	public User(String name)
	{
		data = new HashMap<String, Vector<String>>();
		addAttr("name");
		addData("name", name);
		
		userObjects = new HashMap<String, Vector<Object>>();
	}
	
	/**
	 * Add an attribute to the user.
	 * 
	 * @param attr	The attribute/data grouping to add.
	 * 
	 * @return	True if the attribute was successfully added, false otherwise.
	 */
	public boolean addAttr(String attr)
	{
		boolean retval = false;
		
		if(!data.containsKey(attr))
		{
			data.put(attr, new Vector<String>());
			retval = true;
		}
		
		return retval;
	}
	
	/**
	 * Add a datum to the provided attribute.
	 * 
	 * @param attr	Attribute to add the datum to.s
	 * @param attrData		Datum to add.
	 * 
	 * @return	True if the datum was successfully added, false otherwise.
	 */
	public boolean addData(String attr, String attrData)
	{
		boolean retval = false;
		
		if( data.containsKey(attr))
		{
			data.get(attr).add(attrData);
			retval = true;
		}
		
		return retval;
	}
	
	/**
	 * Retrive the dataset for the provided attribute.
	 * 
	 * @param attr	The attribute to retrieve data for.
	 * 
	 * @return	The dataset for the given attribute.
	 */
	public Vector<String> getAttrData(String attr)
	{
		Vector<String> retval = null;
		
		if(data.containsKey(attr))
		{
			retval = data.get(attr);
		}
		
		return retval;
	}
	
	/**
	 * Delete an attribute's data.
	 * 
	 * @param attr	Attribute who's data to delete.
	 * 
	 * @return	True if the deletion was successful, false otherwise.
	 */
	public boolean deleteAttrData(String attr)
	{
		boolean retval = false;
		if(data.containsKey(attr))
		{
			data.remove(attr);
			retval = true;
		}
		return retval;
	}

	/**
	 * Accessor for this user's attributes.
	 * 
	 * @return	A list of this users' attributes.
	 */
	public Vector<String> getAttrList() 
	{
		Vector<String> retval = new Vector<String>();
		
		retval.addAll(data.keySet());
		
		
		return retval;
	}
	
	/**
	 * Add a custom object to the user, using the provided object designation.
	 * 
	 * @param objectName	Attribute name for the object.
	 * @param data	The object itself.
	 * 
	 */
	public void addObject(String objectName, Object data)
	{
		if(!userObjects.containsKey(objectName))
		{
			userObjects.put(objectName, new Vector<Object>());
		}
		
		userObjects.get(objectName).add(data);
	}
	
	/**
	 * Determine if a group of custom objects is already defined.
	 * 
	 * @param objectName	Name to check for existence.
	 * 
	 * @return	True if the group exists, false otherwise.
	 */
	public boolean hasObjectName(String objectName)
	{
		return userObjects.containsKey(objectName);
	}
	
	/**
	 * Remove the group of custom objects mapped by the supplied name.
	 * 
	 * @param objectName	Name of mapping key to delete, along with the value.
	 * 
	 */
	public void deleteObject(String objectName)
	{
		userObjects.remove(objectName);
	}
	
	/**
	 * Return the list of objects stored at the given key.
	 * 
	 * @param objectName	Object list name to check..
	 * 
	 * @return	The list of objects mapped to the given key.
	 */
	public Vector<Object> getUserObjects(String objectName)
	{
		return userObjects.get(objectName);
	}
	
	/**
	 * Stringify this User. Contains all of the Users data and custom objects.
	 * 
	 * @return	A String describing this user.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(String attr : data.keySet())
		{
			sb.append(attr);
			sb.append(": ");
			sb.append(Arrays.asList(data.get(attr).toArray()).toString());
			sb.append("\n");
		}
		
		for(String attr : userObjects.keySet())
		{
			sb.append("Custom Object: " + attr);
			sb.append(": ");
			sb.append(Arrays.asList(userObjects.get(attr).toArray()).toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
//		User user = new User("Jason");
//		
//		String startDate1 = "2012-07-03 00:00:00";
//		String startDate2 = "2012-07-04 00:00:00";
//		String startDate3 = "2012-07-05 00:00:00";
//		
//		Schedule sch1 = new Schedule(startDate1, "2012-07-03 08:00:00");
//		Schedule sch2 = new Schedule(startDate2, "2012-07-04 08:00:00");
//		Schedule sch3 = new Schedule(startDate3, "2012-07-05 08:00:00");
//		
//		user.addObject(startDate1, sch1);
//		user.addObject(startDate2, sch2);
//		user.addObject(startDate3, sch3);
//		
//		((Schedule)user.getObject(startDate1)).addShift(new Shift("2012-07-04 00:01:00", "2012-07-04 00:01:00"));
//		((Schedule)user.getObject(startDate1)).addShift(new Shift("2012-07-04 00:02:00", "2012-07-04 00:05:00"));
//		((Schedule)user.getObject(startDate1)).addShift(new Shift("2012-07-04 00:10:00", "2012-07-04 04:01:00"));
//		((Schedule)user.getObject(startDate1)).addShift(new Shift("2012-07-04 04:05:00", "2012-07-04 08:01:00"));
//		
//		
//		user.deleteObject(startDate2);
//		
//		user.addObject(startDate3, sch1);
//		
//		//print all schedules and shifts
//		for(Object o : user.getUserObjects())
//		{
//			Schedule sch = (Schedule)o;
//			
//			System.out.println(sch);
////			for(Shift s : sch.getSortedShifts())
////			{
////				System.out.println(s.toString());
////			}
//		}
//		
	}
}
