package jtp2.ss.protocol;

public enum Status {
    NOT_AVAILABLE,
    AVAILABLE;

    private static final Status[] vals = Status.values();

    public static Status fromByte(byte ord) throws InvalidFormatException {
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