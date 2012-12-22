package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class StatusMessageTest {

    private StatusMessage message;
    private int actualLength;
    
    @Before
    public void setUp() throws Exception {
        String name = "user77";
        String desc = "Some fancy description";
        message = new StatusMessage(name, Status.AVAILABLE, desc);
        actualLength = StatusMessage.FIXED_PART_LENGTH + 
                Utils.encodedSize(name)+ Utils.encodedSize(desc);
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
        
        StatusMessage decoded = 
                (StatusMessage) StatusMessage.getParser().parse(buffer);
        assertEquals(message.getStatus(), decoded.getStatus());
        assertEquals(message.getUsername(), decoded.getUsername());
        assertEquals(message.getDescription(), decoded.getDescription());
    }

}
