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
    private final MessageRepository messageRepo;
    private final ApplicationEventListener listener;

    private static final int MESSAGE_THRESHOLD = 10;
    private Deque<Message> buffer = new ArrayDeque<>(MESSAGE_THRESHOLD);

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
        Room room = (privateRoom != null) ? privateRoom :
                publicRoomRepository.findById(roomId).orElseThrow(() -> new NoRoomFoundException(roomId));
        return room.getMessages().stream().map(message -> MessageDto.builder()
                .fromUserId(message.getUser().getId())
                .fromUserName(message.getUser().getUsername())
                .content(message.getMessage())
                .timestamp(message.getTimestamp())
                .build()).collect(Collectors.toList());
    }

    /**
     * case 1: only one user logs in, message is persisted into database right away (infrequent)
     * case 2: user logs out, clear buffer
     * case 3: both users log in, message is saved to buffer (frequent)
     */
    @Transactional
    public void addMessage(RoomDto roomDto, MessageDto messageDto) {
        if (roomDto.isPrivate()) {
            Message message = Message.builder()
                    .room(new PrivateRoom(roomDto.getId()))
                    .user(new User(messageDto.getFromUserId()))
                    .message(messageDto.getContent())
                    .timestamp(messageDto.getTimestamp())
                    .build();

            entityManager.persist(message);
        } else {
            Message message = Message.builder()
                    .room(new PublicRoom(roomDto.getId()))
                    .user(new User(messageDto.getFromUserId()))
                    .message(messageDto.getContent())
                    .timestamp(messageDto.getTimestamp())
                    .build();
            entityManager.persist(message);
        }
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void clearIfOverLimit() {
        synchronized (mutex) {
            if (buffer.size() > MESSAGE_THRESHOLD) {
                clearBuffer();
            }
        }
    }

    private void clearBuffer() {
        // shadow copy
        List<Message> clonedMessageList = new ArrayList<>(buffer);
        saveMessages(clonedMessageList);
        buffer.clear();
    }

    @Async
    void saveMessages(List<Message> list) {
        messageRepo.saveAll(list);
        log.info("list of {} elems saved", list.size());
    }

}
