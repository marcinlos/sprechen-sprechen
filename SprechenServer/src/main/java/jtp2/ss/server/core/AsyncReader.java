package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;


public class AsyncReader {
    
    private AsynchronousSocketChannel channel;
    private boolean open = true;
    
    public AsyncReader(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    
    public synchronized void close() {
        open = false;
    }
    
    public synchronized boolean isOpen() {
        return open;
    }
    
    public <A> void asyncRead(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        if (isOpen()) {
            ReadAction<A> reader = new ReadAction<>(buffer, attachment, handler);
            reader.readSome();
        } else if (handler != null) {
            Throwable exc = new ClosedChannelException();
            handler.failed(exc, attachment);
        }
    }
    
    private class ReadAction<A> implements CompletionHandler<Integer, A> {
    
        private ByteBuffer buffer;
        private int remaining;
        private A attachment;
        private CompletionHandler<Integer, A> handler;

        
        public ReadAction(ByteBuffer buffer, A attachment,
                CompletionHandler<Integer, A> handler) {
            this.buffer = buffer;
            this.remaining = buffer.remaining();
            this.attachment = attachment;
            this.handler = handler;
        }
        
        @Override
        public void completed(Integer bytes, A attachment) {
            remaining -= bytes;
            if (remaining > 0) {
                if (isOpen()) {
                    readSome();
                } else if (handler != null) {
                    Throwable exc = new ClosedChannelException();
                    handler.failed(exc, attachment);
                }
            } else if (handler != null) {
                handler.completed(bytes, attachment);
            }
        }
    
        @Override
        public void failed(Throwable e, A attachment) {
            if (handler != null) {
                handler.failed(e, attachment);
            } // else swallow :/
        }
        
        private void readSome() {
            channel.read(buffer, attachment, this);
        }
    }
    
}
