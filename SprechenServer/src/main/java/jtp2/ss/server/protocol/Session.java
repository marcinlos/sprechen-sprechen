package jtp2.ss.server.protocol;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import jtp2.ss.protocol.DataUnit;
import jtp2.ss.protocol.Status;
import jtp2.ss.server.core.MessageConnection;
import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;

public class Session {

    private static final Logger logger = Logger.getLogger(Session.class);

    private MessageConnection connection;
    private ServerProtocol protocol;
    private ServerInterface server;
    private String login;

    public Session(ServerInterface server, MessageConnection connection,
            ServerProtocol protocol) {
        this.server = server;
        this.connection = connection;
        this.protocol = protocol;
        protocol.setContext(new SessionContext());
        connection.beginCommunication();
    }

    public ProtocolInterface getProtocol() {
        return protocol;
    }

    private class SessionContext implements Context {

        @Override
        public void registerUser(User user) throws UserAlreadyExistsException {
            server.registerUser(user);
        }

        @Override
        public void loginUser(String login) throws UserAlreadyLoggedException {
            server.loginUser(login, Session.this);
            Session.this.login = login;
        }

        @Override
        public void logout() {
            server.logoutUser(login);
            try {
                connection.close();
            } catch (IOException e) {
                logger.error("Error while closing connection", e);
            }
        }

        @Override
        public User getUser(String login) {
            return server.getUser(login);
        }

        @Override
        public void changeStatus(Status status, String description) {
            server.changeStatus(login, status, description);
        }

        @Override
        public boolean authenticate(String login, long passwordHash)
                throws NoSuchUserException {
            return server.authenticate(login, passwordHash);
        }

        @Override
        public void sendToOtherUser(Message message) {
            server.sendMessage(login, message);
        }

        @Override
        public Collection<Message> getPendingMessages() {
            return server.getPendingMessages(login);
        }

        @Override
        public void sendBack(DataUnit message) {
            connection.sendMessage(message);
        }

        @Override
        public void error(String message, Throwable exc) {
            logger.error(message, exc);
        }

        @Override
        public void error(String message) {
            logger.error(message);
        }

    }

}
