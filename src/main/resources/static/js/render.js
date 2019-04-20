var groupChatMap = new Map(); // {roomId: [{fromUserId, message, timestamp}]}
var privateChatMap = new Map(); // {fromUserId: [{fromUserId, message, timestamp}]}

function displaySearchedUsers(users) {
    var usersList = "";
    users.forEach(user => {usersList += displayUserListhtml(user.userId, user.username);});
    $("#user-search-result ul").html(usersList);
}

function displayChat(displayId, isPrivate) {
    let chatArr = isPrivate
    ? privateChatMap.get(displayId)
    : groupChatMap.get(displayId);

    let chatHtml = "";

    if (chatArr == undefined) {
        $("#chat-text-area ul").html(chatHtml);
    } else {
        for (let msg of chatArr) {
            chatHtml += (msg.fromUserId == user.userId)
            ? `<li><span>${msg.message}</span></li>`
            : `<li class='right-align'><span>${msg.message}<span></li>`
        }
        $("#chat-text-area ul").html(chatHtml);
    }
}

// <-- {userId, roomId, message}
function displayGroupChatMessage(messageData) {
    const userId = messageData.userId;
    const roomId = messageData.roomId;
    const message = messageData.message;
    const timestamp = messageData.timestamp;

    if (messageData.roomId == activeRoom.displayId) {
        (messageData.userId == this.user.userId)
        ? $("#chat-text-area ul").append("<li><span>" + message + "</span></li>")
        : $("#chat-text-area ul").append("<li class='right-align'><span>" + message + "<span></li>");
    }

    if (groupChatMap.has(roomId)) {
        groupChatMap.get(roomId).push({userId: userId, message: message, timestamp: timestamp});
    } else {
        var arr = [];
        arr.push({userId: userId, message: message, timestamp: timestamp});
        groupChatMap.set(roomId, arr);
    }
}

// {fromUserId, toUserId, message, fromUserName}
function displayPrivateChatMessage(messageData) {
    const fromUserId = messageData.fromUserId;
    const toUserId = messageData.toUserId;
    const message = messageData.message;
    const timestamp = messageData.timestamp;
    const fromUserName = messageData.fromUserName;

    // Render if user is in activeRoom
    if ((messageData.fromUserId == activeRoom.displayId || messageData.toUserId == activeRoom.displayId)
         && activeRoom.isPrivate) {
        (messageData.fromUserId == this.user.userId)
        ? $("#chat-text-area ul").append("<li><span>" + message + "</span></li>")
        : $("#chat-text-area ul").append("<li class='right-align'><span>" + message + "<span></li>");
    }

    // Saving to dictionary
    if (privateChatMap.has(fromUserId)) { // Existing user
        privateChatMap.get(fromUserId).push({fromUserId: fromUserId, message: message, timestamp: timestamp});
    } else if (privateChatMap.has(toUserId)) { // Self message
        privateChatMap.get(toUserId).push({fromUserId: fromUserId, message: message, timestamp: timestamp});
    } else { // Responding to message from a new user.
        $('#user-subscribed-rooms ul').prepend(
            displayUserListhtml(fromUserId, fromUserName)
        );
        user.rooms.push({displayId: fromUserId, displayName: fromUserName, isPrivate: true});
        var arr = [];
        arr.push({fromUserId: fromUserId, message: message, timestamp: timestamp});
        privateChatMap.set(fromUserId, arr);
    }
}

function scrollToBottom() {
    var chatArea = $("#chat-text-area")
    chatArea.animate({ scrollTop: chatArea.prop('scrollHeight')}, 1000);
}

function displayUserListhtml(userId, username) {
    return `<li data-id=${userId} data-private=true data-name=${username}>${username}</li>`;
}
