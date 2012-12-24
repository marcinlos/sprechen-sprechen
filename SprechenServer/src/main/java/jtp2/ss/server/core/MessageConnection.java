package jtp2.ss.server.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import jtp2.ss.protocol.PDU;
import jtp2.ss.protocol.Header;
import jtp2.ss.protocol.InvalidFormatException;
import jtp2.ss.protocol.MessageRecipient;

/**
 * Klasa reprezentująca połączenie na poziomie przesyłania komunikatów w postaci
 * {@link PDU}. Wykorzystuje obiekt {@link Connection} jako medium do
 * przesyłania danych binarnych. Nad tą wartstwą implementuje logikę wysylania i
 * odbierania pełnych wiadomości.
 */
public class MessageConnection {

    // Instancja {@link Connection} wykorzystywany do transportu danych
    private Connection connection;
    // Odbiorca komunikatow
    private MessageRecipient protocol;
    // Pomocniczy obiekt implementujący logikę składania wiadomości z bajtów
    private MessageReader reader;
    // Odbiorca informacji o błędach
    private FailureHandler failureHandler;
    // Pomocniczy adapter na {@code failureHandler}
    private CompletionHandler<Integer, Void> adaptedFailureHandler;

    /**
     * Twozy nowe połączenie z podanych obiektów składowych.
     * 
     * @param connection
     *            Obiekt zapewniający transport strumienia bajtów
     * @param protocol
     *            Odbiorca komunikatów
     * @param handler
     *            Odbiorca informacji o błędach
     */
    public MessageConnection(Connection connection, MessageRecipient protocol,
            FailureHandler handler) {
        this.connection = connection;
        this.protocol = protocol;
        this.reader = new MessageReader();
        this.failureHandler = handler;
        this.adaptedFailureHandler = new FailureHandlerAdapter(handler);
    }

    /**
     * Metoda przygotowująca połączenie bajtowe do pracy
     */
    public void beginCommunication() {
        reader.readNext();
    }

    /**
     * Zamyka połączenie
     * 
     * @throws IOException
     *             jeśli wystąpią błędy podczas zamykania połączenia bajtowego
     */
    public void close() throws IOException {
        connection.close();
    }

    /**
     * Wysyła na drugą stronę połączenia wiadomość
     * 
     * @param message
     *            Wiadomość, która ma zostać przesłana
     */
    public void sendMessage(PDU message) {
        ByteBuffer buffer = message.toBuffer();
        buffer.flip();
        connection.write(buffer, null, adaptedFailureHandler);
    }

    /*
     * Pomocniczy enum określający stan automatu odczytującego wiadomość
     */
    private enum ReadingState {
        READ_HEADER, READ_BODY
    }

    /*
     * Klasa pomocnicza, opakowująca implementację FailureHandler-a tak,
     * by można z niego korzystać w wywołaniach metod java.nio, które
     * wymagają CompletionHandler-a.
     */
    private static class FailureHandlerAdapter implements
            CompletionHandler<Integer, Void> {

        // Opakowywany handler
        private FailureHandler handler;

        public FailureHandlerAdapter(FailureHandler handler) {
            this.handler = handler;
        }

        @Override
        public void completed(Integer result, Void attachment) {
            // Puste ciało
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            if (handler != null) {
                handler.failed(exc);
            }
        }

    }

    /*
     * Klasa pomocnicza implementująca logikę odczytywania wiadomości i
     * składania ich z reprezentacji binarnych. Jest to automat skończony,
     * działający według prostego algorytmu: 
     * <ol> 
     * <li>Odczytaj {@code Header.LENGTH} bajtów nagłówka</li> 
     * <li>Wydobądź z niego długość treści</li> 
     * <li>Odczytaj ilość bajtów znalezioną w kroku drugim</li>
     * <li>Zbuduj wiadomość z odczytanych danych, i przekaż ją wyżej</li> 
     * </ol>
     */
    private class MessageReader implements CompletionHandler<Integer, Void> {

        // Bufor na nagłówek
        private ByteBuffer headerBuffer = ByteBuffer.allocate(Header.LENGTH);
        // Bufor na ciało wiadomości
        private ByteBuffer buffer;
        // Stan automatu
        private ReadingState state = ReadingState.READ_HEADER;

        // Rozpoczyna odczytywanie kolejnej wiadomości - czyści bufor nagłówka,
        // przechodzi w stan READ_HEADER
        public void readNext() {
            headerBuffer.clear();
            state = ReadingState.READ_HEADER;
            connection.read(headerBuffer, null, this);
        }

        /**
         * Metoda wywoływana po zakończeniu operacji odczytu.
         * 
         * @param bytes
         *            Ilość bajtów odczytanych w tym kroku
         * @param attachment
         *            Nieużywany w tej implementacji
         */
        @Override
        public void completed(Integer result, Void attachment) {
            try {
                if (state == ReadingState.READ_HEADER) {
                    // Dwukrotnie odczytujemy nagłówek, stąd 2x flip
                    headerBuffer.flip();
                    Header header = Header.fromBytes(headerBuffer);
                    headerBuffer.flip();
                    // Tworzymy bufor na całą wiadomość
                    int size = Header.LENGTH + header.getLength();
                    buffer = ByteBuffer.allocate(size);
                    buffer.put(headerBuffer);
                    // Przechodzimy do odczytywania treści
                    state = ReadingState.READ_BODY;
                    connection.read(buffer, null, this);
                } else {
                    // Tworzymy PDU, przekazujemy wyżej
                    buffer.flip();
                    PDU message = PDU.fromBytes(buffer);
                    protocol.gotMessage(message);
                    readNext();
                }
            } catch (InvalidFormatException e) {
                // Informujemy handlera o problemie
                if (failureHandler != null) {
                    failureHandler.failed(e);
                }
            }
        }

        /**
         * Wywoływana w przypadku błędów operacji wejścia/wyjścia
         */
        @Override
        public void failed(Throwable exc, Void attachment) {
            if (failureHandler != null) {
                failureHandler.failed(exc);
            }
        }

    }

}
