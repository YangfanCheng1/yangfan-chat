package com.yangfan.chat.service;

import com.yangfan.chat.exception.DuplicatePrivateRoomException;
import com.yangfan.chat.exception.DuplicateRoomException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.ActiveRoomDto;
import com.yangfan.chat.repository.RoomRepository;
import com.yangfan.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepo;
    private final UserRepository userRepo;
    public RoomService(RoomRepository roomRepo, UserRepository userRepo) {
        this.roomRepo = roomRepo;
        this.userRepo = userRepo;
    }

    public boolean addNewPrivateRoom(ActiveRoomDto activeRoomDto) throws UserNotFoundException, DuplicatePrivateRoomException {
        // Validation before room creation
        User fromUser = userRepo.findById(activeRoomDto.getFromUserId()).orElseThrow(UserNotFoundException::new);
        User toUser = userRepo.findById(activeRoomDto.getToUserId()).orElseThrow(UserNotFoundException::new);

        Set<User> privateRooms1 = fromUser.getPrivateRooms();
        Set<User> privateRooms2 = toUser.getPrivateRooms();
        if (!privateRooms1.isEmpty() && privateRooms1.contains(toUser)) {
            throw new DuplicatePrivateRoomException(
                String.format("Private room exists for %s and %s!", fromUser.getUsername(), toUser.getUsername())
            );
        }

        privateRooms1.add(toUser);
        fromUser.setPrivateRooms(privateRooms1);
        User user1 = userRepo.save(fromUser);
        privateRooms2.add(fromUser);
        toUser.setPrivateRooms(privateRooms2);
        User user2 = userRepo.save(toUser);


        return user1 != null && user2 != null;
    }

    // TODO
    public boolean addNewRoom(ActiveRoomDto activeRoomDto) throws DuplicateRoomException {
        String roomName = activeRoomDto.getRoomName();
        if (roomRepo.existsByRoomName(roomName)) {
            throw new DuplicateRoomException(roomName);
        }

        Room room = new Room();
        room.setRoomName(roomName);
        Room roomDao = roomRepo.save(room);

        return roomDao != null;
    }
}
