
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.ProductSize" %>

<%
    String contextPath = request.getContextPath();

    Product product = (Product) request.getAttribute("product");

    List<ProductSize> productSizes =
            (List<ProductSize>) request.getAttribute("productSizes");

    int totalStock = 0;

    if (productSizes != null) {
        for (ProductSize size : productSizes) {
            if (size != null && size.getStockQuantity() > 0) {
                totalStock += size.getStockQuantity();
            }
        }
    }

    boolean inStock = totalStock > 0;

    String imageUrl = "";

    if (product != null) {

        imageUrl = product.getImageUrl();

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = "";
        } else {
            imageUrl = imageUrl.trim();

            if (imageUrl.startsWith("http://")
                    || imageUrl.startsWith("https://")) {
                // External image URL.
            } else if (imageUrl.startsWith(contextPath + "/")) {
                // Context path is already included.
            } else if (imageUrl.startsWith("/")) {
                imageUrl = contextPath + imageUrl;
            } else {
                imageUrl = contextPath + "/" + imageUrl;
            }
        }
    }

    String fallbackImage =
            contextPath + "/assets/images/no-image.png";
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>
        <%= product != null
                ? product.getProductName()
                : "Product Details" %> | Fashion Store
    </title>

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
            background: #ffffff;
            color: #29232e;
            font-family: Arial, Helvetica, sans-serif;
        }

        .details-page {
            width: 100%;
            padding: 55px 25px 80px;
            min-height: 600px;
        }

        .details-container {
            max-width: 1200px;
            margin: auto;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 65px;
            align-items: start;
        }

        .details-image-box {
            width: 100%;
            height: 540px;
            background: #f7f0fa;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }

        .details-image-box img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            display: block;
        }

        .image-fallback {
            display: none;
            color: #9145b2;
            text-align: center;
            font-size: 22px;
            font-weight: bold;
        }

        .details-info {
            padding-top: 12px;
        }

        .details-info h1 {
            font-size: 34px;
            line-height: 1.3;
            margin: 0 0 20px;
            color: #29232e;
        }

        .details-price {
            font-size: 30px;
            font-weight: bold;
            color: #9145b2;
            margin: 20px 0;
        }

        .details-description {
            margin: 28px 0;
        }

        .details-description h3,
        .size-heading,
        .quantity-heading {
            font-size: 17px;
            margin-bottom: 12px;
            color: #29232e;
        }

        .details-description p {
            font-size: 15px;
            color: #666;
            line-height: 1.8;
            white-space: pre-line;
        }

        .stock-message {
            margin: 20px 0;
            font-size: 15px;
            font-weight: bold;
        }

        .stock-available {
            color: #198754;
        }

        .stock-unavailable {
            color: #dc3545;
        }

        .size-section {
            margin: 28px 0;
        }

        .size-options {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
        }

        .size-choice {
            min-width: 65px;
            min-height: 48px;
            padding: 8px 13px;
            border: 1px solid #d6d0dc;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
            cursor: pointer;
            background: #fff;
            color: #333;
            transition: 0.2s;
        }

        .size-choice:hover {
            border-color: #9145b2;
        }

        .size-choice:has(input:checked) {
            background: #f5eafa;
            border: 2px solid #9145b2;
            color: #9145b2;
            font-weight: bold;
        }

        .size-choice input {
            accent-color: #9145b2;
        }

        .size-choice.unavailable {
            background: #f1f1f1;
            color: #999;
            opacity: 0.65;
            cursor: not-allowed;
            text-decoration: line-through;
        }

        .quantity-section {
            margin: 25px 0;
        }

        .quantity-input {
            display: block;
            width: 120px;
            height: 45px;
            padding: 8px 12px;
            border: 1px solid #d5cedc;
            border-radius: 6px;
            font-size: 16px;
            margin-top: 10px;
        }

        .quantity-input:focus {
            outline: none;
            border-color: #9145b2;
        }

        .add-cart-button {
            width: 100%;
            max-width: 350px;
            height: 52px;
            border: none;
            border-radius: 6px;
            background: #9145b2;
            color: #fff;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: 0.2s;
        }

        .add-cart-button:hover:not(:disabled) {
            background: #71318e;
        }

        .add-cart-button:disabled {
            background: #aaa;
            cursor: not-allowed;
        }

        .back-to-products {
            display: inline-block;
            margin-top: 25px;
            color: #9145b2;
            text-decoration: none;
            font-weight: bold;
        }

        .back-to-products:hover {
            text-decoration: underline;
        }

        .product-not-found {
            max-width: 800px;
            margin: 80px auto;
            padding: 40px;
            text-align: center;
        }

        .product-not-found a {
            color: #9145b2;
            font-weight: bold;
        }

        @media (max-width: 850px) {

            .details-container {
                grid-template-columns: 1fr;
                gap: 30px;
            }

            .details-image-box {
                height: 430px;
            }

            .details-info h1 {
                font-size: 28px;
            }
        }

        @media (max-width: 500px) {

            .details-page {
                padding: 25px 15px 50px;
            }

            .details-image-box {
                height: 330px;
            }

            .details-info h1 {
                font-size: 25px;
            }

            .details-price {
                font-size: 25px;
            }
        }

    </style>

</head>

<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <% if (product == null) { %>

        <div class="product-not-found">

            <h2>Product Not Found</h2>

            <p>The requested product could not be found.</p>

            <a href="<%= contextPath %>/products">
                Continue Shopping
            </a>

        </div>

    <% } else { %>

        <main class="details-page">

            <div class="details-container">

                <!-- PRODUCT IMAGE -->

                <div class="details-image-box">

                    <% if (!imageUrl.isEmpty()) { %>

                        <img src="<%= imageUrl %>"
                             alt="<%= product.getProductName() %>"
                             onerror="this.onerror=null;this.src='<%= fallbackImage %>';this.nextElementSibling.style.display='none';">

                    <% } else { %>

                        <img src="<%= fallbackImage %>"
                             alt="Product image"
                             onerror="this.style.display='none';">

                    <% } %>

                    <div class="image-fallback">
                        FASHION STORE
                    </div>

                </div>

                <!-- PRODUCT INFORMATION -->

                <div class="details-info">

                    <h1>
                        <%= product.getProductName() %>
                    </h1>

                    <div class="details-price">
                        &#8377; <%= product.getPrice() %>
                    </div>

                    <div class="details-description">

                        <h3>Product Description</h3>

                        <p><%= product.getDescription() == null
                                ? "No description available."
                                : product.getDescription() %></p>

                    </div>

                    <% if (inStock) { %>

                        <div class="stock-message stock-available">
                            In Stock - <%= totalStock %> items available
                        </div>

                    <% } else { %>

                        <div class="stock-message stock-unavailable">
                            Out of Stock
                        </div>

                    <% } %>

                    <!-- ADD TO CART FORM -->

                    <form action="<%= contextPath %>/cart"
                          method="post"
                          id="addToCartForm">

                        <input type="hidden"
                               name="action"
                               value="add">

                        <!-- SIZE SELECTION -->

                        <div class="size-section">

                            <h3 class="size-heading">
                                Select Size
                            </h3>

                            <div class="size-options">

                                <% if (productSizes != null
                                        && !productSizes.isEmpty()) {

                                    for (ProductSize size : productSizes) {

                                        if (size == null) {
                                            continue;
                                        }

                                        boolean available =
                                                size.getStockQuantity() > 0;
                                %>

                                    <label class="size-choice <%= available
                                            ? "" : "unavailable" %>">

                                        <input type="radio"
                                               name="sizeId"
                                               value="<%= size.getSizeId() %>"
                                               data-stock="<%= size.getStockQuantity() %>"
                                               <%= available ? "" : "disabled" %>
                                               required>

                                        <span>
                                            <%= size.getSize() %>
                                        </span>

                                        <% if (!available) { %>
                                            <small>Sold out</small>
                                        <% } %>

                                    </label>

                                <%  }
                                   } else { %>

                                    <p class="stock-unavailable">
                                        No sizes are configured for this product.
                                    </p>

                                <% } %>

                            </div>

                        </div>

                        <!-- QUANTITY -->

                        <div class="quantity-section">

                            <label for="quantity"
                                   class="quantity-heading">
                                Select Quantity
                            </label>

                            <input type="number"
                                   id="quantity"
                                   name="quantity"
                                   class="quantity-input"
                                   value="1"
                                   min="1"
                                   max="1"
                                   required
                                   disabled>

                            <p id="stockInfo"></p>

                        </div>

                        <!-- ADD TO CART BUTTON -->

                        <button type="submit"
                                id="addCartButton"
                                class="add-cart-button"
                                disabled>

                            <% if (inStock) { %>
                                Select Size to Add to Cart
                            <% } else { %>
                                Out of Stock
                            <% } %>

                        </button>

                    </form>

                    <a href="<%= contextPath %>/products"
                       class="back-to-products">
                        &larr; Continue Shopping
                    </a>

                </div>

            </div>

        </main>

    <% } %>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

    <script>

        const sizeInputs =
            document.querySelectorAll('input[name="sizeId"]');

        const quantityInput =
            document.getElementById("quantity");

        const addCartButton =
            document.getElementById("addCartButton");

        const stockInfo =
            document.getElementById("stockInfo");

        sizeInputs.forEach(function(sizeInput) {

            sizeInput.addEventListener("change", function() {

                const selectedStock =
                    parseInt(this.dataset.stock, 10);

                if (selectedStock > 0) {

                    quantityInput.disabled = false;
                    quantityInput.value = 1;
                    quantityInput.min = 1;
                    quantityInput.max = selectedStock;

                    addCartButton.disabled = false;
                    addCartButton.textContent = "Add to Cart";

                    stockInfo.textContent =
                        selectedStock + " items available in this size";

                    stockInfo.style.color = "#198754";
                }

            });

        });

        if (quantityInput) {

            quantityInput.addEventListener("input", function() {

                const maxQuantity = parseInt(this.max, 10);

                let selectedQuantity = parseInt(this.value, 10);

                if (selectedQuantity > maxQuantity) {
                    this.value = maxQuantity;
                }

                if (selectedQuantity < 1 || isNaN(selectedQuantity)) {
                    this.value = 1;
                }

            });

        }

        const addToCartForm =
            document.getElementById("addToCartForm");

        if (addToCartForm) {

            addToCartForm.addEventListener("submit", function(event) {

                const selectedSize =
                    document.querySelector(
                        'input[name="sizeId"]:checked'
                    );

                const quantity =
                    parseInt(quantityInput.value, 10);

                if (!selectedSize) {
                    event.preventDefault();
                    alert("Please select a size.");
                    return;
                }

                const availableStock =
                    parseInt(selectedSize.dataset.stock, 10);

                if (quantity < 1 || quantity > availableStock) {
                    event.preventDefault();
                    alert("Please select a valid quantity.");
                }

            });

        }

    </script>

</body>
</html>
