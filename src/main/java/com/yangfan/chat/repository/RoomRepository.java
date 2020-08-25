package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface RoomRepository<T extends Room> extends JpaRepository<T, Integer> {

    Optional<T> findByRoomName(String roomName);

}
