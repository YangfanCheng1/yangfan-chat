package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.PublicRoom;
import com.yangfan.chat.model.dao.User;

import java.util.Collection;

public interface PublicRoomRepository extends RoomRepository<PublicRoom> {
    PublicRoom findByUsersIn(Collection<User> users);
}
