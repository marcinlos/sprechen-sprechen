package jtp2.ss.protocol;

/**
 * Stała wyliczeniowa reprezentująca status użytkownika.
 */
public enum Status {

    /** Użytkownik jest niedostępny */
    NOT_AVAILABLE,

    /** Użytkownik jest dostępny */
    AVAILABLE;

    private static final Status[] vals = Status.values();

    /**
     * Dekoduje wartość statusu zapisaną jako bajt. Używane do deserializacji
     * wiadomości.
     * 
     * @param value
     *            Bajt reprezentujący status
     * @return obiekt {@code Status} reprezentowany przez {@code value}
     * @throws InvalidFormatException
     *             jeśli {@code value} nie reprezentuje żadnego ze statusów
     */
    public static Status fromByte(byte value) throws InvalidFormatException {
        if (value < vals.length) {
            return vals[value];
        } else {
            throw new InvalidFormatException("Invalid status value");
        }
    }

    /**
     * Koduje wartość {@code Status} jako bajt.
     * 
     * @param status
     *            Wartość do zakodowania
     * @return bajt reprezentujący {@code status}
     */
    public static byte toByte(Status status) {
        return (byte) status.ordinal();
    }
}