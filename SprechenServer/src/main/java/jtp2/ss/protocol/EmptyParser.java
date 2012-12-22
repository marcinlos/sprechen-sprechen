package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class EmptyParser implements PayloadParser {

    @Override
    public Payload parse(ByteBuffer buffer) {
        return EmptyMessage.INSTANCE;
    }

}
