package com.yangfan.chat.controller;

import com.yangfan.chat.model.dto.*;
import com.yangfan.chat.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.time.Instant;

@Slf4j
@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

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
        @NotNull RoomDto roomDto = messagePayload.getRoomDto();
        @NotNull MessageDto messageDto = messagePayload.getMessageDto();
        log.info("Message sent (user={}, content={}, toRoom={})",
                messageDto.getFromUserId(), messageDto.getContent(), roomDto.getId());
        messageDto.setTimestamp(Instant.now());
        messageService.addMessage(roomDto, messageDto);
        return messageDto;
    }

}
