<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">

<title>JMGCFinance Login</title>

<link rel="stylesheet" href="lib/bootstrap-3.3.7/css/bootstrap.min.css" />
<link rel="stylesheet" href="app/css/crf.css">

<script>
    var validateForm = function() {

        var emailRegexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}])|(([\w\-]+\.)+[a-zA-Z]{2,}))$/;
        var passwordRegexp = /(?=(:?.*[^A-Za-z0-9].*))(?=(:?.*[A-Z].*){1,})(?=(:?.*\d.*){1,})(:?^[\w\&\?\!\$\#\*\+\=\%\^\@\-\.\,\_]{8,32}$)/;

        var displayLoginFormServerErrorResponse = document.getElementById("displayLoginFormServerErrorResponse");
        var displayLoginFormErrorResponse = document.getElementById("displayLoginFormErrorResponse");

        var input1Value = document.loginForm.input1.value;
        var input2Value = document.loginForm.input2.value;

        if (displayLoginFormServerErrorResponse != null && displayLoginFormServerErrorResponse != undefined) {
            displayLoginFormServerErrorResponse.style.display = "none";
        }

        if (input1Value == null || input1Value == undefined || input1Value == "") {
            displayLoginFormErrorResponse.style.display = "block";
            return false;

        } else if (input2Value == null || input2Value == undefined || input2Value == "") {
            displayLoginFormErrorResponse.style.display = "block";
            return false;

        }

        if (!passwordRegexp.test(input2Value)) {
            displayLoginFormErrorResponse.style.display = "block";
            return false;
        }

        return true;
    };
</script>

</head>
<body>
    <div class="container-fluid crf-center">
        <img src="app/img/crf_logo.jpeg" alt="" style="display: block; margin-left: auto; margin-right: auto; width: 40%;">
        <h2 style="text-align: center;">
            <c:out value="${appName}" />
        </h2>
        <form method="post" name="loginForm" action="perform_login" onsubmit="return validateForm()" novalidate>

            <div class="form-group">
                <label for="input1">Email</label> <input type="text" class="form-control" id="input1" name="input1" placeholder="Email" title="Email" maxlength="64" required
                    autofocus autocomplete="off">
            </div>
            <div class="form-group">
                <label for="input2" class="pull-left">Password</label> <label class="pull-right"><a href="passwordForgot.html" tabindex="-1">Forgot Password?</a></label> <input
                    type="password" class="form-control" id="input2" name="input2" placeholder="Password" title="Password" maxlength="32" required autocomplete="off">
            </div>

            <c:choose>
                <c:when test="${not empty failure}">
                    <div id="displayLoginFormServerErrorResponse" style="display: block;" class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span>
                        <c:out value="${failure}" />
                    </div>
                </c:when>
                <c:when test="${not empty logout}">
                    <div id="displayLoginFormServerErrorResponse" style="display: block;" class="alert alert-success" role="alert">
                        <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> <span class="sr-only">Success: </span>
                        <c:out value="${logout}" />
                    </div>
                </c:when>
                <c:when test="${not empty denied}">
                    <div id="displayLoginFormServerErrorResponse" style="display: block;" class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-ban-circle" aria-hidden="true"></span> <span class="sr-only">Error: </span>
                        <c:out value="${denied}" />
                    </div>
                </c:when>
                <c:when test="${not empty deleted}">
                    <div id="displayLoginFormServerErrorResponse" style="display: block;" class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-lock" aria-hidden="true"></span> <span class="sr-only">Error: </span>
                        <c:out value="${deleted}" />
                    </div>
                </c:when>
                <c:when test="${not empty locked}">
                    <div id="displayLoginFormServerErrorResponse" style="display: block;" class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-lock" aria-hidden="true"></span> <span class="sr-only">Error: </span>
                        <c:out value="${locked}" />
                    </div>
                </c:when>
                <c:when test="${not empty credentialsUpdated}">
                    <div id="displayLoginFormServerErrorResponse" style="display: block;" class="alert alert-success" role="alert">
                        <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> <span class="sr-only">Success: </span>
                        <c:out value="${credentialsUpdated}" />
                    </div>
                </c:when>
            </c:choose>

            <div id="displayLoginFormErrorResponse" style="display: none;" class="alert alert-danger" role="alert">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> Invalid email and/or password.
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </div>

        </form>

    </div>

</body>
</html>