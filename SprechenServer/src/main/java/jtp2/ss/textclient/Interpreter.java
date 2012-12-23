package jtp2.ss.textclient;

import java.util.Date;
import java.util.Random;

import jtp2.ss.protocol.LoginMessage;
import jtp2.ss.protocol.Message;
import jtp2.ss.protocol.PDU;
import jtp2.ss.protocol.RegisterMessage;
import jtp2.ss.protocol.Status;
import jtp2.ss.protocol.TextMessage;
import jtp2.ss.protocol.Type;
import jtp2.ss.server.core.MessageConnection;

public class Interpreter {
    
    private MessageConnection connection;
    
    public Interpreter(MessageConnection connection) {
        this.connection = connection;
    }

    public void interpret(String line) {
        Random rand = new Random();
        String[] parts = line.split("\\s+", 2);
        //System.out.println("'" + parts[0] + "'");
        if (parts[0].equals("register")) {
            String[] rest = parts[1].split("\\s+", 2);
            Message msg = new RegisterMessage(rest[0], rest[1].hashCode());
            PDU pdu = new PDU(Type.REGISTER, msg);
            connection.sendMessage(pdu);
            System.out.println("Register message");
        } else if (parts[0].equals("login")) {
            String[] rest = parts[1].split("\\s+", 2);
            Message msg = new LoginMessage(rest[0], rest[1].hashCode(), 
                    Status.AVAILABLE, "Dupa");
            PDU pdu = new PDU(Type.LOGIN, msg);
            connection.sendMessage(pdu);
        } else if (parts[0].equals("msg")) {
            String[] rest = parts[1].split("\\s+", 3);
            long id = rand.nextLong();
            Message msg = new TextMessage(id, rest[0], rest[1], rest[2], new Date());
            PDU pdu = new PDU(Type.SEND_MSG, msg);
            connection.sendMessage(pdu);
        }
    }
}
