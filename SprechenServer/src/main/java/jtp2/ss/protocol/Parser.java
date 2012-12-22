package jtp2.ss.protocol;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

public class Parser {

    private static Map<Type, PayloadParser> parsers = new EnumMap<>(Type.class);

    static {
        parsers.put(Type.LOGIN_OK, new EmptyParser());
        parsers.put(Type.REGISTER_OK, new EmptyParser());
        parsers.put(Type.LOGOUT, new EmptyParser());
        parsers.put(Type.LOGIN, LoginMessage.PARSER);
        parsers.put(Type.LOGIN_FAIL, StringMessage.PARSER);
        parsers.put(Type.REGISTER_FAIL, StringMessage.PARSER);
    }

    public static Payload parseMessage(Type type, ByteBuffer buffer) {
        PayloadParser parser = parsers.get(type);
        return parser.parse(buffer);
    }

}
