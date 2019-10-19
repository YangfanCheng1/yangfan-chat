package com.yangfan.chat.controller;

import com.yangfan.chat.exception.DuplicatePrivateRoomException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.model.dto.request.PrivateRoomRegistration;
import com.yangfan.chat.service.MessageService;
import com.yangfan.chat.service.RoomService;
import com.yangfan.chat.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RoomService roomService;

    // Init user state after sign in
    @GetMapping(path = "/user/{username}", produces = {"application/json"})
    public ResponseEntity<UserDto> getUser(@PathVariable String username) throws UserNotFoundException {
        UserDto userDto = userService.getUserDtoByUsername(username);
        log.info("Getting {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping(path = "/user", produces = {"application/json"})
    public ResponseEntity<UserDto> initUser(Principal principal) throws UserNotFoundException {
        String username = principal.getName();
        UserDto userDto = userService.getUserDtoByUsername(username);
        log.info("Getting {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping(path = "/users", produces = {"application/json"})
    public List<UserDto> getUsersContaining(@RequestParam(value = "keyword") String searchedTerm) {
        log.info("Searched item: {}", searchedTerm);
        return userService.getUsersContaining(searchedTerm);
    }

    @GetMapping(path = "/room/{roomId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageDto> getMessagesByRoom(@RequestParam(required = false) Integer size,
                                              @PathVariable Integer roomId) {
        log.info("Getting messages (room={})", roomId);
        return messageService.getMessagesByRoomId(roomId);
    }

    @PostMapping(path = "/room", consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto addNewRoom(@Valid @RequestBody PrivateRoomRegistration privateRoomRegistration)
            throws UserNotFoundException, DuplicatePrivateRoomException {
        UserDto fromUser = privateRoomRegistration.getFromUser();
        UserDto toUser = privateRoomRegistration.getToUser();
        log.info("Creating new room for user '{}' and '{}'", fromUser.getName(), toUser.getName());

        Room savedRoom = roomService.addRoom(privateRoomRegistration);
        return new RoomDto(savedRoom.getRoomId(), toUser.getName(), true);
    }

    @Data
    @AllArgsConstructor
    private class Response {
        private int status;
        private String message;
    }

    /*-- No longer used --*/

    @Deprecated
    @GetMapping(path = "/all-users")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    /* --
    @Deprecated
    @PostMapping(path = "/sign-in", consumes = {"application/json"}, produces = {"application/json"})
    public String getUser(@RequestBody User user) throws UserNotFoundException {
        log.info("I am at sign in");
        String username = user.getUsername();
        User savedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        log.info("{} logged in", username);
        return "{\"status\": \"Welcome, " + username + "\"}";
    }
    -- */

}
