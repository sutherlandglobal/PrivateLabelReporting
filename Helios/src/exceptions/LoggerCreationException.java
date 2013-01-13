/**
 * 
 */
package exceptions;

/**
 * Exception for failures in building a logger.
 * 
 * @author Jason Diamond
 *
 */
public class LoggerCreationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7740799869350216984L;

	/**
	 * 
	 */
	public LoggerCreationException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public LoggerCreationException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public LoggerCreationException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LoggerCreationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
