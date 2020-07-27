package com.yangfan.chat.model.dto;

import lombok.*;

import java.time.Instant;

@Value
@With
@Builder
public class MessageDto {
    int fromUserId;
    String fromUserName;
    String content;
    Instant timestamp;
}
