package com.yangfan.chat.controller;

import com.yangfan.chat.exception.DuplicatePrivateRoomException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dao.Room;
import com.yangfan.chat.model.dao.User;
import com.yangfan.chat.model.dto.ActiveRoomDto;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.UserDto;
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
import org.springframework.web.server.ResponseStatusException;

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
        return ResponseEntity.ok(userDto);
    }

    @GetMapping(path = "/get-all-users-containing", produces = {"application/json"})
    public List<UserDto> getUsersContaining(@RequestParam(value = "keyword") String var1) {
        log.info("searched item: {}", var1);
        return userService.getUsersContaining(var1);
    }

    @GetMapping(path = "/chat-history/private", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageDto> getChatHistoryPrivate(@RequestParam(required = false) Integer size,
                                                  @RequestParam int fromUserId,
                                                  @RequestParam int toUserId) {
        log.info("getting chat history");
        ActiveRoomDto activeRoom = new ActiveRoomDto();
        activeRoom.setFromUserId(fromUserId);
        activeRoom.setToUserId(toUserId);
        activeRoom.setPrivate(true);

        return messageService.getChatHistory(activeRoom);
    }

    @GetMapping(path = "/chat-history/group", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageDto> getChatHistoryGroup(@RequestParam(required = false) Integer size,
                                                @RequestParam int roomId) {
        log.info("getting chat history");
        ActiveRoomDto activeRoom = new ActiveRoomDto();
        activeRoom.setRoomId(roomId);
        activeRoom.setPrivate(false);

        List<MessageDto> messageDtos = messageService.getChatHistory(activeRoom);
        log.info(messageDtos.toString());

        return messageDtos;
    }

    @PostMapping(path = "/room", consumes = {"application/json"}, produces = {"application/json"})
    public Response addNewRoom(@RequestBody ActiveRoomDto activeRoomDto)
            throws UserNotFoundException, DuplicatePrivateRoomException {
        String fromUserName = activeRoomDto.getFromUserName();
        String toUserName = activeRoomDto.getToUserName();
        log.info("Creating new room for user '{}' and '{}'", fromUserName, toUserName);
        if (roomService.addNewPrivateRoom(activeRoomDto)) {
            return new Response(
                    HttpStatus.OK.value(),
                    String.format("PM room created for '%s' and '%s'", fromUserName, toUserName)
            );
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PM room couldn't be created in database");
        }
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

    @Deprecated
    @PostMapping(path = "/add-new-private-room", consumes = "application/json", produces = "application/json")
    public String addNewPrivateRoom(@RequestBody List<User> userList) throws UserNotFoundException {
        Room savedRoom = userService.createNewPrivateRoom(userList);
        if (savedRoom != null) return "{\"roomId\": \"" + savedRoom.getRoomId() + "\"}";
        else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "{\"status\": \"room not saved!\"}");
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
