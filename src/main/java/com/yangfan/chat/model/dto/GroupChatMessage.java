package com.yangfan.chat.model.dto;

import lombok.Data;

import java.time.Instant;

/** payload for message dto **/
@Data
public class GroupChatMessage {
    private int fromUserId;
    private int roomId;
    private String message;
    private Instant timestamp;

    private String fromUserName;
    private String roomName;
}
