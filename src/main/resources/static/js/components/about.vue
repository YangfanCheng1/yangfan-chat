<template>
    <b-container>
        <b-row>
            <b-col col lg="2"></b-col>
            <b-col cols="8">
                <div class="box-content">
                    <div class="links">
                        <a href="https://github.com/YangfanCheng1">
                            <i class="fa fa-github fa-2x" aria-hidden="true"></i>
                        </a>
                        <a href="https://stackoverflow.com/users/4493352">
                            <i class="fa fa-stack-overflow fa-2x" aria-hidden="true"></i>
                        </a>
                        <a href="#">
                            <i class="fa fa-linkedin-square fa-2x" aria-hidden="true"></i>
                        </a>
                    </div>
                    <h3>About</h3>
                    <p>
<<<<<<< HEAD
                        Hello, my name is Yangfan. I am a software engineer with extensive experience in the Java ecosystem.
                        My expertise lies in designing and developing high-performing microservices deployed in the cloud.
                        I am very proficient in building web applications using technologies like Spring Boot, AWS, Docker, and MongoDB.
                        A lot of what I use during day-to-day job is very much language/framework specific,
                        but I spent a lot of time in gaining advanced knowledge about various subjects even if they may not directly be used at work.
                        My focus is on continually challenging myself with new emerging technologies in order to build applications on them.
                        My recent interests are in Reactor, RSocket and anything AI related subjects like LLMs (Don't fight the trend!)
=======
                        This project was my first attempt at building a chat-like application. Some of technologies I have incorporated in
                        this project are Spring Websocket, Spring MVC, Spring Redis (as session storage), & Spring JPA using MySQL. Source
                        can be found at <a href="https://github.com/YangfanCheng1/yangfan-chat" class="purple heavy">github</a>
>>>>>>> e10e9443ae4325bd8ba8572399d48a8076619644
                    </p>
                    <h3>Skill Breakdown</h3>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="90">Spring/Spring Boot</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="80">Java</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="50">MongoDB/DynamoDB</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="50">MySQL/Postgres</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="30">VueJs</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="30">Docker/Kubernetes</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="20">Python</b-progress-bar>
                    </b-progress>
                    <b-progress :max="max" class="mb-3">
                        <b-progress-bar :value="10">Others</b-progress-bar>
                    </b-progress>
                </div>
            </b-col>
            <b-col col lg="2"></b-col>
        </b-row>
    </b-container>
</template>

<script>
module.exports = {
    data() {
        return {
            max: 100
        }
    }
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
.fa-stack-overflow:hover {
    color: orange;
    cursor: pointer;
}
.fa-github:hover {
    color: gray;
    cursor: pointer;
}
.fa-linkedin-square:hover {
    color: #0077b5;
    cursor: pointer;
}
.links {
    text-align: center;
}
.fa {
    margin-left: 5px;
    margin-right: 5px;
}
</style>