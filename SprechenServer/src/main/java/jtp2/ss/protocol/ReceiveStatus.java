package jtp2.ss.protocol;

public enum ReceiveStatus {
    DELIVERED, 
    QUEUED,
    NOT_DELIVERED,
    NO_SUCH_USER;

    private static final ReceiveStatus[] vals = ReceiveStatus.values();

    public static ReceiveStatus fromByte(byte ord)
            throws InvalidFormatException {
        if (ord < vals.length) {
            return vals[ord];
        } else {
            throw new InvalidFormatException("Invalid receive status value");
        }
    }

    public static byte toByte(ReceiveStatus status) {
        return (byte) status.ordinal();
    }
}
