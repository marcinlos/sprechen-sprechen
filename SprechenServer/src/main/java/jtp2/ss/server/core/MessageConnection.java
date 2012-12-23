package jtp2.ss.server.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import jtp2.ss.protocol.PDU;
import jtp2.ss.protocol.Header;
import jtp2.ss.protocol.InvalidFormatException;
import jtp2.ss.protocol.MessageRecipient;

public class MessageConnection {

    private Connection connection;
    private MessageRecipient protocol;
    private MessageReader reader;
    private FailureHandler failureHandler;
    private CompletionHandler<Integer, Void> adaptedFailureHandler;

    public MessageConnection(Connection connection, MessageRecipient protocol,
            FailureHandler handler) {
        this.connection = connection;
        this.protocol = protocol;
        this.reader = new MessageReader();
        this.failureHandler = handler;
        this.adaptedFailureHandler = new FailureHandlerAdapter(handler);
    }

    public void beginCommunication() {
        reader.readNext();
    }
    
    public void close() throws IOException {
        connection.close();
    }

    public void sendMessage(PDU message) {
        ByteBuffer buffer = message.toBuffer();
        buffer.flip();
        connection.write(buffer, null, adaptedFailureHandler);
    }

    private enum ReadingState {
        READ_HEADER, READ_BODY
    }

    private static class FailureHandlerAdapter implements
            CompletionHandler<Integer, Void> {
        
        private FailureHandler handler;
        
        public FailureHandlerAdapter(FailureHandler handler) {
            this.handler = handler;
        }

        @Override
        public void completed(Integer result, Void attachment) {
            // empty
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            if (handler != null) {
                handler.failed(exc);
            }
        }

    }

    private class MessageReader implements CompletionHandler<Integer, Void> {

        private ByteBuffer headerBuffer = ByteBuffer.allocate(Header.LENGTH);
        private ByteBuffer buffer;
        private ReadingState state = ReadingState.READ_HEADER;

        public void readNext() {
            headerBuffer.clear();
            state = ReadingState.READ_HEADER;
            connection.read(headerBuffer, null, this);
        }

        @Override
        public void completed(Integer result, Void attachment) {
            try {
                if (state == ReadingState.READ_HEADER) {
                    headerBuffer.flip();
                    Header header = Header.fromBytes(headerBuffer);
                    headerBuffer.flip();
                    int size = Header.LENGTH + header.getLength();
                    buffer = ByteBuffer.allocate(size);
                    buffer.put(headerBuffer);
                    state = ReadingState.READ_BODY;
                    connection.read(buffer, null, this);
                } else {
                    buffer.flip();
                    PDU message = PDU.fromBytes(buffer);
                    protocol.sendMessage(message);
                    readNext();
                }
            } catch (InvalidFormatException e) {
                if (failureHandler != null) {
                    failureHandler.failed(e);
                }
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            if (failureHandler != null) {
                failureHandler.failed(exc);
            }
        }

    }

}
