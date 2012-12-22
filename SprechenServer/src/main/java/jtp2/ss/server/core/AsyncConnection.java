package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncConnection implements Connection {

    private AsyncReader reader;
    private AsyncWriter writer;
    
    public AsyncConnection(AsynchronousSocketChannel channel) {
        this.reader = new AsyncReader(channel);
        this.writer = new AsyncWriter(channel);
    }
    
    public <A> void read(ByteBuffer buffer, int bytes, A attachment,
            CompletionHandler<Integer, A> handler) {
        reader.asyncRead(buffer, attachment, handler);
    }
    
    public <A> void read(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        reader.asyncRead(buffer, attachment, handler);
    }
    
    public <A> void write(ByteBuffer buffer, A attachment, 
            CompletionHandler<Integer, A> handler) {
        writer.asyncWrite(buffer, attachment, handler);
    }
    
}
