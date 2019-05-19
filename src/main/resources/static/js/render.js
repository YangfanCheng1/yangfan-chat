var groupChatMap = new Map(); // {roomId: [{fromUserId, message, timestamp}...]}
var privateChatMap = new Map(); // {fromUserId: [{fromUserId, toUserId, message, timestamp, fromUserName, toUserName}...]}

function displaySearchedUsers(users) {
    var usersList = "";
    users.forEach(user => {usersList += displayUserListhtml(user.userId, user.username);});
    $("#user-search-result ul").html(usersList);
}

function displayChat(displayId, isPrivate) {
    let msgArr = isPrivate
    ? privateChatMap.get(displayId)
    : groupChatMap.get(displayId);

    let chatHtml = "";
    if (msgArr == undefined) {
        $("#chat-text-area ul").html(chatHtml);
    } else {
        var curName = null;
        for (let msg of msgArr) {
            let isSelf = (msg.fromUserId == user.userId);
            let label = "";
            if (curName != msg.fromUserName) {
                label = (isSelf)
                ? `<div>${msg.fromUserName}</div>`
                : `<div class='right-align'>${msg.fromUserName}</div>`;
                curName = msg.fromUserName;
            }

            chatHtml += (isSelf)
            ? `<li>${label}<span>${msg.message}</span></li>`
            : `<li class='right-align'>${label}<span>${msg.message}</span></li>`;
        }
        $("#chat-text-area ul").html(chatHtml);
    }

    scrollToBottom();
}

// <-- {userId, roomId, message, timestamp}
function displayGroupChatMessage(messageData) {
    const userId = messageData.fromUserId;
    const fromUserName = messageData.fromUserName;
    const roomId = messageData.roomId;
    const message = messageData.message;
    const timestamp = messageData.timestamp;

    if (roomId == activeRoom.displayId) {
        if (userId == this.user.userId) {
            $("#chat-text-area ul").append(
                `<li>
                    <div>${fromUserName}</div>
                    <span>${message}<span>
                </li>`);
        } else {
            $("#chat-text-area ul").append(
                `<li class='right-align'>
                    <div class='right-align'>${fromUserName}</div>
                    <span>${message}<span>
                </li>`);
        }
        scrollToBottom();
    }

    if (groupChatMap.has(roomId)) {
        groupChatMap.get(roomId).push({fromUserId:userId, roomId:roomId, message: message, fromUserName: fromUserName});
    } else {
        let arr = [];
        arr.push({fromUserId:userId, roomId:roomId, message: message, fromUserName: fromUserName});
        groupChatMap.set(roomId, arr);
    }
}

/*
--> {fromUserId, toUserId, message, fromUserName, toUserName}
*/
function displayPrivateChatMessage(messageData) {
    const fromUserId = messageData.fromUserId;
    const toUserId = messageData.toUserId;
    const message = messageData.message;
    const timestamp = messageData.timestamp;
    const fromUserName = messageData.fromUserName;
    const toUserName = messageData.toUserName;

    // Render if user is in activeRoom
    if ((messageData.fromUserId == activeRoom.displayId || messageData.toUserId == activeRoom.displayId)
         && activeRoom.isPrivate) {
        if (messageData.fromUserId == this.user.userId) {
            $("#chat-text-area ul").append(
                `<li>
                    <div>${fromUserName}</div>
                    <span>${message}<span>
                </li>`);
        } else {
            $("#chat-text-area ul").append(
                `<li class='right-align'>
                    <div class='right-align'>${fromUserName}</div>
                    <span>${message}<span>
                </li>`);
        }
        scrollToBottom();
    }

    // Saving to dictionary
    if (privateChatMap.has(fromUserId)) { // Existing user
        privateChatMap.get(fromUserId).push(messageData);
    } else if (privateChatMap.has(toUserId)) { // Self message
        privateChatMap.get(toUserId).push(messageData);
    } else { // Responding to message from a new user.
        $('#user-subscribed-rooms ul').prepend(
            displayUserListhtml(fromUserId, fromUserName)
        );
        user.rooms.push({displayId: fromUserId, displayName: fromUserName, isPrivate: true});
        let arr = [];
        arr.push(messageData);
        privateChatMap.set(fromUserId, arr);
    }

    $('#user-subscribed-rooms ul').find(`[data-name='${fromUserName}']`).css("font-weight","Bold");
}

function scrollToBottom() {
    var chatArea = $("#chat-text-area");
    chatArea.animate({ scrollTop: chatArea.prop('scrollHeight')}, 1000);
}

function displayUserListhtml(userId, username) {
    return `<li data-id=${userId} data-private=true data-name=${username}>${username}</li>`;
}
