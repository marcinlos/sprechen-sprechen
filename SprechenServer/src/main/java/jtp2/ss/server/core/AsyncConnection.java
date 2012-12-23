package jtp2.ss.server.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncConnection implements Connection {

    private AsynchronousSocketChannel channel;
    private AsyncReader reader;
    private AsyncWriter writer;
    
    public AsyncConnection(AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.reader = new AsyncReader(channel);
        this.writer = new AsyncWriter(channel);
    }

    @Override
    public <A> void read(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        reader.asyncRead(buffer, attachment, handler);
    }
    
    @Override
    public <A> void write(ByteBuffer buffer, A attachment, 
            CompletionHandler<Integer, A> handler) {
        writer.asyncWrite(buffer, attachment, handler);
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        channel.close();
    }
    
    @Override
    public SocketAddress getLocalAddress() throws IOException { 
        return channel.getLocalAddress();
    }
    
    @Override
    public SocketAddress getRemoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }
    
}
