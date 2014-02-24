<%--
  Date: 13-10-28
  Time: 下午5:05
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>SpringMVC + jQuery Auto-complete testing</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="css/bootstrap.css" rel="stylesheet" media="screen">
    <link href="css/autoComplete.css" rel="stylesheet" media="screen">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="js/html5shiv.js"></script>
    <script src="js/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<jsp:include page="header.jsp"></jsp:include>
<div class="container">
    <div class="panel panel-success">
        <div class="col-xs-12 col-md-12 panel-heading">
            <h2>Welcome to test ajax autocomplete page</h2>
        </div>
        <div class="panel-body">
            <div class="row">
                <form class="form-inline" action="javascript:void(0);" role="form" id="form1-submit">
                    <div class="form-group col-md-8 col-xs-12">
                        <label class="sr-only" for="inputField1">Please input a program language</label>
                        <input class="form-control" id="inputField1" type="text" name="inputField1" placeholder="Plz input a programme launguage"/>
                    </div>
                    <button class="btn btn-default col-md-4 col-xs-12" type="submit">Ok</button>
                </form>
            </div>
            <div class="row marginTopDown10">
                <div class="col-md-3"></div>
                <div class="col-md-6 col-xs-12 alert alert-info" id="msg-box1">
                    <b>server says:</b>
                    <span id="msg"></span>
                </div>
                <div class="col-md-3"></div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"></jsp:include>
<script src="js/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
    $('#inputField1').autocomplete({
        serviceUrl: '${pageContext.request.contextPath}/autoComplete/getTags',
        paramName: "tagName",
        delimiter: ",",
        transformResult: function (response) {
            return {
                //must convert json to javascript object before process
                suggestions: $.map($.parseJSON(response), function (item) {
                    return { value: item.tagName, data: item.id };
                })
            };
        }
    });
    $("#form1-submit").submit(function () {
        $.get("autoComplete/submit",
            {
                inputField1: $("input#inputField1").val()
            },
            function (data) {
                $("#msg").text(data);
                $("#msg-box1").show();
            });
    });
</script>
</body>
</html>
