
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Order" %>

<%
    String contextPath = request.getContextPath();

    List<Order> orders =
        (List<Order>) request.getAttribute("orders");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>My Orders | FashionStore</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/navbar.css">

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

        .orders-container {
            max-width: 1150px;
            margin: 45px auto;
            padding: 0 20px;
            min-height: 60vh;
        }

        .orders-heading {
            font-size: 30px;
            margin-bottom: 8px;
        }

        .orders-subtitle {
            color: #777;
            margin-bottom: 30px;
        }

        .order-card {
            background: white;
            border: 1px solid #eee6f2;
            border-radius: 12px;
            margin-bottom: 22px;
            overflow: hidden;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
        }

        .order-header {
            background: #f5eff8;
            padding: 20px 25px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .order-id {
            font-size: 19px;
            font-weight: bold;
            color: #81399a;
        }

        .order-date {
            font-size: 14px;
            color: #777;
            margin-top: 7px;
        }

        .order-body {
            padding: 25px;
        }

        .order-info-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 25px;
        }

        .info-label {
            color: #777;
            font-size: 13px;
            margin-bottom: 8px;
        }

        .info-value {
            font-weight: bold;
            font-size: 15px;
        }

        .total-amount {
            font-size: 21px;
            color: #81399a;
        }

        .status-badge {
            display: inline-block;
            padding: 8px 14px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: bold;
        }

        .status-placed {
            background: #e6f8ec;
            color: #168c45;
        }

        .status-pending {
            background: #fff3dc;
            color: #a76b12;
        }

        .status-cancelled {
            background: #ffe9e9;
            color: #bd2929;
        }

        .order-footer {
            border-top: 1px solid #eee;
            padding: 18px 25px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .payment-text {
            color: #777;
            font-size: 14px;
        }

        .shop-btn {
            display: inline-block;
            background: #9346ad;
            color: white;
            padding: 13px 23px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: bold;
        }

        .shop-btn:hover {
            background: #79338f;
        }

        .empty-orders {
            background: white;
            padding: 65px 25px;
            border-radius: 14px;
            text-align: center;
            border: 1px solid #eee6f2;
        }

        .empty-icon {
            font-size: 60px;
            margin-bottom: 15px;
        }

        .empty-orders h2 {
            font-size: 25px;
        }

        .empty-orders p {
            color: #777;
            margin-bottom: 25px;
        }

        @media (max-width: 750px) {

            .order-info-grid {
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .orders-heading {
                font-size: 26px;
            }
        }

        @media (max-width: 450px) {

            .order-info-grid {
                grid-template-columns: 1fr;
            }

            .order-header,
            .order-footer {
                align-items: flex-start;
                flex-direction: column;
            }
        }

    </style>

</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<div class="orders-container">

    <h1 class="orders-heading">My Orders</h1>

    <p class="orders-subtitle">
        View your order history and payment status.
    </p>

    <% if (orders == null || orders.isEmpty()) { %>

        <div class="empty-orders">

            <div class="empty-icon">🛍️</div>

            <h2>No Orders Yet</h2>

            <p>
                You haven't placed any orders yet.
                Start shopping and find something you love!
            </p>

            <a href="<%= contextPath %>/products"
               class="shop-btn">
                Start Shopping
            </a>

        </div>

    <% } else { %>

        <% for (Order order : orders) {

            String paymentMethod = order.getPaymentMethod();

            String paymentLabel = "Cash on Delivery";

            if ("UPI".equalsIgnoreCase(paymentMethod)) {
                paymentLabel = "UPI";
            } else if ("CARD".equalsIgnoreCase(paymentMethod)) {
                paymentLabel = "Credit / Debit Card";
            }

            String orderStatus = order.getOrderStatus();

            String statusClass = "status-placed";

            if ("CANCELLED".equalsIgnoreCase(orderStatus)) {
                statusClass = "status-cancelled";
            } else if ("PENDING".equalsIgnoreCase(orderStatus)) {
                statusClass = "status-pending";
            }

            String paymentStatus = order.getPaymentStatus();
        %>

        <div class="order-card">

            <div class="order-header">

                <div>

                    <div class="order-id">
                        Order #<%= order.getOrderId() %>
                    </div>

                    <div class="order-date">
                        <% if (order.getOrderDate() != null) { %>
                            Placed on <%= order.getOrderDate() %>
                        <% } %>
                    </div>

                </div>

                <span class="status-badge <%= statusClass %>">
                    <%= orderStatus %>
                </span>

            </div>

            <div class="order-body">

                <div class="order-info-grid">

                    <div>

                        <div class="info-label">
                            Total Amount
                        </div>

                        <div class="info-value total-amount">
                            ₹ <%= order.getTotalAmount() %>
                        </div>

                    </div>

                    <div>

                        <div class="info-label">
                            Payment Method
                        </div>

                        <div class="info-value">
                            <%= paymentLabel %>
                        </div>

                    </div>

                    <div>

                        <div class="info-label">
                            Payment Status
                        </div>

                        <div class="info-value">
                            <%= paymentStatus %>
                        </div>

                    </div>

                </div>

            </div>

            <div class="order-footer">

                <span class="payment-text">
                    Order Status: <%= orderStatus %>
                </span>

                <a href="<%= contextPath %>/products"
                   class="shop-btn">
                    Continue Shopping
                </a>

            </div>

        </div>

        <% } %>

    <% } %>

</div>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
