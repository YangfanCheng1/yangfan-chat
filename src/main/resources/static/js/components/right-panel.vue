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
        <div id="chat-message-form">
            <b-form @submit.prevent="onSubmit">
                <!-- quill svg taken from https://www.svgrepo.com/svg/88095/quill -->
                <div class="message-bar">
                    <b-input type="text" v-model="content" placeholder="Hello"></b-input>
                    <div class="quill">
                        <button class="custom-button" type="submit">
                            <svg id="quill" width="32px" height="32px" viewBox="0 0 363.818 363.818">
                                <path d="M358.872,0.841c-3.196-1.538-7.014-0.931-9.572,1.526c-19.515,18.728-53.141,46.415-102.511,71.961
                                c-2.159,1.118-3.737,3.106-4.333,5.463c-4.028,15.908-11.933,33.271-23.492,51.607l-4.705,7.462l8.772-38.205
                                c0.715-3.115-0.378-6.368-2.828-8.42c-2.451-2.052-5.846-2.556-8.786-1.303l-1.015,0.428
                                C110.79,133.291,81.352,198.24,72.67,233.22c-3.013,12.141-4.516,24.163-4.465,35.738c0.02,4.466,0.272,8.722,0.75,12.705
                                l-66.39,67.703c-3.211,3.273-3.246,8.505-0.078,11.822c1.667,1.745,3.904,2.629,6.149,2.629c2.02,0,4.045-0.717,5.664-2.164
                                l182.428-163.851c0.896,0.059-103.874,109.806-102.925,109.806c14.22,0,33.863-6.555,56.804-18.95
                                c30.935-16.717,65.508-42.37,99.979-74.185c2.832-2.612,3.551-6.805,1.753-10.213c-1.798-3.407-5.662-5.181-9.42-4.315
                                l-21.363,4.904l7.465-4.706c20.835-13.136,40.313-21.511,57.891-24.897c1.901-0.367,3.622-1.372,4.875-2.849
                                c41.348-48.75,58.853-96.919,66.256-128.743c2.69-11.567,4.579-23.134,5.607-34.38C363.972,5.742,362.069,2.379,358.872,0.841z"/>
                            </svg>
                        </button>
                    </div>
                </div>
            </b-form>
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
#chat-message-form {
    margin-top: 10px;
    margin-bottom: 10px;
}
.message-bar {
    position: relative;
    width: 100%;
    display: inline-block;
}
.quill {
    position: absolute;
    right: 0;
    top: 2px;
}
path:hover{
    fill:orange;
}
.custom-button {
    border:none;
    background: none;
}
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