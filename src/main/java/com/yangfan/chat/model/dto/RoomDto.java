package com.yangfan.chat.model.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDto {
    private int displayId;
    private String displayName;
    private boolean isPrivate;

    // Jackson strips 'is' by default during serialization
    @JsonGetter(value = "isPrivate")
    public boolean isPrivate(){
        return isPrivate;
    }


}
