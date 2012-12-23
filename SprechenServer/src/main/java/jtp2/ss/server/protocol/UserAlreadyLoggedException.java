package jtp2.ss.server.protocol;

@SuppressWarnings("serial")
public class UserAlreadyLoggedException extends ProtocolException {

    public UserAlreadyLoggedException() {
    }

    public UserAlreadyLoggedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyLoggedException(String message) {
        super(message);
    }

    public UserAlreadyLoggedException(Throwable cause) {
        super(cause);
    }

}
