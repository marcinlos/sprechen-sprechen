package jtp2.ss.server.core;

import jtp2.ss.server.persistence.MemoryPersistenceManager;
import jtp2.ss.server.persistence.PersistenceManager;

import org.apache.log4j.Logger;

public class Application {

    private static final Logger logger = Logger.getLogger(Application.class);
    
    public static void main(String[] args) {
        Server server = null;
        try {
            IOManager io = new IOManager(2);
            PersistenceManager persistence = new MemoryPersistenceManager();
            server = new Server(io, persistence);
            server.run(6666);
            server.waitForShutdown();
        } catch (Exception e) {
            logger.error("Fatal error", e);
        } finally {
            if (server != null) {
                server.cleanup();
            }
        }
    }
    
}
