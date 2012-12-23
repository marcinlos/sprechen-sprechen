package jtp2.ss.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TextMessageTest {

    private TextMessage message;
    private int actualLength;
    
    @Before
    public void setUp() throws Exception {
        String sender = "user77";
        String recipient = "user x666";
        String content = "Some content of a message, whatevah";
        Date date = new Date(1234567890);
        message = new TextMessage(1111, sender, recipient, content, date);
        
        int senderSize = Utils.encodedSize(sender);
        int recipientSize = Utils.encodedSize(recipient);
        int contentSize = Utils.encodedSize(content);
        actualLength = TextMessage.FIXED_PART_LENGTH + senderSize + 
                recipientSize + contentSize;
    }
    
    @Test
    public void testLength() {
        int length = message.length();
        assertEquals(length, actualLength);
    }

    @Test
    public void testDecoding() throws InvalidFormatException {
        ByteBuffer buffer = ByteBuffer.allocate(actualLength);
        message.write(buffer);
        buffer.flip();
        
        TextMessage decoded = 
                (TextMessage) TextMessage.getParser().parse(buffer);
        assertEquals(message.getId(), decoded.getId());
        assertEquals(message.getSender(), decoded.getSender());
        assertEquals(message.getRecipient(), decoded.getRecipient());
        assertEquals(message.getDate(), decoded.getDate());
        assertEquals(message.getContent(), decoded.getContent());
    }

}
