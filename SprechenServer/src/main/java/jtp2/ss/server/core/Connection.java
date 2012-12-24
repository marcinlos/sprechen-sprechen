package jtp2.ss.server.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * Interfejs połączenia umożliwiającego asynchroniczny odczyt i zapis
 * dowolnych danych binarnych. Obsługuje również timeout. Stanowi w większości
 * wrapper klasy {@link AsynchronousSocketChannel} z jdk7, której brakuje
 * możliwości odczytania/zapisania żądanej liczby bajtów (trzeba zapewnić
 * to ręcznie, tamta klasa nie gwarantuje zapisania całości żądanych
 * danych w jednym wywołaniu)
 */
public interface Connection {

    /**
     * Inicjuje asynchroniczny zapis danych do przechowywanego socketu
     * 
     * @param buffer Bufor zawierający dane do zapisania
     * @param attachment Dowolny obiekt, który zostanie przekazany do handlera
     * gdy ten zostanie wywołany
     * @param handler Callback wywoływany po zakończeniu operacji, bądź w razie
     * wystąpienia błędu
     */
    <A> void write(ByteBuffer buffer, A attachment, 
            CompletionHandler<Integer, A> handler);
    
    /**
     * Inicjuje asynchroniczny odczyt żądanej ilości danych z przechowywanego
     * socketu. Callback wywoływany jest dopiero po odczytaniu całej
     * ilości bajtów. Ilość bajtów wyznaczana jest na podstawie bufora - jest
     * taka, że bufor zostanie zapełniony w całości.
     * 
     * @param buffer Bufor, do którego dane mają być zapisane. 
     * @param attachment Dowolny obiekt, który zostanie przekazany do handlera
     * po zapełnieniu bufora
     * @param handler Callback wywoływany po zakończeniu operacji, bądź w razie
     * wystąpienia błędu
     */
    <A> void read(ByteBuffer buffer, A attachment,
            CompletionHandler<Integer, A> handler);
    
    /**
     * Inicjuje asynchroniczny odczyt żądanej ilości bajtów w czasie nie 
     * przekraczającym podanego timeoutu. Jeśli czas ten zostanie przekroczony,
     * operacja zawiedzie, i wywołana zostanie metoda {@code failed} handlera.
     * 
     * @param buffer Bufor, do którego dane mają być zapisane
     * @param timeout Ilość jednostek czasu na timeout
     * @param unit Jednostka czasu, w której podany jest timeout
     * @param attachment Dowolny obiekt, który zostanie przekazany do handlera
     * po zapełnieniu bufora
     * @param handler Callback wywoływany po zakończeniu operacji, bądź w razie
     * wystąpienia błędu
     */
    <A> void read(ByteBuffer buffer, long timeout, TimeUnit unit, 
            A attachment, CompletionHandler<Integer, A> handler);
    
    /**
     * Zamyka natychmiast przechowywany socket.
     * 
     * @throws IOException jeśli wystąpi błąd przy przerywaniu połączenia
     */
    void close() throws IOException;
    
    /**
     * @return adres lokalnego końca połączenia
     * @throws IOException jeśli pobieranie adresu napotka przeszkody
     */
    SocketAddress getLocalAddress() throws IOException;
    
    /**
     * @return adres przeciwnego końca połączenia
     * @throws IOException jeśli pobieranie adresu napotka przeszkody
     */
    SocketAddress getRemoteAddress() throws IOException;

    
}
