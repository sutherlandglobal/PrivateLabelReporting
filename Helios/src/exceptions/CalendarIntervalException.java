/**
 * 
 */
package exceptions;

/**
 * Exception for invalid date intervals.
 * 
 * @author Jason Diamond
 *
 */
public class CalendarIntervalException extends Exception {

	private static final long serialVersionUID = 5068157084742452136L;

	/**
	 * 
	 */
	public CalendarIntervalException() {
	}

	/**
	 * @param message
	 */
	public CalendarIntervalException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CalendarIntervalException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CalendarIntervalException(String message, Throwable cause) {
		super(message, cause);
	}
}
