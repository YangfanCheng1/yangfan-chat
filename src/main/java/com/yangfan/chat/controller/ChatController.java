package com.yangfan.chat.controller;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.yangfan.chat.model.dto.EventMessage;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.MessagePayload;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.service.ApplicationEventListener;
import com.yangfan.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.security.Principal;
import java.time.Instant;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ApplicationEventListener eventListener;

    // takes message sent from front end to the backend messaging queue /chat-app/all
    @MessageMapping("/all") //application destination
    @SendTo("/topic/all") //broker destination
    public EventMessage sendMessage(@Payload EventMessage event, Principal user) {
        messageService.handleEvent(event);
        log.info("{} sent a msg", user.getName());
        return event;
    }

    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public MessageDto sendMessage(@Payload @Valid MessagePayload messagePayload) {
        val roomDto = messagePayload.getRoomDto();
        val messageDto = messagePayload.getMessageDto().withTimestamp(Instant.now());
        log.info("Message sent (user={}, content={}, toRoom={})",
                messageDto.getFromUserId(), messageDto.getContent(), roomDto.getId());
        messageService.addMessage(roomDto, messageDto);
        return messageDto;
    }

    @MessageMapping("/user")
    public void handleEvent(@Payload @Valid EventDto eventDto, Principal principal, @Header("username") String toUserName) {
        val fromUserName = eventDto.fromUser.name;
        log.info("New event from User(id={}, name={})", eventDto.fromUser.id, fromUserName);
        val toUserRoom = RoomDto.builder()
                .id(eventDto.room.getId())
                .name(fromUserName)
                .status(eventListener.getStatus(fromUserName)).build();
        simpMessagingTemplate.convertAndSendToUser(toUserName, "/queue/notify", toUserRoom);

        val fromUserRoom = RoomDto.builder()
                .id(eventDto.room.getId())
                .name(toUserName)
                .status(eventListener.getStatus(toUserName)).build();
        simpMessagingTemplate.convertAndSendToUser(fromUserName, "/queue/notify", fromUserRoom);
    }

    @Value
    static class EventDto {

        User fromUser;

        RoomDto room;

        @Value
        static class User {
            int id;
            String name;
        }

        boolean isPrivate = true;

        @JsonGetter(value = "isPrivate")
        public boolean isPrivate(){
            return isPrivate;
        }
    }
}
