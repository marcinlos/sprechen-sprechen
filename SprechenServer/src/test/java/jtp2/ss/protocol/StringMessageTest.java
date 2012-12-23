package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class StringMessageTest {
    
    private StringMessage message;
    private int actualLength;

    @Before
    public void setUp() throws Exception {
        String msg = "Some message zażółcić gęślą jaźń";
        message = new StringMessage(msg);
        actualLength = StringMessage.FIXED_PART_LENGTH + 
                Utils.encodedSize(msg);
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
        
        StringMessage decoded = 
                (StringMessage) StringMessage.getParser().parse(buffer);
        assertEquals(message.getMessage(), decoded.getMessage());
    }

}
