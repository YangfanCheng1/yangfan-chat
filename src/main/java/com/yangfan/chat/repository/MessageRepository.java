package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.Message;
import com.yangfan.chat.model.dao.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByRoomOrderByTimestampAsc(Room room);
}
