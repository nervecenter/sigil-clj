function shownotifications() {
    $("#header-user-icon").off("click").click(hidenotifications);

    var $noteparent = $("<div>")
        .attr("id", "note-parent")
        .addClass("panel-body");

    var $callout = $("<img>")
        .addClass("callout")
        .attr("src", "images/callout.png");

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

    $.get("/checknotes", function (data) {
        var notes = JSON.parse(data);
        if (notes[0] == 0) {
            var $nonotes = $("<h5>").attr("style", "text-align:center;").html("No notifications. You're all caught up. :)")
            $("#note-parent").append($nonotes);
        } else {
            $.each(notes, function (index, note) {
                //img container for the user icon
                var $img = $("<img>")
                    .addClass("media-object")
                    .addClass("notification-icon")
                    .attr("src", note.icon);
                //icon for from user -- need to change return value of from userid to a link to their icon instead
                var $imganchor = $("<a>")
                    .addClass("media-left")
                    .append($img);

                //link container that allows clicking the notification to take you to where it is
                var $message = $("<a>")
                    .attr("href", note.url)
                    .html(note.message);

                var $deletebutton = $("<span>")
                    .addClass("glyphicon glyphicon-remove-sign")
                    .click({ id: note.id }, deletenotification);
                // var $deleteanchor = $("<a>")
                //     .append($deletebutton)
                //     .attr("href", "#");
                var $controls = $("<div>")
                    .addClass("media-right")
                    .append($deletebutton);

                //div container that includes the title and link
                var $mediabody = $("<div>")
                    .addClass("media-body")
                    .append($message);

                //parent div for entire notification
                var $media = $("<div>")
                    .addClass("media")
                    .append($imganchor)
                    .append($mediabody)
                    .append($controls);
                $("#note-parent").append($media);
            });
        }
    });
}

function deletenotification(event) {
    var $note = $(this);
    $.post("/deletenote", { id: event.data.id }, function () {
        $note.parent().parent().remove();
        if ($("#note-parent").html() == "") {
            var $nonotes = $("<h5>")
                .attr("style", "text-align:center;")
                .html("No notifications. You're all caught up. :)")
            $("#note-parent").append($nonotes);
        }
        refreshnumnotes();
    });
}

function hidenotifications() {
    $("#header-user-icon").off("click").click(shownotifications);
    $("#notifications-shade").remove();
}

function refreshnumnotes() {
    $.get("/numnotes", function (data) {
        var parsed = JSON.parse(data);
        if (parsed.numnotes > 0) {
            $("#num-notes-back").show();
            $("#num-notes").html(parsed.numnotes).show();
        } else {
            $("#num-notes-back").hide();
            $("#num-notes").hide();
        }
    });
}

$(document).ready(function () {
    $("#header-user-icon").click(shownotifications);
});
