package jtp2.ss.protocol;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

public class Parser {

    private static Map<Type, PayloadParser> parsers = new EnumMap<>(Type.class);

    static {
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
    }

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
