package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public interface PayloadParser {

    Payload parse(ByteBuffer buffer);

}
