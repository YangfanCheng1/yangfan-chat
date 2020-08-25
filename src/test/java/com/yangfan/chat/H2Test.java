package com.yangfan.chat;

import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class H2Test {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void test(){
        User user = new User();
        user.setUsername("userY");
        user.setPassword("passY");

        testEntityManager.persist(user);
        User userDao = userRepository.findByUsername("userY").orElse(null);

        assertThat(userDao).isNotNull();
        assertThat(userDao.getUsername()).isEqualTo("userY");
    }
}
