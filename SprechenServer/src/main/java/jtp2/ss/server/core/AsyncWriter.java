package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AsyncWriter {
    
    private AsynchronousSocketChannel channel;
    private Lock mutex = new ReentrantLock();
    private Queue<WriteTask<?>> writeQueue = new ArrayDeque<>();
    private WriteTask<?> current;

    
    public AsyncWriter(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    
    public <A> void asyncWrite(ByteBuffer buffer, A attachment, 
            CompletionHandler<Integer, A> handler) {
        mutex.lock();
        try {
            WriteTask<A> task = new WriteTask<>(buffer, attachment, handler);
            writeQueue.add(task);
            if (current == null) {
                runNext();
            }
        } finally {
            mutex.unlock();
        }
    }
    
    private void runNext() {
        mutex.lock();
        try {
            current = writeQueue.poll();
            if (current != null) {
                current.writeSome();
            }
        } finally {
            mutex.unlock();
        }
    }

    private class WriteTask<A> implements CompletionHandler<Integer, A> {
        
        private ByteBuffer buffer;
        private CompletionHandler<Integer, A> handler;
        private A attachment;
        private int remaining;
        
        WriteTask(ByteBuffer buffer, A attachment, 
                CompletionHandler<Integer, A> handler) {
            this.buffer = buffer;
            this.handler = handler;
            this.attachment = attachment;
            this.remaining = buffer.remaining();
        }
        
        public void writeSome() {
            channel.write(buffer, attachment, this);
        }
        
        @Override
        public void completed(Integer bytes, A attachment) {
            remaining -= bytes;
            if (remaining > 0) {
                writeSome();
            } else {
                runNext();
                if (handler != null) {
                    handler.completed(bytes, attachment);
                }
            }
        }
    
        @Override
        public void failed(Throwable e, A attachment) {
            if (handler != null) {
                handler.failed(e, attachment);
            } // else swallow :(
        }
    }

}
