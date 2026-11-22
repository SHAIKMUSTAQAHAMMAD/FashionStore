
package com.fashionstore.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;
import com.fashionstore.util.DBConnection;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public int placeOrder(Order order, List<OrderItem> orderItems, int cartId)
            throws SQLException {

        if (orderItems == null || orderItems.isEmpty()) {
            throw new SQLException("Order must contain at least one item.");
        }

        String cartQuery = "SELECT cart_id FROM carts "
                + "WHERE cart_id = ? AND user_id = ? FOR UPDATE";

        String orderQuery = "INSERT INTO orders "
                + "(user_id, total_amount, delivery_address, delivery_city, "
                + "delivery_state, delivery_pincode, payment_method, "
                + "payment_status, order_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String itemQuery = "INSERT INTO order_items "
                + "(order_id, product_id, product_name, size, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String stockQuery = "UPDATE product_sizes "
                + "SET stock_quantity = stock_quantity - ? "
                + "WHERE product_id = ? AND size = ? AND stock_quantity >= ?";

        String clearCartQuery =
                "DELETE FROM cart_items WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try {
                connection.setAutoCommit(false);

                // Step 1: Verify customer's cart.

                try (PreparedStatement ps =
                        connection.prepareStatement(cartQuery)) {

                    ps.setInt(1, cartId);
                    ps.setInt(2, order.getUserId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Invalid customer cart.");
                        }
                    }
                }

                // Step 2: Insert order details.

                int orderId;

                try (PreparedStatement ps = connection.prepareStatement(
                        orderQuery, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, order.getUserId());
                    ps.setBigDecimal(2, order.getTotalAmount());
                    ps.setString(3, order.getDeliveryAddress());
                    ps.setString(4, order.getDeliveryCity());
                    ps.setString(5, order.getDeliveryState());
                    ps.setString(6, order.getDeliveryPincode());
                    ps.setString(7, order.getPaymentMethod());

                    String paymentStatus = order.getPaymentStatus() == null
                            ? "PENDING" : order.getPaymentStatus();

                    String orderStatus = order.getOrderStatus() == null
                            ? "PLACED" : order.getOrderStatus();

                    ps.setString(8, paymentStatus);
                    ps.setString(9, orderStatus);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Order ID was not generated.");
                        }

                        orderId = keys.getInt(1);
                    }
                }

                // Step 3: Insert order items and decrease stock.

                try (PreparedStatement itemPS =
                        connection.prepareStatement(itemQuery);

                     PreparedStatement stockPS =
                        connection.prepareStatement(stockQuery)) {

                    for (OrderItem item : orderItems) {

                        if (item.getQuantity() <= 0) {
                            throw new SQLException("Invalid item quantity.");
                        }

                        if (item.getUnitPrice() == null
                                || item.getUnitPrice().signum() < 0) {
                            throw new SQLException("Invalid item price.");
                        }

                        // Insert order item.

                        itemPS.setInt(1, orderId);
                        itemPS.setInt(2, item.getProductId());
                        itemPS.setString(3, item.getProductName());
                        itemPS.setString(4, item.getSize());
                        itemPS.setInt(5, item.getQuantity());
                        itemPS.setBigDecimal(6, item.getUnitPrice());

                        itemPS.executeUpdate();

                        // Reduce stock for the selected size.

                        stockPS.setInt(1, item.getQuantity());
                        stockPS.setInt(2, item.getProductId());
                        stockPS.setString(3, item.getSize());
                        stockPS.setInt(4, item.getQuantity());

                        int updatedRows = stockPS.executeUpdate();

                        if (updatedRows == 0) {
                            throw new SQLException(
                                    "Insufficient stock for product: "
                                    + item.getProductName()
                                    + ", size: " + item.getSize());
                        }
                    }
                }

                // Step 4: Clear customer's cart.

                try (PreparedStatement ps =
                        connection.prepareStatement(clearCartQuery)) {

                    ps.setInt(1, cartId);
                    ps.executeUpdate();
                }

                // Step 5: Commit transaction.

                connection.commit();

                order.setOrderId(orderId);

                return orderId;

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public Order getOrderByIdAndUserId(int orderId, int userId)
            throws SQLException {

        String query = "SELECT * FROM orders "
                + "WHERE order_id = ? AND user_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps =
                    connection.prepareStatement(query)) {

                ps.setInt(1, orderId);
                ps.setInt(2, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return extractOrder(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) throws SQLException {

        List<Order> orders = new ArrayList<>();

        String query = "SELECT * FROM orders "
                + "WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps =
                    connection.prepareStatement(query)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        orders.add(extractOrder(rs));
                    }
                }
            }
        }

        return orders;
    }

    @Override
    public boolean cancelOrder(int orderId, int userId)
            throws SQLException {

        String orderQuery = "SELECT order_status FROM orders "
                + "WHERE order_id = ? AND user_id = ? FOR UPDATE";

        String itemsQuery = "SELECT product_id, size, quantity "
                + "FROM order_items WHERE order_id = ?";

        String stockQuery = "UPDATE product_sizes "
                + "SET stock_quantity = stock_quantity + ? "
                + "WHERE product_id = ? AND size = ?";

        String cancelQuery = "UPDATE orders "
                + "SET order_status = 'CANCELLED' "
                + "WHERE order_id = ? AND user_id = ? "
                + "AND order_status = 'PLACED'";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try {
                connection.setAutoCommit(false);

                // Step 1: Check customer's order.

                String status;

                try (PreparedStatement ps =
                        connection.prepareStatement(orderQuery)) {

                    ps.setInt(1, orderId);
                    ps.setInt(2, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }

                        status = rs.getString("order_status");
                    }
                }

                // Only PLACED orders can be cancelled.

                if (!"PLACED".equalsIgnoreCase(status)) {
                    connection.rollback();
                    return false;
                }

                // Step 2: Restore stock.

                try (PreparedStatement itemPS =
                        connection.prepareStatement(itemsQuery);

                     PreparedStatement stockPS =
                        connection.prepareStatement(stockQuery)) {

                    itemPS.setInt(1, orderId);

                    try (ResultSet rs = itemPS.executeQuery()) {

                        while (rs.next()) {

                            stockPS.setInt(1, rs.getInt("quantity"));
                            stockPS.setInt(2, rs.getInt("product_id"));
                            stockPS.setString(3, rs.getString("size"));

                            int updatedRows = stockPS.executeUpdate();

                            if (updatedRows == 0) {
                                throw new SQLException(
                                        "Unable to restore product stock.");
                            }
                        }
                    }
                }

                // Step 3: Update order status.

                try (PreparedStatement ps =
                        connection.prepareStatement(cancelQuery)) {

                    ps.setInt(1, orderId);
                    ps.setInt(2, userId);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "Order cancellation failed.");
                    }
                }

                connection.commit();

                return true;

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    // Extract order details from ResultSet.

    private Order extractOrder(ResultSet rs) throws SQLException {

        Order order = new Order();

        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setDeliveryCity(rs.getString("delivery_city"));
        order.setDeliveryState(rs.getString("delivery_state"));
        order.setDeliveryPincode(rs.getString("delivery_pincode"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setOrderDate(rs.getTimestamp("order_date"));

        return order;
    }
}
