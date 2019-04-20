package com.yangfan.chat.model.dao;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Entity
@Table(
    name = "DevUsers"
//  JPA generates index for unique key column, below is redundant
//    , indexes = {
//        @Index(name ="idx", columnList = "username")
//    }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(unique = true, nullable = false, length = 32)
    @Size(max = 32)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "Dev_User_Room",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "room_id") }
    )
    private List<Room> rooms;


    @JsonView
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "DEV_USER_PRIVATE_ROOM",
            joinColumns = {@JoinColumn(name = "user_1_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_2_id")}
    )
    private Set<User> privateRooms;

    // copy constructor
    User(User user) {
        this.userId = user.getUserId();
    }

    public User(int id) { this.userId = id;}


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Set<User> getPrivateRooms() {
        return privateRooms;
    }

    public void setPrivateRooms(Set<User> privateRooms) {
        this.privateRooms = privateRooms;
    }
}
