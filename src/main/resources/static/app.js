import {store} from "./js/store.js";

var vm = new Vue({
    el: '#app',
    store,
    components: {
        'navbar': httpVueLoader("js/components/navbar.vue"),
        'left-panel': httpVueLoader("js/components/left-panel.vue"),
        'right-panel': httpVueLoader("js/components/right-panel.vue"),
        "test": httpVueLoader("js/components/test.vue")
    },
    computed: {
        getUserId() {
            return this.$store.state.user.id;
        },
        getUserName() {
            return this.$store.state.user.name;
        },
        getUserRooms() {
            return this.$store.state.user.rooms;
        },
        getCurRoom() {
            return this.$store.state.curRoom;
        }
    },
    created() {
        this.$store.dispatch('init');
    }
});