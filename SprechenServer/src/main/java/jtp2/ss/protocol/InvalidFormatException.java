package jtp2.ss.protocol;

/**
 * Wyjątek rzucany po napotkaniu niepoprawnej binarnej reprezentacji wiadomości.
 */
@SuppressWarnings("serial")
public class InvalidFormatException extends Exception {

    /**
     * {@inheritDoc}
     */
    public InvalidFormatException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public InvalidFormatException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public InvalidFormatException(Throwable cause) {
        super(cause);
    }

}
