function shownotifications() {
    $("#header-user-icon").off("click").click(hidenotifications);

    var $noteparent = $("<div>")
        .attr("id", "NoteParent")
        .addClass("panel-body");

    var $callout = $("<img>")
        .addClass("callout")
        .attr("src", "/Content/Images/callout.png");

    var $panel = $("<div>")
        .addClass("panel")
        .addClass("panel-default")
        .addClass("notifications-panel")
        .append($noteparent);

    var $shade = $("<div>")
        .attr("id", "notifications-shade")
        .append($callout)
        .append($panel);

    $("#navbar-header").append($shade);

    $.get("/check_notes",
        function (data) {
            if (data.response == "none") {
                var $nonotes = $("<h5>").attr("style", "text-align:center;").html("No notifications. You're all caught up. :)")
                $("#NoteParent").append($nonotes);
            } else {
                $.each(data, function (index, Note) {
                    //img container for the user icon
                    var $img = $("<img>")
                        .addClass("media-object")
                        .addClass("notification-icon")
                        .attr("src", Note.icon);
                    //icon for from user -- need to change return value of from userid to a link to their icon instead
                    var $imganchor = $("<a>")   
                        .addClass("media-left")
                        .append($img);

                    //link container that allows clicking the notification to take you to where it is
                    var $notificationtext = $("<a>")
                        .attr("href", Note.url)
                        .html(Note.title);

                    var $deletebutton = $("<span>")
                        .addClass("glyphicon glyphicon-remove-sign");
                    var $deleteanchor = $("<a>")
                        .append($deletebutton)
                        .attr("href", "#")
                        .click({ id: Note.id }, deletenotification);
                    var $controls = $("<div>")
                        .addClass("media-right")
                        .append($deleteanchor);

                    //div container that includes the title and link
                    var $mediabody = $("<div>")
                        .addClass("media-body")
                        .append($notificationtext);

                    //parent div for entire notification
                    var $media = $("<div>")
                        .addClass("media")
                        .append($imganchor)
                        .append($mediabody)
                        .append($controls);
                    $("#NoteParent").append($media);
                });
            }
        }
    );

}

function deletenotification(event) {
    var $note = $(this);
    $.post("/delete_notification/" + event.data.id, function () {
        $note.parent().parent().remove();
        if ($("#NoteParent").html() == "") {
            var $nonotes = $("<h5>").attr("style", "text-align:center;").html("No notifications. You're all caught up. :)")
            $("#NoteParent").append($nonotes);
        }
        refreshnumnotes();
    });
}

function hidenotifications() {
    $("#header-user-icon").off("click").click(function () { shownotifications() });
    $("#notifications-shade").remove();
}

function refreshnumnotes() {
    $.get("/num_notes", function (data) {
        if (data.numnotes > 0) {
            $("#num-notes-back").show();
            $("#num-notes").html(data.numnotes).show();
        } else {
            $("#num-notes-back").hide();
            $("#num-notes").hide();
        }
    });
}