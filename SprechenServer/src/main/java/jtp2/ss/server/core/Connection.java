package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public interface Connection {

    <A> void read(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler);
    
    <A> void write(ByteBuffer buffer, A attachment, 
            CompletionHandler<Integer, A> handler);
    
}
