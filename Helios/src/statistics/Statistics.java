package statistics;

import java.util.Vector;

/**
 * Statistics implementation and processing.	Values passed in will almost certainly be strings via database queries, so string conversion is done here.
 * 
 * @author Jason Diamond
 *
 */
public final class Statistics 
{

	/**
	 * 
	 */
	protected Statistics() {}

	/**
	 * Determine the sum of the provided dataset.
	 * 
	 * @param dataSet		The dataset to run the summation on.
	 * 
	 * @return	The summation of the dataset.
	 */
	public double getTotal(Vector<String> dataSet)
	{
		double retval = 0.0;

		
		if(dataSet != null )
		{
			try
			{
				for(Double i : convertToDouble(dataSet))
				{
					retval += i;
				}
				
			}
			catch (NumberFormatException e)
			{
				System.err.println("Error Converting dataSet data to Double");
				
				retval = 0.0;
			}

			
		}


		return retval;
	}

	/**
	 * Determine the average of the provided dataset.
	 * 
	 * @param dataSet		The dataset to run the average on.
	 * 
	 * @return	The average of the dataset.
	 */
	public double getAverage(Vector<String> dataSet)
	{
		Double retval = 0.0;

		Double total = getTotal(dataSet);
		
		if(dataSet != null && total != null )
		{
			double setSize = dataSet.size();
			if(setSize > 0)
			{
				retval = total/setSize;
			}
		}
		else
		{
			System.err.println("Error running Average - invalid data or null data set");
		}

		return retval;
	}

	/**
	 * Convert a vector of strings to a vector of doubles.
	 * 
	 * 
	 * @param dataSet		Vector of strings to convert.
	 * 
	 * @return	Vector of doubles resulting from string conversions.
	 * 
	 * @throws NumberFormatException	If a number fails conversion from string to double.
	 */
	private Vector<Double> convertToDouble(Vector<String> dataSet) throws NumberFormatException
	{
		Vector<Double> retval = new Vector<Double>();

		if(dataSet != null)
		{
			for(String s : dataSet)
			{
				retval.add(Double.parseDouble(s));
			}
		}

		return retval;
	}


	public static void main(String[] args) 
	{

	}

}
