package com.yangfan.chat.model.dto;

import lombok.Data;

@Data
public class EventMessage {
    int fromUserId;
    int toUserId;
    String action;
}
