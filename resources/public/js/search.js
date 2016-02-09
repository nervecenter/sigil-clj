$('#site-search-box').autocomplete({
    source: '/search/',
    _renderItem: function( ul, item ) {
        return $( "<li>" )
          .attr( "data-value", item.label )
          .append( item.label )
          .appendTo( ul );
    },
    response: function( event, ui ) {},
    select: function (event, ui) {
        //alert(ui.item.label);
        window.location = ui.item.value;
        ui.item.value = ui.item.label;
    }
});

var typeTimer; // Our timer

$('#issues-by-org-search').keyup(function () {
    clearTimeout(typeTimer);
    typeTimer = setTimeout(SearchIssuesByOrg, 800);
});

$('#issues-by-org-search').keydown(function () {
    clearTimeout(typeTimer);
    var $issues = $("#issues");
    if ($issues.first().id != "loader") {
        $issues.html("<img id=\"loader\" src=\"/Content/Images/ajax-loader.gif\">");
    }
});

function SearchIssuesByOrg() {
    var $searchBox = $('#issues-by-org-search');
    var searchQuery = { id: $searchBox.data('orgid'), term: $searchBox.val() };
    $.ajax({
        url: '/searchissuesbyorg/',
        type: 'POST',
        data: JSON.stringify(searchQuery),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        async: false,
        success: function (data) {
            $("#issues").html("");
            $("#issues").append(data);
        },
        error: function () {
            //alert('Search didn\'t work.');
        }
    });
}

$('#issues-by-product-search').keyup(function () {
    clearTimeout(typeTimer);
    typeTimer = setTimeout(SearchIssuesByProduct, 800);
});

$('#issues-by-product-search').keydown(function () {
    clearTimeout(typeTimer);
    var $issues = $("#issues");
    if ($issues.first().id != "loader") {
        $issues.html("<img id=\"loader\" src=\"/Content/Images/ajax-loader.gif\">");
    }
});

function SearchIssuesByProduct() {
    var $searchBox = $('#issues-by-product-search');
    var searchQuery = { id: $searchBox.data('productid'), term: $searchBox.val() };
    $.ajax({
        url: '/searchissuesbyproduct/',
        type: 'POST',
        data: JSON.stringify(searchQuery),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        async: false,
        success: function (data) {
            $("#issues").html("");
            $("#issues").append(data);
        },
        error: function (e) {
            //alert('Search didn\'t work.');
        }
    });
}

var issuePartialInner = function (partialVM) {
    return "";
}

/*$('#AdminProductSearch').autocomplete({
    source: 'https://localhost:44301/search/AdminProductSearch/'
})

$('#UserSearch').autocomplete({
    source: 'https://localhost:44301/search/AdminUserSearch/',
});
*/
