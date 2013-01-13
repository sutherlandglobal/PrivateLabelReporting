package util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Manage IO operations for sensitive files. Java seems to avoid coherent locking between threads and JVM instances in favor of portability. Since this framework is 
 * targeted for Linux, locking has to have a more personal touch. We also can't allow reads from a database file when it's being written to.
 * 
 * When a file is locked, a [epoch date].lck file is created in the specified directory, provided there are no existing locks, and no open filehandles to the target file exist.
 * 
 * Lockfiles are created in the designated lock directory, with names of the format [filename].[epoch time].lck
 * 
 * @author Jason Diamond
 *
 */
public class IOLocker
{
	private static final String LOCK_FILE_EXT = ".lck";
	private static IOLocker thisInstance;
	private final HashMap<String, String> lockTable;
	private final static long LOCK_WAIT = 3000;
	private final static int MAX_ATTEMPTS = 20; 
	private static String lockDir;
	

	/**
	 * Although we can lock any file, we need to pick a directory to put the lock files in.
	 * 
	 * @param lockDir	The directory to manage lockfiles in.
	 */
	private IOLocker(String lockDir)
	{
		
		lockTable = new HashMap<String, String>();
		
		if(!lockDir.endsWith("/"))
		{
			lockDir += "/";
		}
		
		IOLocker.lockDir = lockDir;
		
	}

	/**
	 * Retrieve an instance of the locker, build a new one if necessary.
	 * 
	 * @param lockDir	The directory to manage lockfiles in.
	 * 
	 * @return	An instance of the Locker.
	 */
	public static IOLocker getInstance(String lockDir)
	{
		if(thisInstance == null)
		{
			thisInstance = new IOLocker(lockDir);
		}
		
		return thisInstance;
	}
	
	/**
	 * Check if the given file is locked. Simply if the file is locked, open file handles are not considered.
	 * 
	 * @param fileName	The file name to check.
	 * 
	 * @return	True if a lock file exists describing the file, false otherwise.
	 */
	public boolean isLocked(String fileName)
	{
		//true if locked, false if not
		boolean retval = false;
		
		//search for lock files in the lock dir 
		
		File f = new File(lockDir);
		
		//try to check hashes for entries, faster than running to the disk.
		
		if(f.exists() && f.isDirectory())
		{
			for(String file : f.list())
			{
				//System.out.println(file);
				if( file.startsWith(fileName+".") && file.endsWith(LOCK_FILE_EXT) )
				{
					retval = true;
					break;
				}
			}
			
		}
		else
		{
			System.err.println("Invalid lockDir: " + lockDir);
			
			//consider it locked if we can't determine
			retval = true;
		}
		
		return retval;
	}
	
	/**
	 * Check if the specified file has any open file handles with lsof. Used when determining if reports are being run. 
	 * 
	 * @param file	The file to check.
	 * 
	 * @return	True if the file has open file handles, false otherwise.
	 */
	public static boolean hasOpenFileHandles(String file)
	{
		boolean retval = false;
		
		String sysCall = "/usr/sbin/lsof " + " " + file;

		String output = SystemCallProcessor.runAndGetOutput(sysCall).trim();

		if(!output.equals(""))
		{
			retval = true;
		}

		return retval;
	}
	
	/**
	 * Lock the specified file. Must wait for the absence of lock files and the absence of file handles to the file.
	 * 
	 * @param fileName	The file name to lock.
	 * 
	 * @return	The id to reference the lock file by.
	 */
	public String lock(String fileName)
	{
		String retval = null;
		
		//check for preexisting locks and open file handles, existence of either prevents a file lock
		int attempts = 0;
		while( attempts < MAX_ATTEMPTS && (isLocked(fileName) || hasOpenFileHandles(fileName)))
		{
			try
			{
				attempts++;
				System.out.println("Resource " + fileName + " is locked or is being read, waiting.");
				Thread.sleep(LOCK_WAIT);
				
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		//create a lock file on the disk
		
//		while( lockTable.containsKey("" + newID) &&  newID < MIN_ID)
//		{
//			newID = randGen.nextInt(MAX_ID);
//		}
		
		if(attempts < MAX_ATTEMPTS)
		{
			long newID = System.currentTimeMillis();

			try
			{
				File lockFile =  new File(lockDir + fileName + "." + newID + LOCK_FILE_EXT);

				if(lockFile.createNewFile())
				{
					lockTable.put("" + newID, lockFile.getAbsolutePath());
					retval = "" + newID;
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	
		//map an id to the file name
		
		return retval;
	}
	
	/**
	 * Remove the lock on a file given the file's lock id.
	 * 
	 * @param id	The id to remove the lock for.
	 * 
	 * @return	True if the file was unlocked, false otherwise.
	 */
	public boolean unlock(String id)
	{
		boolean retval = false;
		
		//check mapping
		if(lockTable.containsKey(id))
		{
			File f = new File(lockTable.get(id));
			
			//check if file exists
			if(f.exists())
			{
				//remove file and mapping
				retval = (f.delete() && lockTable.remove(id) != null);
			}
		}
		
		if(!retval)
		{
			System.err.println("Error removing lock id: " + id + " => " + lockTable.get(id));
		}
		
		
		
		return retval;
	}
	
}
