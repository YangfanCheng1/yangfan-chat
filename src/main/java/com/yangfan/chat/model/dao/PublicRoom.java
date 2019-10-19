package com.yangfan.chat.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@DiscriminatorValue("public")
@NoArgsConstructor
public class PublicRoom extends Room {

    public PublicRoom(int id) {
        super(id);
    }

    // Owner side
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "Room_User",
            joinColumns = { @JoinColumn(name = "room_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> users = new HashSet<>();

    public static PublicRoom buildFromName(String name) {
        PublicRoom room = new PublicRoom();
        room.setRoomName(name);
        return room;
    }
}
