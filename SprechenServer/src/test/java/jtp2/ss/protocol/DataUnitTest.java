package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

public class DataUnitTest {
    
    
    @Test
    public void testRegisterMessage() throws InvalidFormatException {
        RegisterMessage msg = new RegisterMessage("login", 123454321);
        DataUnit data = new DataUnit(Type.REGISTER, msg);
        Header header = data.getHeader();
        ByteBuffer buffer = data.toBuffer();
        buffer.flip();
        
        DataUnit decoded = DataUnit.fromBytes(buffer);
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
        DataUnit data = new DataUnit(Type.LOGIN, msg);
        ByteBuffer buffer = data.toBuffer();
        buffer.flip();
        
        DataUnit decoded = DataUnit.fromBytes(buffer);
        LoginMessage newMsg = (LoginMessage) decoded.getPayload();
        assertEquals(msg.getLogin(), newMsg.getLogin());
        assertEquals(msg.getPasswordHash(), msg.getPasswordHash());
        assertEquals(msg.getInitialStatus(), newMsg.getInitialStatus());
        assertEquals(msg.getDescription(), newMsg.getDescription());
    }
    
}
