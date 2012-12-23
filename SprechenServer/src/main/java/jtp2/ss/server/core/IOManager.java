package jtp2.ss.server.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IOManager {

    private AsynchronousChannelGroup workers;
    private ExecutorService executor;

    public IOManager(int threads) throws IOException {
        executor = Executors.newFixedThreadPool(threads);
        workers = AsynchronousChannelGroup.withThreadPool(executor);
    }

    public AsynchronousServerSocketChannel createSocket(int port)
            throws IOException {
        AsynchronousServerSocketChannel channel = 
                AsynchronousServerSocketChannel.open(workers);
        channel.bind(new InetSocketAddress(port));
        return channel;
    }
    
    public void shutdown() {
        workers.shutdown();
    }
    
    public void waitForShutdown() throws InterruptedException {
        workers.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

}
