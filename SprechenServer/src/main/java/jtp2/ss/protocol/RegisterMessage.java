package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class RegisterMessage implements Payload {

    private String login;
    private long passwordHash;
    
    public static final int FIXED_PART_LENGTH = 8 + 4;

    public RegisterMessage(String login, long passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Payload parse(ByteBuffer buffer) {
            long hash = buffer.getLong();
            int length = buffer.getInt();
            byte[] loginBytes = new byte[length];
            buffer.get(loginBytes, 0, length);
            String login = Utils.decode(loginBytes);
            return new RegisterMessage(login, hash);
        }
    };

    public String getLogin() {
        return login;
    }

    public long getPasswordHash() {
        return passwordHash;
    }

    @Override
    public int length() {
        return FIXED_PART_LENGTH + Utils.encodedSize(login);
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.putLong(passwordHash);
        byte[] content = Utils.encode(login);
        buffer.putInt(content.length);
        buffer.put(content);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Login: '").append(this.login).append("'\n")
                .append("Password hash: ").append(passwordHash);
        return sb.toString();
    }

}