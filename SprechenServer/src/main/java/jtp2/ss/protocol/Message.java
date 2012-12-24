package jtp2.ss.protocol;

import java.nio.ByteBuffer;

/**
 * Interfejs klas służących do przekazywania wiadomości pomiędzy serwerem, a
 * klientami.
 * 
 * Zawiera metody wykorzystywany jest przez {@code PDU} do zapisywania instancji
 * tych klas do tablicy bajtów.
 */
public interface Message {

    /**
     * Oblicza długość bajtowej reprezentacji wiadomości
     * 
     * @return Wielkość (w bajtach) bufora potrzebnego na zapisanie tej
     *         wiadomości
     */
    int length();

    /**
     * Zapisuje wiadomość do podanego bufora. Powinien on być wielkości co
     * najmniej takiej, jak wartośc zwracana przez {@code length}
     * 
     * @param buffer
     *            Bufor, do którego zapisać chcemy zawartość wiadomości
     */
    void write(ByteBuffer buffer);

}
