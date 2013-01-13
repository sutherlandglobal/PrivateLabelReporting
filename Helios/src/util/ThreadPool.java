/**
 * 
 */
package util;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A generic thread pool, used mostly for quickly running and processing database queries, specifically in MS Access databases. 
 * General enough to be used for other parallel problems too.
 * 
 * @author Jason Diamond
 *
 */
public class ThreadPool {

	private final static int TERMINATION_TIMEOUT = 2;
	
	private ExecutorService pool;
	
	/**
	 * Build the threadpool.
	 * 
	 * @param	poolSize	The size of the threadpool.
	 * 
	 */
	public ThreadPool(int poolSize) 
	{		
		pool = Executors.newFixedThreadPool(poolSize);

	}
	
	/**
	 * Execute a task in a thread.
	 * 
	 * @param task	the task to execute.
	 */
	public void runTask(Runnable task)
	{
		pool.execute(task);
		//pool.submit(task);
	}

	/**
	 * Shutdown the threadpool safely. Wait for all running threads to terminate.
	 */
	public void close()
	{
		//shutdown of a shutdown pool -> noop
		
		//shutdown prevents new tasks from coming in
		pool.shutdown();
		try 
		{
			//while(!pool.isTerminated())
			while(!pool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS))
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		int rows = 100;
		ThreadPool pool = new ThreadPool(10);
		
		Vector<String[]> coreList = new Vector<String[]>(rows);
		coreList.setSize(rows);
		final List<String[]> output = Collections.synchronizedList(coreList);
		

		for(int i =0; i< rows; i++)
		{
			final int index = i;
			pool.runTask
			(
				new Runnable()
				{
					@Override
					public void run() 
					{
						long sleep = (long) (Math.random()*100);
						//System.out.println("Starting thread " + index + " sleeping " + sleep);
						
						try 
						{
							Thread.sleep(sleep);
						} 
						catch (InterruptedException e) 
						{

							e.printStackTrace();
						}
						
						//System.out.println("Ending thread " + index);
						output.set(index, new String[]{""+index, ""+sleep});
					}
				}
			);
		}
		
		pool.close();

		for(String[] row : output)
		{
			for(String col : row)
			{
				System.out.print(col + ",");
			}
			System.out.println();
		}
		
	}

}
