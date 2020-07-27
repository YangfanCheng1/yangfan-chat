package com.yangfan.chat.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Value
@With
public class MessagePayload {
    @NotNull
    @JsonProperty("room")
    RoomDto roomDto;

    @NotNull
    @JsonProperty("message")
    MessageDto messageDto;
}
