<%-- 
    Document   : index
    Created on : 16 вер. 2025 р., 09:12:44
    Author     : Lector
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

            h1, h2, h3 {
                font-weight: 700;
                margin-top: 1.5rem;
                margin-bottom: 0.5rem;
            }

            h1 {
                font-size: 2rem;
                text-align: center;
                margin: 2rem 0;
                background: linear-gradient(to right, #ffffff, #bfdbfe);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }

            h2 {
                 text-align: center;
                font-size: 1.5rem;
                color: #93c5fd;
            }

            h3 {
                 text-align: center;
                font-size: 1.25rem;
                color: #cbd5e1;
            }
            h5{  max-width: 700px;
                margin: 1rem auto;
                font-size: 1rem;
                color: #e2e8f0;
                padding: 0 1rem;
                background: rgba(255, 255, 255, 0.05);
                border: 1px solid rgba(148, 163, 184, 0.2);
                border-radius: 8px;
                text-align: center;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);}
            
            p {
                
                max-width: 800px;
                margin: 0 auto 1rem auto;
                font-size: 1rem;
                color: #e2e8f0;
                padding: 0 1rem;
            }
            
            

            a {
                color: #60a5fa;
                text-decoration: none;
                transition: color 0.2s ease-in-out;
            }

            a:hover {
                color: #93c5fd;
                text-decoration: underline;
            }

            code {
                background: rgba(255, 255, 255, 0.1);
                padding: 2px 5px;
                border-radius: 4px;
                font-size: 0.9rem;
                font-family: Consolas, monospace;
            }

            pre {
                background: rgba(15, 23, 42, 0.7);
                border: 1px solid rgba(148, 163, 184, 0.2);
                padding: 1rem;
                border-radius: 8px;
                overflow-x: auto;
                max-width: 850px;
                margin: 1rem auto;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
            }

            ul {
                list-style: none;
                padding-left: 0;
                max-width: 800px;
                margin: 1rem auto;
            }

            ul li {
                background: rgba(255, 255, 255, 0.05);
                padding: 0.5rem 1rem;
                border-radius: 6px;
                margin: 0.3rem 0;
                transition: background 0.2s ease-in-out, transform 0.2s ease;
            }

            ul li:hover {
                background: rgba(255, 255, 255, 0.1);
                transform: translateX(5px);
            }

            b {
                color: #facc15;
            }

            i {
                color: #38bdf8;
            }
        </style>
        
        
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
         <jsp:include page="WEB-INF/header.jsp" />
        <h1>Java web. JSP</h1>
<%
    String fromServlet = (String) request.getAttribute("HomeServlet");
    if(fromServlet == null) { %>
    
        <b>Нелегальний запит в обхід сервлету</b>
        
<% } else { %>
<%=fromServlet%>
        <h2>Java EE</h2>
        <p>
            Java Enterprise Edition - Java + додаткові модулі для роботи з мережею.
            Також до складу входить сервер застосунків (Application server),
            проте, може знадобитись встановити його окремо: 
            <a href="https://tomcat.apache.org/">Apache Tomcat</a>
            <a href="https://glassfish.org/">Eclipse GlassFish</a>
            <a href="https://www.wildfly.org/">WildFly</a>
     
   
  
        <h3>Шаблонізація, підключення компонентів</h3>
        <jsp:include page="WEB-INF/fragment.jsp" >
            <jsp:param name="key" value="Value"/>
        </jsp:include>
        <h3>Передача даних від сервлету (контролера)</h3>
      <h5>  <%= request.getAttribute("HomeServlet") %></h5>
      <h5>UNIX Timestamp: <%= request.getAttribute("Timestamp") %></h5>
<% } %>


 <jsp:include page="WEB-INF/footer.jsp" />
    </body>
</html>
