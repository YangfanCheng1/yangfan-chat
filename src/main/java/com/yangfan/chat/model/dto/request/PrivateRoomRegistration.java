package com.yangfan.chat.model.dto.request;

import com.yangfan.chat.model.dto.UserDto;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class PrivateRoomRegistration {

    @NotNull
    UserDto fromUser;
    @NotNull
    UserDto toUser;

}
