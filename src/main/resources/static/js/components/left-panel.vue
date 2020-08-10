<template>
    <div class="margin-bottom-sm">
        <div class="height-50">
            <b-form method="post" v-on:submit.prevent="onSubmit" autocomplete="off">
                <b-input v-on:keyup="search" v-model="query" name="search-user" placeholder="Search an user"></b-input>
            </b-form>
        </div>
        <div v-if="isSearchOn" id="user-search-result">
            <div class="rooms">
                <div @click="addRoom(user.id, user.name)"
                    v-for="user in searchResult"
                    v-bind:key="user.id"
                    v-bind:data-id="user.id"
                    v-bind:data-private="true"
                    v-bind:data-name="user.name">{{user.name}}</div>
            </div>
        </div>
        <div v-else id="user-subscribed-rooms">
            <div class="rooms">
                <div @click="setCurRoom(room, idx)"
                     v-for="(room, idx) in rooms"
                     :key="idx"
                     :data-id="room.id"
                     :data-private="room.isPrivate"
                     :data-name="room.name"
                     :class="[{selected: idx === activeIdx}, {'weight-900': hasPush(room.status)}]">
                    <div v-bind:class="['status', getStatus(room)]"></div>
                    {{room.name}}
                    <b-badge v-if="hasPush(room.status)" variant="dark" pill>1</b-badge>
                </div>
            </div>
        </div>
    </div>
</template>
    
<script>
module.exports = {
    data() {
        return {
            query: '',
            isSearchOn: false,
            searchResult: [],
            activeIdx: null
        }
    },
    methods: {
        search: function() {
            if (this.query.length > 0) {
                this.isSearchOn = true;
                axios
                    .get("/api/users?keyword=" + this.query)
                    .then(res => res.data)
                    .then(data => (this.searchResult = data))
                    .catch(error => console.error("Couldn't load search:", error));
            } else {
                this.isSearchOn = false;
            }
        },
        addRoom: function(userId, username) {
            let csrf_token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            let data = {
                fromUser: {
                    id: this.$root.getUserId,
                    name: this.$root.getUserName
                },
                toUser: {
                    id: userId,
                    name: username
                },
                isPrivate: true
            };

            let headers =
            {
                headers : {
                    "X-CSRF-TOKEN" : csrf_token,
                    "Content-Type" : "application/json"
                }
            };

            axios
                .post("/api/room", data, headers)
                .then(res => res.data)
                .then(room => {
                    // i.e {id: 8, name: "user7", isPrivate: true}
                    // data - {user: {id: 123, name: foo}, room: {id: 25, name: user1, isPrivate: true}}
                    this.$store.dispatch('sendEvent', {user: data.toUser, room: room}); // Notify the other user
                    this.isSearchOn = false;
                })
                .catch(error => console.log("Couldn't add new room:", error.response.data));
        },
        setCurRoom: function(room, idx) {
            this.activeIdx = idx;
            console.log("Switching room: ", room);
            this.$store.commit('RESET', idx);
            this.$store.dispatch('setCurRoom', room);
            this.$store.dispatch('getMessages', room);
        },
        getStatus: function (room) {
            switch (room.status) {
                case 'ONLINE':
                case 'PUSH_ONLINE':
                    return 'online';
                case 'OFFLINE':
                case 'PUSH_OFFLINE':
                    return 'offline';
                default:
                    return '';
            }
        },
        hasPush: function (status) {
            return status === 'PUSH_ONLINE' || status === 'PUSH_OFFLINE';
        },
        init: function() {
            console.log("Mounting left panel");
        }
    },
    computed: {
        rooms () {
            return this.$store.getters.rooms;
        }
    },
    mounted() {
        this.init()
    }
}
</script>

<style scoped>
.selected {
    background-color: #B0E0E6;
}
.weight-900 {
    font-weight: 900;
}
.rooms div:hover {
    background: #f8e7e7;
    cursor: pointer;
}
.status {
    width: 10px;
    height: 10px;
    display: inline-block;
}
.online {
    background: #86d597;
    border: 1px solid #9c9c9c;
    border-radius: 2px;
}
.offline {
    border: 1px solid #9c9c9c;
    border-radius: 2px;
}
.none {

}
.background-none {
    background: none;
}
.height-50 {
    height: 50px;
}
</style>