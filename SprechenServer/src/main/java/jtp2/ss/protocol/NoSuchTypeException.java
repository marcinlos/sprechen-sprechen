package jtp2.ss.protocol;

@SuppressWarnings("serial")
public class NoSuchTypeException extends InvalidFormatException {

    public NoSuchTypeException() {
        super();
    }

    public NoSuchTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchTypeException(String message) {
        super(message);
    }

    public NoSuchTypeException(Throwable cause) {
        super(cause);
    }
}
