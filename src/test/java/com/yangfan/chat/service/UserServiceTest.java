package com.yangfan.chat.service;

import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.repository.PrivateRoomRepository;
import com.yangfan.chat.repository.PublicRoomRepository;
import com.yangfan.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    private String USER = "user0";
    private User user = new User();
    private User user2 = new User();
    private final PrivateRoom privateRoom = new PrivateRoom();

    private UserService userService;
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private PrivateRoomRepository privateRoomRepo = mock(PrivateRoomRepository.class);
    private PublicRoomRepository publicRoomRepo = mock(PublicRoomRepository.class);

    @BeforeEach
    void setUp() {
        user.setId(1);
        user.setUsername(USER);
        user.setRooms(Collections.emptyList());

        user2.setId(2);
        user2.setUsername("user2");

        privateRoom.setUser1(user);
        privateRoom.setUser2(user2);
        userService = new UserService(passwordEncoder, userRepository, privateRoomRepo, publicRoomRepo);
    }

    @Test
    void getUserByname() throws UserNotFoundException {
        given(userRepository.findByUsername(USER)).willReturn(Optional.of(user));
        given(privateRoomRepo.findByUser(user)).willReturn(Collections.singletonList(privateRoom));

        UserDto userDto = userService.getUserByName(USER);

        assertThat(userDto).hasFieldOrPropertyWithValue("id", 1);
        assertThat(userDto).hasFieldOrPropertyWithValue("name", USER);
        assertThat(userDto.getRooms()).hasSize(1);
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void getUsersContaining() {
        given(userRepository.findOtherUsers("us", USER)).willReturn(Stream.of(user2));

        List<UserDto> userDtos = userService.getUsersContaining("us", USER);

        assertThat(userDtos).hasSize(1);
        assertThat(userDtos.get(0).getId()).isEqualTo(2);

    }

    @Test
    void addNewUser() {
    }
}