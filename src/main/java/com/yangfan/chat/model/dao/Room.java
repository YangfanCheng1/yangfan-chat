package com.yangfan.chat.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "DevRooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomId;

    @Column(length = 32)
    private String roomName;

    // a room has many messages
    // mapped by Message.room
    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private List<Message> messages;

    // mapped by User.rooms
    @JsonIgnore
    @ManyToMany(mappedBy = "rooms")
    private Set<User> users;

    // copy constructor
    public Room(Room room) {
        this.roomId = room.getRoomId();
    }

    public Room(int id) {
        this.roomId = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
