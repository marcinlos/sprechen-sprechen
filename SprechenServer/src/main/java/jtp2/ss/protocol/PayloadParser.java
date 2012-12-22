package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public interface PayloadParser {

    Message parse(ByteBuffer buffer) throws InvalidFormatException;

}
