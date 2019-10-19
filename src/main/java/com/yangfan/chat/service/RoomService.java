package com.yangfan.chat.service;

import com.yangfan.chat.exception.DuplicatePrivateRoomException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.request.PrivateRoomRegistration;
import com.yangfan.chat.repository.PrivateRoomRepository;
import com.yangfan.chat.repository.RoomRepository;
import com.yangfan.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class RoomService {

    private final PrivateRoomRepository privateRoomRepository;
    private final UserRepository userRepo;
    public RoomService(PrivateRoomRepository privateRoomRepository, UserRepository userRepo) {
        this.privateRoomRepository = privateRoomRepository;
        this.userRepo = userRepo;
    }

    public Room addRoom(PrivateRoomRegistration privateRoomRegistration) throws UserNotFoundException, DuplicatePrivateRoomException {
        // Validation before room creation
        User fromUser = userRepo.findById(privateRoomRegistration.getFromUser().getId()).orElseThrow(UserNotFoundException::new);
        User toUser = userRepo.findById(privateRoomRegistration.getToUser().getId()).orElseThrow(UserNotFoundException::new);
        Set<User> users = Stream.of(fromUser, toUser).collect(Collectors.toSet());

        if (privateRoomRepository.findByTwoUsers(fromUser, toUser) != null) {
            throw new DuplicatePrivateRoomException(
                    String.format("Private room exists for %s and %s!", fromUser.getUsername(), toUser.getUsername())
            );
        }

        PrivateRoom privateRoom = new PrivateRoom();
        privateRoom.setUser1(fromUser);
        privateRoom.setUser2(toUser);

        return privateRoomRepository.save(privateRoom);
    }
}
