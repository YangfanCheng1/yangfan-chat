package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.PrivateMessage;
import com.yangfan.chat.model.dao.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Stream;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Integer> {

    Stream<PrivateMessage> findAllByFromUser(User fromUser);

    @Query("SELECT u FROM PrivateMessage u " +
            "WHERE (u.fromUser = ?1 AND u.toUser = ?2) OR (u.fromUser = ?2 AND u.toUser = ?1) " +
            "ORDER BY u.timestamp ASC")
    List<PrivateMessage> findMessagesBySize(int id1, int id2, Pageable pageable);

    @Query("SELECT u FROM PrivateMessage u " +
            "WHERE (u.fromUser = ?1 AND u.toUser = ?2) OR (u.fromUser = ?2 AND u.toUser = ?1) " +
            "ORDER BY u.timestamp ASC")
    List<PrivateMessage> findMessages(User user1, User user2);

}
