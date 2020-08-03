
/**
 * Wrapper class for Stomp Client
 */
class StompClient {
    constructor(){
        this.client = Stomp.over(new SockJS('/sockJS'));
        this.connected = false;
    }

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

            self.client.subscribe(`/topic/all`, event => {
               const room = JSON.parse(event.body);
               commit("SET_ROOM_STATUS", room);
            });

            self.client.subscribe(`/user/queue/notify`, event => {
                // room - {id: 25, name: user1, isPrivate: true}
                const room = JSON.parse(event.body);
                commit('ADD_ROOM', room);
                this.subscribe(room, commit);
            })
        });
    }

    subscribe(room, commit) {
        console.log("Subscribing to room ", room);
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

    sendEvent(fromUser, event) {
        const url = `/chat-app/user`;
        const ev =
            {
                fromUser: {
                    id: fromUser.id,
                    name: fromUser.name
                },
                room: event.room
            };
        this.client.send(
            url,
            {username: event.user.name},
            JSON.stringify(ev)
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

export {StompClient, MessagePayload, Room, User}
