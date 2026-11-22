
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.ProductSize" %>

<%
    String contextPath = request.getContextPath();

    List<Map<String, Object>> cartDetails =
        (List<Map<String, Object>>) request.getAttribute("cartDetails");

    BigDecimal cartTotal =
        (BigDecimal) request.getAttribute("cartTotal");

    if (cartTotal == null) {
        cartTotal = BigDecimal.ZERO;
    }

    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Checkout | FashionStore</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/navbar.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #faf8fc;
            color: #29232e;
        }

        .checkout-container {
            max-width: 1150px;
            margin: 45px auto;
            padding: 0 20px;
        }

        .checkout-title {
            font-size: 32px;
            margin-bottom: 30px;
        }

        .checkout-layout {
            display: grid;
            grid-template-columns: 1.5fr 1fr;
            gap: 30px;
            align-items: start;
        }

        .checkout-box {
            background: white;
            padding: 28px;
            border-radius: 12px;
            border: 1px solid #eee7f1;
            margin-bottom: 24px;
        }

        .checkout-box h2 {
            font-size: 21px;
            margin-top: 0;
            margin-bottom: 22px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%;
            padding: 13px;
            border: 1px solid #d9d2de;
            border-radius: 7px;
            font-size: 15px;
            font-family: inherit;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #9346ad;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }

        .payment-option {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 17px;
            border: 1px solid #e2dbe7;
            border-radius: 9px;
            margin-bottom: 12px;
            cursor: pointer;
        }

        .payment-option:hover {
            border-color: #9346ad;
            background: #faf5fd;
        }

        .payment-option input {
            accent-color: #9346ad;
            width: 18px;
            height: 18px;
        }

        .payment-option span {
            font-weight: 600;
        }

        .order-item {
            display: flex;
            justify-content: space-between;
            gap: 15px;
            padding: 14px 0;
            border-bottom: 1px solid #eee;
        }

        .order-item-name {
            font-weight: 600;
        }

        .order-item-size {
            font-size: 13px;
            color: #777;
            margin-top: 5px;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin: 18px 0;
        }

        .summary-total {
            font-size: 22px;
            font-weight: bold;
            color: #9346ad;
            border-top: 1px solid #eee;
            padding-top: 18px;
        }

        .place-order-btn {
            width: 100%;
            padding: 16px;
            border: none;
            border-radius: 8px;
            background: #9346ad;
            color: white;
            font-size: 17px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 15px;
        }

        .place-order-btn:hover {
            background: #79338f;
        }

        .place-order-btn:disabled {
            background: #aaa;
            cursor: not-allowed;
        }

        .error-message {
            background: #ffe9e9;
            color: #a52020;
            padding: 14px;
            border-radius: 7px;
            margin-bottom: 20px;
        }

        .empty-cart {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 12px;
        }

        .shop-link {
            display: inline-block;
            background: #9346ad;
            color: white;
            padding: 13px 22px;
            border-radius: 7px;
            text-decoration: none;
            margin-top: 20px;
        }

        .payment-note {
            color: #777;
            font-size: 13px;
            line-height: 1.5;
            margin-top: 10px;
        }

        @media (max-width: 800px) {
            .checkout-layout,
            .form-row {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<div class="checkout-container">

    <h1 class="checkout-title">Secure Checkout</h1>

    <% if (error != null) { %>
        <div class="error-message">
            <%= error %>
        </div>
    <% } %>

    <% if (cartDetails == null || cartDetails.isEmpty()) { %>

        <div class="empty-cart">
            <h2>Your cart is empty</h2>
            <p>Add products before proceeding to checkout.</p>

            <a href="<%= contextPath %>/products"
               class="shop-link">
                Continue Shopping
            </a>
        </div>

    <% } else { %>

    <form action="<%= contextPath %>/checkout"
          method="post"
          id="checkoutForm">

        <div class="checkout-layout">

            <div>

                <div class="checkout-box">

                    <h2>Delivery Information</h2>

                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text"
                               name="fullName"
                               placeholder="Enter full name"
                               maxlength="100"
                               required>
                    </div>

                    <div class="form-group">
                        <label>Mobile Number</label>
                        <input type="tel"
                               name="mobile"
                               pattern="[0-9]{10}"
                               maxlength="10"
                               placeholder="10-digit mobile number"
                               required>
                    </div>

                    <div class="form-group">
                        <label>Delivery Address</label>
                        <textarea name="address"
                                  rows="3"
                                  maxlength="500"
                                  placeholder="House number, street, area"
                                  required></textarea>
                    </div>

                    <div class="form-row">

                        <div class="form-group">
                            <label>City</label>
                            <input type="text"
                                   name="city"
                                   maxlength="100"
                                   required>
                        </div>

                        <div class="form-group">
                            <label>State</label>
                            <input type="text"
                                   name="state"
                                   maxlength="100"
                                   required>
                        </div>

                    </div>

                    <div class="form-group">
                        <label>PIN Code</label>
                        <input type="text"
                               name="pincode"
                               pattern="[0-9]{6}"
                               maxlength="6"
                               required>
                    </div>

                </div>

                <div class="checkout-box">

                    <h2>Select Payment Method</h2>

                    <label class="payment-option">
                        <input type="radio"
                               name="paymentMethod"
                               value="COD"
                               checked
                               required>
                        <span>Cash on Delivery</span>
                    </label>

                    <label class="payment-option">
                        <input type="radio"
                               name="paymentMethod"
                               value="UPI">
                        <span>UPI Payment</span>
                    </label>

                    <label class="payment-option">
                        <input type="radio"
                               name="paymentMethod"
                               value="CARD">
                        <span>Credit / Debit Card</span>
                    </label>

                    <p class="payment-note">
                        UPI and card payments are demonstration
                        options until a payment gateway is connected.
                        Do not enter actual card or UPI credentials.
                    </p>

                </div>

            </div>

            <div>

                <div class="checkout-box">

                    <h2>Order Summary</h2>

                    <%
                        for (Map<String, Object> item : cartDetails) {

                            Product product =
                                (Product) item.get("product");

                            ProductSize productSize =
                                (ProductSize) item.get("productSize");

                            Integer quantity =
                                (Integer) item.get("quantity");

                            if (product == null ||
                                productSize == null ||
                                quantity == null) {
                                continue;
                            }

                            BigDecimal itemTotal =
                                product.getPrice().multiply(
                                    BigDecimal.valueOf(quantity)
                                );
                    %>

                    <div class="order-item">

                        <div>
                            <div class="order-item-name">
                                <%= product.getProductName() %>
                            </div>

                            <div class="order-item-size">
                                Size: <%= productSize.getSize() %>
                                | Qty: <%= quantity %>
                            </div>
                        </div>

                        <strong>
                            ₹ <%= itemTotal %>
                        </strong>

                    </div>

                    <% } %>

                    <div class="summary-row">
                        <span>Subtotal</span>
                        <strong>₹ <%= cartTotal %></strong>
                    </div>

                    <div class="summary-row">
                        <span>Delivery</span>
                        <strong>FREE</strong>
                    </div>

                    <div class="summary-row summary-total">
                        <span>Total</span>
                        <span>₹ <%= cartTotal %></span>
                    </div>

                    <button type="submit"
                            class="place-order-btn"
                            id="placeOrderBtn">
                        Place Order
                    </button>

                    <p class="payment-note">
                        Your order will be confirmed after
                        the checkout request is processed.
                    </p>

                </div>

            </div>

        </div>

    </form>

    <% } %>

</div>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

<script>
    const checkoutForm = document.getElementById("checkoutForm");

    if (checkoutForm) {
        checkoutForm.addEventListener("submit", function () {
            const button = document.getElementById("placeOrderBtn");

            if (button && checkoutForm.checkValidity()) {
                button.disabled = true;
                button.textContent = "Processing Order...";
            }
        });
    }
</script>

</body>
</html>
