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
public class IVRSurvey extends Survey
{
	public final static String rID_ATTR = "rID";
	public final static String USER_ID_ATTR = "userID";
	public final static String DATE_ATTR = "date";
	public final static String RESULT_ATTR = "result";
	public final static String CUST_FNAME_ATTR = "fname";
	public final static String CUST_LNAME_ATTR = "lname";
	public final static String Q1_ATTR = "q1";
	public final static String Q2_ATTR = "q2";
	public final static String Q3_ATTR = "q3";
	public final static String Q4_ATTR = "q4";
	public final static String Q5_ATTR = "q5";
	public final static String Q6_ATTR = "q6";
	public final static String Q7_ATTR = "q7";
	public final static String Q8_ATTR = "q8";

	/**
	 * @param surveyData
	 */
	public IVRSurvey(HashMap<String, String> surveyData) throws IncompleteSurveyException
	{
		super(surveyData);

		if
		(
				!data.containsKey(rID_ATTR) ||
				!data.containsKey(USER_ID_ATTR) ||
				!data.containsKey(DATE_ATTR) ||
				!data.containsKey(RESULT_ATTR) ||
				!data.containsKey(CUST_FNAME_ATTR) ||
				!data.containsKey(CUST_LNAME_ATTR) ||
				!data.containsKey(Q1_ATTR) ||
				!data.containsKey(Q2_ATTR) ||
				!data.containsKey(Q3_ATTR) ||
				!data.containsKey(Q4_ATTR) ||
				!data.containsKey(Q5_ATTR) ||
				!data.containsKey(Q6_ATTR) ||
				!data.containsKey(Q7_ATTR) ||
				!data.containsKey(Q8_ATTR) 
		)
		{
			throw new IncompleteSurveyException("IVR Survey created with missing required fields");
		}
	}
	
	//b14
	public boolean isFirstCall()
	{
		return getAttr(Q1_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b15
	public boolean isFirstCallAndYesQ2()
	{
		return isFirstCall() && getAttr(Q2_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b16
	public boolean isServiceSatisfactionOnFirstCallAndYesQ2()
	{
		return isFirstCallAndYesQ2() && getAttr(Q6_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b17
	public boolean isWillingToRecommendOnFirstCallAndYesQ2()
	{
		return isFirstCallAndYesQ2() && getAttr(Q7_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b18
	public boolean isCustomerResolutionConfidenceQ3()
	{
		return isFirstCall() && getAttr(Q3_ATTR).equalsIgnoreCase("Yes");
	}

	//b19
	public boolean isCustomerUnderstandingQ4()
	{
		return isFirstCall() && getAttr(Q4_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b20
	public boolean isCommunicationAbilityQ5()
	{
		return isFirstCall() && getAttr(Q5_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b21
	public boolean isServiceSatisfactionQ6()
	{
		return isFirstCall() && getAttr(Q6_ATTR).equalsIgnoreCase("Yes");
	}
	
	//b22
	public boolean isRecommendQ7()
	{
		return isFirstCall() && getAttr(Q7_ATTR).equalsIgnoreCase("Yes");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{


	}

}
