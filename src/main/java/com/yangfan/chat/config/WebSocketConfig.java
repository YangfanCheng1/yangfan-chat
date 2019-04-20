package com.yangfan.chat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnWebApplication
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // in memory message broker -- to be replaced by activeMQ
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/chat-app"); // messages are routed to application controller
    }

    /** to be registered by client js **/
    @Override
    public void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/sockJS")
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .withSockJS();
    }

}
