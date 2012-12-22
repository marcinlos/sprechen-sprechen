package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import jtp2.ss.protocol.RegisterMessage;

import org.junit.Before;
import org.junit.Test;

public class RegisterMessageTest {
    
    private RegisterMessage message;
    private int actualLength;

    @Before
    public void setUp() throws Exception {
        String login = "user55";
        message = new RegisterMessage(login, 55443322);
        actualLength = RegisterMessage.FIXED_PART_LENGTH + 
                Utils.encodedSize(login);
    }
    
    @Test
    public void testLength() {
        int length = message.length();
        assertEquals(actualLength, length);
    }

    @Test
    public void testDecoding() throws InvalidFormatException {
        ByteBuffer buffer = ByteBuffer.allocate(actualLength);
        message.write(buffer);
        buffer.flip();
        
        RegisterMessage decoded = 
                (RegisterMessage) RegisterMessage.getParser().parse(buffer);
        
        assertEquals(message.getLogin(), decoded.getLogin());
        assertEquals(message.getPasswordHash(), decoded.getPasswordHash());
    }

}
