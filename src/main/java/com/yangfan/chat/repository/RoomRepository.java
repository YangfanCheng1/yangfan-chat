package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface RoomRepository<T extends Room> extends JpaRepository<T, Integer> {

    Optional<T> findByRoomName(String roomName);

    /*
    @Query("SELECT r from Room r " +
            "JOIN r.users u1 JOIN r.users u2 WHERE u1.id = ?1 AND u2.id = ?2 AND r.roomName = null")
    Room findByTwoUsers(Set<User> users);
    boolean existsByRoomName(String roomName);
    */

}
