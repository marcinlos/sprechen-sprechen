package jtp2.ss.protocol;

/**
 * Stała wyliczeniowa reprezentująca efekt wysłania wiadomości do innego
 * użytkownika.
 */
public enum ReceiveStatus {
    /** Wiadomość została poprawnie dostarczona */
    DELIVERED,
    /** Wiadomość została zakolejkowana */
    QUEUED,
    /** Wiadomość nie zostałą dostarczona */
    NOT_DELIVERED,
    /** Użytkownik stanowiący adresata nie istnieje */
    NO_SUCH_USER;

    private static final ReceiveStatus[] vals = ReceiveStatus.values();

    /**
     * Dekoduje wartość {@code ReceiveStatus} zapisaną jako bajt. Używane do
     * deserializacji wiadomości.
     * 
     * @param value
     *            Bajt reprezentujący status wysłanej wiadomości
     * @return obiekt {@code ReceiveStatus} odpowiadający wartości {@code value}
     * @throws NoSuchTypeException
     *             jeśli {@code value} nie jest reprezentacją żadnego ze
     *             statusów wiadomości
     * 
     * @see toByte
     */
    public static ReceiveStatus fromByte(byte value)
            throws InvalidFormatException {
        if (value < vals.length) {
            return vals[value];
        } else {
            throw new InvalidFormatException("Invalid receive status value");
        }
    }

    /**
     * Koduje wartość {@code ReceiveStatus} jako bajt.
     * 
     * @param status
     *            Wartość do zakodowania
     * @return bajt reprezentujący {@code status}
     */
    public static byte toByte(ReceiveStatus status) {
        return (byte) status.ordinal();
    }
}
