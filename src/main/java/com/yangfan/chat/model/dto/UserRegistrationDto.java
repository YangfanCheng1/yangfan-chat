package com.yangfan.chat.model.dto;

import lombok.Value;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserRegistrationDto {

    @NotBlank
    String username;

    @NotBlank
    String password;

    @Email
    String email;

}
