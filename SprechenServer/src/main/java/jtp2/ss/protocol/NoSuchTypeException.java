package jtp2.ss.protocol;

/**
 * Wyjątek rzucany, gdy bajt statusu jest niepoprawny.
 */
@SuppressWarnings("serial")
public class NoSuchTypeException extends InvalidFormatException {

    /**
     * {@inheritDoc}
     */
    public NoSuchTypeException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public NoSuchTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public NoSuchTypeException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public NoSuchTypeException(Throwable cause) {
        super(cause);
    }
    
}
