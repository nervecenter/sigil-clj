// DEFINE our simple redirect function for when the user is not logged in
function redirectToLogin() {
    window.location = "login";
}

/*
 *  SET our mousover events to change the arrow when we hover
 */

$(".votelogin, .voteup, .unvoteup")
    .mouseover(function () {
        var $this = $(this);
        if ($this.hasClass('voteup') || $this.hasClass("votelogin")) {
            $this.attr("src", "/images/notvoted-hover.png");
        }
    })
    .mouseout(function () {
        var $this = $(this);
        if ($this.hasClass('voteup') || $this.hasClass("votelogin")) {
            $this.attr("src", "/images/notvoted.png");
        }
    });

// ENSURE the user is redirected to login when they're not logged in
$(".votelogin").click(redirectToLogin);

/*
 *  DEFINE our vote functions, which (un)votes and then inverts the button
 *  for the next click to have the opposite action
 */

function voteup(event) {
    $.post("/voteup", {issue_id: event.data.issueid}, function () {
        event.data.$button.removeClass("voteup")
            .addClass("unvoteup")
            .attr("src", "/images/voted.png")
            .off("click")
            .click({ $button: event.data.$button, issueid: event.data.issueid }, unvoteup);
        var $count = $("#count-" + event.data.issueid);
        $count.html(parseInt($count.html(), 10) + 1);
    });
}

function unvoteup(event) {
    $.post("/unvoteup", {issue_id: event.data.issueid}, function () {
        event.data.$button.removeClass("unvoteup")
            .addClass("voteup")
            .attr("src", "/images/notvoted-hover.png")
            .off("click")
            .click({ $button: event.data.$button, issueid: event.data.issueid }, voteup);
        var $count = $("#count-" + event.data.issueid);
        $count.html(parseInt($count.html(), 10) - 1);
    });
}

/*
 *  BIND our vote buttons with their actions when the page renders
 */

$(".voteup").each(function () {
    $(this).click({ $button: $(this), issueid: $(this).data("issueid") }, voteup);
});

$(".unvoteup").each(function () {
    $(this).click({ $button: $(this), issueid: $(this).data("issueid") }, unvoteup);
});

$(".vote-button").popover(
    { trigger: "hover",
      title: "Vote up this post!",
      content: "If you agree or would like to see a response.",
      placement: "top" }
);

$(".votelogin").popover(
    { trigger: "hover",
      title: "Login or register to vote",
      content: "Help contribute to the discussion on Sigil!",
      placement: "top" }
);


/*
 *  Reports stuff
 */

function report(event) {
    $.post("/report", {issue_id: event.data.issueid}, function () {
        event.data.$button.removeClass("unreported")
            .addClass("reported")
            .off("click")
            .click({ $button: event.data.$button, issueid: event.data.issueid }, unreport);
    });
}

function unreport(event) {
    $.post("/unreport", {issue_id: event.data.issueid}, function () {
        event.data.$button.removeClass("reported")
            .addClass("unreported")
            .off("click")
            .click({ $button: event.data.$button, issueid: event.data.issueid }, report);
    });
}

$(".unreported").each(function () {
    $(this).click({ $button: $(this), issueid: $(this).data("issueid") }, report);
});

$(".reported").each(function () {
    $(this).click({ $button: $(this), issueid: $(this).data("issueid") }, unreport);
});

/*
 * Popover for report
 */

$('.report-flag').popover(
    { trigger: "hover",
      title: "Report this post",
      content: "This post is toxic or subtracts value from the discussion.",
      placement: "bottom" }
);
