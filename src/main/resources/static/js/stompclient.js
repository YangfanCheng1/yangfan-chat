
/**
 * Wrapper class for Stomp Client
 */
class StompClient {
    constructor(){
        this.client = Stomp.over(new SockJS('/sockJS'));
        this.connected = false;
    }

    // refer to http://jmesnil.net/stomp-websocket/doc
    // client.connect(headers, connectCallback);
    connect(user) {
        if (this.client == null) return;
        this.client.connect({}, function (data) {
            console.log("Stomp client connected to the server.");
            this.connected = true;
            console.log("Subscribing to rooms...");
            this.subscribe(user);
        }.bind(this));
    }

    subscribe(user) {
        let rooms = user.rooms;
        let userId = user.userId;
        var self = this;
        if (self.client == null) return;
        // Only subscribe to group rooms
        rooms.forEach(function(room) {
            if (!room.isPrivate) {
                self.client.subscribe('/topic/group.' + room.displayId, function (chatMessage) {
                    displayGroupChatMessage(JSON.parse(chatMessage.body));
                });
            }
        })
        self.client.subscribe('/topic/private.' + userId, function (roomData) {
            if (roomData.isNew) {
                $("#user-subscribed-rooms ul").prepend(roomData.userId, roomData.username);
                rooms.push({displayId: roomData.fromUserId, displayName: roomData.fromUserName, isPrivate: true});
            }

            displayPrivateChatMessage(JSON.parse(roomData.body));
        });
    }

    sendPrivateMessage(privateMessage) {
        displayPrivateChatMessage(privateMessage);
        this.client.send(
            "/chat-app/private/" + privateMessage.toUserId
            ,{}
            ,JSON.stringify(privateMessage)
        );
    }

    sendGroupMessage(groupMessage) {
        // client.send("/queue/test", {priority: 9}, "Hello, STOMP");
        this.client.send("/chat-app/group/" + groupMessage.roomId
            ,{}
            ,JSON.stringify(groupMessage)
        );

    }

    newEvent(fromUserId, toUserId, action) {
        this.client.send("/chat-app/all/", {}
            ,JSON.stringify(new Event(fromUserId, toUserId, action))
        );
    }

    disconnect() {
        if (this.client != null) {
            this.client.disconnect();
            console.log("See you next time!");
            this.connected = false;
        } else {
            console.log("Wasn't connected to begin with!");
        }
    }
}

class User{
    constructor(userId, userName) {
        this.userId = userId;
        this.username = userName;
    }
}

class GroupMessage {
    constructor(fromUserId, roomId, message, fromUserName) {
        this.fromUserId = fromUserId;
        this.message = message;
        this.roomId = roomId;
        this.fromUserName = fromUserName;
    }
}

// {fromUserId, toUserId, message}
class PrivateMessage {
    constructor(fromUserId, toUserId, message, fromUserName, toUserName) {
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
        this.message = message;
    }
}

class Room {
    constructor(roomId, roomName, toUserId) {
        this.roomId = null;
        this.roomName = null;
        this.toUserId = toUserId;
    }
}

class Event {
    constructor(fromUserId, toUserId, action) {
        this.fromUserId = fromUserId,
        this.toUserId = toUserId,
        this.action = action;
    }
}
