
function init () {
    var url = "greetingData";
    $.getJSON(url,
        {
            name : "Tomcat"
        },
        function (data) {
        $("#welcome").text(data.greeting + ", and you id is " + data.id + ".");
    });
}

init();