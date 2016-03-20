package ThesisMan;

/**
 *
 * @author Kristina Miklasova, 4333 83
 */
public class ServiceFailureException extends Exception {
    
    public ServiceFailureException() {
        super();
    }
    
    public ServiceFailureException(String message) {
        super(message);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
