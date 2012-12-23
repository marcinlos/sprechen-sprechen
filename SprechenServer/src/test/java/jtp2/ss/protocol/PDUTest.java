package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

public class PDUTest {
    
    
    @Test
    public void testRegisterMessage() throws InvalidFormatException {
        RegisterMessage msg = new RegisterMessage("login", 123454321);
        PDU data = new PDU(Type.REGISTER, msg);
        Header header = data.getHeader();
        ByteBuffer buffer = data.toBuffer();
        buffer.flip();
        
        PDU decoded = PDU.fromBytes(buffer);
        assertEquals(header.getType(), decoded.getHeader().getType());
        assertEquals(header.getLength(), decoded.getHeader().getLength());
        
        RegisterMessage newMsg = (RegisterMessage) decoded.getPayload();
        assertEquals(msg.getLogin(), newMsg.getLogin());
        assertEquals(msg.getPasswordHash(), newMsg.getPasswordHash());
    }
    
    @Test
    public void testLoginMessage() throws InvalidFormatException {
        LoginMessage msg = new LoginMessage("user7", 666, Status.AVAILABLE, 
                "Some status");
        PDU data = new PDU(Type.LOGIN, msg);
        ByteBuffer buffer = data.toBuffer();
        buffer.flip();
        
        PDU decoded = PDU.fromBytes(buffer);
        LoginMessage newMsg = (LoginMessage) decoded.getPayload();
        assertEquals(msg.getLogin(), newMsg.getLogin());
        assertEquals(msg.getPasswordHash(), msg.getPasswordHash());
        assertEquals(msg.getInitialStatus(), newMsg.getInitialStatus());
        assertEquals(msg.getDescription(), newMsg.getDescription());
    }
    
}
