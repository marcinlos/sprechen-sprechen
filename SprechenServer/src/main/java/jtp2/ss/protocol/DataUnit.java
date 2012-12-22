package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class DataUnit {

    private Header header;
    private Payload payload;

    public DataUnit(Type type, Payload payload) {
        this.header = new Header(type, payload.length());
        this.payload = payload;
    }

    public static DataUnit fromBytes(ByteBuffer buffer)
            throws InvalidFormatException {
        Header header = Header.fromBytes(buffer);
        Payload payload = Parser.parseMessage(header.getType(), buffer);
        DataUnit data = new DataUnit(header.getType(), payload);
        return data;
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void write(ByteBuffer buffer) {
        header.write(buffer);
        payload.write(buffer);
    }
    
    public int length() {
        return Header.LENGTH + payload.length();
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
