
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<footer class="main-footer">

    <div class="container footer-container">

        <!-- Store Information -->
        <div class="footer-section">

            <h2>FASHION<span>STORE</span></h2>

            <p>
                Discover the latest fashion trends.
                Find your perfect style with us.
            </p>

        </div>

        <!-- Quick Links -->
        <div class="footer-section">

            <h3>Quick Links</h3>

            <a href="<%= request.getContextPath() %>/home">
                Home
            </a>

            <a href="<%= request.getContextPath() %>/products">
                Shop Products
            </a>

            <a href="<%= request.getContextPath() %>/cart">
                My Cart
            </a>

        </div>

        <!-- Customer Links -->
        <div class="footer-section">

            <h3>Customer Account</h3>

            <a href="<%= request.getContextPath() %>/login">
                Login
            </a>

            <a href="<%= request.getContextPath() %>/register">
                Register
            </a>

            <a href="<%= request.getContextPath() %>/orders">
                My Orders
            </a>

        </div>

    </div>

    <!-- Copyright -->
    <div class="footer-bottom">

        <p>
            &copy; 2026 FASHIONSTORE. All Rights Reserved.
        </p>

    </div>

</footer>
