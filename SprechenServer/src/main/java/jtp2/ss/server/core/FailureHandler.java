package jtp2.ss.server.core;

/**
 * Interfejs reprezentujący odbiorcę informacji o błędach.
 */
public interface FailureHandler {

    /**
     * Informuje o wystąpieniu błędu
     * 
     * @param exc
     *            Wyjątek stanowiący bezpośrednią przyczynę błędu
     */
    void failed(Throwable exc);

}
