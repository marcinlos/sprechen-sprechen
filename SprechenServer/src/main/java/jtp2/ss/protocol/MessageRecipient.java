package jtp2.ss.protocol;

/**
 * Interfejs reprezentujący byt zdolny do odbierania wiadomości.
 */
public interface MessageRecipient {

    /**
     * Informuje obiekt o zaadresowanej do niego wiadomości
     * 
     * @param message
     *            Wiadomość, którą należy dostarczyć
     */
    void gotMessage(PDU message);

}
