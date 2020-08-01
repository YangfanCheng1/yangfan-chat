package com.yangfan.chat.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Room")
@Inheritance
@DiscriminatorColumn(name = "room_type")
public abstract class Room {

    public Room(int id) {
        this.roomId = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    @EqualsAndHashCode.Include
    private int roomId;

    @Column(length = 32)
    private String roomName;

    // a room has many messages
    // mapped by Message.room
    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private List<Message> messages;

}
