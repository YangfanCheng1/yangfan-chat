package com.yangfan.chat.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {HealthCheck.class})
class HealthCheckTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    UserDetailsService userDetailsService;

    @Test
    void healthCheck() throws Exception {
        mockMvc.perform(get("/health")).andExpect(status().isOk());
    }
}