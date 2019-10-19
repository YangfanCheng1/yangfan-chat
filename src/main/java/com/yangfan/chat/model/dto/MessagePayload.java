package com.yangfan.chat.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessagePayload {
    @NotNull @JsonProperty("room")
    RoomDto roomDto;
    @NotNull @JsonProperty("message")
    MessageDto messageDto;
}
