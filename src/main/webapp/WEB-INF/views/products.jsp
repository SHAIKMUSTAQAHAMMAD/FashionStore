
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.Category" %>

<%
    String contextPath = request.getContextPath();

    List<Product> products =
            (List<Product>) request.getAttribute("products");

    List<Category> categories =
            (List<Category>) request.getAttribute("categories");

    Object keywordObject = request.getAttribute("keyword");
    String keyword = keywordObject == null
            ? "" : String.valueOf(keywordObject);

    Object categoryIdObject = request.getAttribute("categoryId");
    String categoryId = categoryIdObject == null
            ? "" : String.valueOf(categoryIdObject);

    Object minPriceObject = request.getAttribute("minPrice");
    String minPrice = minPriceObject == null
            ? "" : String.valueOf(minPriceObject);

    Object maxPriceObject = request.getAttribute("maxPrice");
    String maxPrice = maxPriceObject == null
            ? "" : String.valueOf(maxPriceObject);

    Object sizeObject = request.getAttribute("size");
    String selectedSize = sizeObject == null
            ? "" : String.valueOf(sizeObject);

    Object sortObject = request.getAttribute("sortBy");
    String sortBy = sortObject == null
            ? "newest" : String.valueOf(sortObject);

    Integer currentPageValue =
            (Integer) request.getAttribute("currentPage");

    Integer totalPagesValue =
            (Integer) request.getAttribute("totalPages");

    Integer totalProductsValue =
            (Integer) request.getAttribute("totalProducts");

    int currentPage =
            currentPageValue == null ? 1 : currentPageValue;

    int totalPages =
            totalPagesValue == null ? 1 : totalPagesValue;

    int totalProducts =
            totalProductsValue == null ? 0 : totalProductsValue;

    StringBuilder paginationQuery = new StringBuilder();

    if (!keyword.isEmpty()) {
        paginationQuery.append("&keyword=")
                .append(URLEncoder.encode(keyword, "UTF-8"));
    }

    if (!categoryId.isEmpty()) {
        paginationQuery.append("&categoryId=")
                .append(URLEncoder.encode(categoryId, "UTF-8"));
    }

    if (!minPrice.isEmpty()) {
        paginationQuery.append("&minPrice=")
                .append(URLEncoder.encode(minPrice, "UTF-8"));
    }

    if (!maxPrice.isEmpty()) {
        paginationQuery.append("&maxPrice=")
                .append(URLEncoder.encode(maxPrice, "UTF-8"));
    }

    if (!selectedSize.isEmpty()) {
        paginationQuery.append("&size=")
                .append(URLEncoder.encode(selectedSize, "UTF-8"));
    }

    if (!sortBy.isEmpty()) {
        paginationQuery.append("&sortBy=")
                .append(URLEncoder.encode(sortBy, "UTF-8"));
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Shop Products | Fashion Store</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/navbar.css">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            background: #fff;
            color: #29232e;
            font-family: Arial, Helvetica, sans-serif;
        }

        .products-page {
            width: 100%;
        }

        .products-banner {
            width: 100%;
            min-height: 125px;
            background: linear-gradient(110deg, #f3e8f8, #ead8f2);
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 25px;
        }

        .products-banner h1 {
            font-size: 34px;
            margin: 0 0 10px;
            color: #29232e;
        }

        .products-banner p {
            margin: 0;
            color: #716778;
            font-size: 15px;
        }

        .products-container {
            max-width: 1350px;
            margin: 45px auto;
            padding: 0 25px;
            display: grid;
            grid-template-columns: 275px minmax(0, 1fr);
            gap: 35px;
            align-items: start;
        }

        .filter-sidebar {
            border: 1px solid #e5e0e8;
            border-radius: 9px;
            padding: 25px;
            background: #fff;
        }

        .filter-heading {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-bottom: 22px;
            border-bottom: 1px solid #eee;
        }

        .filter-heading h2 {
            margin: 0;
            font-size: 22px;
        }

        .clear-filters {
            color: #9145b2;
            font-size: 13px;
            font-weight: bold;
            text-decoration: none;
        }

        .filter-group {
            padding: 23px 0;
            border-bottom: 1px solid #eee;
        }

        .filter-group h3 {
            font-size: 16px;
            margin: 0 0 17px;
        }

        .filter-option {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 13px 0;
            color: #555;
            font-size: 14px;
            cursor: pointer;
        }

        .filter-option input,
        .size-filter-option input {
            accent-color: #9145b2;
            cursor: pointer;
        }

        .price-inputs {
            display: flex;
            gap: 10px;
        }

        .price-inputs input {
            width: 50%;
            min-width: 0;
            height: 42px;
            padding: 9px;
            border: 1px solid #ddd;
            border-radius: 5px;
            outline: none;
        }

        .price-inputs input:focus {
            border-color: #9145b2;
        }

        .size-filter-options {
            display: flex;
            flex-wrap: wrap;
            gap: 9px;
        }

        .size-filter-option {
            min-width: 48px;
            height: 42px;
            border: 1px solid #ddd;
            border-radius: 5px;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
            font-size: 14px;
        }

        .apply-filter-btn {
            width: 100%;
            height: 45px;
            background: #9145b2;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 22px;
        }

        .apply-filter-btn:hover {
            background: #71318e;
        }

        .products-content {
            min-width: 0;
        }

        .products-toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            margin-bottom: 35px;
        }

        .search-form {
            display: flex;
            width: min(530px, 100%);
        }

        .search-form input {
            flex: 1;
            min-width: 0;
            height: 47px;
            padding: 12px 16px;
            border: 1px solid #ddd;
            border-radius: 5px 0 0 5px;
            font-size: 14px;
            outline: none;
        }

        .search-form button {
            height: 47px;
            padding: 0 23px;
            background: #9145b2;
            border: none;
            color: white;
            font-weight: bold;
            border-radius: 0 5px 5px 0;
            cursor: pointer;
        }

        .sort-form {
            display: flex;
            align-items: center;
            gap: 10px;
            white-space: nowrap;
        }

        .sort-form label {
            font-size: 14px;
            color: #555;
        }

        .sort-form select {
            height: 44px;
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background: white;
            min-width: 150px;
        }

        .products-heading {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .products-heading h2 {
            font-size: 23px;
            margin: 0;
        }

        .products-count {
            font-size: 14px;
            color: #777;
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 25px;
        }

        .product-card {
            border: 1px solid #e9e5eb;
            border-radius: 8px;
            overflow: hidden;
            background: #fff;
            transition: transform 0.25s, box-shadow 0.25s;
        }

        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 28px rgba(0, 0, 0, 0.09);
        }

        .product-image-link {
            display: block;
            width: 100%;
            height: 290px;
            background: #f5edf8;
            overflow: hidden;
            text-decoration: none;
        }

        .product-image-link img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
            transition: transform 0.35s;
        }

        .product-card:hover .product-image-link img {
            transform: scale(1.04);
        }

        .product-image-placeholder {
            width: 100%;
            height: 100%;
            display: none;
            align-items: center;
            justify-content: center;
            color: #9145b2;
            font-size: 20px;
            font-weight: bold;
            text-align: center;
        }

        .product-info {
            padding: 19px;
        }

        .product-info h3 {
            font-size: 16px;
            margin: 0 0 13px;
            color: #29232e;
            line-height: 1.4;
        }

        .product-price {
            font-size: 19px;
            font-weight: bold;
            color: #9145b2;
            margin-bottom: 17px;
        }

        .product-details-link {
            display: inline-block;
            padding: 11px 20px;
            background: #29232e;
            color: #fff;
            border-radius: 5px;
            text-decoration: none;
            font-size: 13px;
            font-weight: bold;
        }

        .product-details-link:hover {
            background: #9145b2;
        }

        .no-products {
            grid-column: 1 / -1;
            text-align: center;
            padding: 65px 20px;
            background: #faf7fb;
            border-radius: 8px;
        }

        .no-products h3 {
            font-size: 23px;
            margin-bottom: 10px;
        }

        .no-products p {
            color: #777;
        }

        .no-products a {
            display: inline-block;
            margin-top: 15px;
            padding: 12px 23px;
            color: #fff;
            background: #9145b2;
            text-decoration: none;
            border-radius: 5px;
        }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            flex-wrap: wrap;
            gap: 9px;
            margin-top: 45px;
        }

        .pagination-link {
            min-width: 40px;
            height: 40px;
            padding: 0 12px;
            display: flex;
            justify-content: center;
            align-items: center;
            border: 1px solid #ddd;
            border-radius: 5px;
            text-decoration: none;
            color: #333;
            font-size: 14px;
        }

        .pagination-link.active,
        .pagination-link:hover {
            background: #9145b2;
            border-color: #9145b2;
            color: white;
        }

        @media (max-width: 1100px) {
            .product-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .products-container {
                grid-template-columns: 240px minmax(0, 1fr);
                gap: 22px;
            }
        }

        @media (max-width: 750px) {
            .products-container {
                grid-template-columns: 1fr;
                margin: 25px auto;
            }

            .products-toolbar {
                flex-direction: column;
                align-items: stretch;
            }

            .search-form {
                width: 100%;
            }

            .sort-form {
                justify-content: flex-end;
            }

            .products-banner h1 {
                font-size: 27px;
            }
        }

        @media (max-width: 480px) {
            .product-grid {
                grid-template-columns: 1fr;
            }

            .product-image-link {
                height: 320px;
            }

            .products-container {
                padding: 0 14px;
            }

            .products-heading {
                align-items: flex-start;
                flex-direction: column;
                gap: 10px;
            }
        }
    </style>
</head>

<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="products-page">

        <section class="products-banner">
            <div>
                <h1>Our Collection</h1>
                <p>Discover your style. Find your perfect fashion.</p>
            </div>
        </section>

        <div class="products-container">

            <!-- FILTER SIDEBAR -->
            <aside class="filter-sidebar">

                <div class="filter-heading">
                    <h2>Filters</h2>

                    <a href="<%= contextPath %>/products"
                       class="clear-filters">
                        Clear All
                    </a>
                </div>

                <form action="<%= contextPath %>/products"
                      method="get">

                    <!-- CATEGORY FILTER -->
                    <div class="filter-group">

                        <h3>Categories</h3>

                        <% if (categories != null && !categories.isEmpty()) {
                            for (Category category : categories) {

                                boolean selectedCategory =
                                        String.valueOf(category.getCategoryId())
                                                .equals(categoryId);
                        %>

                            <label class="filter-option">
                                <input type="radio"
                                       name="categoryId"
                                       value="<%= category.getCategoryId() %>"
                                       <%= selectedCategory ? "checked" : "" %>>

                                <%= category.getCategoryName() %>
                            </label>

                        <%
                            }
                        } else {
                        %>
                            <p>No categories available.</p>
                        <% } %>

                    </div>

                    <!-- PRICE FILTER -->
                    <div class="filter-group">

                        <h3>Price Range</h3>

                        <div class="price-inputs">

                            <input type="number"
                                   name="minPrice"
                                   placeholder="Min"
                                   min="0"
                                   value="<%= minPrice %>">

                            <input type="number"
                                   name="maxPrice"
                                   placeholder="Max"
                                   min="0"
                                   value="<%= maxPrice %>">

                        </div>

                    </div>

                    <!-- SIZE FILTER -->
                    <div class="filter-group">

                        <h3>Size</h3>

                        <div class="size-filter-options">

                            <%
                                String[] sizes = {"S", "M", "L", "XL", "XXL"};

                                for (String sizeValue : sizes) {
                            %>

                                <label class="size-filter-option">

                                    <input type="radio"
                                           name="size"
                                           value="<%= sizeValue %>"
                                           <%= sizeValue.equals(selectedSize)
                                                   ? "checked" : "" %>>

                                    <%= sizeValue %>

                                </label>

                            <% } %>

                        </div>

                    </div>

                    <!-- PRESERVE SEARCH AND SORT -->
                    <input type="hidden"
                           name="keyword"
                           value="<%= keyword %>">

                    <input type="hidden"
                           name="sortBy"
                           value="<%= sortBy %>">

                    <button type="submit"
                            class="apply-filter-btn">
                        Apply Filters
                    </button>

                </form>

            </aside>

            <!-- PRODUCTS CONTENT -->
            <section class="products-content">

                <!-- SEARCH AND SORT -->
                <div class="products-toolbar">

                    <form action="<%= contextPath %>/products"
                          method="get"
                          class="search-form">

                        <input type="text"
                               name="keyword"
                               placeholder="Search products..."
                               value="<%= keyword %>">

                        <input type="hidden"
                               name="categoryId"
                               value="<%= categoryId %>">

                        <input type="hidden"
                               name="minPrice"
                               value="<%= minPrice %>">

                        <input type="hidden"
                               name="maxPrice"
                               value="<%= maxPrice %>">

                        <input type="hidden"
                               name="size"
                               value="<%= selectedSize %>">

                        <input type="hidden"
                               name="sortBy"
                               value="<%= sortBy %>">

                        <button type="submit">Search</button>

                    </form>

                    <form action="<%= contextPath %>/products"
                          method="get"
                          class="sort-form">

                        <input type="hidden"
                               name="keyword"
                               value="<%= keyword %>">

                        <input type="hidden"
                               name="categoryId"
                               value="<%= categoryId %>">

                        <input type="hidden"
                               name="minPrice"
                               value="<%= minPrice %>">

                        <input type="hidden"
                               name="maxPrice"
                               value="<%= maxPrice %>">

                        <input type="hidden"
                               name="size"
                               value="<%= selectedSize %>">

                        <label for="sortBy">Sort By:</label>

                        <select name="sortBy"
                                id="sortBy"
                                onchange="this.form.submit()">

                            <option value="newest"
                                <%= "newest".equals(sortBy)
                                        ? "selected" : "" %>>
                                Newest First
                            </option>

                            <option value="price_low_to_high"
                                <%= "price_low_to_high".equals(sortBy)
                                        ? "selected" : "" %>>
                                Price: Low to High
                            </option>

                            <option value="price_high_to_low"
                                <%= "price_high_to_low".equals(sortBy)
                                        ? "selected" : "" %>>
                                Price: High to Low
                            </option>

                        </select>

                    </form>

                </div>

                <!-- PRODUCTS HEADING -->
                <div class="products-heading">

                    <h2>
                        <% if (!categoryId.isEmpty() && categories != null) {
                            String categoryName = "Products";

                            for (Category category : categories) {
                                if (String.valueOf(category.getCategoryId())
                                        .equals(categoryId)) {
                                    categoryName = category.getCategoryName();
                                    break;
                                }
                            }
                        %>
                            <%= categoryName %>
                        <% } else { %>
                            All Products
                        <% } %>
                    </h2>

                    <span class="products-count">
                        <%= totalProducts %> products found
                    </span>

                </div>

                <!-- PRODUCT CARDS -->
                <div class="product-grid">

                    <% if (products != null && !products.isEmpty()) {

                        for (Product product : products) {

                            String imageUrl = product.getImageUrl();

                            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                                imageUrl = "";
                            } else {
                                imageUrl = imageUrl.trim();

                                if (!imageUrl.startsWith("http://")
                                        && !imageUrl.startsWith("https://")) {

                                    if (imageUrl.startsWith(contextPath + "/")) {
                                        // Already has context path.
                                    } else if (imageUrl.startsWith("/")) {
                                        imageUrl = contextPath + imageUrl;
                                    } else {
                                        imageUrl = contextPath + "/" + imageUrl;
                                    }
                                }
                            }
                    %>

                        <article class="product-card">

                            <a href="<%= contextPath %>/product-details?id=<%= product.getProductId() %>"
                               class="product-image-link">

                                <% if (!imageUrl.isEmpty()) { %>

                                    <img src="<%= imageUrl %>"
                                         alt="<%= product.getProductName() %>"
                                         loading="lazy"
                                         onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">

                                <% } %>

                                <div class="product-image-placeholder"
                                     style="display:<%= imageUrl.isEmpty()
                                             ? "flex" : "none" %>;">
                                    FASHION STORE
                                </div>

                            </a>

                            <div class="product-info">

                                <h3>
                                    <%= product.getProductName() %>
                                </h3>

                                <div class="product-price">
                                    &#8377; <%= product.getPrice() %>
                                </div>

                                <a href="<%= contextPath %>/product-details?id=<%= product.getProductId() %>"
                                   class="product-details-link">
                                    View Details
                                </a>

                            </div>

                        </article>

                    <%  }

                    } else { %>

                        <div class="no-products">
                            <h3>No Products Found</h3>

                            <p>
                                We couldn't find products matching your selection.
                            </p>

                            <a href="<%= contextPath %>/products">
                                View All Products
                            </a>
                        </div>

                    <% } %>

                </div>

                <!-- PAGINATION -->
                <% if (totalPages > 1) { %>

                    <div class="pagination">

                        <% if (currentPage > 1) { %>

                            <a class="pagination-link"
                               href="<%= contextPath %>/products?page=<%= currentPage - 1 %><%= paginationQuery.toString() %>">
                                Previous
                            </a>

                        <% } %>

                        <% for (int pageNumber = 1;
                                pageNumber <= totalPages;
                                pageNumber++) { %>

                            <a class="pagination-link <%= pageNumber == currentPage
                                    ? "active" : "" %>"
                               href="<%= contextPath %>/products?page=<%= pageNumber %><%= paginationQuery.toString() %>">

                                <%= pageNumber %>

                            </a>

                        <% } %>

                        <% if (currentPage < totalPages) { %>

                            <a class="pagination-link"
                               href="<%= contextPath %>/products?page=<%= currentPage + 1 %><%= paginationQuery.toString() %>">
                                Next
                            </a>

                        <% } %>

                    </div>

                <% } %>

            </section>

        </div>

    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
