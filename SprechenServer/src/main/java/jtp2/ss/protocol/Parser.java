package jtp2.ss.protocol;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

/**
 * Klasa pomocnicza, umożliwiająca odczytywanie treści wiadomości zapisanych w
 * postaci binarnej na podstawie ich znanego typu. Wykorzystuje funkcje to
 * realizujące zdefiniowane w samych typach, zawiera jedynie logikę wybierającą
 * odpowiednią funkcję.
 */
public class Parser {

    // Odwzorowanie typu na funkcję odczytującą
    private static Map<Type, PayloadParser> parsers = new EnumMap<>(Type.class);

    static {
        // Umieszczamy w mapie parsery do wszystkich typów wiadomości
        parsers.put(Type.KEEPALIVE, EmptyMessage.getParser());
        parsers.put(Type.REGISTER, RegisterMessage.getParser());
        parsers.put(Type.REGISTER_OK, EmptyMessage.getParser());
        parsers.put(Type.REGISTER_FAIL, StringMessage.getParser());
        parsers.put(Type.LOGIN, LoginMessage.getParser());
        parsers.put(Type.LOGIN_OK, EmptyMessage.getParser());
        parsers.put(Type.LOGIN_FAIL, StringMessage.getParser());
        parsers.put(Type.LOGOUT, EmptyMessage.getParser());
        parsers.put(Type.NEW_STATUS, StatusMessage.getParser());
        parsers.put(Type.NOTIFY_STATUS, StatusMessage.getParser());
        parsers.put(Type.GET_STATUS, StringMessage.getParser());
        parsers.put(Type.SEND_MSG, TextMessage.getParser());
        parsers.put(Type.SEND_ACK, AckMessage.getParser());
        parsers.put(Type.RECV_MSG, TextMessage.getParser());
        parsers.put(Type.RECV_ACK, AckMessage.getParser());
    }

    /**
     * Odczytuje zapisaną binarnie wiadomość.
     * 
     * @param type
     *            Typ wiadomości - znany skądinąd
     * @param buffer
     *            Bufor zawierający binarną reprezentację wiadomości
     * @return wiadomość reprezentowana przez dane w buforze
     * @throws InvalidFormatException
     *             jeśli dane w buforze nie stanowiły poprawnej reprezentacji
     *             binarnej wiadomości typu {@code type}
     */
    public static Message parseMessage(Type type, ByteBuffer buffer)
            throws InvalidFormatException {
        PayloadParser parser = parsers.get(type);
        if (parser != null) {
            return parser.parse(buffer);
        } else {

            return null;
        }
    }

}
