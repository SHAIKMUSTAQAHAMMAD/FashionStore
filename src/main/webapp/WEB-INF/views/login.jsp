
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String contextPath = request.getContextPath();
    String error = request.getParameter("error");
    String registered = request.getParameter("registered");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Login | Fashion Store</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/auth.css">
</head>

<body>

    <jsp:include page="partials/navbar.jsp" />

    <main class="auth-page">

        <div class="auth-container">

            <div class="auth-header">

                <h1>Welcome Back!</h1>

                <p>Login to your Fashion Store account.</p>

            </div>

            <% if ("true".equals(registered)) { %>

                <div class="auth-success">
                    Registration successful! Please login to continue.
                </div>

            <% } %>

            <% if ("empty".equals(error)) { %>

                <div class="auth-error">
                    Please enter both email and password.
                </div>

            <% } else if ("invalid".equals(error)) { %>

                <div class="auth-error">
                    Invalid email or password. Please try again.
                </div>

            <% } %>

            <form action="<%= contextPath %>/login"
                  method="post"
                  class="auth-form">

                <div class="form-group">

                    <label for="email">Email Address</label>

                    <input type="email"
                           id="email"
                           name="email"
                           placeholder="Enter your email address"
                           maxlength="150"
                           autocomplete="email"
                           required>

                </div>

                <div class="form-group">

                    <label for="password">Password</label>

                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Enter your password"
                           autocomplete="current-password"
                           required>

                </div>

                <button type="submit" class="auth-button">
                    Login
                </button>

            </form>

            <div class="auth-footer">

                <p>
                    Don't have an account?

                    <a href="<%= contextPath %>/register">
                        Create Account
                    </a>
                </p>

            </div>

        </div>

    </main>

    <jsp:include page="partials/footer.jsp" />

</body>
</html>
