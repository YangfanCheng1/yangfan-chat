package com.yangfan.chat.model.dto.request;

import com.yangfan.chat.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateRoomRegistration {

    @NotNull
    private UserDto fromUser;
    @NotNull
    private UserDto toUser;

}
