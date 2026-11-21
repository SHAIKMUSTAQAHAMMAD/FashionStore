
<%@ page import="com.fashionstore.model.User" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%
    String contextPath = request.getContextPath();
    User loggedInUser =
            (User) session.getAttribute("loggedInUser");
%>

<link rel="stylesheet"
      href="<%= contextPath %>/assets/css/navbar.css">

<header class="main-header">

    <div class="header-container">

        <a href="<%= contextPath %>/home" class="logo">
            FASHION<span>STORE</span>
        </a>

        <nav class="main-navigation">

            <a href="<%= contextPath %>/home">Home</a>
            <a href="<%= contextPath %>/products">Shop</a>
            <a href="<%= contextPath %>/products">Collections</a>
            <a href="<%= contextPath %>/orders">Orders</a>

        </nav>

        <div class="header-actions">

            <a href="<%= contextPath %>/cart" class="header-link">
                <span class="header-icon">🛒</span>
                <span>Cart</span>
            </a>

            <% if (loggedInUser != null) { %>

                <div class="account-dropdown">

                    <button type="button" class="account-button">
                        <span class="header-icon">👤</span>
                        <span>My Account</span>
                        <span class="dropdown-arrow">▾</span>
                    </button>

                    <div class="dropdown-menu">

                        <a href="<%= contextPath %>/account">My Profile</a>
                        <a href="<%= contextPath %>/orders">My Orders</a>
                        <a href="<%= contextPath %>/logout">Logout</a>

                    </div>

                </div>

            <% } else { %>

                <a href="<%= contextPath %>/login"
                   class="header-link">
                    <span class="header-icon">👤</span>
                    <span>Login</span>
                </a>

                <a href="<%= contextPath %>/register"
                   class="register-button">
                    Register
                </a>

            <% } %>

        </div>

    </div>

</header>
