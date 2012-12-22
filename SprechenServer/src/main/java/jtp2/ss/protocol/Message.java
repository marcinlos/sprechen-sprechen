package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public interface Message {

    int length();

    void write(ByteBuffer buffer);

}
