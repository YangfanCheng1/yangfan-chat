package com.yangfan.chat.model.dto;

import com.yangfan.chat.model.dao.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Comparable<MessageDto> {
    private int fromUserId;
    private String fromUserName;
    private String content;
    private Instant timestamp;


    @Override
    public int compareTo(MessageDto obj) {
        return this.getTimestamp().compareTo(obj.getTimestamp());
    }
}
