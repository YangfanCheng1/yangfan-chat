package com.yangfan.chat.controller;

import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.service.MessageService;
import com.yangfan.chat.service.RoomService;
import com.yangfan.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApiController.class)
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
    private UserDetailsService userDetailsService;

    private static String testUserName;

    static {
        testUserName = "user";
    }

    @WithMockUser
    @Test
    void getUserByName() throws Exception {
        UserDto userDto = UserDto.builder()
                .userId(1)
                .username(testUserName)
                .build();

        when(userService.getUserDtoByUsername(testUserName)).thenReturn(userDto);
        mockMvc.perform(get("/api/user/{user}", testUserName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is(testUserName)));
    }

}