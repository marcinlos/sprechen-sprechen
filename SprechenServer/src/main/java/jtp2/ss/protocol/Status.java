package jtp2.ss.protocol;

public enum Status {
    NOT_AVAILABLE,
    NOT_AVAILABLE_DESC,
    AVAILABLE,
    AVAILABLE_DESC;

    private static final Status[] vals = Status.values();

    public static Status fromByte(byte ord) {
        if (ord < vals.length) {
            return vals[ord];
        } else {
            throw new InvalidFormatException("Invalid status value");
        }
    }

    public static byte toByte(Status status) {
        return (byte) status.ordinal();
    }
}