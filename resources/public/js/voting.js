// DEFINE our simple redirect function for when the user is not logged in
function redirectToLogin() {
    window.location = "/login";
}

/*
 *  SET our mousover events to change the arrow when we hover
 */

$(".votelogin, .voteup, .unvoteup")
    .mouseover(function () {
        var $this = $(this);
        if ($this.hasClass('voteup') || $this.hasClass("votelogin")) {
            $this.attr("src", "/Content/Images/notvoted-hover.png");
        }
    })
    .mouseout(function () {
        var $this = $(this);
        if ($this.hasClass('voteup') || $this.hasClass("votelogin")) {
            $this.attr("src", "/Content/Images/notvoted.png");
        }
    });

// ENSURE the user is redirected to login when they're not logged in
$(".votelogin").click(redirectToLogin);

/*
 *  DEFINE our vote functions, which (un)votes and then inverts the button
 *  for the next click to have the opposite action
 */

function voteup(event) {
    $.post("/voteup/" + event.data.issueid + "/", function () {
        event.data.$button.removeClass("voteup")
            .addClass("unvoteup")
            .attr("src", "/Content/Images/voted.png")
            .off("click")
            .click({ $button: event.data.$button, issueid: event.data.issueid }, unvoteup);
        var $count = $("#count-" + event.data.issueid);
        $count.html(parseInt($count.html(), 10) + 1);
    });
}

function unvoteup(event) {
    $.post("/unvoteup/" + event.data.issueid + "/", function () {
        event.data.$button.removeClass("unvoteup")
            .addClass("voteup")
            .attr("src", "/Content/Images/notvoted-hover.png")
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





/*function voteup(votebutton, issueid) {
    $.ajax({
        type: "POST",
        url: '/voteup/' + issueid + '/',
        success: function () {
            votebutton.classList.remove('voteup');
            votebutton.classList.add('unvoteup');
            votebutton.src = "/Content/Images/voted.png";
            votebutton.setAttribute('onClick', 'unvoteup(this, ' + issueid + ')');
            var count = document.getElementById("count-" + issueid);
            count.innerHTML = (parseInt(count.innerHTML, 10) + 1);
        },
        error: function (ts) {
            //alert('Could not vote up.');
            alert(ts.responseText);
        }
    });
}

function unvoteup(votebutton, issueid) {
    $.ajax({
        type: "POST",
        url: '/unvoteup/' + issueid + '/',
        success: function () {
            votebutton.classList.remove('unvoteup');
            votebutton.classList.add('voteup');
            votebutton.src = "/Content/Images/notvoted-hover.png";
            votebutton.setAttribute('onClick', 'voteup(this, ' + issueid + ')');
            var count = document.getElementById("count-" + issueid);
            count.innerHTML = (parseInt(count.innerHTML, 10) - 1);
        },
        error: function (ts) {
            alert('Could not unvote up.');
            alert(ts.responseText);
        }
    });
}


function official_voteup(votebutton, officialId) {

}

function official_unvoteup(votebutton, officialId)
{

}

function official_votedown(votebutton, officialId)
{

}

function official_unvotedown(votebutton, officialId)
{

}


function votehover(votebutton) {
    if (votebutton.classList.contains('voteup')) {
        votebutton.src = "/Content/Images/notvoted-hover.png";
    }
}

function voteunhover(votebutton) {
    if (votebutton.classList.contains('voteup')) {
        votebutton.src = "/Content/Images/notvoted.png";
    }
}*/