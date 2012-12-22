package jtp2.ss.protocol;

import java.nio.ByteBuffer;
import java.util.Date;

public class TextMessage implements Payload {
    
    private String sender;
    private String recipient;
    private String content;
    private Date date;
    
    public static final int FIXED_PART_LENGTH = 8 + 4 + 4 + 4;
    
    public TextMessage(String sender, String recipient, String content,
            Date date) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = date;
    }
    
    private static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Payload parse(ByteBuffer buffer) {
            long timestamp = buffer.getLong();
            Date date = new Date(timestamp);
            int senderSize = buffer.getInt();
            int recipientSize = buffer.getInt();
            int contentSize = buffer.getInt();
            byte[] senderBytes = new byte[senderSize];
            byte[] recipientBytes = new byte[recipientSize];
            byte[] contentBytes = new byte[contentSize];
            buffer.get(senderBytes);
            buffer.get(recipientBytes);
            buffer.get(contentBytes);
            String sender = Utils.decode(senderBytes);
            String recipient = Utils.decode(recipientBytes);
            String content = Utils.decode(contentBytes);
            return new TextMessage(sender, recipient, content, date);
        }
    };
    
    public static PayloadParser getParser() {
        return PARSER;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceipient() {
        return recipient;
    }

    public void setReceipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int length() {
        int senderSize = Utils.encodedSize(sender);
        int recipientSize = Utils.encodedSize(recipient);
        int contentSize = Utils.encodedSize(content);
        return FIXED_PART_LENGTH + senderSize + recipientSize + contentSize;
    }

    @Override
    public void write(ByteBuffer buffer) {
        /*
         * Format:
         * | timestamp | sender_size | rec_size | text_size |
         * | sender ... | recipient | text ... |
         */
        long timestamp = date.getTime();
        byte[] senderBytes = Utils.encode(sender);
        byte[] recipientBytes = Utils.encode(recipient);
        byte[] contentBytes = Utils.encode(content);
        buffer.putLong(timestamp);
        buffer.putInt(senderBytes.length);
        buffer.putInt(recipientBytes.length);
        buffer.putInt(contentBytes.length);
        buffer.put(senderBytes);
        buffer.put(recipientBytes);
        buffer.put(contentBytes);
    }

}
