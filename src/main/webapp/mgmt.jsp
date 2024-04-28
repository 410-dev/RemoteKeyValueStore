<%@ page import="me.hysong.remotekeyvaluestore.Authorization" %>
<%@ page import="me.hysong.remotekeyvaluestore.Database" %><%--
  Created by IntelliJ IDEA.
  User: Hoyoun Song
  Date: 2024-04-29
  Time: 오전 4:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mgmt</title>
</head>
<body>
<%
    if (session.getAttribute("main.authorization") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
    if (!Authorization.authorized(session)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    String list = Database.list();
    String[] listArray = list.split("\n");
%>
<h1>Key List</h1>
<hr>
<form action="control" method="post">
    <input type="hidden" name="action" value="write">
    <input type="text" name="path" placeholder="Location">
    <input type="text" name="value" placeholder="Value">
    <input type="text" name="key" placeholder="Key">
    <input type="submit" value="Add">
</form>
<%
    for (String s : listArray) {
        %> <%= s %> <br> <%
    }
%>
</body>
</html>
