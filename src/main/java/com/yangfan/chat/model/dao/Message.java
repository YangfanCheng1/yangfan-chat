package com.yangfan.chat.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DevMessages")
public class Message implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
    @Column(length = 510, columnDefinition = "text")
    private String message;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "timestamp")
    private Instant timestamp;

    // copy constructor
    public Message(Message message) {
        this.message = message.getMessage();
        this.room = new Room(message.getRoom());
        this.user = new User(message.getUser());
    }

    // deep copy
    @Override
    public Message clone() throws CloneNotSupportedException {
        Message clone = (Message) super.clone();

        Room room = new Room();
        room.setRoomId(this.room.getRoomId());
        User user = new User();
        user.setUserId(this.user.getUserId());

        clone.setMessage(this.message);
        clone.setRoom(room);
        clone.setUser(user);
        return clone;
    }
}
