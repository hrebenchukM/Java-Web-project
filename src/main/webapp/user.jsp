<%-- 
    Document   : user
    Created on : 16 вер. 2025 р., 11:23:25
    Author     : Lenovo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
  <style>
            body {
                font-family: "Segoe UI", Roboto, sans-serif;
                margin: 0;
                padding: 0;
                background: linear-gradient(to bottom, #0f172a, #1e293b);
                color: #e2e8f0;
                line-height: 1.6;
            }

            h1, h3 {
                font-weight: 700;
                margin-top: 1.5rem;
                margin-bottom: 0.5rem;
                text-align: center;
            }

            h1 {
                font-size: 2rem;
                background: linear-gradient(to right, #ffffff, #bfdbfe);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                margin-top: 2rem;
            }

            h3 {
                font-size: 1.25rem;
                color: #cbd5e1;
                margin-top: 1rem;
            }

            p {
                max-width: 700px;
                margin: 1rem auto;
                font-size: 1rem;
                color: #e2e8f0;
                padding: 0 1rem;
                background: rgba(255, 255, 255, 0.05);
                border: 1px solid rgba(148, 163, 184, 0.2);
                border-radius: 8px;
                text-align: center;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
            }
              b {
                color: #facc15;
            }
        </style>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Page</title>
    </head>
    <body>
             <jsp:include page="WEB-INF/header.jsp" />
        <h1>Hello User!</h1>
        <%
    String fromServlet = (String) request.getAttribute("UserServlet");
    if(fromServlet == null) { %>
    
        <b>Нелегальний запит в обхід сервлету</b>
        
<% } else { %>
           <h3>Передача даних від серверу (контроллера)</h3>
        <p><%= request.getAttribute("UserServlet") %></p>
        
        <% } %>
             <jsp:include page="WEB-INF/footer.jsp" />
    </body>
</html>
