package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class AckMessage implements Message {

    private ReceiveStatus status;
    private long identifier;
    
    public static final int FIXED_PART_LENGTH = 1 + 8;
    
    public AckMessage(ReceiveStatus status, long identifier) {
        this.status = status;
        this.identifier = identifier;
    }
    
    private static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Message parse(ByteBuffer buffer) throws InvalidFormatException {
            ReceiveStatus status = ReceiveStatus.fromByte(buffer.get());
            long id = buffer.getLong();
            return new AckMessage(status, id);
        }
    };
    
    public static PayloadParser getParser() {
        return PARSER;
    }

    public ReceiveStatus getStatus() {
        return status;
    }

    public void setStatus(ReceiveStatus status) {
        this.status = status;
    }

    public long getId() {
        return identifier;
    }

    public void setId(long identifier) {
        this.identifier = identifier;
    }

    @Override
    public int length() {
        return FIXED_PART_LENGTH;
    }

    @Override
    public void write(ByteBuffer buffer) {
        /*
         * Format:
         * | status | identifier |
         * 
         * [1] status       - status of message
         * [8] identifier   - message identifier
         */
        buffer.put(ReceiveStatus.toByte(status));
        buffer.putLong(identifier);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(identifier).append("\n");
        sb.append("Status: ").append(status).append("\n");
        return sb.toString();
    }
    
    
}
