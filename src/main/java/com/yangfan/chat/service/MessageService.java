package com.yangfan.chat.service;

import com.yangfan.chat.model.dao.*;
import com.yangfan.chat.model.dto.*;
import com.yangfan.chat.repository.MessageRepository;
import com.yangfan.chat.repository.PrivateMessageRepository;
import com.yangfan.chat.repository.PrivateRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageService {

    private final Object mutex = new Object();
    private final Object mutex2 = new Object();

    private final PrivateRoomRepository privateRoomRepository;
    private final MessageRepository messageRepository;
    private final PrivateMessageRepository privateMessageRepository;
    public MessageService(PrivateRoomRepository privateRoomRepository,
                          MessageRepository messageRepository,
                          PrivateMessageRepository privateMessageRepository) {
        this.privateRoomRepository = privateRoomRepository;
        this.messageRepository = messageRepository;
        this.privateMessageRepository = privateMessageRepository;
    }

    private Queue<Message> buffer = new ArrayDeque<>(MESSAGE_THRESHOLD);
    private Deque<PrivateMessage> buffer2 = new ArrayDeque<>(MESSAGE_THRESHOLD);
    private static final int MESSAGE_THRESHOLD = 5;

    public void handleEvent(EventMessage event) {
        switch (event.getAction()) {
            case "CREATE":
                PrivateRoom newPrivateRoom = PrivateRoom.builder()
                        .user1(new User(event.getFromUserId()))
                        .user2(new User(event.getToUserId()))
                        .build();
                privateRoomRepository.save(newPrivateRoom);
            case "DELETE":
                //...
            default:
                log.warn("No action found");
        }
    }

    public List<MessageDto> getChatHistory(ActiveRoomDto activeRoom) {
        if (activeRoom.isPrivate()) {
            User user1 = new User(activeRoom.getFromUserId());
            User user2 = new User(activeRoom.getToUserId());
            List<PrivateMessage> pms = privateMessageRepository.findMessages(user1, user2);
            return pms.stream()
                    .map(privateMessage -> MessageDto.builder()
                        .fromUserId(privateMessage.getFromUser().getUserId())
                        .fromUserName(privateMessage.getFromUser().getUsername())
                        .timestamp(privateMessage.getTimestamp())
                        .message(privateMessage.getMessage()).build())
                    .collect(Collectors.toList());
        } else {
            List<Message> messages = messageRepository.findByRoomOrderByIdDesc(new Room(activeRoom.getRoomId()));
            return messages.stream()
                    .map(message -> MessageDto.builder()
                        .fromUserId(message.getUser().getUserId())
                        .fromUserName(message.getUser().getUsername())
                        .timestamp(message.getTimestamp())
                        .message(message.getMessage()).build())
                    .collect(Collectors.toList());
        }
    }

    public void add(GroupChatMessage groupChatMessage) {
        Room room = new Room();
        room.setRoomId(groupChatMessage.getRoomId());
        User user = new User();
        user.setUserId(groupChatMessage.getUserId());
        Message message = Message.builder()
                .room(room).user(user).message(groupChatMessage.getMessage()).timestamp(Instant.now())
                .build();

        synchronized (mutex) {
            buffer.offer(message);
        }
    }

    public void add(PrivateChatMessage privateChatMessage) {
        User fromUser = new User();
        fromUser.setUserId(privateChatMessage.getFromUserId());
        User toUser = new User();
        toUser.setUserId(privateChatMessage.getToUserId());
        PrivateMessage message = PrivateMessage.builder()
                .fromUser(fromUser).toUser(toUser).message(privateChatMessage.getMessage()).timestamp(Instant.now())
                .build();

        synchronized (mutex) {
            buffer2.offer(message);
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void saveAndClear() {
        synchronized (mutex) {
            if (buffer.size() < MESSAGE_THRESHOLD) {
                return;
            }

            List<Message> clonedMessageList = new ArrayList<>(buffer.size());
            while (!buffer.isEmpty()) {
                clonedMessageList.add(buffer.poll());
            }
            log.info("buffer cleared");

            save(clonedMessageList);
        }
    }

    @Scheduled(cron = "0 0/6 * * * ?")
    public void savePrivateMessages() {
        synchronized (mutex2) {
            if (buffer2.size() < MESSAGE_THRESHOLD) {
                return;
            }

            List<PrivateMessage> clonedMessageList = new ArrayList<>(buffer2.size());
            while (!buffer2.isEmpty()) {
                clonedMessageList.add(buffer2.poll());
            }
            log.info("buffer2 cleared");

            savePrivateMessages(clonedMessageList);
        }
    }

    @Async
    private <E extends Message> void save(List<E> list) {
        messageRepository.saveAll(list);
        log.info("list of {} elems saved", list.size());
    }

    @Async
    private void savePrivateMessages(List<PrivateMessage> list) {
        privateMessageRepository.saveAll(list);
        log.info("list of {} elems saved", list.size());
    }

    // TODO save right away when user disconnects
}
