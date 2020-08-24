package com.yangfan.chat.service;

import com.yangfan.chat.exception.NoRoomFoundException;
import com.yangfan.chat.model.dao.*;
import com.yangfan.chat.model.dto.EventMessage;
import com.yangfan.chat.model.dto.MessageDto;
import com.yangfan.chat.model.dto.RoomDto;
import com.yangfan.chat.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final Object mutex = new Object();

    private final EntityManager entityManager;
    private final PrivateRoomRepository privateRoomRepository;
    private final PublicRoomRepository publicRoomRepository;

    public void handleEvent(EventMessage event) {
        switch (event.getAction()) {
            case "CREATE":
            case "DELETE":
                //...
            default:
                log.warn("No action found");
        }
    }

    @Transactional
    public List<MessageDto> getMessagesByRoomId(Integer roomId) {
        PrivateRoom privateRoom = privateRoomRepository.findById(roomId).orElse(null);
        Room room = (privateRoom != null)
                ? privateRoom
                : publicRoomRepository.findById(roomId).orElseThrow(() -> new NoRoomFoundException(roomId));
        return room.getMessages().stream().map(message -> MessageDto.builder()
                .fromUserId(message.getUser().getId())
                .fromUserName(message.getUser().getUsername())
                .content(message.getMessage())
                .timestamp(message.getTimestamp())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void addMessage(RoomDto roomDto, MessageDto messageDto) {
        Message message;
        if (roomDto.isPrivate()) {
            message = Message.builder()
                    .room(new PrivateRoom(roomDto.getId()))
                    .user(new User(messageDto.getFromUserId()))
                    .message(messageDto.getContent())
                    .timestamp(messageDto.getTimestamp())
                    .build();

        } else {
            message = Message.builder()
                    .room(new PublicRoom(roomDto.getId()))
                    .user(new User(messageDto.getFromUserId()))
                    .message(messageDto.getContent())
                    .timestamp(messageDto.getTimestamp())
                    .build();
        }
        entityManager.persist(message);
    }
}
