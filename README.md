# yangfan-chat
An experimental chat-app with features such as user account flow, private chatting, and 
room chatting (not yet available).
###  backend
Main modules used are directly provided out of box by Spring
* Spring Boot
* Spring WebSocket
* Spring Redis (User Session Storage)
* Spring JPA (MySQL)
* etc
### frontend
* Vue, with VueX and VueRouter (failed to incorporate for SPA)
* BoostrapVue (as CSS framework, in hindsight, I think Vuetify is superior)
* Sring MVC with thymeleaf to go hand in hand with Spring Security to control user
registration, login and logout flow
### screenshots
* 
![alt text](images/sample1.PNG)
* 
![alt text](images/sample2.PNG)