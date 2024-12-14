package payrollsystem;

/**
 * A custom exception class for handling payroll related exceptions
 */
public class PayRollException extends RuntimeException {
    /**
     * Constructs a new PayRollException with the specified error message.
     *
     * @param message The error message to be presented to the user
     */
    public PayRollException(String message) {
        super(message);
    }
}