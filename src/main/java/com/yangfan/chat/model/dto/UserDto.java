package com.yangfan.chat.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UserDto {
    int id;
    String name;
    List<RoomDto> rooms;
}
