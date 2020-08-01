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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;

    // Init user state after sign in
    @GetMapping("/user/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) throws UserNotFoundException {
        val userDto = userService.getUserDtoByUsername(username);
        log.info("Getting {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> initUser(Principal principal) throws UserNotFoundException {
        val username = principal.getName();
        val userDto = userService.getUserDtoByUsername(username);
        log.info("Getting {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsersContaining(@RequestParam(value = "keyword") String searchedTerm, Principal principal) {
        log.info("Searching (text={}, user={})", searchedTerm, principal.getName());
        return userService.getUsersContaining(searchedTerm, principal.getName());
    }

    @GetMapping("/room/{roomId}/messages")
    public List<MessageDto> getMessagesByRoom(@RequestParam(required = false) Integer size,
                                              @PathVariable Integer roomId) {
        log.info("Getting messages (room={})", roomId);
        return messageService.getMessagesByRoomId(roomId);
    }

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto addNewRoom(@Valid @RequestBody PrivateRoomRegistration privateRoomRegistration)
            throws UserNotFoundException, DuplicatePrivateRoomException {
        UserDto fromUser = privateRoomRegistration.getFromUser();
        UserDto toUser = privateRoomRegistration.getToUser();
        log.info("Creating new room for user '{}' and '{}'", fromUser.getName(), toUser.getName());

        Room savedRoom = roomService.addRoom(privateRoomRegistration);
        return RoomDto.builder()
                .id(savedRoom.getRoomId())
                .name(toUser.getName())
                .isPrivate(true).build();
    }

    /*-- No longer used --*/

    @Deprecated
    @GetMapping(path = "/all-users")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

}
