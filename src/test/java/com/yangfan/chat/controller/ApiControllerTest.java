package com.yangfan.chat.controller;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static String testUserName;

    static {
        testUserName = "user";
    }

    @WithMockUser
    @Test
    void getUserByName() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name(testUserName)
                .build();

        when(userService.getUserDtoByUsername(testUserName)).thenReturn(userDto);
        mockMvc.perform(get("/api/user/{user}", testUserName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(testUserName)));
    }

}