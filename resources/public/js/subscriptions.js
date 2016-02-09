function subscribe(subbutton, orgurl, type) {
    $.ajax({
        type: "POST",
        url: '/subscribe/' + orgurl + '/' + type + '/',
        success: function () {
            subbutton.classList.remove('subscribe');
            subbutton.classList.remove('btn-primary');
            subbutton.classList.add('unsubscribe');
            subbutton.classList.add('btn-danger');
            subbutton.innerHTML = subbutton.innerHTML.replace('Subscribe to', 'Unsubscribe from');
            subbutton.setAttribute('onClick', 'unsubscribe(this, ' + orgurl + ')');
        },
        error: function (ts) {
            //alert('Could not vote up.');
            alert(ts.responseText);
        }
    });
}

function unsubscribe(subbutton, orgurl, type) {
    $.ajax({
        type: "POST",
        url: '/unsubscribe/' + orgurl + '/' + type + '/',
        success: function () {
            subbutton.classList.remove('unsubscribe');
            subbutton.classList.remove('btn-danger');
            subbutton.classList.add('subscribe');
            subbutton.classList.add('btn-primary');
            subbutton.innerHTML = subbutton.innerHTML.replace('Unsubscribe from', 'Subscribe to');
            subbutton.setAttribute('onClick', 'subscribe(this, ' + orgurl + ')');
        },
        error: function (ts) {
            //alert('Could not unvote up.');
            alert(ts.responseText);
        }
    });
}
