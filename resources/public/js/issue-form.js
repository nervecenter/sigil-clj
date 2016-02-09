$("#product-select-group").hide();

$("#issues-by-org-search").keyup(function () {
    $("#new-feedback-group").show();
    $(this).off("keyup");
});

$("#new-feedback-group").hide();

$("#new-feedback-button").click(function () {
    var $issuesearchpostform = $("#issue-search-post-form");
    var $newlabel = $("<label>").attr("for", "title").html("Title");
    $("#suggest-label").replaceWith($newlabel);
    $("#new-feedback-group").remove();
    $("#product-select-group").show();

    var $textlabel = $("<label>").attr("for", "text").html("Explanation");
    var $text = $("<textarea/>")
                    .attr("id", "issues-by-org-search")
                    .attr("type", "text")
                    .attr("name", "text")
                    .attr("placeholder", "Talk about your feedback in-depth")
                    .attr("style", "height:100px;min-height:100px;max-width:100%;min-width:100%;")
                    .addClass("form-control");
    var $submit = $("<button/>")
                    .attr("type", "submit")
                    .attr("class", "btn btn-primary pull-right")
                    .attr("style", "padding:4px 9px;")
                    .text("Post new feedback");

    $issuesearchpostform.append($("<div/>").addClass("form-group").append($textlabel).append($text));
    $issuesearchpostform.append($("<div/>").addClass("form-group").append($submit));
});