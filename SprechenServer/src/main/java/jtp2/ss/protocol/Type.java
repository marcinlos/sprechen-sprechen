package jtp2.ss.protocol;

public enum Type {
    REGISTER,
    REGISTER_OK,
    REGISTER_FAIL,
    LOGIN, 
    LOGIN_OK,
    LOGIN_FAIL, 
    LOGOUT, 
    NEW_STATUS,
    SEND_MSG,
    SEND_ACK,
    RECV_MSG,
    GET_STATUS,
    NOTIFY_STATUS;

    private static final Type[] vals = Type.values();

    public static Type fromByte(byte ord) throws NoSuchTypeException {
        if (ord < vals.length) {
            return vals[ord];
        } else {
            throw new NoSuchTypeException();
        }
    }

    public static byte toByte(Type type) {
        return (byte) type.ordinal();
    }
}