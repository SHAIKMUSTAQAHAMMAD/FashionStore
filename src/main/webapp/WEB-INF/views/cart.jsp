
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.fashionstore.model.CartItem" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.ProductSize" %>

<%
    String contextPath = request.getContextPath();

    List<Map<String, Object>> cartDetails =
            (List<Map<String, Object>>) request.getAttribute("cartDetails");

    BigDecimal cartTotal =
            (BigDecimal) request.getAttribute("cartTotal");

    Integer totalItems =
            (Integer) request.getAttribute("totalItems");

    if (cartDetails == null) {
        cartDetails = new java.util.ArrayList<>();
    }

    if (cartTotal == null) {
        cartTotal = BigDecimal.ZERO;
    }

    if (totalItems == null) {
        totalItems = 0;
    }

    boolean isCartEmpty = cartDetails.isEmpty();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Shopping Cart | Fashion Store</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/navbar.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/cart.css">
</head>

<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="cart-page">

        <div class="cart-header">
            <h1>My Shopping Cart</h1>
            <p>Review your selected products before checkout.</p>
        </div>

        <% if (isCartEmpty) { %>

            <section class="empty-cart">

                <div class="empty-cart-icon">
                    <span>[ CART ]</span>
                </div>

                <h2>Your Cart is Empty</h2>

                <p>
                    You have not added any products to your cart yet.
                </p>

                <a href="<%= contextPath %>/products"
                   class="continue-shopping-btn">
                    Continue Shopping
                </a>

            </section>

        <% } else { %>

            <section class="cart-content">

                <div class="cart-items-section">

                    <div class="cart-items-heading">

                        <h2>Cart Items</h2>

                        <span>
                            <%= totalItems %> Items
                        </span>

                    </div>

                    <% for (Map<String, Object> item : cartDetails) {

                        CartItem cartItem =
                                (CartItem) item.get("cartItem");

                        Product product =
                                (Product) item.get("product");

                        ProductSize productSize =
                                (ProductSize) item.get("productSize");

                        BigDecimal subtotal =
                                (BigDecimal) item.get("subtotal");

                        if (cartItem == null || product == null ||
                                productSize == null) {
                            continue;
                        }

                        int quantity = cartItem.getQuantity();

                        if (subtotal == null) {
                            subtotal = product.getPrice().multiply(
                                    BigDecimal.valueOf(quantity));
                        }

                        String imageUrl = product.getImageUrl();

                        if (imageUrl == null || imageUrl.trim().isEmpty()) {
                            imageUrl = contextPath +
                                    "/assets/images/no-image.png";
                        } else if (!imageUrl.startsWith("http://")
                                && !imageUrl.startsWith("https://")
                                && !imageUrl.startsWith(contextPath + "/")) {

                            imageUrl = contextPath + "/" +
                                    imageUrl.replaceFirst("^/+", "");
                        }
                    %>

                        <div class="cart-item">

                            <div class="cart-item-image">

                                <img src="<%= imageUrl %>"
                                     alt="Product Image"
                                     onerror="this.onerror=null;this.src='<%= contextPath %>/assets/images/no-image.png';">

                            </div>

                            <div class="cart-item-details">

                                <h3>
                                    <%= product.getProductName() %>
                                </h3>

                                <p class="cart-item-price">
                                    Rs. <%= product.getPrice() %>
                                </p>

                                <p class="cart-item-size">
                                    Size:
                                    <strong>
                                        <%= productSize.getSize() %>
                                    </strong>
                                </p>

                                <p class="cart-item-stock">
                                    Available Stock:
                                    <%= productSize.getStockQuantity() %>
                                </p>

                            </div>

                            <div class="cart-item-actions">

                                <form action="<%= contextPath %>/cart"
                                      method="post"
                                      class="quantity-form">

                                    <input type="hidden"
                                           name="action"
                                           value="update">

                                    <input type="hidden"
                                           name="cartItemId"
                                           value="<%= cartItem.getCartItemId() %>">

                                    <label>Quantity</label>

                                    <input type="number"
                                           name="quantity"
                                           value="<%= quantity %>"
                                           min="1"
                                           max="<%= productSize.getStockQuantity() %>"
                                           required>

                                    <button type="submit"
                                            class="update-cart-btn">
                                        Update
                                    </button>

                                </form>

                                <form action="<%= contextPath %>/cart"
                                      method="post"
                                      class="remove-form">

                                    <input type="hidden"
                                           name="action"
                                           value="remove">

                                    <input type="hidden"
                                           name="cartItemId"
                                           value="<%= cartItem.getCartItemId() %>">

                                    <button type="submit"
                                            class="remove-cart-btn"
                                            onclick="return confirm('Remove this product from your cart?');">
                                        Remove
                                    </button>

                                </form>

                            </div>

                            <div class="cart-item-subtotal">

                                <span>Subtotal</span>

                                <strong>
                                    Rs. <%= subtotal %>
                                </strong>

                            </div>

                        </div>

                    <% } %>

                    <a href="<%= contextPath %>/products"
                       class="continue-shopping-link">
                        Continue Shopping
                    </a>

                </div>

                <aside class="cart-summary">

                    <h2>Order Summary</h2>

                    <div class="summary-row">
                        <span>Total Items</span>
                        <span><%= totalItems %></span>
                    </div>

                    <div class="summary-row">
                        <span>Subtotal</span>
                        <span>Rs. <%= cartTotal %></span>
                    </div>

                    <div class="summary-row">
                        <span>Delivery</span>
                        <span class="free-delivery">Free</span>
                    </div>

                    <div class="summary-divider"></div>

                    <div class="summary-total">
                        <span>Total Amount</span>

                        <strong>
                            Rs. <%= cartTotal %>
                        </strong>
                    </div>

                    <a href="<%= contextPath %>/checkout"
                       class="checkout-btn">
                        Proceed to Checkout
                    </a>

                    <p class="secure-checkout">
                        Secure Checkout
                    </p>

                </aside>

            </section>

        <% } %>

    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
