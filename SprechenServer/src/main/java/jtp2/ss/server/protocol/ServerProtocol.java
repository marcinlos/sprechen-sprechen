package jtp2.ss.server.protocol;

import java.util.Collection;

import jtp2.ss.protocol.AckMessage;
import jtp2.ss.protocol.PDU;
import jtp2.ss.protocol.EmptyMessage;
import jtp2.ss.protocol.LoginMessage;
import jtp2.ss.protocol.Message;
import jtp2.ss.protocol.MessageRecipient;
import jtp2.ss.protocol.ReceiveStatus;
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
        PDU pdu = new PDU(Type.LOGIN_FAIL, msg);
        context.sendBack(pdu);
    }
    
    private void sendLoginOk() {
        PDU pdu = new PDU(Type.LOGIN_OK, EmptyMessage.INSTANCE);
        context.sendBack(pdu);
    }
    
    private void sendRegisterFailed(String message) {
        Message msg = new StringMessage(message);
        PDU pdu = new PDU(Type.REGISTER_FAIL, msg);
        context.sendBack(pdu);
    }
    
    private void sendRegisterOk() {
        PDU pdu = new PDU(Type.REGISTER_OK, EmptyMessage.INSTANCE);
        context.sendBack(pdu);
    }
    
    private void sendAck(long id, ReceiveStatus status) {
        PDU pdu = new PDU(Type.SEND_ACK, new AckMessage(status, id));
        context.sendBack(pdu);
    }

    @Override
    public void gotMessage(PDU message) {
        if (state == State.NOT_LOGGED) {
            whenNotLogged(message);
        } else if (state == State.LOGGED) {
            whenLogged(message);
        }
    }
    
    private void whenNotLogged(PDU message) {
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
    
    private void whenLogged(PDU message) {
        if (message.getType() == Type.NEW_STATUS) {
            StatusMessage msg = (StatusMessage) message.getPayload();
            handleStatusChange(msg);
        } else if (message.getType() == Type.SEND_MSG) {
            TextMessage msg = (TextMessage) message.getPayload();
            handleTextMessage(msg);
        } else {
            logger.error("Invalid message " + message.getType() + 
                    " after login");
        }
    }
    
    private void handleTextMessage(TextMessage msg) {
        if (msg.getSender().equals(login)) {
            try {
                jtp2.ss.server.data.Message message = 
                        new jtp2.ss.server.data.Message();
                message.setId(msg.getId());
                message.setSender(msg.getSender());
                message.setRecipient(msg.getRecipient());
                message.setContent(msg.getContent());
                message.setDate(msg.getDate());
                ReceiveStatus status = context.sendToOtherUser(message);
                sendAck(message.getId(), status);
            } catch (NoSuchUserException e) {
                sendAck(msg.getId(), ReceiveStatus.NO_SUCH_USER);
            }
        } else {
            logger.error("Invalid message from '" + login + "' - sent with " +
                    "login '" + msg.getSender() + "'");
        }
    }

    private void handleStatusChange(StatusMessage msg) {
        if (msg.getLogin().equals(login)) {
            context.changeStatus(msg.getStatus(), msg.getDescription());
        } else {
            logger.error("Invalid message from '" + login + "' - sent with " +
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
                logger.error("Authentication failure for user '" + login + "'");
                sendLoginFailed("Authentication failure");
            }
        } catch (UserAlreadyLoggedException e) {
            logger.error("Login error", e);
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
            goTo(State.LOGGED);
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
        String sender = message.getSender();
        TextMessage msg = new TextMessage(message.getId(), sender, login, 
                message.getContent(), message.getDate());
        context.sendBack(new PDU(Type.RECV_MSG, msg));
    }

    @Override
    public void notifyStatusChange(String login, Status status,
            String description) {
        StatusMessage msg = new StatusMessage(login, status, description);
        context.sendBack(new PDU(Type.NOTIFY_STATUS, msg));
    }

}
