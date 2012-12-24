package jtp2.ss.protocol;

/**
 * Stała wyliczeniowa reprezentująca typ przesyłanej wiadomości.
 */
public enum Type {
    /**
     * Wiadomość wysyłana w stałych odstępach czasowych przez klienta, by
     * poinformować serwer, że połączenie nie zostało przerwane.
     * 
     * @see EmptyMessage
     */
    KEEPALIVE,

    /**
     * Wiadomość stanowiąca żądanie zarejestrowania nowego użytkownika.
     * 
     * @see RegisterMessage
     */
    REGISTER,

    /**
     * Wiadomość zwrotna informująca klienta, że żądanie rejestracji zostało
     * przetworzone i zakończyło się sukcesem.
     * 
     * @see EmptyMessage
     */
    REGISTER_OK,

    /**
     * Wiadomość zwrotna informująca klienta, że żądanie rejestracji zostało
     * przetworzone, ale nie było możliwe jego spełnienie. Dołączony string
     * zawiera powód takiego stanu rzeczy.
     * 
     * @see StringMessage
     */
    REGISTER_FAIL,

    /**
     * Wiadomość stanowiąca próbę zalogowania się użytkownika.
     * 
     * @see LoginMessage
     */
    LOGIN,

    /**
     * Wiadomość zwrotna informująca, że logowanie zakończyło się sukcesem.
     * 
     * @see EmptyMessage
     */
    LOGIN_OK,

    /**
     * Wiadomość zwrotna informująca, że logowanie zakończyło się
     * niepowodzeniem. Dołączony string zawiera przyczynę błędu.
     * 
     * @see StringMessage
     */
    LOGIN_FAIL,

    /**
     * Informacja o wylogowaniu się użytkownika.
     * 
     * @see EmptyMessage
     */
    LOGOUT,

    /**
     * Informacja o zmianie statusu.
     * 
     * @see StatusMessage
     */
    NEW_STATUS,

    /**
     * Wiadomość zawierająca wysłaną do serwera przez użytkownika wiadomość
     * tekstową.
     * 
     * @see TextMessage
     */
    SEND_MSG,

    /**
     * Wysyłana klientowi przez serwer informacja o statusie dostarczenia
     * wiadomości przez niego wysłanej.
     * 
     * @see AckMessage
     */
    SEND_ACK,

    /**
     * Wysyłana przez serwer do klienta wiadomość zawierająca zaadresowaną do
     * niego wiadomość wysłaną przez innego użytkownika.
     * 
     * @see TextMessage
     */
    RECV_MSG,

    /**
     * Wysyłane przez klienta do serwera potwierdzenie otrzymania wiadomości.
     * 
     * @see EmptyMessage
     */
    RECV_ACK,

    /**
     * Wysyłane przez klienta żądanie przekazania statusu i opisu innego
     * użytkownika.
     * 
     * @see StringMessage
     */
    GET_STATUS,

    /**
     * Wysyłana przez serwer do klienta notyfikacja o zmianie statusu
     * obserwowanego przez niego użytkownika.
     * 
     * @see StatusMessage
     */
    NOTIFY_STATUS;

    private static final Type[] vals = Type.values();

    /**
     * Dekoduje wartość {@code Type} zapisaną jako bajt. Używane do
     * deserializacji wiadomości.
     * 
     * @param value
     *            Bajt reprezentujący typ wiadomości
     * @return obiekt {@code Type} odpowiadający wartości {@code value}
     * @throws NoSuchTypeException
     *             jeśli {@code value} nie jest reprezentacją żadnego z typów
     *             wiadomości
     * 
     * @see toByte
     */
    public static Type fromByte(byte value) throws NoSuchTypeException {
        if (value < vals.length) {
            return vals[value];
        } else {
            throw new NoSuchTypeException();
        }
    }

    /**
     * Koduje wartość {@code Type} jako bajt.
     * 
     * @param type
     *            Wartość do zakodowania
     * @return bajt reprezentujący {@code type}
     */
    public static byte toByte(Type type) {
        return (byte) type.ordinal();
    }
}