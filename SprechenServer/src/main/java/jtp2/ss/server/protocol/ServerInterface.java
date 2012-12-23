package jtp2.ss.server.protocol;


import java.util.List;

import jtp2.ss.protocol.ReceiveStatus;
import jtp2.ss.protocol.Status;
import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;

public interface ServerInterface {

    void registerUser(User user) throws UserAlreadyExistsException;

    void loginUser(String login, Session session)
            throws UserAlreadyLoggedException;

    void subscribeToStatusNotifications(String subscriber, String target);

    void logoutUser(String login);

    User getUser(String login);

    List<Message> getPendingMessages(String login);

    void changeStatus(String login, Status status, String description);

    boolean authenticate(String login, long passwordHash)
            throws NoSuchUserException;

    ReceiveStatus sendMessage(Message message) throws NoSuchUserException;

}
