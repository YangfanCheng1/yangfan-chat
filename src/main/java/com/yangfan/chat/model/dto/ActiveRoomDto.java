package com.yangfan.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveRoomDto {
    private int fromUserId;
    private int toUserId;
    private int RoomId;
    private boolean isPrivate;

    // optional
    private String fromUserName;
    private String toUserName;
    private String roomName;
}
