<template>
    <div>
        <div id="target-room-area" class="max-height-50 border-bottom-sm">
            <div id="target-room-profile">{{ curRoom.name }}</div>
        </div>
        <div id="chat-text-area" class="max-height-50vh">
            <ul class="messages">
                <li v-for="message in messages">
                    <div>{{ message.fromUserName }}</div>
                    <span v-bind:class="align(message.fromUserId)">{{ message.content }}</span>
                </li>
            </ul>
        </div>
        <div>
            <form id="chat-message-form" @submit.prevent="onSubmit">
                <div class="form-group">
                    <input type="text" id="message" v-model="content" class="form-control">
                </div>
                <button type="submit" id="send-message" class="btn btn-primary">Submit</button>
            </form>
        </div>
    </div>
</template>

<script>
module.exports = {
    data () {
        return {
            content: '',
            isSelf: true,
            counter: 0
        }
    },
    methods: {
        onSubmit: function() {
            if (this.content.length <= 0) {
                console.log("Content cannot be empty");
                return;
            }

            let message = {
                fromUserId: this.user.id,
                fromUserName: this.user.name,
                content: this.content
            };

            this.$store.dispatch('sendMessage', message);
        },
        align: function(id) {
            return (id !== this.user.id ? 'orange' : 'blue') + "-background"
        },
        scroll: function(element, to, duration) {
            if (duration <= 0) return;
            var difference = to - element.scrollTop;
            var perTick = difference / duration * 10;

            setTimeout(function() {
                element.scrollTop = element.scrollTop + perTick;
                if (element.scrollTop === to) return;
                scroll(element, to, duration - 10);
            }, 10);
        }
    },
    computed: {
        user () {
            return this.$store.getters.user;
        },
        curRoom () {
            return this.$store.getters.curRoom;
        },
        messages () {
            return this.$store.getters.messages;
        }
    },
    mounted () {
        console.log("Mounting right panel");
    },
    updated () {
        console.log("updated");
        let container = document.getElementById("chat-text-area");
        container.scrollTop = container.scrollHeight;
    }
}
</script>

<style scoped>
.messages li {
    margin: 6px 0;
}
.messages div {
    font-size: 14px;
    font-weight: 200;
}
.messages span {
    color: white;
    font-size: 16px;
    margin: 2px 0;
    border-radius: 25px;
    padding: 5px 12px;
}

.blue-background {
    background: #8eb2ff;
}

.orange-background {
    background: #ffbb30;
}
</style>