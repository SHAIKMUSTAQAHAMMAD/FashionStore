
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.fashionstore.model.Order" %>

<%
    String contextPath = request.getContextPath();

    Order order = (Order) request.getAttribute("order");

    if (order == null) {
        response.sendRedirect(contextPath + "/orders");
        return;
    }

    String paymentMethod = order.getPaymentMethod();

    String paymentLabel = "Cash on Delivery";

    if ("UPI".equalsIgnoreCase(paymentMethod)) {
        paymentLabel = "UPI Payment";
    } else if ("CARD".equalsIgnoreCase(paymentMethod)) {
        paymentLabel = "Credit / Debit Card";
    }

    String paymentStatus = order.getPaymentStatus();

    String paymentMessage = "Payment Pending";

    if ("PAID".equalsIgnoreCase(paymentStatus)
            || "SUCCESS".equalsIgnoreCase(paymentStatus)) {
        paymentMessage = "Payment Completed";
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Order Successful | FashionStore</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/navbar.css">

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f8f6fa;
            color: #29232e;
        }

        .success-container {
            max-width: 750px;
            margin: 60px auto;
            padding: 0 20px;
        }

        .success-card {
            background: #ffffff;
            padding: 45px 35px;
            border-radius: 16px;
            text-align: center;
            box-shadow: 0 8px 35px rgba(0,0,0,0.07);
        }

        .success-icon {
            width: 105px;
            height: 105px;
            background: #e6f8ec;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 25px;
        }

        .success-icon svg {
            width: 65px;
            height: 65px;
        }

        .success-title {
            font-size: 30px;
            color: #168c45;
            margin-bottom: 12px;
        }

        .success-description {
            color: #777;
            font-size: 16px;
            line-height: 1.7;
            margin-bottom: 30px;
        }

        .order-number {
            display: inline-block;
            background: #f3eaf7;
            color: #81399a;
            font-size: 17px;
            font-weight: bold;
            padding: 12px 22px;
            border-radius: 9px;
            margin-bottom: 30px;
        }

        .order-details {
            text-align: left;
            background: #faf8fc;
            padding: 25px;
            border-radius: 12px;
            border: 1px solid #eee6f2;
            margin-bottom: 30px;
        }

        .order-details h2 {
            font-size: 20px;
            margin-top: 0;
            margin-bottom: 20px;
        }

        .detail-row {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding: 13px 0;
            border-bottom: 1px solid #eae4ee;
            font-size: 15px;
        }

        .detail-row:last-child {
            border-bottom: none;
        }

        .detail-label {
            color: #777;
        }

        .detail-value {
            font-weight: 600;
            text-align: right;
        }

        .total-value {
            color: #81399a;
            font-size: 19px;
        }

        .payment-pending {
            color: #b7791f;
        }

        .payment-completed {
            color: #168c45;
        }

        .success-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .btn {
            display: inline-block;
            padding: 14px 25px;
            border-radius: 8px;
            font-size: 15px;
            font-weight: bold;
            text-decoration: none;
            transition: 0.2s;
        }

        .btn-primary {
            background: #9346ad;
            color: white;
        }

        .btn-primary:hover {
            background: #79338f;
        }

        .btn-secondary {
            background: white;
            color: #9346ad;
            border: 1px solid #9346ad;
        }

        .btn-secondary:hover {
            background: #f7effa;
        }

        .confirmation-note {
            margin-top: 25px;
            color: #777;
            font-size: 14px;
            line-height: 1.6;
        }

        @media (max-width: 600px) {

            .success-container {
                margin: 25px auto;
            }

            .success-card {
                padding: 30px 18px;
            }

            .success-title {
                font-size: 25px;
            }

            .detail-row {
                font-size: 14px;
            }

            .success-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }

    </style>

</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<div class="success-container">

    <div class="success-card">

        <!-- Green Success Tick -->

        <div class="success-icon">

            <svg viewBox="0 0 24 24"
                 fill="none"
                 xmlns="http://www.w3.org/2000/svg">

                <circle cx="12"
                        cy="12"
                        r="10"
                        fill="#16a34a"/>

                <path d="M7 12.5L10.5 16L17 9"
                      stroke="white"
                      stroke-width="2.4"
                      stroke-linecap="round"
                      stroke-linejoin="round"/>

            </svg>

        </div>

        <h1 class="success-title">
            Order Placed Successfully!
        </h1>

        <p class="success-description">
            Thank you for shopping with FashionStore!
            <br>
            Your order has been placed successfully.
        </p>

        <div class="order-number">
            Order ID: #<%= order.getOrderId() %>
        </div>

        <!-- Order Summary -->

        <div class="order-details">

            <h2>Order Details</h2>

            <div class="detail-row">
                <span class="detail-label">
                    Order Number
                </span>

                <span class="detail-value">
                    #<%= order.getOrderId() %>
                </span>
            </div>

            <div class="detail-row">
                <span class="detail-label">
                    Total Amount
                </span>

                <span class="detail-value total-value">
                    ₹ <%= order.getTotalAmount() %>
                </span>
            </div>

            <div class="detail-row">
                <span class="detail-label">
                    Payment Method
                </span>

                <span class="detail-value">
                    <%= paymentLabel %>
                </span>
            </div>

            <div class="detail-row">
                <span class="detail-label">
                    Payment Status
                </span>

                <span class="detail-value
                    <%= "Payment Completed".equals(paymentMessage)
                            ? "payment-completed"
                            : "payment-pending" %>">

                    <%= paymentMessage %>

                </span>
            </div>

            <div class="detail-row">
                <span class="detail-label">
                    Order Status
                </span>

                <span class="detail-value">
                    <%= order.getOrderStatus() %>
                </span>
            </div>

        </div>

        <!-- Navigation Buttons -->

        <div class="success-buttons">

            <a href="<%= contextPath %>/products"
               class="btn btn-primary">
                Continue Shopping
            </a>

            <a href="<%= contextPath %>/orders"
               class="btn btn-secondary">
                View My Orders
            </a>

        </div>

        <p class="confirmation-note">
            You can view your order details and
            track your order from the My Orders page.
        </p>

    </div>

</div>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
