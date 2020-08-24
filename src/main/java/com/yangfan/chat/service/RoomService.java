package com.yangfan.chat.service;

import com.yangfan.chat.exception.DuplicatePrivateRoomException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.request.PrivateRoomRegistration;
import com.yangfan.chat.repository.PrivateRoomRepository;
import com.yangfan.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final PrivateRoomRepository privateRoomRepository;
    private final UserRepository userRepo;

    public RoomDto addRoom(PrivateRoomRegistration privateRoomRegistration) throws UserNotFoundException, DuplicatePrivateRoomException {
        val fromUser = userRepo.findById(privateRoomRegistration.getFromUser().getId())
                .orElseThrow(UserNotFoundException::new);
        val toUser = userRepo.findById(privateRoomRegistration.getToUser().getId())
                .orElseThrow(UserNotFoundException::new);

        if (privateRoomRepository.findByTwoUsers(fromUser, toUser) != null) {
            throw new DuplicatePrivateRoomException(
                    String.format("Private room already exists for %s and %s!", fromUser.getUsername(), toUser.getUsername())
            );
        }

        PrivateRoom privateRoom = new PrivateRoom();
        privateRoom.setUser1(fromUser);
        privateRoom.setUser2(toUser);
        val savedRoom = privateRoomRepository.save(privateRoom);
        return RoomDto.builder()
                .id(savedRoom.getRoomId())
                .name(privateRoomRegistration.getToUser().getName())
                .isPrivate(true).build();
    }
}
