<template>
    <div class="margin-bottom-sm">
        <form id="search-user-room-form" method="post" v-on:submit.prevent="onSubmit">
            <input v-on:keyup="search" v-model="query" name="search-user" class="form-control" placeholder="Search an user">
        </form>
        <div v-if="isSearchOn" id="user-search-result">
            <ul class="rooms">
                <li @click="addRoom(user.id, user.name)"
                    v-for="user in searchResult"
                    v-bind:key="user.id"
                    v-bind:data-id="user.id"
                    v-bind:data-private="true"
                    v-bind:data-name="user.name">
                    {{user.name}}
                </li>
            </ul>
        </div>
        <div v-if="!isSearchOn" id="user-subscribed-rooms">
            <ul class="rooms">
                <li @click="setCurRoom(room)"
                    v-for="room in rooms"
                    v-bind:key="room.id + room.name"
                    v-bind:data-id="room.id"
                    v-bind:data-private="room.isPrivate"
                    v-bind:data-name="room.name">
                    {{room.name}}
                </li>
            </ul>
        </div>
    </div>
</template>
    
<script>
module.exports = {
    data() {
        return {
            query: '',
            isSearchOn: false,
            searchResult: []
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
                .then(data => {
                    // i.e {id: 8, name: "user7", isPrivate: true}
                    this.$store.dispatch('addRoom', data);
                    this.$store.dispatch('subscribe', data);
                    this.isSearchOn = false;
                })
                .catch(error => console.log("Couldn't add new room:", error.response.data));
        },
        setCurRoom: function(room) {
            console.log("Switching room: ", room);
            this.$store.dispatch('setCurRoom', room);
            this.$store.dispatch('getMessages', room);
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

</style>