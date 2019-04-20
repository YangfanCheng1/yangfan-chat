package com.yangfan.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class MessageEventListener {

    private final SimpMessageSendingOperations msgOperator;

    public MessageEventListener(SimpMessageSendingOperations msgOperator) {
        this.msgOperator = msgOperator;
    }

    @EventListener
    public void listenOnConnect(SessionConnectedEvent ev) {
        log.info("received a new connection: {}", ev.getMessage());
    }

    @EventListener
    public void listenOnDisconnect(SessionDisconnectEvent ev) {
        log.info("user disconnected: " + ev.getMessage());
    }

}
