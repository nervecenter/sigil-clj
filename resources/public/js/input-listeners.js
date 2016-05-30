// $("input[type=file]").each(function () {
// });

function EnableTagSubmit() {
    var $button = $("#new-tag-submit");
    if ($("#tag-name").val() != "") {
        $button.removeClass("disabled").removeClass("btn-default").addClass("btn-success");
    } else if (!$button.hasClass("disabled")) {
        $button.removeClass("btn-success").addClass("disabled").addClass("btn-default");
    }
}

$("#tag-name").keyup(EnableTagSubmit);

function EnableFileSubmit ($fileinput) {
    $fileinput.parent().parent().parent().parent().parent().children().last().children().first().removeClass("disabled").removeClass("btn-default").addClass("btn-success");
    $fileinput.off("change");
}

function ChangeBanner ($browsebutton) {
    var label = $browsebutton.val().replace(/\\/g, '/').replace(/.*\//, '');
    $browsebutton.parent().parent().parent().children().last().attr("value", label);
}

$(".btn-file :file").each(function () {
    $(this).change(function () {
        EnableFileSubmit($(this));
        ChangeBanner($(this));
    });
});

$("#policy-accept").change(function () {
  var checked = $(this).prop("checked");
  if (checked) {
    $("#sign-up-button").removeClass("disabled");
  } else {
    $("#sign-up-button").addClass("disabled");
  }
});

function AddChangeTagForm (event) {
    var $button = event.data.$button;
    var orgid = event.data.orgid;
    var tagid = event.data.tagid;
    var parentpanel = $button.parent().parent();

    var orgidfield = $("<input>")
        .prop("name", "orgid")
        .prop("value", orgid)
        .prop("type", "hidden");

    var tagidfield = $("<input>")
        .prop("name", "tagid")
        .prop("value", tagid)
        .prop("type", "hidden");

    var browsebutton = $("<div>")
        .addClass("input-group-btn")
        .append($("<span>")
                .attr("class", "btn btn-default btn-file")
                .append("Browse")
                .append($("<input>")
                        .attr("id", "tag-icon-browse")
                        .attr("name", "icon-30-upload")
                        .attr("type", "file")
                        .change(function () {
                            EnableFileSubmit($(this));
                            ChangeBanner($(this));
                        })));

    var filebanner = $("<input>")
        .attr("class", "form-control image-input")
        .attr("id", "tag-icon-banner")
        .attr("name", "tag-icon-banner")
        .attr("type", "text")
        .prop("readonly", true);

    var fileformgroup = $("<div>")
        .addClass("form-group")
        .append($("<div>")
                .addClass("input-group")
                .append(browsebutton)
                .append(filebanner));

    var submitgroup = $("<div>")
        .addClass("form-group")
        .append($("<input>")
                .attr("type", "submit")
                .attr("value", "Upload new 30x30 tag icon")
                .attr("class", "btn btn-default form-control disabled"));

    var form = $("<form>").prop("method", "post")
        .prop("action", "/tagicon30")
        .attr("enctype", "multipart/form-data")
        .append(orgidfield)
        .append(tagidfield)
        .append(fileformgroup)
        .append(submitgroup);

    parentpanel.append($("<br>")).append(form);

    $button.removeClass("btn-default")
        .addClass("btn-warning")
        .html("Cancel")
        .off("click")
        .click(function () {
            parentpanel.children().last().remove();
            parentpanel.children().last().remove();
            $(this).removeClass("btn-warning")
                .addClass("btn-default")
                .html("Change icon")
                .off("click")
                .click({ $button: $(this),
                         orgid: $(this).data("orgid"),
                         tagid: $(this).data("tagid") },
                       AddChangeTagForm)
        });
}

$(".change-tag-icon").each(function () {
    $(this).click({ $button: $(this),
                    orgid: $(this).data("orgid"),
                    tagid: $(this).data("tagid") },
                  AddChangeTagForm);
});
