package com.yangfan.chat.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private int userId;
    private String username;
    private List<RoomDto> rooms;
}
