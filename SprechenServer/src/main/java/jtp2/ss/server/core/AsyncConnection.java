package jtp2.ss.server.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * Implementacja {@link Connection} wykorzystująca wprowadzone w javie 7 klasy z
 * {@code java.nio}.
 */
public class AsyncConnection implements Connection {

    // Przechowywany socket
    private AsynchronousSocketChannel channel;

    // Instancje klas pomocnicznych
    private AsyncReader reader;
    private AsyncWriter writer;

    /**
     * Buduje połączenie oparte na przekazanym sockecie
     * 
     * @param channel
     *            Socket do komunikacji
     */
    public AsyncConnection(AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.reader = new AsyncReader(channel);
        this.writer = new AsyncWriter(channel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A> void read(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        reader.asyncRead(buffer, attachment, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A> void read(ByteBuffer buffer, long timeout, TimeUnit unit,
            A attachment, CompletionHandler<Integer, A> handler) {
        reader.asyncRead(buffer, attachment, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A> void write(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        writer.asyncWrite(buffer, attachment, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // Informujemy najpierw klasy pomocnicze
        reader.close();
        writer.close();
        // Socket zamykamy dopiero teraz
        channel.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddress getLocalAddress() throws IOException {
        return channel.getLocalAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddress getRemoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }

}
