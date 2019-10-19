package com.yangfan.chat.model.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoomDto {
    private int id;
    private String name;
    private boolean isPrivate;

    // Jackson strips 'is' by default during serialization
    @JsonGetter(value = "isPrivate")
    public boolean isPrivate(){
        return isPrivate;
    }

}
