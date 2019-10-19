package com.yangfan.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoRoomFoundException extends RuntimeException {
    public NoRoomFoundException(Integer roomId) {
        super("Room " + roomId + " is not found!");
    }
}
