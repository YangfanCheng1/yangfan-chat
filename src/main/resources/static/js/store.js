import * as models from "./stompclient.js";

export const store = new Vuex.Store({
    state: {
        user: {
            id: -1,
            name: '',
            rooms: [], // {id: 1, name: user2, isPrivate: true, status: online}
        },
        curRoom: {
            id: -1,
            name: '',
            isPrivate: true
        },
        curMessages: {
            messages: []
        },
        messageMap: new Map(), // roomId : messages
        client: new models.StompClient(),
        load: false,
        count: 0
    },
    mutations: {
        SET_USER(state, user) {
            state.user = user
        },
        SET_CUR_ROOM(state, room) {
            console.log("Setting room", room);
            state.curRoom = room;
        },
        SET_CUR_MESSAGES(state, id) {
            state.curMessages.messages = state.messageMap.get(id);
        },
        SET_ROOM_STATUS: (state, entry) => { // entry: {id: -1, name: another_user, status: ONLINE}
            if (entry.status === "ONLINE") state.count++;
            else state.count--;

            state.user.rooms.forEach(room => {
                if (room.name === entry.name) {
                    room.status = entry.status;
                }
            })
        },
        ADD_ROOM(state, room) {
            state.user.rooms.push(room);
        },
        ADD_MESSAGES(state, entry) {
            // roomId: messages
            const key = entry.key;
            const val = entry.val;
            state.curMessages.messages = (state.messageMap.set(key, val)).get(key);
        },
        ADD_MESSAGE(state, entry) {
            const key = entry.key;
            const val = entry.val;
            if (!state.messageMap.has(key)) {
                state.messageMap.set(key, []);
            }
            state.messageMap.get(key).push(val);
            // if user is in curRoom
            if (state.curRoom.id === key) {
                state.curMessages.messages = state.messageMap.get(key);
            } else {
                state.user.rooms.forEach(room => {
                    if (room.id === key) {
                        room.status = "PUSH_ONLINE";
                    }
                })
            }
        },
        RESET(state, idx) {
            const room = state.user.rooms[idx];
            if (room.status === "PUSH_ONLINE") {
                room.status = "ONLINE";
            } else if (room.status === "PUSH_OFFLINE") {
                room.status = "OFFLINE";
            }
        }
        // commit + track state changes actions -> mutations
    },
    actions: {
        init({commit, state, dispatch}) {
            axios
                .get("/api/users")
                .then(res => res.data)
                .then(user => {
                    console.log("Init user: ");
                    console.log(user);
                    commit('SET_USER', user);
                    commit('SET_CUR_ROOM', user.rooms[0]);
                    dispatch('getMessages', user.rooms[0]);
                    state.client.connect(user, commit);
                })
                .catch(error => console.log("Couldn't load user:", error));

            axios
                .get("/api/stats")
                .then(resp => resp.data)
                .then(data => {
                    console.log(data);
                    state.count = data.count;
                })
        },
        setCurRoom({commit, state}, room) {
            commit('SET_CUR_ROOM', room);
            commit('SET_CUR_MESSAGES', state.messageMap.get(room.id));
        },
        addRoom({commit, state}, room) {
            // room - {id: 25, name: user1, isPrivate: true}
            console.log("Adding room, ", room);
            commit('ADD_ROOM', room);
        },
        getMessages({commit, state}, room) {
            const roomId = room.id;
            if (!state.messageMap.has(roomId)) {
                const url = `/api/rooms/${roomId}/messages`;
                state.load = !state.load;
                axios
                    .get(url)
                    .then(res => res.data)
                    .then(messages => {
                        console.log("Getting room messages: ");
                        console.log(messages);
                        commit('ADD_MESSAGES', {key: roomId, val: messages});
                    })
                    .catch(error => console.log("Couldn't load room:", error));
            } else {
                commit('SET_CUR_MESSAGES', roomId);
            }
        },
        sendMessage({commit, state}, message) {
            state.client.sendMessage(state.curRoom, message)
        },
        sendEvent({commit, state}, event) {
            state.client.sendEvent(state.user, event);
        }
        // mutate state (commit is synchronous)
    },
    getters: {
        user: state => {
            return state.user
        },
        rooms: state => {
            return state.user.rooms
        },
        curRoom: state => {
            return state.curRoom
        },
        messages: state => {
            return state.curMessages.messages;
        },
        client: state => {
            return state.client
        },
        messageMap: state => {
            return state.messageMap
        },
        // below isn't cached because it's method style.
        getMessagesByRoomId: (state) => (id) => {
            if (state.messageMap.has(id)) {
                return state.messageMap.get(id);
            } else {
                return [];
            }
        }
        // access state
    }
});
