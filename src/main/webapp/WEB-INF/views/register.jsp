
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String contextPath = request.getContextPath();
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Create Account | Fashion Store</title>

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
                <h1>Create Your Account</h1>
                <p>Join Fashion Store and discover your style.</p>
            </div>

            <% if ("empty".equals(error)) { %>
                <div class="auth-error">
                    Please fill in all required fields.
                </div>
            <% } else if ("email".equals(error)) { %>
                <div class="auth-error">
                    Please enter a valid email address.
                </div>
            <% } else if ("password".equals(error)) { %>
                <div class="auth-error">
                    Password must contain at least 8 characters.
                </div>
            <% } else if ("mobile".equals(error)) { %>
                <div class="auth-error">
                    Please enter a valid mobile number.
                </div>
            <% } else if ("exists".equals(error)) { %>
                <div class="auth-error">
                    This email is already registered. Please login.
                </div>
            <% } else if ("failed".equals(error)) { %>
                <div class="auth-error">
                    Registration failed. Please try again.
                </div>
            <% } %>

            <form action="<%= contextPath %>/register"
                  method="post"
                  class="auth-form">

                <div class="form-group">
                    <label for="name">Full Name *</label>

                    <input type="text"
                           id="name"
                           name="name"
                           placeholder="Enter your full name"
                           maxlength="100"
                           autocomplete="name"
                           required>
                </div>

                <div class="form-group">
                    <label for="email">Email Address *</label>

                    <input type="email"
                           id="email"
                           name="email"
                           placeholder="Enter your email address"
                           maxlength="150"
                           autocomplete="email"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">Password *</label>

                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Create a password (minimum 8 characters)"
                           minlength="8"
                           autocomplete="new-password"
                           required>
                </div>

                <div class="form-group">
                    <label for="mobile">Mobile Number *</label>

                    <input type="tel"
                           id="mobile"
                           name="mobile"
                           placeholder="Enter your mobile number"
                           pattern="[0-9]{10,15}"
                           minlength="10"
                           maxlength="15"
                           autocomplete="tel"
                           required>
                </div>

                <div class="form-group">
                    <label for="address">Address</label>

                    <textarea id="address"
                              name="address"
                              placeholder="Enter your delivery address"
                              rows="3"
                              maxlength="500"
                              autocomplete="street-address"></textarea>
                </div>

                <div class="form-row">

                    <div class="form-group">
                        <label for="city">City</label>

                        <input type="text"
                               id="city"
                               name="city"
                               placeholder="City"
                               maxlength="100"
                               autocomplete="address-level2">
                    </div>

                    <div class="form-group">
                        <label for="state">State</label>

                        <input type="text"
                               id="state"
                               name="state"
                               placeholder="State"
                               maxlength="100"
                               autocomplete="address-level1">
                    </div>

                </div>

                <div class="form-group">
                    <label for="pincode">Pincode</label>

                    <input type="text"
                           id="pincode"
                           name="pincode"
                           placeholder="Enter pincode"
                           pattern="[0-9]{6}"
                           maxlength="6"
                           autocomplete="postal-code">
                </div>

                <button type="submit" class="auth-button">
                    Create Account
                </button>

            </form>

            <div class="auth-footer">
                <p>
                    Already have an account?
                    <a href="<%= contextPath %>/login">Login</a>
                </p>
            </div>

        </div>

    </main>

    <jsp:include page="partials/footer.jsp" />

</body>
</html>
