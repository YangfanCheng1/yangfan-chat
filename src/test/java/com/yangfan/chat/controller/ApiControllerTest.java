package com.yangfan.chat.controller;

import com.google.common.collect.ImmutableList;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.repository.UserRepository;
import com.yangfan.chat.service.ApplicationEventListener;
import com.yangfan.chat.service.MessageService;
import com.yangfan.chat.service.RoomService;
import com.yangfan.chat.service.UserDetailsServiceImpl;
import com.yangfan.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApiController.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {UserDetailsServiceImpl.class})
)
class ApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;
    @MockBean
    MessageService messageService;
    @MockBean
    RoomService roomService;
    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    ApplicationEventListener applicationEventListener;
    @MockBean
    UserRepository userRepository;

    String user = "user";

    @WithMockUser()
    @Test
    void getUserByName() throws Exception {
        String user = "user";
        UserDto userDto = UserDto.builder()
                .id(1)
                .name(user)
                .build();

        given(userService.getUserDtoByUsername(user)).willReturn(userDto);

        mockMvc.perform(get("/api/users/{user}", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(user)));
        then(userService).should().getUserDtoByUsername("user");
    }

    @Test
    @WithMockUser()
    void getUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name(user)
                .build();

        given(userService.getUserDtoByUsername(user)).willReturn(userDto);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(user)));
    }

    @Test
    @WithMockUser
    void getUsersContaining() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name(user)
                .rooms(ImmutableList.of())
                .build();
        List<UserDto> userDtos = ImmutableList.of(userDto);

        given(userService.getUsersContaining("blah", "user"))
                .willReturn(userDtos);

        mockMvc.perform(get("/api/users/search")
                .param("keyword", "blah"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("user")))
                .andExpect(jsonPath("$.[0].rooms", empty()));
    }

    @Test
    void getMessagesByRoom() throws Exception {
        List<MessageDto> messageDtos = ImmutableList.of(
                MessageDto.builder().fromUserId(2).fromUserName("user2").content("foo").timestamp(Instant.now()).build(),
                MessageDto.builder().fromUserId(2).fromUserName("user2").content("bar").timestamp(Instant.now()).build()
        );

        given(messageService.getMessagesByRoomId(1))
                .willReturn(messageDtos);

        mockMvc.perform(get("/api/rooms/{roomId}/messages", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @WithMockUser
    @Test
    void addNewRoom() throws Exception {
        String payload =
                "{" +
                "  \"fromUser\": {" +
                "    \"id\": 1," +
                "    \"name\": \"user\"" +
                "  }," +
                "  \"toUser\": {" +
                "    \"id\": 2," +
                "    \"name\": \"user2\"" +
                "  }" +
                "}";

        RoomDto roomDto = RoomDto.builder().id(1).name("user2").isPrivate(true).build();

        given(roomService.addRoom(any())).willReturn(roomDto);

        mockMvc.perform(post("/api/rooms")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    void getNumberOfUsers() throws Exception {

        given(applicationEventListener.getSize()).willReturn(1_000_000);

        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1_000_000)));
    }

}