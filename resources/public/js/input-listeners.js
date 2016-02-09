$("input[type=file]").change(function () {
    $(this).parent().parent().parent().parent().parent().children().last().children().first().removeClass("disabled").removeClass("btn-default").addClass("btn-success");
});


function EnableProductSubmit() {
    var $button = $("#new-product-submit");
    if ($("#product-name").val() != "" && $("#product-url").val() != "") {
        $button.removeClass("disabled").removeClass("btn-default").addClass("btn-success");
    } else if (!$button.hasClass("disabled")) {
        $button.removeClass("btn-success").addClass("disabled").addClass("btn-default");
    }
}

$("#product-name").keyup(EnableProductSubmit);
$("#product-url").keyup(EnableProductSubmit);

$(".btn-file :file").change(function () {
    var label = $(this).val().replace(/\\/g, '/').replace(/.*\//, '');
    $(this).parent().parent().parent().children().last().attr("value", label);
});