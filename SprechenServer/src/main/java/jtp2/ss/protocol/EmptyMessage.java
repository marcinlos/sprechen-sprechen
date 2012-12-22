package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class EmptyMessage implements Payload {

    public static final EmptyMessage INSTANCE = new EmptyMessage();

    private static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Payload parse(ByteBuffer buffer) {
            return new EmptyMessage();
        }
    };
    
    public static PayloadParser getParser() { 
        return PARSER;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public void write(ByteBuffer buffer) {

    }

    @Override
    public String toString() {
        return "<no value>";
    }

}
