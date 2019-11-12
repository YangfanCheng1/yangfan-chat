package com.yangfan.chat.controller;

import com.yangfan.chat.model.dao.PrivateRoom;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.model.dto.request.PrivateRoomRegistration;
import com.yangfan.chat.service.MessageService;
import com.yangfan.chat.service.RoomService;
import com.yangfan.chat.service.UserService;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@WithMockUser
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private RoomService roomService;
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    private final String testUserName = "user";
    private UserDto userDto = UserDto.builder()
            .id(1)
            .name(testUserName)
            .build();
    private UserDto userDto2 = UserDto.builder()
            .id(2)
            .name("user2")
            .build();
    private UserDto userDto3 = UserDto.builder()
            .id(3)
            .name("user3")
            .build();

    private MessageDto messageDto1 = MessageDto.builder()
            .content("hello")
            .fromUserId(1)
            .fromUserName("user")
            .build();
    private MessageDto messageDto2 = MessageDto.builder()
            .content("world")
            .fromUserId(1)
            .fromUserName("user")
            .build();

    private final io.vavr.collection.List<UserDto> immUserList = List.of(userDto2, userDto3);
    private final io.vavr.collection.List<MessageDto> immMessageList = List.of(messageDto1, messageDto2);
    private final RoomDto roomDto = RoomDto.builder().id(1).isPrivate(true).build();

    @Test
    void initUser() throws Exception {

        given(userService.getUserByName(testUserName)).willReturn(userDto);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(testUserName)));
    }

    @Test
    void getUsersContaining() throws Exception {

        given(userService.getUsersContaining("user", testUserName)).willReturn(immUserList.toJavaList());

        mockMvc.perform((get("/api/users")).param("keyword", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[1].id", is(3)));
    }

    @Test
    void getMessagesByRoom() throws Exception {

        given(messageService.getMessagesByRoomId(1)).willReturn(immMessageList.toJavaList());

        mockMvc.perform(get("/api/room/{id}/messages", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].content", is("hello")))
                .andExpect(jsonPath("$.[1].content", is("world")));
    }

    @Test
    void addNewRoom() throws Exception {
        String payload =
                "{\n" +
                "    \"fromUser\": {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"user\" \n" +
                "    },\n" +
                "    \"toUser\": {\n" +
                "        \"id\": 2,\n" +
                "        \"name\": \"user2\"\n" +
                "    },\n" +
                "    \"isPrivate\": true\n" +
                "}";
        PrivateRoomRegistration privateRoom = PrivateRoomRegistration.builder()
                .fromUser(userDto)
                .toUser(userDto2)
                .build();
        Room savedRoom = new PrivateRoom();
        savedRoom.setRoomId(1);

        given(roomService.addRoom(privateRoom)).willReturn(savedRoom);

        mockMvc.perform(post("/api/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }
}