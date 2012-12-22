package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class LoginMessageTest {

    private LoginMessage message;
    private int actualLength;
    
    @Before
    public void setUp() throws Exception {
        String login = "user5";
        String desc = "Some desc";
        message = new LoginMessage(login, 1234321, Status.AVAILABLE, desc);
        actualLength = LoginMessage.FIXED_PART_LENGTH + 
                login.length() + desc.length();
    }
    
    @Test
    public void testLength() {
        int length = message.length();
        assertEquals(actualLength, length);
    }

    @Test
    public void testDecoding() {
        ByteBuffer buffer = ByteBuffer.allocate(actualLength);
        message.write(buffer);
        buffer.flip();
        
        LoginMessage decoded = (LoginMessage) LoginMessage.PARSER.parse(buffer);
        assertEquals(message.getPasswordHash(), decoded.getPasswordHash());
        assertEquals(message.getLogin(), decoded.getLogin());
        assertEquals(message.getInitialStatus(), decoded.getInitialStatus());
        assertEquals(message.getDescription(), decoded.getDescription());
    }

}
