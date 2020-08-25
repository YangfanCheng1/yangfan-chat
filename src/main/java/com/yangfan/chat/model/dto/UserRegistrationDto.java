package com.yangfan.chat.model.dto;

import lombok.Value;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserRegistrationDto {

    @NotBlank
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE) /* Strip all html */
    String username;

    @NotBlank
    String password;

    @Email
    String email;

}
