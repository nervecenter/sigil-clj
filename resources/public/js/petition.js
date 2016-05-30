function AddPetitionForm(event) {
    $button = event.data.$button;
    $adminFooter = event.data.$adminFooter;
    issueid = event.data.issueid;
    orgid = event.data.orgid;

    $button.removeClass("start-petition")
        .addClass("cancel-petition")
        .html("Cancel this petition")
        .off("click")
        .click({ $button: $button,
                 $adminFooter: $adminFooter,
                 issueid: issueid,
                 orgid: orgid },
               RemovePetitionForm);

    var form = $("<form>").prop("method", "post")
        .prop("action", "/petitionissue");

    var issueidfield = $("<input>")
        .prop("name", "issue-id")
        .prop("value", issueid)
        .prop("type", "hidden");

    var orgidfield = $("<input>")
        .prop("name", "org-id")
        .prop("value", orgid)
        .prop("type", "hidden");

    var body = $("<textarea>")
        .addClass("form-control")
	.addClass("petition-input-box")
	.prop("name", "body")
	.prop("placeholder", "Why should this be removed?");

    var submit = $("<a>")
	.addClass("btn")
	.addClass("btn-primary")
        .addClass("form-control")
	.html("Petition Sigil to remove issue")
	.click({ $button: $button,
	         $adminFooter: $adminFooter,
		 issueidfield: issueidfield,
                 orgidfield: orgidfield,
                 bodyfield: body },
               SubmitPetition);

    var form = $("<div>")
        .addClass("petition-form")
        .append(issueidfield)
	.append(orgidfield)
    	.append($("<div>").addClass("form-group").append(body))
    	.append($("<div>").addClass("form-group").append(submit));

    $adminFooter.append($("<br>"))
        .append(form);
}

function RemovePetitionForm(event) {
    $button = event.data.$button;
    $adminFooter = event.data.$adminFooter;
    issueid = event.data.issueid;
    orgid = event.data.orgid;

    $adminFooter.children().last().remove();
    $adminFooter.children().last().remove();

    $button.removeClass("cancel-petition")
        .addClass("start-petition")
        .html("Petition removal of this issue")
        .off("click")
        .click({ $button: $button,
                 $adminFooter: $adminFooter,
                 issueid: issueid,
                 orgid: orgid },
               AddPetitionForm);
}

function SubmitPetition(event) {
    var petitiondata = {
    	issueid: parseInt(event.data.issueidfield.val()),
        orgid: parseInt(event.data.orgidfield.val()),
        body: event.data.bodyfield.val()
    }

    $.post("/postpetition", petitiondata, function () {
    	$button = event.data.$button;
    	$adminFooter = event.data.$adminFooter;

    	$adminFooter.children().last().remove();
    	$adminFooter.children().last().remove();

    	$button.removeClass("cancel-petition")
	    .addClass("disabled")
            .html("Petition submitted.")
            .off("click");
    });
}

$(".start-petition").each(function() {
    $(this).click({ $button: $(this),
                    $adminFooter: $(this).parent(),
                    issueid: $(this).data("issueid"),
   		    orgid: $(this).data("orgid") },
                  AddPetitionForm);
});
