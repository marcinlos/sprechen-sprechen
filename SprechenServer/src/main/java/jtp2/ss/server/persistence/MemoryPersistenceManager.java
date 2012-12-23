package jtp2.ss.server.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;
import jtp2.ss.server.protocol.NoSuchUserException;
import jtp2.ss.server.protocol.UserAlreadyExistsException;

public class MemoryPersistenceManager implements PersistenceManager {

    private Map<String, List<Message>> messages = new HashMap<>();

    private Map<String, User> users = new HashMap<>();

    @Override
    public synchronized void register(User user)
            throws UserAlreadyExistsException {
        if (users.containsKey(user.getLogin())) {
            throw new UserAlreadyExistsException(user.getLogin());
        } else {
            users.put(user.getLogin(), user);
            messages.put(user.getLogin(), new ArrayList<Message>());
        }
    }

    @Override
    public synchronized void delete(User user) {
        users.remove(user.getLogin());
    }

    @Override
    public synchronized User getUser(String login) {
        return users.get(login);
    }

    @Override
    public synchronized List<Message> getPendingMessages(String login) {
        return messages.get(login);
    }

    @Override
    public synchronized void addMessage(Message message)
            throws NoSuchUserException {
        String recipient = message.getRecipient();
        List<Message> msgList = messages.get(recipient);
        if (msgList != null) {
            msgList.add(message);
        } else {
            throw new NoSuchUserException(recipient);
        }
    }

}
