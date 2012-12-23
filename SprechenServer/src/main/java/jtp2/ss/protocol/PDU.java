package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class PDU {

    private Header header;
    private Message payload;

    public PDU(Type type, Message payload) {
        this.header = new Header(type, payload.length());
        this.payload = payload;
    }

    public static PDU fromBytes(ByteBuffer buffer)
            throws InvalidFormatException {
        Header header = Header.fromBytes(buffer);
        Message payload = Parser.parseMessage(header.getType(), buffer);
        PDU data = new PDU(header.getType(), payload);
        return data;
    }

    public Header getHeader() {
        return header;
    }

    public Message getPayload() {
        return payload;
    }

    public void write(ByteBuffer buffer) {
        header.write(buffer);
        payload.write(buffer);
    }
    
    public int length() {
        return Header.LENGTH + payload.length();
    }
    
    public Type getType() {
        return header.getType();
    }

    public ByteBuffer toBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        write(buffer);
        return buffer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(header.toString());
        sb.append('\n');
        sb.append(payload.toString());
        return sb.toString();
    }

}
