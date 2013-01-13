package statistics;

/**
 * Statistics object factory. This is a singleton factory implementation for the hell of it.
 * 
 * @author Jason Diamond
 *
 */
public class StatisticsFactory 
{
	private static StatisticsFactory sFact = null;
	private static Statistics stats= null;
	
	/**
	 * 
	 */
	private StatisticsFactory() {}
	
	/**
	 * Retrieve the singleton factory object.
	 * 
	 * @return	A statistics factory.
	 */
	public static StatisticsFactory getInstance()
	{
		if(sFact == null)
		{
			sFact = new StatisticsFactory();
		}
		
		return sFact;
	}
	
	/**
	 * Retrieve the singleton factory object.
	 * 
	 * @return	A statistics object.
	 */
	public static Statistics getStatsInstance()
	{
		if(stats == null)
		{
			stats = new Statistics();
		}
		
		return stats;
	}
	
	public static void main(String[] args) 
	{

	}

}
