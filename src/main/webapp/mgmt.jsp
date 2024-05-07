<%@ page import="me.hysong.remotekeyvaluestore.Authorization" %>
<%@ page import="me.hysong.remotekeyvaluestore.Database" %>
<%@ page import="java.util.ArrayList" %><%--
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
    } else if (Authorization.unauthorized(session)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    String list = Database.list();
    String[] listArray = list.split("\n");
    ArrayList<String> convertedList = new ArrayList<>();
    for (String s : listArray) {
        if (s.startsWith("auth/") || s.startsWith("auth\\")) {
            continue;
        }
        convertedList.add(s);
    }
%>
<h1>Key List</h1>
<hr>
Create:
<form action="control" method="post">
    <input type="hidden" name="action" value="write">
    <input type="text" name="path" placeholder="Location" required>
    <input type="text" name="key" placeholder="Key">
    <br>
    <textarea name="value" placeholder="Value" rows="4" cols="50" required></textarea>
    <input type="submit" value="Add">
</form>
Delete:
<form action="control" method="post">
    <input type="hidden" name="action" value="delete">
    <input type="text" name="path" placeholder="Location" required>
    <input type="submit" value="Delete">
</form>
<%
    for (String s : convertedList) {
%> <a href="<%=request.getRequestURL().toString().replace("mgmt.jsp", "api/")%><%= s %>"><%=request.getRequestURL().toString().replace("mgmt.jsp", "api/")%><%= s %></a><br> <%
    }
%>
</body>
</html>
