package jtp2.ss.server.protocol;

import jtp2.ss.protocol.Status;
import jtp2.ss.server.data.Message;

public interface ProtocolInterface {

    void sendMessage(Message message);
    
    void notifyStatusChange(String login, Status status, String description);
    
}
