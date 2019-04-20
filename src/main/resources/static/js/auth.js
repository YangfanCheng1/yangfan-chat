// Switched to Thymeleaf
function signUp() {
    var formData = {username: $("#username").val(), password: $("#password").val()};
    $.post({
        url: "/api/add-new-user",
        contentType: "application/json",
        data: JSON.stringify(formData),
        dataType: "json",
        cache: false,
        success: function(data, textStatus, xhr) {
            console.log(xhr);
            console.log(data);
            window.location.href = "/log-in.html";
        },
        error: function(err) {
            console.log(err.responseText);
        }
    });
}

// switched to Thymeleaf
function signIn() {
    var formData = {"username": $("#usernameSignIn").val()};
    console.log(formData);
    $.ajax({
        url: "/api/sign-in",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(formData),
        dataType: "json",
        cache: false,
        success: function(data, textStatus, xhr) {
            console.log(xhr);
            console.log(data);
        },
        error: function(err) {
            console.log(err);
        }
    });
}

$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
//
//    $("#sign-up-form").on('submit', function (e) {
//        e.preventDefault();
//    });
//    $("#chat-message-form").on('submit', function (e) {
//        e.preventDefault();
//    });
//
//    $("#sign-up-submit").click(function() { signUp(); });
});