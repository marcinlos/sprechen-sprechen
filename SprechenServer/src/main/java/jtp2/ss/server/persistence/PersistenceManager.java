package jtp2.ss.server.persistence;

import java.util.List;

import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;

public interface PersistenceManager {

    void register(User user);

    void delete(User user);

    User getUser(String login);

    List<Message> getPendingMessages(String login);

    void addMessage(Message message);
    
}
