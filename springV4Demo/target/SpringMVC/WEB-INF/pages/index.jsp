<%--
  Date: 13-10-29
--%>
<%@ page contentType="text/html; utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Welcome</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="css/bootstrap.css" rel="stylesheet" media="screen">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="js/html5shiv.js"></script>
    <script src="js/respond.min.js"></script>
    <![endif]-->
</head>
<jsp:include page="header.jsp" />
<div class="container">
    <div class="jumbotron">
        <h2 id="welcome"></h2>
        <div class="alert alert-info">
            <strong>Hi from server side</strong>
            ${introSelf}
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
<!--init page use ajax-->
<script type="text/javascript" src="js/init.js"></script>
</body>
</html>
