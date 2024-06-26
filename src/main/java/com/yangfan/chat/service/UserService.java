package com.yangfan.chat.service;

import com.yangfan.chat.exception.DuplicateUserException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.PublicRoom;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.model.dto.UserRegistrationDto;
import com.yangfan.chat.repository.PrivateRoomRepository;
import com.yangfan.chat.repository.PublicRoomRepository;
import com.yangfan.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yangfan.chat.model.dto.RoomDto.Status.NONE;
import static com.yangfan.chat.model.dto.RoomDto.Status.OFFLINE;
import static com.yangfan.chat.model.dto.RoomDto.Status.ONLINE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PrivateRoomRepository privateRoomRepository;
    private final PublicRoomRepository publicRoomRepository;
    private final ApplicationEventListener eventListener;

    public UserDto getUserDtoByUsername(String username) throws UserNotFoundException {
        val user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        val roomDtos = Stream.of(privateRoomRepository.findByUser(user), user.getRooms())
                .flatMap(Collection::stream)
                .map(room -> convertToRoomDto(room, user))
                .collect(Collectors.toList());

        return UserDto.builder()
                .id(user.getId())
                .name(user.getUsername())
                .rooms(roomDtos)
                .build();
    }

    private <E extends Room> RoomDto convertToRoomDto(E room, User self) {
        if (room instanceof PrivateRoom) {
            PrivateRoom pr = (PrivateRoom) room;
            String roomName = pr.getUser1().getUsername().equals(self.getUsername())
                    ? pr.getUser2().getUsername()
                    : pr.getUser1().getUsername();
            return RoomDto.builder()
                    .id(room.getRoomId())
                    .name(roomName)
                    .isPrivate(true)
                    .status(eventListener.getStatus(roomName)).build();
        } else {
            return RoomDto.builder()
                    .id(room.getRoomId())
                    .name(room.getRoomName())
                    .isPrivate(false)
                    .status(NONE).build();
        }
    }

    public List<UserDto> getUsersContaining(String text, String curUserName) {
        return userRepository.findOtherUsers(text, curUserName)
                .stream()
                .filter(user -> !user.getUsername().equals(curUserName))
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .name(user.getUsername())
                        .build())
                .limit(18)
                .collect(Collectors.toList());
    }

    public void addNewUser(UserRegistrationDto userRegistrationDto) throws DuplicateUserException {
        val username = userRegistrationDto.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException(username);
        }

        val allRoom = publicRoomRepository.findByRoomName("All")
                .orElseGet(this::initAllChatRoom);
        val newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .email(userRegistrationDto.getEmail())
                .rooms(Collections.singletonList(allRoom))
                .build();

        allRoom.getUsers().add(newUser);
        publicRoomRepository.save(allRoom);
    }

    private PublicRoom initAllChatRoom() {
        return PublicRoom.buildFromName("All");
    }

}
