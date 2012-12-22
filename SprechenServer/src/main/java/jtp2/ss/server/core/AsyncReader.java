package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class AsyncReader {
    
    private AsynchronousSocketChannel channel;
    
    public AsyncReader(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    
    public <A> void asyncRead(ByteBuffer buffer, int bytes, A attachment,
            CompletionHandler<Integer, A> handler) {
        ReadAction<A> reader = new ReadAction<A>(buffer, bytes, 
                attachment, handler);
        reader.readSome();
    }
    
    public <A> void asyncRead(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        asyncRead(buffer, buffer.remaining(), attachment, handler);
    }
    
    private class ReadAction<A> implements CompletionHandler<Integer, A> {
    
        private ByteBuffer buffer;
        private int remaining;
        private A attachment;
        private CompletionHandler<Integer, A> handler;
        
        public ReadAction(ByteBuffer buffer, int bytes, A attachment,
                CompletionHandler<Integer, A> handler) {
            this.buffer = buffer;
            this.remaining = bytes;
            this.attachment = attachment;
            this.handler = handler;
        }
        
        @Override
        public void completed(Integer bytes, A attachment) {
            remaining -= bytes;
            if (remaining > 0) {
                readSome();
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
