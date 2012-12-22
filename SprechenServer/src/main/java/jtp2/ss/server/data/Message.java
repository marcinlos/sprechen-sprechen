package jtp2.ss.server.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@Entity
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User source;

    @OneToOne
    private User destination;

    @Column
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSource() {
        return source;
    }

    public void setSource(User source) {
        this.source = source;
    }

    public User getDestination() {
        return destination;
    }

    public void setDestination(User destination) {
        this.destination = destination;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
