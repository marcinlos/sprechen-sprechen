package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public interface Payload {

    int length();

    void write(ByteBuffer buffer);

}
