package com.yangfan.chat.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserRegistrationDto {

    @NotNull
    @NotEmpty
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) /* Strip all html */
    private String username;

    @NotNull
    @NotEmpty
    private String password;
}
