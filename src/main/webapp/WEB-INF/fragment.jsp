<%-- 
    Document   : fragment
    Created on : 16 вер. 2025 р., 10:21:22
    Author     : Lenovo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<style>
    .fragment-container {
        max-width: 700px;
        margin: 2rem auto;
        padding: 1.5rem;
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(148, 163, 184, 0.2);
        border-radius: 12px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.25);
        text-align: center;
        font-family: "Segoe UI", Roboto, sans-serif;
        color: #e2e8f0;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .fragment-container:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.35);
    }

    .fragment-container h4 {
        margin-top: 0;
        font-size: 1.4rem;
        color: #f8fafc;
        background: linear-gradient(to right, #60a5fa, #93c5fd);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .fragment-container p {
        font-size: 1rem;
        margin: 0.5rem 0 0;
        color: #cbd5e1;
    }

    .fragment-key {
        font-weight: bold;
        color: #facc15;
    }
</style>

<div class="fragment-container">
    <h4>🔧 This is the fragment!</h4>
    <p>
        Fragment's key = <span class="fragment-key"><%= request.getParameter("key")%></span>
    </p>
</div>