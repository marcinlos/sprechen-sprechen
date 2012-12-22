package jtp2.ss.server.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @Column
    private String login;

    @Column
    private long passwordHash;    

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
    

}
