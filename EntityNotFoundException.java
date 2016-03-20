package ThesisMan;

/**
 *
 * @author Kristina Miklasova, 4333 83
 */
public class EntityNotFoundException extends RuntimeException {
    
    /**
     * Constructs an instance of <code>EntityNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EntityNotFoundException(String msg) {
        super(msg);
    }
    
}
