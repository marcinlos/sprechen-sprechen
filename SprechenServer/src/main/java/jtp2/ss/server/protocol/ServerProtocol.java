package jtp2.ss.server.protocol;

import java.util.Collection;

import jtp2.ss.protocol.DataUnit;
import jtp2.ss.protocol.EmptyMessage;
import jtp2.ss.protocol.LoginMessage;
import jtp2.ss.protocol.Message;
import jtp2.ss.protocol.MessageRecipient;
import jtp2.ss.protocol.RegisterMessage;
import jtp2.ss.protocol.Status;
import jtp2.ss.protocol.StatusMessage;
import jtp2.ss.protocol.StringMessage;
import jtp2.ss.protocol.TextMessage;
import jtp2.ss.protocol.Type;
import jtp2.ss.server.data.User;

import org.apache.log4j.Logger;

public class ServerProtocol implements MessageRecipient, ProtocolInterface {
    
    private static final Logger logger = Logger.getLogger(ServerProtocol.class);
    private String login;
    
    private enum State {
        NOT_LOGGED,
        LOGGED
    }
    
    private Context context;
    private State state = State.NOT_LOGGED;
    
    private void goTo(State state) {
        this.state = state;
    }
    
    public void setContext(Context context) {
        this.context = context;
    }
    
    private void sendLoginFailed(String message) {
        Message msg = new StringMessage(message);
        DataUnit pdu = new DataUnit(Type.LOGIN_FAIL, msg);
        context.sendBack(pdu);
    }
    
    private void sendLoginOk() {
        DataUnit pdu = new DataUnit(Type.LOGIN_OK, EmptyMessage.INSTANCE);
        context.sendBack(pdu);
    }
    
    private void sendRegisterFailed(String message) {
        Message msg = new StringMessage(message);
        DataUnit pdu = new DataUnit(Type.REGISTER_FAIL, msg);
        context.sendBack(pdu);
    }
    
    private void sendRegisterOk() {
        DataUnit pdu = new DataUnit(Type.REGISTER_OK, EmptyMessage.INSTANCE);
        context.sendBack(pdu);
    }

    @Override
    public void sendMessage(DataUnit message) {
        if (state == State.NOT_LOGGED) {
            whenNotLogged(message);
        } else if (state == State.LOGGED) {
            whenLogged(message);
        }
    }
    
    private void whenNotLogged(DataUnit message) {
        if (message.getType() == Type.LOGIN) {
            LoginMessage msg = (LoginMessage) message.getPayload();
            handleLogin(msg);
        } else if (message.getType() == Type.REGISTER) {
            RegisterMessage msg = (RegisterMessage) message.getPayload();
            handleRegister(msg);
        } else {
            logger.error("Invalid message " + message.getType() + 
                    " before login");
        }
    }
    
    private void whenLogged(DataUnit message) {
        if (message.getType() == Type.NEW_STATUS) {
            StatusMessage msg = (StatusMessage) message.getPayload();
            handleStatusChange(msg);
        } else if (message.getType() == Type.SEND_MSG) {
            TextMessage msg = (TextMessage) message.getPayload();
            handleTextMessage(msg);
        } else {
            context.error("Invalid message " + message.getType() + 
                    " after login");
        }
    }
    
    private void handleTextMessage(TextMessage msg) {
        if (msg.getSender().equals(login)) {
        } else {
            context.error("Invalid message from '" + login + "' - sent with " +
                    "login '" + msg.getSender() + "'");
        }
    }

    private void handleStatusChange(StatusMessage msg) {
        if (msg.getLogin().equals(login)) {
            context.changeStatus(msg.getStatus(), msg.getDescription());
        } else {
            context.error("Invalid message from '" + login + "' - sent with " +
                    "login '" + msg.getLogin() + "'");
        }
    }

    private void handleLogin(LoginMessage message) {
        String login = message.getLogin();
        long hash = message.getPasswordHash();
        try {
            if (context.authenticate(login, hash)) {
                this.login = login;
                context.loginUser(login);
                Status status = message.getInitialStatus();
                String desc = message.getDescription();
                context.changeStatus(status, desc);
                goTo(State.LOGGED);
                sendLoginOk();
            } else {
                context.error("Authentication failure for user '" + login + "'");
                sendLoginFailed("Authentication failure");
            }
        } catch (UserAlreadyLoggedException e) {
            context.error("Login error", e);
            sendLoginFailed("User already logged in");
        } catch (NoSuchUserException e) {
            sendLoginFailed("User does not exist");
        }
    }
    
    private void handleRegister(RegisterMessage message) {
        String login = message.getLogin();
        long hash = message.getPasswordHash();
        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(hash);
        try {
            context.registerUser(user);
            context.loginUser(login);
            this.login = login;
            sendRegisterOk();
            sendPendingMessages();
        } catch (UserAlreadyExistsException e) {
            sendRegisterFailed("Login already in use");
        } catch (UserAlreadyLoggedException e) {
            sendRegisterFailed("User already logged in");
        }
    }
    
    private void sendPendingMessages() {
        Collection<jtp2.ss.server.data.Message> pending = 
                context.getPendingMessages(); 
        
        for (jtp2.ss.server.data.Message message: pending) {
            sendMessage(message);
        }
    }

    @Override
    public void sendMessage(jtp2.ss.server.data.Message message) {
        User sender = message.getSource();
        TextMessage msg = new TextMessage(sender.getLogin(), login, 
                message.getContent(), message.getDate());
        context.sendBack(new DataUnit(Type.RECV_MSG, msg));
    }

    @Override
    public void notifyStatusChange(String login, Status status,
            String description) {
        StatusMessage msg = new StatusMessage(login, status, description);
        context.sendBack(new DataUnit(Type.NOTIFY_STATUS, msg));
    }

}
