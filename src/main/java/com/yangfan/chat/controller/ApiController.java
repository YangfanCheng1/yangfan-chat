package com.yangfan.chat.controller;

import com.yangfan.chat.exception.DuplicatePrivateRoomException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.model.dto.request.PrivateRoomRegistration;
import com.yangfan.chat.service.ApplicationEventListener;
import com.yangfan.chat.service.MessageService;
import com.yangfan.chat.service.RoomService;
import com.yangfan.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final ApplicationEventListener applicationEventListener;

    @GetMapping("users/{username}")
    public UserDto getUser(@PathVariable String username) throws UserNotFoundException {
        return userService.getUserDtoByUsername(username);
    }

    @GetMapping("users")
    public UserDto initUser(Principal principal) throws UserNotFoundException {
        val username = principal.getName();
        return userService.getUserDtoByUsername(username);
    }

    @GetMapping("users/search")
    public List<UserDto> getUsersContaining(@RequestParam(value = "keyword") String searchedTerm,
                                            Principal principal) {
        return userService.getUsersContaining(searchedTerm, principal.getName());
    }

    @GetMapping("rooms/{roomId}/messages")
    public List<MessageDto> getMessagesByRoom(@RequestParam(required = false) Integer size,
                                              @PathVariable Integer roomId) {
        return messageService.getMessagesByRoomId(roomId);
    }

    @PostMapping("rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto addNewRoom(@Validated @RequestBody PrivateRoomRegistration privateRoomRegistration)
            throws UserNotFoundException, DuplicatePrivateRoomException {
        return roomService.addRoom(privateRoomRegistration);
    }

    @GetMapping("stats")
    public StatsResponse getNumberOfUsers(){
        return new StatsResponse(applicationEventListener.getSize());
    }

    @Value
    private static class StatsResponse {
        int count;
    }
}
