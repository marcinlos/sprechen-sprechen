package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class StringMessage implements Payload {

    private String message;

    public static final int FIXED_PART_LENGTH = 4;
    
    public StringMessage(String cause) {
        this.message = cause;
    }

    public static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Payload parse(ByteBuffer buffer) {
            int length = buffer.getInt();
            byte[] msgBytes = new byte[length];
            buffer.get(msgBytes, 0, length);
            String content = Utils.decode(msgBytes);
            return new StringMessage(content);
        }
    };

    @Override
    public int length() {
        return FIXED_PART_LENGTH + Utils.encodedSize(message);
    }

    @Override
    public void write(ByteBuffer buffer) {
        byte[] content = Utils.encode(message);
        buffer.putInt(content.length);
        buffer.put(content);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Text: '");
        sb.append(message).append("'");
        return sb.toString();
    }

}
