package com.yangfan.chat.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private int id;
    private String name;
    private List<RoomDto> rooms;
}
