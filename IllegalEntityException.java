package ThesisManCommon;

/**
 *
 * @author Kristina Miklasova, 4333 83
 */
public class IllegalEntityException extends RuntimeException {
    
    public IllegalEntityException() {
        super();
    }
    
    public IllegalEntityException(String message) {
        super(message);
    }
    
    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IllegalEntityException(Throwable cause) {
        super(cause);
    }
  
}
