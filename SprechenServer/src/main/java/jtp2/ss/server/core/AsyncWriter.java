package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
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
    private boolean open = true;

    
    public AsyncWriter(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    
    public void close() {
        mutex.lock();
        try {
            open = false;
        } finally {
            mutex.unlock();
        }
    }
    
    public synchronized boolean isOpen() {
        mutex.lock();
        try {
            return open;
        } finally {
            mutex.unlock();
        }
    }
    
    public <A> void asyncWrite(ByteBuffer buffer, A attachment, 
            CompletionHandler<Integer, A> handler) {
        mutex.lock();
        try {
            if (isOpen()) {
                WriteTask<A> task = new WriteTask<>(buffer, attachment, handler);
                writeQueue.add(task);
                if (current == null) {
                    runNext();
                }
            } else if (handler != null) {
                Throwable exc = new ClosedChannelException();
                handler.failed(exc, attachment);
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
                if (isOpen()) {
                    writeSome();
                } else if (handler != null) {
                    Throwable exc = new ClosedChannelException();
                    handler.failed(exc, attachment);
                }
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
