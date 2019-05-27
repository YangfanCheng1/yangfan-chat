package com.yangfan.chat.controller;

import com.yangfan.chat.model.dto.EventMessage;
import com.yangfan.chat.model.dto.GroupChatMessage;
import com.yangfan.chat.model.dto.PrivateChatMessage;
import com.yangfan.chat.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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

    // TODO need to build a hanlder for sending to user queue
    // SendToUser is not needed as we can directly send a message to a target single user
    @MessageMapping("/private/{toUserId}")
    @SendTo("/topic/private.{toUserId}") //stomclient to subscribe to
    public PrivateChatMessage sendPrivateMessage(@Payload PrivateChatMessage privateChatMessage) {
        privateChatMessage.setTimestamp(Instant.now());
        messageService.add(privateChatMessage);

        log.info("private message '{}' sent to '{}'", privateChatMessage.getMessage(), privateChatMessage.getToUserId());
        return privateChatMessage;
    }

    @MessageMapping("/group/{roomId}")
    @SendTo("/topic/group.{roomId}")
    public GroupChatMessage sendGroupMessage(@Payload GroupChatMessage groupChatMessage) {
        groupChatMessage.setTimestamp(Instant.now());
        messageService.add(groupChatMessage);

        log.info("group message '{}' sent to '{}'", groupChatMessage.getMessage(), groupChatMessage.getRoomId());
        return groupChatMessage;
    }

}
