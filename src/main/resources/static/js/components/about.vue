<template>
    <b-container>
        <b-row>
            <b-col col lg="2"></b-col>
            <b-col cols="8">
                <div class="box-content">
                    <h3>About</h3>
                    <p>
                        This project was my first attempt at building a chat-like application. Some of technologies I have incorporated in
                        this project are Spring Websocket, Spring MVC, Spring Redis (as session storage), & Spring JPA using MySQL. Source
                        can be found at <a href="https://github.com/YangfanCheng1/yangfan-chat" class="purple heavy">github</a>,
                        although it's not up to date as most of changes are still in my local.
                    </p>
                    <h3>Skill Breakdown</h3>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="90">Spring Boot</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="80">Java</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="50">MongoDB</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="30">VueJs</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="30">Others</b-progress-bar>
                    </b-progress>
                </div>
            </b-col>
            <b-col col lg="2"></b-col>
        </b-row>
    </b-container>
</template>

<script>
module.exports = {

}
</script>

<style scoped>
h3 {
    font-size: 22px;
    color: #333;
    margin-bottom: 10px;
    margin-top: 10px;
}

p {
   font-size: 16px;
}

.heavy {
    font-weight: bolder;
}
</style>