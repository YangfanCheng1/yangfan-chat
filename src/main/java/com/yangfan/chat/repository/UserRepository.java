package com.yangfan.chat.repository;

import com.yangfan.chat.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // select u from user where u.username like ?1 and u.username != name
    List<User> findByUsernameContainingAndUsernameIsNot(String text, String name);

    default List<User> findOtherUsers(String text, String name) {
        return findByUsernameContainingAndUsernameIsNot(text, name);
    }

}
