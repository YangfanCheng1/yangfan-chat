import {store} from "./js/store.js";

Vue.component('navbar', httpVueLoader("js/components/navbar.vue"));
Vue.component('left-panel', httpVueLoader("js/components/left-panel.vue"));
Vue.component('right-panel', httpVueLoader("js/components/right-panel.vue"));

var vm = new Vue({
    el: '#app',
    store,
    components: {
        'app': httpVueLoader("js/app.vue"),
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
    // data : {}
    // methods : {}
    // computed: {} getters
});