<%-- 
    Document   : fragment
    Created on : 16 вер. 2025 р., 10:21:22
    Author     : Lenovo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style>
    
  header {
    background: linear-gradient(to right, #1e293b, #334155, #1e293b);
    border-bottom: 1px solid rgba(100, 116, 139, 0.3);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
    padding: 1rem 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #e2e8f0;
    font-family: "Segoe UI", sans-serif;
  }

  header h1 {
    font-size: 1.5rem;
    font-weight: bold;
    background: linear-gradient(to right, #ffffff, #bfdbfe);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin: 0;
  }
  
  nav {
    display: flex;
    gap: 1.5rem;
  }

  nav a {
    color: #cbd5e1;
    text-decoration: none;
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    font-weight: 500;
    display: inline-flex;
    align-items: center;
    gap: 0.3rem;
    transition: all 0.2s ease-in-out;
  }

  nav a:hover {
    color: #ffffff;
    background-color: rgba(255, 255, 255, 0.08);
    transform: scale(1.05);
  }

  @media (max-width: 768px) {
    nav {
      display: none;
    }
    .menu-toggle {
      display: inline-block;
      cursor: pointer;
      padding: 0.5rem;
      border-radius: 0.5rem;
      transition: background-color 0.2s ease-in-out;
    }
    .menu-toggle:hover {
      background-color: rgba(255, 255, 255, 0.08);
    }
  }
</style>
<header>
    <h1>✨ Java Web App ✨</h1>
    <nav>
        <a href="<%= request.getContextPath() %>/index.jsp">🏠 Home</a>
        <a href="<%= request.getContextPath() %>/user">👤 User</a>
    </nav>
</header>
<hr/>