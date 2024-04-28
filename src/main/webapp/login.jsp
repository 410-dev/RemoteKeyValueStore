<%--
  Created by IntelliJ IDEA.
  User: Hoyoun Song
  Date: 2024-04-29
  Time: 오전 4:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
<form action="auth" method="post">
    <input type="password" name="key" placeholder="Key">
    <input type="hidden" name="redirect" value="mgmt.jsp">
    <input type="submit" value="Login">
</form>
</body>
</html>
