package jtp2.ss.textclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import jtp2.ss.protocol.PDU;
import jtp2.ss.protocol.MessageRecipient;
import jtp2.ss.server.core.AsyncConnection;
import jtp2.ss.server.core.Connection;
import jtp2.ss.server.core.FailureHandler;
import jtp2.ss.server.core.MessageConnection;

public class TestSprechenClient implements MessageRecipient, FailureHandler {

    private AsynchronousSocketChannel socket;
    private MessageConnection connection;
    private Interpreter interpreter;
    
    public TestSprechenClient() throws Exception {
        socket = AsynchronousSocketChannel.open();
        SocketAddress addr = new InetSocketAddress("127.0.0.1", 6666);
        socket.connect(addr).get();
        Connection con = new AsyncConnection(socket);
        connection = new MessageConnection(con, this, this);
        connection.beginCommunication();
        interpreter = new Interpreter(connection);
    }
    
    public void run() throws IOException {
        BufferedReader reader = 
                new BufferedReader(new InputStreamReader(System.in));
        
        String line = null;
        while ((line = reader.readLine()) != null) {
            interpreter.interpret(line);
        }
        connection.close();
    }

    public static void main(String[] args) {
        try {
            TestSprechenClient client = new TestSprechenClient();
            client.run();
        } catch (Exception e) {
            System.out.println("Fatal error");
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void failed(Throwable exc) {
        System.err.println("Error");
        exc.printStackTrace(System.err);
    }

    @Override
    public void gotMessage(PDU message) {
        System.out.println("Response:");
        System.out.println(message);
    }

}
