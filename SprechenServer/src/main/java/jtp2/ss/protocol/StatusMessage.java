package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class StatusMessage implements Message {

    private String login;
    private Status status;
    private String description;
    
    public static final int FIXED_PART_LENGTH = 1 + 4 + 4;
    
    public StatusMessage(String login, Status status, String description) {
        this.login = login;
        this.status = status;
        this.description = description;
    }
    
    private static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Message parse(ByteBuffer buffer) throws InvalidFormatException {
            Status status = Status.fromByte(buffer.get());
            int nameSize = buffer.getInt();
            int descSize = buffer.getInt();
            byte[] nameBytes = new byte[nameSize];
            byte[] descBytes = new byte[descSize];
            buffer.get(nameBytes);
            buffer.get(descBytes);
            String username = Utils.decode(nameBytes);
            String description = Utils.decode(descBytes);
            return new StatusMessage(username, status, description);
        }
    };
    
    public static PayloadParser getParser() {
        return PARSER;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int length() {
        return FIXED_PART_LENGTH + Utils.encodedSize(login) + 
                Utils.encodedSize(description);
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.put(Status.toByte(status));
        byte[] descBytes = Utils.encode(description);
        byte[] nameBytes = Utils.encode(login);
        buffer.putInt(nameBytes.length);
        buffer.putInt(descBytes.length);
        buffer.put(nameBytes);
        buffer.put(descBytes);
    }

}
