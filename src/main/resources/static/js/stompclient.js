
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
    connect(user, commit) {
        let self = this;
        if (self.client == null) {
            console.log("StompClient wasn't instantiated!");
            return;
        }
        self.client.connect({}, data => {
            console.log("Stomp client connected to the server: ", data);
            console.log("Subscribing to rooms...");
            const rooms = user.rooms;

            rooms.forEach(room => {
                self.client.subscribe(`/topic/room.${room.id}`, message => {
                    console.log("Receiving message: ", message.body);
                    commit('ADD_MESSAGE', {key: room.id, val: JSON.parse(message.body)});
                });
            });
        });
    }

    subscribe(room, commit) {
        this.client.subscribe(`/topic/room.${room.id}`, message => {
            console.log("Receiving message: ", message.body);
            commit('ADD_MESSAGE', {key: room.id, val: JSON.parse(message.body)});
        })
    }

    sendMessage(room, message) {
        const url = `/chat-app/room/${room.id}`;
        console.log("Producing message to " + url);
        this.client.send(
            url,
            {},
            JSON.stringify(new MessagePayload(room, message))
        )
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

class User {
    constructor(userId, userName) {
        this.userId = userId;
        this.username = userName;
    }
}

class MessagePayload {
    constructor(room, message) {
        this.room = room;
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
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.action = action;
    }
}

export {StompClient, MessagePayload, Room, User}
