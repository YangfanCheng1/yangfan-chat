package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.Message;
import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateRoomRepository extends JpaRepository<PrivateRoom, Integer> {

    List<Message> findByUser1AndUser2(User user1, User user2);
}
