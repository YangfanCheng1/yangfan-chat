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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageService {

    private final Object mutex = new Object();

    private final EntityManager entityManager;
    private final PrivateRoomRepository privateRoomRepo;
    private final MessageRepository messageRepo;
    private final PrivateMessageRepository privateMessageRepo;
    private final ApplicationEventListener ael;
    public MessageService(EntityManager entityManager,
                          PrivateRoomRepository privateRoomRepo,
                          MessageRepository messageRepo,
                          PrivateMessageRepository privateMessageRepo,
                          ApplicationEventListener ael) {
        this.entityManager = entityManager;
        this.privateRoomRepo = privateRoomRepo;
        this.messageRepo = messageRepo;
        this.privateMessageRepo = privateMessageRepo;
        this.ael = ael;
    }

    private static final int MESSAGE_THRESHOLD = 5;
    private Deque<PrivateMessage> buffer = new ArrayDeque<>(MESSAGE_THRESHOLD);

    public void handleEvent(EventMessage event) {
        switch (event.getAction()) {
            case "CREATE":
                PrivateRoom newPrivateRoom = PrivateRoom.builder()
                        .user1(new User(event.getFromUserId()))
                        .user2(new User(event.getToUserId()))
                        .build();
                privateRoomRepo.save(newPrivateRoom);
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
            List<PrivateMessage> pms = privateMessageRepo.findMessages(user1, user2);
            return pms.stream()
                    .map(privateMessage -> MessageDto.builder()
                        .fromUserId(privateMessage.getFromUser().getUserId())
                        .fromUserName(privateMessage.getFromUser().getUsername())
                        .timestamp(privateMessage.getTimestamp())
                        .message(privateMessage.getMessage()).build())
                    .collect(Collectors.toList());
        } else {
            List<Message> messages = messageRepo.findByRoomOrderByIdDesc(new Room(activeRoom.getRoomId()));
            return messages.stream()
                    .map(message -> MessageDto.builder()
                        .fromUserId(message.getUser().getUserId())
                        .fromUserName(message.getUser().getUsername())
                        .timestamp(message.getTimestamp())
                        .message(message.getMessage()).build())
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void add(GroupChatMessage groupChatMessage) {
        Room room = new Room();
        room.setRoomId(groupChatMessage.getRoomId());
        User user = new User();
        user.setUserId(groupChatMessage.getUserId());
        Message message = Message.builder()
                .room(room).user(user).message(groupChatMessage.getMessage()).timestamp(Instant.now())
                .build();

        entityManager.persist(message);
    }

    /**
     * case 1: only one user logs in, message is persisted into database right away (infrequent)
     * case 2: user logs out, clear buffer
     * case 3: both users log in, message is saved to buffer (frequent)
     */
    public void add(PrivateChatMessage privateChatMessage) {
        User fromUser = new User();
        fromUser.setUserId(privateChatMessage.getFromUserId());
        User toUser = new User();
        toUser.setUserId(privateChatMessage.getToUserId());
        Instant now = Instant.now();
        PrivateMessage message = PrivateMessage.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .message(privateChatMessage.getMessage())
                .timestamp(privateChatMessage.getTimestamp()).build();

        // If toUser isn't in session
        if (!ael.hasUser(ael.getLoggedInUsers(), toUser.getUsername())) {
            // If toUser disconnects from listening to queue
            if (ael.hasUser(ael.getLoggedOutUsers(), toUser.getUsername())) {
                clearPrivateMessageBuffer();
                ael.getLoggedOutUsers().remove(toUser.getUsername());
            }

            privateMessageRepo.save(message);
        } else {
            synchronized (mutex) {
                buffer.offer(message);
            }
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void clearPrivateMessageBuffer() {
        synchronized (mutex) {
            if (buffer.size() < MESSAGE_THRESHOLD) {
                return;
            }

            List<PrivateMessage> clonedMessageList = new ArrayList<>(buffer.size());
            while (!buffer.isEmpty()) {
                clonedMessageList.add(buffer.poll());
            }
            log.info("buffer cleared");

            savePrivateMessages(clonedMessageList);
        }
    }

    @Async
    private void savePrivateMessages(List<PrivateMessage> list) {
        privateMessageRepo.saveAll(list);
        log.info("list of {} elems saved", list.size());
    }

}
