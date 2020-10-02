package com.yangfan.chat;

import com.yangfan.PlaygroundApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PlayGroundApplicationTest {

    @Autowired
    PlaygroundApplication application;

    @Test
    public void contextLoads() {
        assertThat(application).isNotNull();
    }

}
