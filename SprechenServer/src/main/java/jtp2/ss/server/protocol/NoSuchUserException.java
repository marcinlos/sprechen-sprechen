package jtp2.ss.server.protocol;

@SuppressWarnings("serial")
public class NoSuchUserException extends ProtocolException {

    public NoSuchUserException() {
    }

    public NoSuchUserException(String message) {
        super(message);
    }

    public NoSuchUserException(Throwable cause) {
        super(cause);
    }

    public NoSuchUserException(String message, Throwable cause) {
        super(message, cause);
    }

}
