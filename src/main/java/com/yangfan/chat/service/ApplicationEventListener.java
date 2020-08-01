package com.yangfan.chat.service;

import com.yangfan.chat.model.dto.RoomDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yangfan.chat.model.dto.RoomDto.Status.OFFLINE;
import static com.yangfan.chat.model.dto.RoomDto.Status.ONLINE;

@Slf4j
@Component
public class ApplicationEventListener {

    private final SimpMessageSendingOperations msgOperator;
    private final Map<String, Boolean> loggedInUsers;
    private final Map<String, Boolean> loggedOutUsers;

    public ApplicationEventListener(SimpMessageSendingOperations msgOperator) {
        this.msgOperator = msgOperator;
        this.loggedInUsers = new ConcurrentHashMap<>();
        this.loggedOutUsers = new ConcurrentHashMap<>();
    }

    @EventListener
    public void listenOnConnect(SessionConnectedEvent ev) {
        log.info("user connected: {}", ev.getMessage());
        if (ev.getUser() != null) {
            val username = ev.getUser().getName();
            val roomDto = RoomDto.builder().id(-1).name(username).status(RoomDto.Status.ONLINE).build();
            loggedOutUsers.remove(username);
            loggedInUsers.putIfAbsent(username, Boolean.TRUE);
            msgOperator.convertAndSend("/topic/all", roomDto);
        }
    }

    @EventListener
    public void listenOnDisconnect(SessionDisconnectEvent ev) {
        log.info("user disconnected: {}", ev.getMessage());
        if (ev.getUser() != null) {
            String username = ev.getUser().getName();
            val roomDto = RoomDto.builder().id(-1).name(username).status(RoomDto.Status.OFFLINE).build();
            loggedInUsers.remove(username);
            loggedOutUsers.putIfAbsent(username, Boolean.TRUE);
            msgOperator.convertAndSend("/topic/all", roomDto);
        }
    }

    Map<String, Boolean> getLoggedInUsers() {
        return loggedInUsers;
    }

    Map<String, Boolean> getLoggedOutUsers() {
        return loggedOutUsers;
    }

    boolean hasUser(Map<String, Boolean> users, String name) {
        if (name == null) return false;
        return users.containsKey(name);
    }

    public RoomDto.Status getStatus(String userName) {
        return this.getLoggedInUsers().containsKey(userName) ? ONLINE : OFFLINE;
    }
}
