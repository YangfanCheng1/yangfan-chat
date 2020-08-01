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
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<RoomDto> roomDtos = Stream.of(privateRoomRepository.findByUser(user), user.getRooms())
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDto> getUsersContaining(String text, String curUserName) {
        List<User> users = userRepository.findOtherUsers(text, curUserName);
        return users.stream()
                .filter(user -> !user.getUsername().equals(curUserName))
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .name(user.getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    public void addNewUser(UserRegistrationDto userRegistrationDto) throws DuplicateUserException {
        String username = userRegistrationDto.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException(username);
        }

        User newUser = new User();
        PublicRoom allRoom = publicRoomRepository.findByRoomName("All").orElseGet(this::initAllChatRoom);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        newUser.setEmail(userRegistrationDto.getEmail());
        newUser.setRooms(Collections.singletonList(allRoom));

        allRoom.getUsers().add(newUser);
        publicRoomRepository.save(allRoom);
    }

    private PublicRoom initAllChatRoom() {
        return PublicRoom.buildFromName("All");
    }

}
