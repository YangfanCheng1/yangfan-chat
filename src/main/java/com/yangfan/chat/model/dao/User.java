package com.yangfan.chat.model.dao;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Entity
@Table(
    name = "User"
//  JPA generates index for unique key column, below is redundant
//    , indexes = {
//        @Index(name ="idx", columnList = "username")
//    }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 32)
    @Size(max = 32)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    // mapped by room.users
    @ManyToMany(mappedBy = "users", targetEntity = PublicRoom.class)
    private List<Room> rooms;

    // copy constructor
    User(User user) {
        this.id = user.getId();
    }

    public User(int id) { this.id = id;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    /*  NO LONGER USED AS PRIVATE ROOMS
    @JsonView
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "DEV_USER_PRIVATE_ROOM",
            joinColumns = {@JoinColumn(name = "user_1_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_2_id")}
    )
    private Set<User> privateRooms;
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
