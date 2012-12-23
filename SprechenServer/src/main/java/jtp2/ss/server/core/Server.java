package jtp2.ss.server.core;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jtp2.ss.protocol.ReceiveStatus;
import jtp2.ss.protocol.Status;
import jtp2.ss.server.data.Message;
import jtp2.ss.server.data.User;
import jtp2.ss.server.persistence.PersistenceManager;
import jtp2.ss.server.protocol.NoSuchUserException;
import jtp2.ss.server.protocol.ServerInterface;
import jtp2.ss.server.protocol.ServerProtocol;
import jtp2.ss.server.protocol.Session;
import jtp2.ss.server.protocol.UserAlreadyExistsException;
import jtp2.ss.server.protocol.UserAlreadyLoggedException;

import org.apache.log4j.Logger;

public class Server implements ServerInterface {

    private static final Logger logger = Logger.getLogger(Server.class);

    private IOManager io;
    private AsynchronousServerSocketChannel acceptingSocket;
    // private ConnectionManager connections;
    private PersistenceManager persistenceManager;

    private Map<String, Session> connections = new HashMap<>();

    public Server(IOManager io, PersistenceManager persistenceManager) {
        this.io = io;
        this.persistenceManager = persistenceManager;
    }

    public void run(int port) throws IOException {
        acceptingSocket = io.createSocket(port);
        acceptor = new ConnectionAcceptor();
        logger.info("Waiting for incoming connections...");
        acceptNext();
    }

    public void cleanup() {
        logger.info("Shutting down...");
    }

    public void shutdown() {
        io.shutdown();
    }

    public void waitForShutdown() throws InterruptedException {
        io.waitForShutdown();
    }

    private void acceptNext() {
        acceptingSocket.accept(null, acceptor);
    }

    private synchronized void registerConnection(
            AsynchronousSocketChannel socket) {
        try {
            logger.info("Connected [" + socket.getRemoteAddress() + "]");
        } catch (IOException e) {
            logger.error(e);
        }
        Connection connection = new AsyncConnection(socket);
        ServerProtocol protocol = new ServerProtocol();
        MessageConnection c = new MessageConnection(connection, protocol, null);
        new Session(this, c, protocol);
    }

    private ConnectionAcceptor acceptor;

    private class ConnectionAcceptor implements
            CompletionHandler<AsynchronousSocketChannel, Void> {

        @Override
        public void completed(AsynchronousSocketChannel socket, Void unused) {
            registerConnection(socket);
            acceptNext();
        }

        @Override
        public void failed(Throwable exc, Void unused) {
            logger.error("Error while accepting", exc);
        }

    }

    @Override
    public void registerUser(User user) throws UserAlreadyExistsException {
        persistenceManager.register(user);
    }

    @Override
    public synchronized void loginUser(String login, Session session)
            throws UserAlreadyLoggedException {
        if (connections.containsKey(login)) {
            throw new UserAlreadyLoggedException(login);
        } else {
            connections.put(login, session);
        }
    }

    @Override
    public synchronized void logoutUser(String login) {
        // Session session = connections.get(login);
        connections.remove(login);
    }

    @Override
    public synchronized User getUser(String login) {
        return persistenceManager.getUser(login);
    }

    @Override
    public synchronized List<Message> getPendingMessages(String login) {
        return persistenceManager.getPendingMessages(login);
    }

    @Override
    public synchronized void changeStatus(String login, Status status,
            String description) {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized boolean authenticate(String login, long passwordHash)
            throws NoSuchUserException {
        User user = persistenceManager.getUser(login);
        if (user != null) {
            return user.getPasswordHash() == passwordHash;
        } else {
            throw new NoSuchUserException();
        }
    }

    @Override
    public synchronized ReceiveStatus sendMessage(Message message)
            throws NoSuchUserException {
        String recipient = message.getRecipient();
        Session session = connections.get(recipient);
        if (session != null) {
            session.getProtocol().sendMessage(message);
            return ReceiveStatus.DELIVERED;
        } else {
            persistenceManager.addMessage(message);
            return ReceiveStatus.QUEUED;
        }
    }

    @Override
    public void subscribeToStatusNotifications(String subscriber, String target) {
        // TODO Auto-generated method stub

    }

}
