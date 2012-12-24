package jtp2.ss.protocol;

import java.nio.charset.StandardCharsets;

/**
 * Klasa pomocnicza, zawierająca metody służące do serializacji/deserializacji
 * stringów.
 */
public class Utils {

    /**
     * Funkcja kodująca string do formatu UTF-8
     * 
     * @param string
     *            String do zakodowania
     * @return Tablica bajtów zawierająca reprezentację stringa w UTF-8, bądź
     *         pusta tablica, jeśli {@code string} ma wartość {@code null}
     */
    public static byte[] encode(String string) {
        if (string != null) {
            return string.getBytes(StandardCharsets.UTF_8);
        } else {
            return new byte[0];
        }
    }

    /**
     * Funkcja dekodująca ciąg bajtów do wewnętrznego formatu javy
     * 
     * @param bytes
     *            Ciąg bajtów reprezentujący string w kodowaniu UTF-8
     * @return {@code String} utworzony na jego podstawie
     */
    public static String decode(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Funkcja obliczająca długość reprezentacji stringa zakodowanego w UTF-8
     * 
     * @param string
     *            String, którego długość chcemy poznać
     * @return ilość bajtów potrzebna do reprezentowania {@code string}-a w
     *         kodowaniu UTF-8
     */
    public static int encodedSize(String string) {
        return encode(string).length;
    }
}
