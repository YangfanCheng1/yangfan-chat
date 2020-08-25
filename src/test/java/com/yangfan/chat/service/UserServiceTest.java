package com.yangfan.chat.service;

import com.google.common.collect.ImmutableList;
import com.yangfan.chat.exception.DuplicateUserException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.PublicRoom;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.UserRegistrationDto;
import com.yangfan.chat.repository.PrivateRoomRepository;
import com.yangfan.chat.repository.PublicRoomRepository;
import com.yangfan.chat.repository.UserRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    PrivateRoomRepository privateRoomRepository;
    @Mock
    PublicRoomRepository publicRoomRepository;
    @Mock
    ApplicationEventListener eventListener;

    @InjectMocks
    UserService userService;

    String username = "user";
    User user;
    User user2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username(username)
                .rooms(ImmutableList.of(new PublicRoom(1)))
                .build();
        user2 = User.builder()
                .id(2)
                .username("user2")
                .rooms(ImmutableList.of())
                .build();
    }

    @Test
    void getUserDtoByUsername() throws UserNotFoundException {

        val privateRoom = new PrivateRoom(user, user2);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(privateRoomRepository.findByUser(user)).willReturn(ImmutableList.of(privateRoom));

        val actual = userService.getUserDtoByUsername(username);

        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getRooms()).hasSize(2);
    }

    @Test
    void getUsersContaining() {
        given(userRepository.findOtherUsers("blah", username))
                .willReturn(ImmutableList.of(user, user2));

        val users = userService.getUsersContaining("blah", username);

        assertThat(users).hasSize(1);
    }

    @Test
    void addNewUser() throws DuplicateUserException {
        val allChatRoom = new PublicRoom();
        given(userRepository.existsByUsername(username)).willReturn(false);
        given(publicRoomRepository.findByRoomName("All")).willReturn(Optional.of(allChatRoom));

        userService.addNewUser(new UserRegistrationDto(username, "password", username + "@example.com"));

        then(userRepository).should().existsByUsername(username);
        then(publicRoomRepository).should().save(argThat(room -> room.getUsers().size() == 1));
    }
}