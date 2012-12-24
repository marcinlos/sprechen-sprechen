package jtp2.ss.protocol;

import java.nio.ByteBuffer;

/**
 * Klasa reprezentująca nagłówek wiadomości. Zawiera pola określające długość
 * części wiadomości zawierającej treść właściwą, oraz typ wiadomości.
 */
public class Header {

    /**
     * Stała długość binarnej reprezentacji nagłówka
     */
    public final static int LENGTH = 5;

    // Typ wiadomości
    private Type type;

    // Wielkość pola danych
    private int length;

    public Header(Type type, int length) {
        this.type = type;
        this.length = length;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Zapisuje nagłówek w postaci binarnej do podanego bufora
     * 
     * @param buffer
     *            Bufor, do którego zapisany zostanie nagłówek
     */
    public void write(ByteBuffer buffer) {
        buffer.put(Type.toByte(type));
        buffer.putInt(length);
    }

    /**
     * Buduje nagłówek z surowych danych binarnych
     * 
     * @param buffer
     *            Bufor zawierający binarną reprezentację nagłówka
     * @return nagłówek zbudowany na podstawie danych z bufora
     * @throws NoSuchTypeException
     *             jeśli odczytane pole typu jest nieprawidłowe (wartość nie
     *             odpowiada żadnej z tych stałych)
     */
    public static Header fromBytes(ByteBuffer buffer)
            throws NoSuchTypeException {
        Type type = Type.fromByte(buffer.get());
        int length = buffer.getInt();
        return new Header(type, length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.valueOf(type));
        sb.append(" (");
        sb.append(length);
        sb.append(" bytes)");
        return String.valueOf(type);
    }

}
