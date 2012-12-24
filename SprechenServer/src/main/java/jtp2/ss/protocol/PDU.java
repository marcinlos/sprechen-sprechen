package jtp2.ss.protocol;

import java.nio.ByteBuffer;

/**
 * Klasa reprezentująca najmniejszą jednostkę informacji wymienianej pomiędzy
 * klientem a serwerem.
 * 
 * PDU (Protocol Data Unit) składa się z dwóch części - stałej wielkości
 * nagłówka, oraz dowolnej długości treści przekazywanej wiadomości. Podczas
 * przesyłania wiadomości przez kanał komunikacji (np. internet) obiekt ten
 * zapisywany jest do bufora w postaci binarnej. Nagłówek poprzedza treść
 * wiadomości. Po drugiej stronie odczyt następuje na podstawie zawartej w
 * nagłówku wielkości, oraz typu.
 */
public class PDU {

    // Nagłówek wiadomości
    private Header header;

    // Ciało wiadomości
    private Message payload;

    /**
     * Buduje pakiet na podstawie typu i treści. Długość potrzebna do utworzenia
     * nagłówka obliczana jest przy użyciu metody {@code length} interfejsu
     * {@code Message}.
     * 
     * @param type
     *            Typ wiadomości
     * @param payload
     *            Zawartość wiadomości
     */
    public PDU(Type type, Message payload) {
        this.header = new Header(type, payload.length());
        this.payload = payload;
    }

    /**
     * Funkcja budująca pakiet na podstawie surowych danych binarnych.
     * 
     * @param buffer
     *            Bufor zawierający binarną reprezentację obiektu PDU
     * @return obiekt PDU skonstruowany z surowych danych z bufora
     * @throws InvalidFormatException
     *             gdy dane w buforze nie stanowią poprawnej reprezentacji
     *             binarnej pakietu
     */
    public static PDU fromBytes(ByteBuffer buffer)
            throws InvalidFormatException {
        // Odczytujemy nagłówek
        Header header = Header.fromBytes(buffer);
        // Na podstawie typu odczytujemy treść
        Message payload = Parser.parseMessage(header.getType(), buffer);
        PDU data = new PDU(header.getType(), payload);
        return data;
    }

    /**
     * @return nagłówek pakietu
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @return treść wiadomości
     */
    public Message getPayload() {
        return payload;
    }

    /**
     * Zapisuje do bufora binarną reprezentację obiektu
     * 
     * @param buffer
     *            Bufor, do którego zostanie zapisany obiekt. Powinien być on
     *            wielkości nie mniejszej, niż wartość zwracana przez metodę
     *            {@code  length}
     */
    public void write(ByteBuffer buffer) {
        header.write(buffer);
        payload.write(buffer);
    }

    /**
     * Oblicza długość binarnej reprezentacji obiektu - (długość nagłówka) +
     * (długość wiadomości)
     * 
     * @return wielkość (w bajtach) binarnej reprezentacji obiektu
     */
    public int length() {
        return Header.LENGTH + payload.length();
    }

    /**
     * @return typ enkapsulowanej wiadomości
     */
    public Type getType() {
        return header.getType();
    }

    /**
     * Tworzy bufor i wypełnia go swoją binarną reprezentacją. Równoważne
     * 
     * <pre>
     * <code>
     *      ByteBuffer buffer = ByteBuffer.allocate(pdu.length());
     *      pdu.write(buffer);
     * </code>
     * </pre>
     * 
     * @return Bufor zawierający binarną reprezentację obiektu
     */
    public ByteBuffer toBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        write(buffer);
        return buffer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(header.toString());
        sb.append('\n');
        sb.append(payload.toString());
        return sb.toString();
    }

}
