package jtp2.ss.server.protocol;

import java.util.Collection;

import jtp2.ss.protocol.Status;
import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;

public interface ServerInterface {

    void registerUser(User user) throws UserAlreadyExistsException;

    void loginUser(String login, Session session)
            throws UserAlreadyLoggedException;

    void logoutUser(String login);

    User getUser(String login);

    Collection<Message> getPendingMessages(String login);

    void changeStatus(String login, Status status, String description);

    boolean authenticate(String login, long passwordHash)
            throws NoSuchUserException;

    void sendMessage(String login, Message message);

}
