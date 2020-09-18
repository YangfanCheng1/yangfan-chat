<template>
    <div>
        <v-row>
            <div class="profile">
                <v-icon large>mdi-account-box</v-icon>
                {{ curRoom.name }}
            </div>
            <v-spacer>
            </v-spacer>
            <div>
                <v-btn icon>
                    <v-icon color="blue darken-2">mdi-email</v-icon>
                </v-btn>
                <v-btn icon>
                    <v-icon color="blue darken-2">mdi-phone</v-icon>
                </v-btn>
                <v-btn icon>
                    <v-icon color="blue darken-2">mdi-video</v-icon>
                </v-btn>
            </div>
        </v-row>

        <v-row>
            <v-col  cols="12">
                <div id="chat-text-area" class="max-height-50vh messages">
                    <div v-for="message in messages">
                        <div>{{ message.fromUserName }}</div>
                        <span v-bind:class="align(message.fromUserId)">{{ message.content }}</span>
                    </div>
                </div>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12">
                <v-form @submit.prevent="onSubmit">
                    <v-text-field filled clearable hide-details v-model="content" placeholder="Say Hello" append-icon="mdi-send" @click:append="onSubmit">
                    </v-text-field>
                </v-form>
            </v-col>
        </v-row>

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
            this.content = '';
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
.profile {
    padding-left: 10px;
    font-size: 22px;
}
.messages div {
    margin: 6px 0;
    font-size: 14px;
    font-weight: 300;
}
.max-height-50vh {
    padding-left: 10px;
    border-top: 1px solid #e4e6e8;
    height: 50vh;
    overflow: auto;
    -webkit-overflow-scrolling: touch;
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