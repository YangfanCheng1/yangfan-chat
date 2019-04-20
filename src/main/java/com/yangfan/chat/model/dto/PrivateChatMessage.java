package com.yangfan.chat.model.dto;

import lombok.Data;

@Data
public class PrivateChatMessage {
    private int fromUserId;
    private int toUserId;
    private String message;

    // Optional if new user;
    private String fromUserName;
}
