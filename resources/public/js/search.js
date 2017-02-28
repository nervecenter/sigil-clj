$('#site-search-box').autocomplete({
    source: '/searchbar',
    _renderItem: function( ul, item ) {
        return $("<li>")
          .attr("data-value", item.label)
          .append(item.label)
          .appendTo(ul);
    },
    response: function(event, ui) {
    },
    select: function (event, ui) {
        window.location = ui.item.value;
        ui.item.value = ui.item.label;
    }
});

var typeTimer; // Our timer

$('#issues-by-org-search').keyup(function () {
    clearTimeout(typeTimer);
    typeTimer = setTimeout(SearchIssuesByOrg, 500);
});

$('#issues-by-org-search').keydown(function () {
    clearTimeout(typeTimer);
    var $issues = $("#issues");
    if ($issues.first().id != "loader") {
        $issues.html("<img id=\"loader\" src=\"/images/ajax-loader.gif\">");
    }
});

$("#tag-select").change(function () {
    alert($(this).val());
});

function SearchIssuesByOrg() {
    var $searchBox = $('#issues-by-org-search');
    var selectedtagid = $("#tag-select").val();
    var searchQuery = { orgid: $searchBox.data('orgid'),
                        tagid: selectedtagid,
                        term: $searchBox.val() };

    $.get("/searchorgissues", searchQuery, function (data) {
        $("#issues").html("").append(data);
    }).error(function() {
        var $wrong = $("<h3>")
            .html("Something went wrong. :( Try again later.")
        $("#issues").html("").append($wrong);
    });
}
