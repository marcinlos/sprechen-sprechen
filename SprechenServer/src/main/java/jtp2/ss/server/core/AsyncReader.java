package jtp2.ss.server.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Klasa pomocnicza dostarczająca funkcjonalności odczytu z socketu. Nie można
 * korzystać z niej bezpiecznie z wielu wątków (w przeciwieństwie do
 * {@link AsyncWriter}-a nie kolejkuje żądań).
 */
public class AsyncReader {

    // Przechowywany socket
    private AsynchronousSocketChannel channel;
    // Czy socket wciąż jest otwarty
    private boolean open = true;

    public AsyncReader(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    /**
     * Informuje obiekt o fakcie zamknięcia połączenia
     */
    public synchronized void close() {
        open = false;
    }

    /**
     * @return {@code true } jeśli połączenie jest otwarte, {@code false} w
     *         przeciwnym wypadku
     */
    public synchronized boolean isOpen() {
        return open;
    }

    /**
     * Inicjuje asynchroniczny odczyt.
     * 
     * @param buffer
     *            Bufor, do którego zapisane mają zostać dane
     * @param attachment
     *            Dowolny obiekt, który zostanie przekazany do handlera gdy ten
     *            zostanie wywołany
     * @param handler
     *            Callback wywoływany po zakończeniu operacji, bądź w razie
     *            wystąpienia błędu
     * 
     * @see Connection#read(ByteBuffer, Object, CompletionHandler)
     */
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

    /**
     * Inicjuje operację asynchronicznego odczytu z określonym maksymalnym
     * czasem wykonania.
     * 
     * @param buffer
     *            Bufor, do którego dane mają być zapisane
     * @param timeout
     *            Ilość jednostek czasu na timeout
     * @param unit
     *            Jednostka czasu, w której podany jest timeout
     * @param attachment
     *            Dowolny obiekt, który zostanie przekazany do handlera po
     *            zapełnieniu bufora
     * @param handler
     *            Callback wywoływany po zakończeniu operacji, bądź w razie
     *            wystąpienia błędu
     * 
     * @see Connection#read(ByteBuffer, long, TimeUnit, Object,
     *      CompletionHandler)
     */
    public <A> void asyncRead(ByteBuffer buffer, long timeout, TimeUnit unit,
            A attachment, CompletionHandler<Integer, A> handler) {
        if (isOpen()) {
            ReadAction<A> reader = new ReadAction<>(buffer, timeout, unit,
                    attachment, handler);
            reader.readSome();
        } else if (handler != null) {
            Throwable exc = new ClosedChannelException();
            handler.failed(exc, attachment);
        }
    }

    /*
     * Klasa pomocnicza, reprezenująca pojedynczą operację odczytu.
     */
    private class ReadAction<A> implements CompletionHandler<Integer, A> {

        // Bufor z danymi do zapisania
        private ByteBuffer buffer;
        // Ilość bajtów, które pozostaly do zapisania
        private int remaining;
        // Obiekt związany z operacją
        private A attachment;
        // Handler wywoływany po zakończeniu operacji
        private CompletionHandler<Integer, A> handler;
        // Ilość milisekund pozostałych do timeoutu
        private long timeoutMilis;
        // Czas (w milisekundach) ostatniego częściowego odczytu
        private long prevTime;
        // Czy żądanie posiada określony timeout
        private boolean timeout = false;

        /**
         * Tworzy operację odczytu bez timeoutu.
         * 
         * @param buffer
         *            Bufor, to którego zapisane mają być dane
         * @param attachment
         *            Dowolny obiekt, który zostanie przekazany do handlera po
         *            zapełnieniu bufora
         * @param handler
         *            Callback wywoływany po zakończeniu operacji, bądź w razie
         *            wystąpienia błędu
         */
        public ReadAction(ByteBuffer buffer, A attachment,
                CompletionHandler<Integer, A> handler) {
            this.buffer = buffer;
            this.remaining = buffer.remaining();
            this.attachment = attachment;
            this.handler = handler;
        }

        /**
         * Tworzy operację odczytu z podanym timeoutem.
         * 
         * @param buffer
         *            Bufor, do którego zapisane mają być dane
         * @param timeout
         *            Maksymalny czas oczekiwania na ukończenie operacji
         * @param unit
         *            Jednostka, w jakiej podany jest czas timeoutu
         * @param attachment
         *            Dowolny obiekt, który zostanie przekazany do handlera po
         *            zapełnieniu bufora
         * @param handler
         *            Callback wywoływany po zakończeniu operacji, bądź w razie
         *            wystąpienia błędu
         */
        public ReadAction(ByteBuffer buffer, long timeout, TimeUnit unit,
                A attachment, CompletionHandler<Integer, A> handler) {
            this(buffer, attachment, handler);
            timeoutMilis = TimeUnit.MILLISECONDS.convert(timeout, unit);
            this.timeout = true;
            this.prevTime = System.currentTimeMillis();
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
            if (timeout) {
                // Uaktualniamy dane o timeoucie
                long time = System.currentTimeMillis();
                timeoutMilis -= time - prevTime;
                prevTime = time;
            }
            // Sprawdzamy licznik pozostałych bajtów
            remaining -= bytes;
            if (remaining > 0) {
                // Pozostały dane do odczytania
                if (timeout && timeoutMilis <= 0) {
                    // Informujemy o timeoucie
                    Throwable exc = new InterruptedByTimeoutException();
                    failed(exc, attachment);
                } else if (isOpen()) {
                    readSome();
                } else {
                    // Inny błąd
                    Throwable exc = new ClosedChannelException();
                    failed(exc, attachment);
                }
            } else if (handler != null) {
                // Informujemy o zakończeniu operacji
                handler.completed(bytes, attachment);
            }
        }

        /**
         * Wywoływana w razie błędów operacji odczytu / timeoutu. W przypadku
         * błędu polegającego na przekroczeniu timeoutu, {@code e} jest
         * wyjątkiem typu {@link InterruptedByTimeoutException}.
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
            }
            // else połykamy wyjątek
        }

        // Inicjuje kolejną operację częściowego odczytu
        private void readSome() {
            if (timeout) {
                channel.read(buffer, timeoutMilis, TimeUnit.MILLISECONDS,
                        attachment, this);
            } else {
                channel.read(buffer, attachment, this);
            }
        }
    }

}
