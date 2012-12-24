package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Klasa pomocnicza dostarczająca funkcjonalności zapisu do socketu. API do
 * operacji asynchronicznych dostarczone przez jdk7 nie pozwala na zapis z wielu
 * wątków, zatem żądania zapisu są kolejkowane i przetwarzane sekwencyjnie.
 */
public class AsyncWriter {

    // Przechowywany socket
    private AsynchronousSocketChannel channel;
    // Mutex do synchronizacji kolejki
    private Lock mutex = new ReentrantLock();
    // Kolejka żądań zapisu
    private Queue<WriteTask<?>> writeQueue = new ArrayDeque<>();
    // Obecnie przetwarzane żądanie zapisu
    private WriteTask<?> current;
    // Czy socket jest obecnie otwarty
    private boolean open = true;

    /**
     * Tworzy obiekt na podstawie przekazanego socketu
     * 
     * @param channel
     *            Socket, który będzie wykorzystywany przez tworzony obiekt
     */
    public AsyncWriter(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    /**
     * Informuje obiekt o fakcie zamknięcia połączenia
     */
    public void close() {
        mutex.lock();
        try {
            open = false;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * @return {@code true } jeśli połączenie jest otwarte, {@code false} w
     *         przeciwnym wypadku
     */
    public synchronized boolean isOpen() {
        mutex.lock();
        try {
            return open;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Inicjuje operację asynchronicznego zapisu. Operacja jest umieszczana w
     * kolejce żądań.
     * 
     * @param buffer
     *            Bufor z danymi do zapisania
     * @param attachment
     *            Dowolny obiekt, który zostanie przekazany do handlera gdy ten
     *            zostanie wywołany
     * @param handler
     *            Callback wywoływany po zakończeniu operacji, bądź w razie
     *            wystąpienia błędu
     * 
     */
    public <A> void asyncWrite(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler) {
        mutex.lock();
        try {
            // jeśli połączenie jest otwarte, dokładamy żądanie
            if (isOpen()) {
                WriteTask<A> task = new WriteTask<>(buffer, attachment, handler);
                writeQueue.add(task);
                // jeśli żadna operacja nie jest w trakcie wykonania,
                // rozpoczynamy wykonywanie tej
                if (current == null) {
                    runNext();
                }
            } else if (handler != null) {
                // w przeciwnym wypadku iformujemy o błędzie
                Throwable exc = new ClosedChannelException();
                handler.failed(exc, attachment);
            }
        } finally {
            mutex.unlock();
        }
    }

    // Wyciąga z kolejki następne żądanie (o ile istnieje), i je realizuje.
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

    /*
     * Klasa pomocnicza, reprezentująca pojedyncze żądanie zapisu.
     */
    private class WriteTask<A> implements CompletionHandler<Integer, A> {

        // Bufor z danymi do zapisania
        private ByteBuffer buffer;
        // Handler ]uakończenia
        private CompletionHandler<Integer, A> handler;
        // Obiekt związany z operacja
        private A attachment;
        // Ilość bajtów pozostałych do odczytu
        private int remaining;

        /**
         * Tworzy operację zapisu.
         * 
         * @param buffer
         *            Bufor zawierający dane do zapisania
         * @param attachment
         *            Dowolny obiekt, który zostanie przekazany do handlera po
         *            zapełnieniu bufora
         * @param handler
         *            Callback wywoływany po zakończeniu operacji, bądź w razie
         *            wystąpienia błędu
         */
        public WriteTask(ByteBuffer buffer, A attachment,
                CompletionHandler<Integer, A> handler) {
            this.buffer = buffer;
            this.handler = handler;
            this.attachment = attachment;
            this.remaining = buffer.remaining();
        }

        /**
         * Metoda wywoływana przy zakończeniu (etapu) operacji IO.
         * 
         * @param bytes
         *            Ilość bajtów odczytanych w tym kroku
         * @param attachment
         *            Obiekt związany z oryginalnym żądaniem
         */
        @Override
        public void completed(Integer bytes, A attachment) {
            // Uaktualniamy ilość pozostałych do odczytania bajtów
            remaining -= bytes;
            if (remaining > 0) {
                if (isOpen()) {
                    writeSome();
                } else if (handler != null) {
                    // Wiadomość nie została zapisana, socket zamknięty
                    Throwable exc = new ClosedChannelException();
                    handler.failed(exc, attachment);
                }
            } else {
                // Umożliwiamy obsługę nowych żądań zanim wywołujemy
                // handler tego zakończonego
                runNext();
                if (handler != null) {
                    handler.completed(bytes, attachment);
                }
            }
        }

        /**
         * Wywoływana w razie błędów operacji zapisu.
         * 
         * @param e
         *            Wyjątek, który spowodował błąd
         * @param attachment
         *            Obiekt związany z oryginalnym żądaniem
         */
        @Override
        public void failed(Throwable e, A attachment) {
            if (handler != null) {
                handler.failed(e, attachment);
            } // else połykamy wyjątek :(
        }

        // Inicjuje operację częściowego zapisu
        private void writeSome() {
            channel.write(buffer, attachment, this);
        }

    }

}
