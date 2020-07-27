package com.yangfan.chat.controller;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.yangfan.chat.model.dto.EventMessage;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.MessagePayload;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.service.MessageService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ChatController {

    @Autowired
    MessageService messageService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

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
        RoomDto roomDto = messagePayload.getRoomDto();
        MessageDto messageDto = messagePayload.getMessageDto().withTimestamp(Instant.now());
        log.info("Message sent (user={}, content={}, toRoom={})",
                messageDto.getFromUserId(), messageDto.getContent(), roomDto.getId());
        messageService.addMessage(roomDto, messageDto);
        return messageDto;
    }

    @MessageMapping("/user")
    public void handleEvent(@Payload @Valid EventDto eventDto, Principal principal, @Header String username) {
        log.info("New event from User(id={}, name={})", eventDto.fromUser.id, eventDto.fromUser.name);
        val room = RoomDto.builder()
                .id(eventDto.room.getId())
                .name(eventDto.fromUser.name).build();
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/notify", room);
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
