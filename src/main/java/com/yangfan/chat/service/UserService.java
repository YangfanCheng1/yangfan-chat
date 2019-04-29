package com.yangfan.chat.service;

import com.yangfan.chat.exception.DuplicateUserException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.model.dto.UserRegistrationDto;
import com.yangfan.chat.repository.RoomRepository;
import com.yangfan.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoomRepository roomRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    private User getUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        log.info("{}'s data: {}",username, user);
        return user;
    }

    /**
     * For private chat, a Users object is populated.
     */
    public UserDto getUserDtoByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        List<RoomDto> roomDtos = Stream.of(user.getRooms(), user.getPrivateRooms())
                .flatMap(Collection::stream)
                .map(room -> convertToRoomDto(room, user))
                .collect(Collectors.toList());
        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .rooms(roomDtos)
                .build();
        log.info("{}'s data: {}",username, user);
        return userDto;
    }

    private <E> RoomDto convertToRoomDto(E room, User user) {
        if (room instanceof User){
            User toUser = (User) room;
            return RoomDto.builder()
                    .displayId(toUser.getUserId())
                    .displayName(toUser.getUsername())
                    .isPrivate(true)
                    .build();
        }

        Room groupRoom = (Room) room;
        return RoomDto.builder()
                .displayId(groupRoom.getRoomId())
                .displayName(groupRoom.getRoomName())
                .isPrivate(false)
                .build();

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDto> getUsersContaining(String var1) {
        List<User> users = userRepository.findByUsernameContaining(var1);
        List<UserDto> userDtos = users.stream()
                .map(user -> UserDto.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .build())
                .collect(Collectors.toList());
        log.info("list of users: {}", userDtos);
        return userDtos;
    }

    public Room createNewPrivateRoom(List<User> userList) throws UserNotFoundException {
        Room newRoom = new Room();
        String username0 = userList.get(0).getUsername();
        String username1 = userList.get(1).getUsername();
        newRoom.setRoomName(null);
        User user0 = this.getUserByUsername(username0);
        User user1 = this.getUserByUsername(username1);

        Set<User> users = new HashSet<>();
        users.add(user0);
        users.add(user1);
        newRoom.setUsers(users);
        Room savedRoom = roomRepository.save(newRoom);

        user0.getRooms().add(savedRoom);
        user1.getRooms().add(savedRoom);

        userRepository.save(user0);
        userRepository.save(user1);

        return savedRoom;
    }

    public UserDto addNewUser(UserRegistrationDto userRegistrationDto) throws DuplicateUserException {
        String username = userRegistrationDto.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException(username);
        }
        User newUser = new User();
        Room allRoom = roomRepository.findByRoomName("All").orElse(getNewRoom());
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        newUser.setRooms(Collections.singletonList(allRoom));
        User savedUser = userRepository.save(newUser);

        return UserDto.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .rooms(Collections.emptyList())
                .build();
    }

    private Room getNewRoom() {
        Room room = new Room();
        room.setRoomName("All");
        return room;
    }

}
