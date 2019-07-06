// Global
var activeRoom = null;
var client = null;
// user = {userId, username, rooms: [{displayId, displayName, isPrivate}...]}
var user = {userId: null, username: null, rooms:[]};


$(function () {

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $("#chat-message-form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#send-message").click(function() {
        let message = $("#message").val();
        let roomName = $("#target-room-profile").text();
        sendMessage(message, roomName);
        scrollToBottom();
    });

    $("#search-user-room-input").on('keyup', function () {
        searchUsers();
    });

    // delegated events
    $("#user-search-result").on("click", "li", function() {
        let _id = parseInt($(this).attr('data-id'));
        let _isPrivate = $(this).attr('data-private') == 'true';
        let _name = $(this).attr('data-name');
        $('#user-search-result').hide();
        $('#user-subscribed-rooms').show();
        $("#user-subscribed-rooms ul").prepend(displayUserListhtml(_id, _name));

        createNewPrivateRoom(_id, _isPrivate, _name);
        changeActiveRoom(_id, _isPrivate, _name);
    });

    $("#user-subscribed-rooms").on("click", "li", function() {
        $('#target-room-profile').text($(this).attr('data-name'));
        changeActiveRoom(parseInt($(this).attr('data-id')),
                        $(this).attr('data-private') == 'true',
                        $(this).attr('data-name'));
    });

    username = $("#username").text();
    init(username);

});

/*---- functionality ----*/

function searchUsers() {
    var var0 = $("#search-user-room-input").val();
    if (var0.length !== 0) {
        $('#user-search-result').show();
        $('#user-subscribed-rooms').hide();
        getUsersContaining(var0);
    } else {
        $('#user-search-result').hide();
        $('#user-subscribed-rooms').show();
    }
}

function changeActiveRoom(id, isPrivate, name) {
    user.rooms.forEach((room) => {
        if (room.displayName === name) {
            activeRoom = room;
        }
    });

    let curRoom = null;
    if (isPrivate) {
        curRoom = {isPrivate: true, fromUserId: this.user.userId, toUserId: id};
        if (privateChatMap.get(id).length === 0) {
            getRoomChatHistory(curRoom, isPrivate, id);
        } else {
            displayChat(id, isPrivate);
        }
    } else {
        curRoom = {isPrivate: false, roomId: id};
        if (groupChatMap.get(id).length === 0) {
            getRoomChatHistory(curRoom, isPrivate, id);
        } else {
            displayChat(id, isPrivate);
        }
    }

    console.log("Switched to " + activeRoom.displayName);
}

function sendMessage(message, roomName) {
    (activeRoom.isPrivate)
    ? client.sendPrivateMessage(new PrivateMessage(user.userId, activeRoom.displayId, message, user.username, roomName))
    : client.sendGroupMessage(new GroupMessage(user.userId, activeRoom.displayId, message, user.username));
}

function createNewPrivateRoom(id, isPrivate, name) {
    user.rooms.push({displayId: id, displayName: name, isPrivate: true});
    addNewRoom(id, isPrivate, name);
    privateChatMap.set(id, []);
    client.newEvent(user.userId, activeRoom.displayId, "CREATE");
}


/*---------------------
        ajax
----------------------*/

function init(username) {
    fetch("/api/user/" + username)
        .then(userData => userData.json())
        .then(userData => {
            console.log(userData);
            this.user = userData;
            this.client = new StompClient();
            this.client.connect(this.user);
            this.activeRoom = this.user.rooms[0];

            // Init privateChatMap
            for (let room of this.user.rooms) {
                if (room.isPrivate) {
                    privateChatMap.set(room.displayId, []);
                } else {
                    groupChatMap.set(room.displayId, []);
                }
            }
        })
        .catch(error => console.error("Error in getting user data:", error));
}

function getUsersContaining(var0) {
    fetch("/api/get-all-users-containing?keyword=" + var0)
        .then(res => res.json())
        .then(data => displaySearchedUsers(data))
        .catch(error => console.error('Error:', error));
}

/*
--> private: curRoom = {isPrivate: true, fromUserId: this.user.userId, toUserId: id}
--> group: curRoom = {isPrivate: false, roomId: id}
<-- [fromUserId: 1, fromUserName: 'name', message: 'message', datetime: "2019-xx..."]
*/
function getRoomChatHistory(curRoom, isPrivate, id) {
    let url = (isPrivate)
        ? `/api/chat-history/private?fromUserId=${curRoom.fromUserId}&toUserId=${curRoom.toUserId}`
        : `/api/chat-history/group?roomId=${curRoom.roomId}`;

    fetch(url)
        .then(res => res.json())
        .then(response => {
            if (isPrivate) {
                privateChatMap.get(id).push(...response);
                displayChat(id, isPrivate);
            } else {
                groupChatMap.get(id).push(...response);
                displayChat(id, isPrivate);
            }
        })
        .catch(error => console.error('Error:', error));
}

/*
--> private: curRoom = {}
*/
function addNewRoom(id, isPrivate, name) {
    let curRoom = {fromUserId: this.user.userId, toUserId: id, isPrivate: true};
    var csrf_token = $("meta[name='_csrf']").attr("content");
    var csrf_header = $("meta[name='_csrf_header']").attr("content");
    var myHeaders = new Headers({
        "X-CSRF-TOKEN" : csrf_token,
        "Content-Type" : "application/json"
    });

    fetch('/api/room', {
        method: "POST",
        cache: "no-cache",
        headers: myHeaders,
        body: JSON.stringify(curRoom)
    })
    .then(res => console.log(res.json()))
    .catch(error => console.error("Error:", error));
}
