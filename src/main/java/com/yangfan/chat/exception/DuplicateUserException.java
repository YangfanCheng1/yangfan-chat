package com.yangfan.chat.exception;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String username) {
        super(String.format("%s already exists", username));
    }
}
