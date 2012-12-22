package jtp2.ss.protocol;

import java.nio.charset.StandardCharsets;

public class Utils {

    public static byte[] encode(String string) {
        if (string != null) {
            return string.getBytes(StandardCharsets.UTF_8);
        } else {
            return new byte[0];
        }
    }
    
    public static String decode(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    public static int encodedSize(String string) {
        return encode(string).length;
    }
}
