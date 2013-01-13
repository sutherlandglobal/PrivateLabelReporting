/**
 * 
 */
package exceptions;

/**
 * Exception for invalid database query conditions.
 * 
 * @author Jason Diamond
 *
 */
public class InvalidConditionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5398580299063968149L;

	/**
	 * 
	 */
	public InvalidConditionException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidConditionException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidConditionException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidConditionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
