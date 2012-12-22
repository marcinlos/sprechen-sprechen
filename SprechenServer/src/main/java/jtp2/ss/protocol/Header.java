package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class Header {

    public final static int LENGTH = 5;

    private Type type;
    private int length;

    public Header(Type type, int length) {
        this.type = type;
        this.length = length;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void write(ByteBuffer buffer) {
        buffer.put(Type.toByte(type));
    }

    public static Header fromBytes(ByteBuffer buffer) {
        Type type = Type.fromByte(buffer.get());
        int length = buffer.getInt();
        return new Header(type, length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.valueOf(type));
        sb.append(" (");
        sb.append(length);
        sb.append(" bytes)");
        return String.valueOf(type);
    }

}
