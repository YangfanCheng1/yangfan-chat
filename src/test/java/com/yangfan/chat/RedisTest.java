package com.yangfan.chat;

import com.yangfan.chat.controller.ApiController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApiController.class)
public class RedisTest {
}
