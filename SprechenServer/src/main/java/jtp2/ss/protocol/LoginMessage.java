package jtp2.ss.protocol;

import java.nio.ByteBuffer;

public class LoginMessage implements Payload {

    private String login;
    private long passwordHash;
    private Status initialStatus;
    private String description;
    
    public static final int FIXED_PART_LENGTH = 8 + 4 + 4 + 1;

    public LoginMessage(String login, long passwordHash, Status initialStatus,
            String description) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.initialStatus = initialStatus;
        this.description = description;
    }

    public static final PayloadParser PARSER = new PayloadParser() {
        @Override
        public Payload parse(ByteBuffer buffer) {
            // | pwd_hash | login_size | desc_size | login ... | desc ... |
            long hash = buffer.getLong();
            int loginSize = buffer.getInt();
            int descSize = buffer.getInt();
            Status status = Status.fromByte(buffer.get());
            
            byte[] loginBytes = new byte[loginSize];
            buffer.get(loginBytes, 0, loginSize);
            String login = Utils.decode(loginBytes);
            
            String desc = null;
            if (descSize > 0) {
                byte[] descBytes = new byte[descSize];
                buffer.get(descBytes, 0, descSize);
                desc = Utils.decode(descBytes); 
            }
            return new LoginMessage(login, hash, status, desc);
        }
    };

    @Override
    public int length() {
        int loginLength = Utils.encodedSize(login);
        int descLength = Utils.encodedSize(description);
        return FIXED_PART_LENGTH + loginLength + descLength;
    }

    @Override
    public void write(ByteBuffer buffer) {
        /*
         * Format:
         * | pwd_hash | login_size | desc_size | status | login ... | desc ... |
         * 
         * [8] pwd_hash   - hash of a password
         * [4] login_size - size in bytes of utf-8-encoded login
         * [4] desc_size  - size in bytes of utf-8-encoded status description
         * [1] status     - initial user status
         * [?] login      - utf-8-encoded user login
         * [?] desc       - utf-8-encoded status description 
         */
        buffer.putLong(passwordHash);
        byte[] loginBytes = Utils.encode(login);
        byte[] descBytes = Utils.encode(description);
        buffer.putInt(loginBytes.length);
        buffer.putInt(descBytes.length);
        buffer.put(Status.toByte(initialStatus));
        buffer.put(loginBytes);
        buffer.put(descBytes);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Login: '").append(this.login).append("'\n")
                .append("Password hash: ").append(passwordHash);
        return sb.toString();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(long passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Status getInitialStatus() {
        return initialStatus;
    }

    public void setInitialStatus(Status initialStatus) {
        this.initialStatus = initialStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
