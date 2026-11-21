
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Category" %>
<%@ page import="com.fashionstore.model.Product" %>

<%
    String contextPath = request.getContextPath();

    List<?> categoryList =
            (List<?>) request.getAttribute("categories");

    List<?> productList =
            (List<?>) request.getAttribute("products");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Fashion Store | Home</title>

   <link rel="stylesheet"
      href="<%= contextPath %>/assets/css/style.css">

<link rel="stylesheet"
      href="<%= contextPath %>/assets/css/home.css">
</head>

<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="home-page">

        <!-- Hero Section -->
        <section class="hero-section">

            <div class="hero-content">

                <span class="hero-label">NEW COLLECTION</span>

                <h1>Discover Your Perfect Style</h1>

                <p>
                    Explore the latest fashion trends.
                    Find clothing, footwear and accessories
                    that match your style.
                </p>

                <a href="<%= contextPath %>/products"
                   class="hero-button">
                    Shop Now
                </a>

            </div>

            <div class="hero-image">

                <div class="hero-image-placeholder">
                    <span>FASHION</span>
                    <h2>YOUR STYLE</h2>
                    <p>YOUR STATEMENT</p>
                </div>

            </div>

        </section>


        <!-- Category Section -->
        <section class="category-section">

            <div class="section-container">

                <h2 class="section-title">Shop By Category</h2>

                <p class="section-description">
                    Explore our collections
                </p>

                <div class="category-grid">

                    <%
                        if (categoryList != null && !categoryList.isEmpty()) {

                            for (Object item : categoryList) {

                                Category category = (Category) item;
                    %>

                    <a class="category-card"
                       href="<%= contextPath %>/products?categoryId=<%= category.getCategoryId() %>">

                        <div class="category-placeholder">
                            <%= category.getCategoryName() %>
                        </div>

                        <h3>
                            <%= category.getCategoryName() %>
                        </h3>

                    </a>

                    <%
                            }

                        } else {
                    %>

                    <p>No categories available.</p>

                    <%
                        }
                    %>

                </div>

            </div>

        </section>


        <!-- Featured Products Section -->
        <section class="featured-section">

            <div class="section-container">

                <div class="section-heading">

                    <div>

                        <h2 class="section-title">
                            Featured Products
                        </h2>

                        <p class="section-description">
                            Discover our latest collection
                        </p>

                    </div>

                    <a href="<%= contextPath %>/products"
                       class="view-all-link">
                        View All Products
                    </a>

                </div>


                <div class="product-grid">

                    <%
                        if (productList != null && !productList.isEmpty()) {

                            int count = 0;

                            for (Object item : productList) {

                                if (count >= 8) {
                                    break;
                                }

                                Product product = (Product) item;

                                count++;

                                String imageUrl = product.getImageUrl();
                    %>

                    <div class="product-card">

                        <a href="<%= contextPath %>/product-details?id=<%= product.getProductId() %>">

                            <%
                                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                            %>

                            <img
                                src="<%= contextPath %>/<%= imageUrl %>"
                                alt="<%= product.getProductName() %>"
                                onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">

                            <div class="product-image-placeholder"
                                 style="display: none;">
                                FASHION STORE
                            </div>

                            <%
                                } else {
                            %>

                            <div class="product-image-placeholder">
                                FASHION STORE
                            </div>

                            <%
                                }
                            %>

                        </a>


                        <div class="product-info">

                            <h3>
                                <%= product.getProductName() %>
                            </h3>

                            <p class="product-price">
                                ₹ <%= product.getPrice() %>
                            </p>

                            <a href="<%= contextPath %>/product-details?id=<%= product.getProductId() %>"
                               class="product-details-link">
                                View Details
                            </a>

                        </div>

                    </div>

                    <%
                            }

                        } else {
                    %>

                    <p>No products available.</p>

                    <%
                        }
                    %>

                </div>

            </div>

        </section>


        <!-- Promotional Section -->
        <section class="section-container promo-section">

            <h2>Upgrade Your Everyday Style</h2>

            <p>
                Explore our collection and find something
                that makes you feel confident.
            </p>

            <a href="<%= contextPath %>/products"
               class="hero-button">
                Explore Collection
            </a>

        </section>

    </main>


    <!-- Footer -->
    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>

</html>
