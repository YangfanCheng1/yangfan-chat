<template>
    <div id="header" class="header">
        <nav class="navbar navbar-expand-md navbar-dark bg-black">
            <a class="navbar-brand w-25" href="#">Yangfan's Chat</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="navbar-collapse collapse" id="navbarNavDropdown">
                <ul class="nav navbar-nav mr-auto justify-content-center">
                    <li><a href="#">About This Project</a></li>
                    <li><a href="#">Tools</a></li>
                    <li><a href="#">Contact Me</a></li>

                </ul>
                <ul class="nav navbar-nav mr-auto justify-content-center">
                    <li class="justify-content-end dropdown">
                        <a id="username" class="white dropdown-toggle" href="#" data-toggle="dropdown">{{ user.name }}</a>
                        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <form class="dropdown-item" action="/log-out" method="post">
                                <input class="dropdown-item" type="submit" value="Log Out"/>
                                <input type="hidden" name="_csrf" v-bind:value="token" />
                            </form>
                        </div>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</template>

<script>
module.exports = {
    data () {
        return {
            token: ''
        }
    },
    computed: {
        user() {
            return this.$store.getters.user;
        }
    },
    mounted() {
        this.token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        console.log("Mounting Navbar")
    }
}
</script>

<style scoped>

</style>