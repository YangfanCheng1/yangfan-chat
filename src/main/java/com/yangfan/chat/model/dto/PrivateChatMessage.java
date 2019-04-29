package com.yangfan.chat.model.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PrivateChatMessage {
    private int fromUserId;
    private int toUserId;
    private String message;
    private Instant timestamp;

    // Optional if new user;
    private String fromUserName;
    private String toUserName;
}
