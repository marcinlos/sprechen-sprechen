package jtp2.ss.server.persistence;

import java.util.List;

import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;
import jtp2.ss.server.protocol.NoSuchUserException;
import jtp2.ss.server.protocol.UserAlreadyExistsException;

public interface PersistenceManager {

    void register(User user) throws UserAlreadyExistsException;

    void delete(User user);

    User getUser(String login);

    List<Message> getPendingMessages(String login);

    void addMessage(Message message) throws NoSuchUserException;
    
}
