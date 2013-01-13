/**
 * 
 */
package exceptions;

/**
 * Exception for invalid database query syntax.
 * 
 * @author Jason Diamond
 *
 */
public class InvalidSyntaxException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3654371945380071241L;

	/**
	 * 
	 */
	public InvalidSyntaxException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidSyntaxException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidSyntaxException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidSyntaxException(String arg0, Throwable arg1) {
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
