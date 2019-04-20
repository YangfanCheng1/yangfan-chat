package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByRoomName(String roomName);

    boolean existsByRoomName(String roomName);
}
