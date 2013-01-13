package exceptions;

/**
 * @author Jason Diamond
 *
 *	Sink for the failure to setup a report. All report constructors need to throw this up.
 *
 */
public class ReportSetupException extends Exception 
{
	private static final long serialVersionUID = -3331002564528770691L;

	/**
	 * Create the exception with the specified message.
	 * 
	 * @param message	The message describing the exception.
	 */
	public ReportSetupException(String message)
	{
		super(message);
	}
	

}
