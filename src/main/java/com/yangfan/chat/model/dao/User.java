package com.yangfan.chat.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public User(int id) {
        this.id = id;
    }

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
