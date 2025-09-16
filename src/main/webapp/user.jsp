<%-- 
    Document   : user
    Created on : 16 вер. 2025 р., 11:23:25
    Author     : Lenovo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Page</title>
    </head>
    <body>
        <h1>Hello User!</h1>
           <h3>Передача даних від серверу (контроллера)</h3>
        <p><%= request.getAttribute("UserServlet") %></p>
    </body>
</html>
