/**
 * 
 */
package survey;

import java.util.HashMap;

import exceptions.IncompleteSurveyException;

/**
 * @author jdiamond
 *
 */
public class LMISurvey extends Survey
{

	private static final String SOURCE_ATTR = "source";
	private static final String SESSION_ID_ATTR = "sessionID";
	private static final String DATE_ATTR = "date";
	private static final String CUST_NAME_ATTR = "source";
	private static final String Q1_ATTR = "q1";
	private static final String Q2_ATTR = "q2";
	private static final String Q3_ATTR = "q3";
	private static final String Q4_ATTR = "q4";
	private static final String Q5_ATTR = "q5";
	private static final String Q6_ATTR = "q6";
	private static final String Q7_ATTR = "q7";
	private static final String COMMENTS_ATTR = "comments";
	private static final String TECH_NAME_ATTR = "techName";
	private static final String TECH_ID_ATTR = "techID";
	
	/**
	 * @param surveyData
	 * @throws IncompleteSurveyException
	 */
	public LMISurvey(HashMap<String, String> surveyData)
			throws IncompleteSurveyException
	{
		super(surveyData);
		
		if
		(
				!data.containsKey(SOURCE_ATTR) ||
				!data.containsKey(SESSION_ID_ATTR) ||
				!data.containsKey(DATE_ATTR) ||
				!data.containsKey(CUST_NAME_ATTR) ||
				!data.containsKey(Q1_ATTR) ||
				!data.containsKey(Q2_ATTR) ||
				!data.containsKey(Q3_ATTR) ||
				!data.containsKey(Q4_ATTR) ||
				!data.containsKey(Q5_ATTR) ||
				!data.containsKey(Q6_ATTR) ||
				!data.containsKey(Q7_ATTR) ||
				!data.containsKey(COMMENTS_ATTR) ||
				!data.containsKey(TECH_NAME_ATTR) ||
				!data.containsKey(TECH_ID_ATTR) 
		)
		{
			throw new IncompleteSurveyException("IVR Survey created with missing required fields");
		}
	}
	
	public String getSurveyAnswer(String question)
	{
		String retval = null;
		if(data.containsKey(question))
		{
			retval = data.get(question);
		}
		
		return retval;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

}
