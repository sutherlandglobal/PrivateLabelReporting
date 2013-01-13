/**
 * 
 */
package survey;

import java.util.HashMap;

import exceptions.IncompleteSurveyException;

/**
 * @author Jason Diamond
 *
 */
public class Survey
{

	protected HashMap<String, String> data;
	
	protected Survey(HashMap<String, String> surveyData)  throws IncompleteSurveyException
	{
		data = new HashMap<String, String>();
		
		data.putAll(surveyData);
	}
	
	public String getAttr(String attr)
	{
		String retval = null;
		
		if(data.containsKey(attr))
		{
			retval = data.get(attr);
		}
		
		return retval;
	}
	
	public boolean setAttr(String attr, String val)
	{
		return data.put(attr, val) != null;
	}

	public String[] getAttrs()
	{
		return data.keySet().toArray(new String[data.keySet().size()]);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for(String key : data.keySet())
		{
			sb.append(key);
			sb.append(": ");
			sb.append(getAttr(key));
			sb.append("\n");
		}

		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

}
