<%@page contentType="text/html" pageEncoding="UTF-8"%>

<style>
    footer {
        background: linear-gradient(to right, #0f172a, #1e293b, #0f172a);
        color: #cbd5e1;
        padding: 40px 20px;
        border-top: 1px solid rgba(148, 163, 184, 0.3);
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    .footer-container {
        max-width: 1200px;
        margin: 0 auto;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
        gap: 30px;
    }

    .footer-brand h3 {
        font-size: 1.25rem;
        font-weight: bold;
        color: white;
        margin: 0;
    }


    .footer-links{
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .footer-links a {
        text-decoration: none;
        color: #94a3b8;
        font-size: 0.9rem;
        transition: color 0.2s, transform 0.2s;
    }

    .footer-links a:hover {
        color: white;
        transform: translateX(4px);
    }

  

</style>

<footer>
    <div class="footer-container">
        <div class="footer-brand">
            <h3>✨ Java Web App</h3>
        </div>

        <div class="footer-links">
            <strong style="color:white;">Quick Links</strong>
            <a href="index.jsp">🏠 Home</a>
            <a href="user">👤 User</a>
        </div>

    </div>

</footer>
