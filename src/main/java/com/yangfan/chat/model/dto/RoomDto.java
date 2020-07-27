package com.yangfan.chat.model.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class RoomDto {
    int id;
    String name;
    @Builder.Default
    boolean isPrivate = true;

    // Jackson strips 'is' by default during serialization
    @JsonGetter(value = "isPrivate")
    public boolean isPrivate(){
        return isPrivate;
    }

}
