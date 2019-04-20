package com.yangfan.chat.model.dto;

import lombok.Data;

/** payload for message dto **/
@Data
public class GroupChatMessage {
    private int userId;
    private int roomId;
    private String message;

    private String roomName;
}
