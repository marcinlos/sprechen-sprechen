package jtp2.ss.server.protocol;

import java.util.Collection;

import jtp2.ss.protocol.DataUnit;
import jtp2.ss.protocol.Status;
import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;

public interface Context {

    void registerUser(User user) throws UserAlreadyExistsException;

    void loginUser(String login) throws UserAlreadyLoggedException;
    
    void logout();

    User getUser(String login);

    void changeStatus(Status status, String description);

    boolean authenticate(String login, long passwordHash)
            throws NoSuchUserException;

    void sendToOtherUser(Message message);

    Collection<Message> getPendingMessages();

    void sendBack(DataUnit message);
    
    void error(String message, Throwable exc);
    
    void error(String message);

}
