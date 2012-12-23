package jtp2.ss.protocol;

import java.nio.ByteBuffer;
import java.util.Date;

public class TextMessage implements Message {
    
    private long identifier;
    private String sender;
    private String recipient;
    private String content;
    private Date date;
    
    public static final int FIXED_PART_LENGTH = 8 + 8 + 4 + 4 + 4;
    
    public TextMessage(long identifier, String sender, String recipient, 
            String content, Date date) {
        this.identifier = identifier;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = date;
    }
    
    private static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Message parse(ByteBuffer buffer) {
            long id = buffer.getLong();
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
            return new TextMessage(id, sender, recipient, content, date);
        }
    };
    
    public static PayloadParser getParser() {
        return PARSER;
    }
    
    public long getId() {
        return identifier;
    }
    
    public void setId(long id) {
        this.identifier = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
         * | id | timestamp | sender_size | rec_size | text_size |
         * | sender ... | recipient | text ... |
         */
        long timestamp = date.getTime();
        byte[] senderBytes = Utils.encode(sender);
        byte[] recipientBytes = Utils.encode(recipient);
        byte[] contentBytes = Utils.encode(content);
        buffer.putLong(identifier);
        buffer.putLong(timestamp);
        buffer.putInt(senderBytes.length);
        buffer.putInt(recipientBytes.length);
        buffer.putInt(contentBytes.length);
        buffer.put(senderBytes);
        buffer.put(recipientBytes);
        buffer.put(contentBytes);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(identifier).append("\n");
        sb.append("From: ").append(sender).append("\n");
        sb.append("To: ").append(recipient).append("\n");
        sb.append(content);
        return sb.toString();
    }

}
