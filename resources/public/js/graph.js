// To use google charts you must load 
// <script type="text/javascript" src="https://www.google.com/jsapi"></script>
// as well as this file on the page you want a chart to appear


google.load('visualization', '1', { 'packages': ['corechart'] });

//default data load function

$(document).ready(function () {
    if (document.getElementById('org_chart_div') != null) {
        var orgURL = get_org_url();
        $.ajax({
            url: "/default_graph/" + orgURL,
            success: function (org_data) {
                //alert("Graphs are cool");
                var chart_data = new google.visualization.DataTable();
                chart_data.addColumn({ type: 'date', id: "Date" });
                chart_data.addColumn({ type: 'number', id: "Count" });

                $.each(org_data, function (index, data) {
                    chart_data.addRow([new Date(data.viewDate), data.viewCount]);
                });

                var default_options = {
                    title: 'Org Data',
                    hAxis: { title: 'Past Week', },
                    vAxis: { title: 'Number of Users' }
                };

                var default_chart = new google.visualization.LineChart(document.getElementById('org_chart_div'));
                default_chart.draw(chart_data, default_options);
            }
        });
    }
    else if(document.getElementById('issue_chart_div') != null)
    {
        var orgURL = get_org_url();
        var issueURL = get_issue_url();
        $.ajax({
            url: "/default_graph/" + orgURL +"/"+issueURL,
            success: function (issue_data) {
                //alert("Graphs are cool");
                var chart_data = new google.visualization.DataTable();
                chart_data.addColumn({ type: 'date', id: "Date" });
                chart_data.addColumn({ type: 'number', id: "Count" });

                $.each(issue_data, function (index, data) {
                    chart_data.addRow([new Date(data.viewDate), data.viewCount]);
                });

                var default_options = {
                    title: 'Issue Data',
                    hAxis: { title: 'Past Week', },
                    vAxis: { title: 'Number of Users' }
                };

                var default_chart = new google.visualization.LineChart(document.getElementById('issue_chart_div'));
                default_chart.draw(chart_data, default_options);
            }
        });
    }
    //$("#dpstart").datepicker();
    //$("#dpend").datepicker();
    BindDatePickers();
    BindDataButton();

    var $controlsCopy;
    var $hider = $("#data-controls-hider");
    var showing = true;
    $hider.click(function () {
        if (showing) {
            showing = !showing;
            $controlsCopy = $("#data-controls").clone();
            $("#data-controls").remove();
            $hider.attr("src", "/Content/Images/heirarchy-hidden.png");
        } else if (!showing) {
            showing = !showing;
            $("#data-header").after($controlsCopy);
            $hider.attr("src", "/Content/Images/heirarchy-extended.png");
            BindDatePickers();
            BindDataButton();
        }
    });
});

function BindDataButton() {
    $("#selected-data").change(function () {
        var $button = $("#data-button");
        //var value = $(this).find(":selected").text;
        if ($(this).find(":selected").text() == "Pick chart data") {
            $button.addClass("disabled");
            $button.unbind("click");
        } else {
            $button.removeClass("disabled");
            $button.click(Custom_Org_Data);
        }
    });
}

function BindDatePickers() {
    //would be cool to have these auto popluate to the last week duration
    var nowTemp = new Date();
    var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
    var month = now.getMonth() + 1;
    var monthTwoDigit = (month > 9 ? "" + (month + 1) : "0" + month);

    var weekago = new Date();
    weekago.setDate(now.getDate() - 7);
    var weekagomonth = weekago.getMonth() + 1;
    var weekagomonthTwoDigit = (weekagomonth > 9 ? "" + (weekagomonth + 1) : "0" + weekagomonth);

    $("#dpstart").datepicker({
        onRender: function (date) {
            return date.valueOf() > now.valueOf() ? 'disabled' : '';
        }
    }).val(weekagomonthTwoDigit + "/" + weekago.getDate() + "/" + weekago.getFullYear());
    $("#dpend").datepicker({
        onRender: function (date) {
            return date.valueOf() > now.valueOf() ? 'disabled' : '';
        }
    }).val(monthTwoDigit + "/" + now.getDate() + "/" + now.getFullYear());
}

function Custom_Org_Data() {
    var adate = $("#dpstart").val();
    var start_date_str = $("#dpstart").val();
    var stop_date_str = $("#dpend").val();
    var date_dif_days = (stop_date_ms - start_date_ms) / 86400000;

    var orgURL = get_org_url();
    var dataOption = document.getElementById('selected-data').value;
    var start_date_ms = jsDateToCSharp(start_date_str);
    var stop_date_ms = jsDateToCSharp(stop_date_str);

    $.get("/custom_graph/" + orgURL + "/" + dataOption + "/" + start_date_ms + "/" + stop_date_ms,
        function (org_data) {
            var chart_data = new google.visualization.DataTable();
            chart_data.addColumn({ type: 'date', id: "Date" });
            chart_data.addColumn({ type: 'number', id: "Count" });

            $("#data-period").html("from " + start_date_str + " to " + stop_date_str);

            $.each(org_data, function (index, data) {
                chart_data.addRow([new Date(data.viewDate), data.viewCount]);
            });

            var default_options = {
                title: dataOption + "Data",
                hAxis: { title: date_dif_days.toFixed(0) + ' days ending with ' + $("#dpend").val() },
                vAxis: { title: 'Number of Users' }
            };

            var default_chart = new google.visualization.LineChart(document.getElementById('org_chart_div'));
            
            default_chart.draw(chart_data, default_options);
        }
    );

    // GET TOP 10 ISSUES FOR PERIOD
    $.get("/customtopissues/" + orgURL + "/" + start_date_ms + "/" + stop_date_ms,
        function (top_issues) {
            $("#top-issues-parent").html("").html(top_issues);
        }
    );

    // GET TOP 10 UNRESPONDED ISSUES FOR PERIOD
    $.get("/customunrespondedissues/" + orgURL + "/" + start_date_ms + "/" + stop_date_ms,
        function (unresponded_issues) {
            $("#response-issues-parent").html("").html(unresponded_issues);
        }
    );

    // GET TOP 10 RISING ISSUES FOR PERIOD
    $.get("/customunderdogissues/" + orgURL + "/" + start_date_ms + "/" + stop_date_ms,
        function (rising_issues) {
            $("#rising-issues-parent").html("").html(rising_issues);
        }
    );

    /*var issueURL = "/custom_data/"+orgURL+"/"+start_date_ms+"/"+stop_date_ms;
    $.ajax({
        url: issueURL,
        success: function (issue_data) {

        }
    })*/
}

function Custom_Issue_Chart() {
    var dataOption = document.getElementById('selected-data').value;
    var start_date = jsDateToCSharp($("#datepickerStart").datepicker("getDate"));
    var stop_date = jsDateToCSharp($("#datepickerStop").datepicker("getDate"));

    var orgURL = get_org_url();
    var issueURL = get_issue_url();
    var URL = "/custom_graph/" + orgURL + "/" + issueURL + "/" + dataOption + "/" + start_date + "/" + stop_date;
    $.ajax({
        url: URL,
        success: function (issue_data) {
            var chart_data = new google.visualization.DataTable();
            chart_data.addColumn({ type: 'date', id: "Date" });
            chart_data.addColumn({ type: 'number', id: "Count" });

            $.each(issue_data, function (index, data) {
                chart_data.addRow([new Date(data.viewDate), data.viewCount]);
            });

            var default_options = {
                title: dataOption + "Data",
                hAxis: { title: 'Past Week', },
                vAxis: { title: 'Number of Users' }
            };

           
            var default_chart = new google.visualization.LineChart(document.getElementById('issue_chart_div'));

            default_chart.draw(chart_data, default_options);
        }
    });
}


function jsDateToCSharp(date)
{
    var time = new Date(date).getTime();

    return time;
}

function get_org_url() {
    var url = window.location.href;

    var orgURL = url.split("/")[3];
   
    //need to add a callback to db to verify this is actually an org
    return orgURL;
};


function get_issue_url() {
    var url = window.location.href;
    var issueURL = url.split("/")[4];

    return issueURL;
}