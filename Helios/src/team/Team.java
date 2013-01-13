package team;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 * A collection of users. This is generic enough to be used as a general aggregation of data, say for a date granularity.
 * 
 * @author Jason Diamond
 *
 */
public class Team 
{
	private HashMap<String, User> teamMembers;
	
	/**
	 * 	Build an empty team.
	 */
	public Team()
	{
		teamMembers = new HashMap<String, User>();
	}
	
	/**
	 * Load a team from a file.
	 * 
	 * @param file		File to read users from.
	 * 
	 * @return	True if the file was sucessfully read, false otherwise.
	 */
	public boolean loadFromFile(String file)
	{
		boolean retval = false;
		
		BufferedReader dataIn = null;
		
		try 
		{
			dataIn = new BufferedReader(new FileReader(file));
			
			String line;
			while( (line = dataIn.readLine()) != null )
			{
				if(line.matches("^[a-zA-Z -]*$"))
				{
					teamMembers.put(line, new User(line));
				}
			}
			retval = true;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally
		{
			try 
			{
				if(dataIn != null )
				{
					dataIn.close();
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		return retval;
	}
	
	public int getSize()
	{
		return teamMembers.size();
	}
	
	/**
	 * Retrieve a user by name.
	 * 
	 * @param userName	Username to query.
	 * 
	 * @return	The User object mapped to the name.
	 */
	public User getUser(String userName)
	{
		return teamMembers.get(userName);
	}
	
	/**
	 * Add a user to the team.
	 * 
	 * @param userName	The username to add.
	 * 
	 * @return	True if the user was added, false otherwise.
	 */
	public boolean addUser(String userName)
	{
		boolean retval = false;
		
		if(!teamMembers.containsKey(userName))
		{
			teamMembers.put(userName, new User(userName));
			retval = true;
		}
		
		return retval;
	}
	
	/**
	 * Remove a user from the team.
	 * 
	 * @param userName	The username to remove.
	 * 
	 * @return	True if the element was removed, false otherwise.
	 */
	public boolean removeUser(String userName)
	{
		return teamMembers.remove(userName) != null;
	}
	
	/**
	 * Accessor for the team's usernames.
	 * 
	 * @return	A list of the usernames.
	 */
	public String[] getUserList()
	{
		return teamMembers.keySet().toArray(new String[teamMembers.size()]);
	}
	
	public static void main(String[] args)
	{
		Team t = new Team();
		
		String testUser1 = "jason";
		String testUser2 = "hades";
		String testUser3 = "zeus";
		
		System.out.println("Adding " + testUser1);
		
		t.addUser(testUser1);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		
		System.out.println("Adding " + testUser2);
		
		t.addUser(testUser2);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		
		System.out.println("Adding " + testUser3);
		
		t.addUser(testUser3);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		//////////////////////////////////////
		
		System.out.println("================");
		
		System.out.println("Removing " + testUser1);
		
		t.removeUser(testUser1);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		
		System.out.println("Removing " + testUser2);
		
		t.removeUser(testUser2);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		
		System.out.println("Removing " + testUser3);
		
		t.removeUser(testUser3);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		System.out.println("================");
		
		t.addUser(testUser3);
		
		String testAttr = "number";
		t.getUser(testUser3).addAttr(testAttr);
		t.getUser(testUser3).addData(testAttr, "1");
		t.getUser(testUser3).addData(testAttr, "2");
		t.getUser(testUser3).addData(testAttr, "3");
		
		for(String d : t.getUser(testUser3).getAttrData(testAttr) ) 
		{
			System.out.println("data: " + d);
		}
		
		System.out.println("================");
		
		t.getUser(testUser3).deleteAttrData(testAttr);
		
		System.out.println(t.getUser(testUser3).getAttrData(testAttr));
		
		
		System.out.println("================");
		t.removeUser(testUser3);
		t.loadFromFile(args[0]);
		
		for(String d : t.getUserList())
		{
			System.out.println("Found User: " + d);
		}
		System.out.println("================");
		
	}
}
