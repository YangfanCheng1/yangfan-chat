package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrivateRoomRepository extends RoomRepository<PrivateRoom> {

    @Query("SELECT u FROM PrivateRoom u " +
            "WHERE (u.user1 = ?1 AND u.user2 = ?2) OR (u.user1 = ?2 AND u.user2 = ?1)"
    )
    PrivateRoom findByTwoUsers(User user1, User user2);

    List<PrivateRoom> findByUser1OrUser2(User user1, User user2);

    default List<PrivateRoom> findByUser(User user) {
        return findByUser1OrUser2(user, user);
    }
}
