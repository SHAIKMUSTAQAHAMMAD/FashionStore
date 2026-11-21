
package com.fashionstore.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fashionstore.util.DBConnection;

public class OrderService {

    public int placeOrder(
            int userId,
            String fullName,
            String mobile,
            String address,
            String city,
            String state,
            String pincode,
            String paymentMethod) throws SQLException {

        if (userId <= 0) {
            throw new SQLException("Invalid user.");
        }

        if (isBlank(fullName)
                || isBlank(mobile)
                || isBlank(address)
                || isBlank(city)
                || isBlank(state)
                || isBlank(pincode)) {
            throw new SQLException(
                "All delivery details are required."
            );
        }

        if (!mobile.matches("[0-9]{10}")
                || !pincode.matches("[0-9]{6}")) {
            throw new SQLException(
                "Invalid mobile number or PIN code."
            );
        }

        if (!"COD".equals(paymentMethod)
                && !"UPI".equals(paymentMethod)
                && !"CARD".equals(paymentMethod)) {
            throw new SQLException(
                "Invalid payment method."
            );
        }

        /*
         * Demo payment handling:
         * No online payment is collected here.
         * UPI and CARD orders remain unpaid.
         */
        String paymentStatus = "PENDING";
        String orderStatus = "PLACED";

        try (Connection connection =
                DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int cartId = getCartId(
                    connection, userId
                );

                if (cartId == -1) {
                    throw new SQLException(
                        "Your cart is empty."
                    );
                }

                /*
                 * Lock the cart so two checkout requests
                 * cannot place the same cart simultaneously.
                 */
                lockCart(connection, cartId);

                BigDecimal totalAmount =
                    calculateAndValidateTotal(
                        connection, cartId
                    );

                int orderId = insertOrder(
                    connection,
                    userId,
                    totalAmount,
                    address,
                    city,
                    state,
                    pincode,
                    paymentMethod,
                    paymentStatus,
                    orderStatus
                );

                insertOrderItemsAndReduceStock(
                    connection,
                    cartId,
                    orderId
                );

                clearCartItems(connection, cartId);

                connection.commit();

                return orderId;

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private int getCartId(
            Connection connection,
            int userId) throws SQLException {

        String sql =
            "SELECT cart_id FROM carts "
            + "WHERE user_id = ?";

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("cart_id");
                }
            }
        }

        return -1;
    }

    private void lockCart(
            Connection connection,
            int cartId) throws SQLException {

        String sql =
            "SELECT cart_id FROM carts "
            + "WHERE cart_id = ? FOR UPDATE";

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                        "Cart no longer exists."
                    );
                }
            }
        }
    }

    private BigDecimal calculateAndValidateTotal(
            Connection connection,
            int cartId) throws SQLException {

        String sql =
            "SELECT ci.quantity, "
            + "ps.stock_quantity, "
            + "p.price "
            + "FROM cart_items ci "
            + "JOIN product_sizes ps "
            + "ON ci.size_id = ps.size_id "
            + "JOIN products p "
            + "ON ps.product_id = p.product_id "
            + "WHERE ci.cart_id = ? "
            + "FOR UPDATE";

        BigDecimal total = BigDecimal.ZERO;
        int itemCount = 0;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int quantity =
                        rs.getInt("quantity");

                    int stock =
                        rs.getInt("stock_quantity");

                    BigDecimal price =
                        rs.getBigDecimal("price");

                    if (quantity <= 0) {
                        throw new SQLException(
                            "Invalid cart quantity."
                        );
                    }

                    if (quantity > stock) {
                        throw new SQLException(
                            "Some products do not have "
                            + "enough stock."
                        );
                    }

                    if (price == null
                            || price.signum() < 0) {
                        throw new SQLException(
                            "Invalid product price."
                        );
                    }

                    total = total.add(
                        price.multiply(
                            BigDecimal.valueOf(quantity)
                        )
                    );

                    itemCount++;
                }
            }
        }

        if (itemCount == 0) {
            throw new SQLException(
                "Your cart is empty."
            );
        }

        return total;
    }

    private int insertOrder(
            Connection connection,
            int userId,
            BigDecimal totalAmount,
            String address,
            String city,
            String state,
            String pincode,
            String paymentMethod,
            String paymentStatus,
            String orderStatus) throws SQLException {

        String sql =
            "INSERT INTO orders "
            + "(user_id, total_amount, "
            + "delivery_address, delivery_city, "
            + "delivery_state, delivery_pincode, "
            + "payment_method, payment_status, "
            + "order_status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
                )) {

            ps.setInt(1, userId);
            ps.setBigDecimal(2, totalAmount);
            ps.setString(3, address);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, pincode);
            ps.setString(7, paymentMethod);
            ps.setString(8, paymentStatus);
            ps.setString(9, orderStatus);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException(
                    "Unable to create order."
                );
            }

            try (ResultSet keys =
                    ps.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
            "Order ID was not generated."
        );
    }

    private void insertOrderItemsAndReduceStock(
            Connection connection,
            int cartId,
            int orderId) throws SQLException {

        String selectSql =
            "SELECT ci.size_id, ci.quantity, "
            + "ps.product_id, ps.size, "
            + "p.product_name, p.price "
            + "FROM cart_items ci "
            + "JOIN product_sizes ps "
            + "ON ci.size_id = ps.size_id "
            + "JOIN products p "
            + "ON ps.product_id = p.product_id "
            + "WHERE ci.cart_id = ? "
            + "ORDER BY ci.cart_item_id";

        String insertSql =
            "INSERT INTO order_items "
            + "(order_id, product_id, "
            + "product_name, size, quantity, "
            + "unit_price) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

        String stockSql =
            "UPDATE product_sizes "
            + "SET stock_quantity = "
            + "stock_quantity - ? "
            + "WHERE size_id = ? "
            + "AND stock_quantity >= ?";

        try (PreparedStatement selectPs =
                connection.prepareStatement(selectSql);

             PreparedStatement insertPs =
                connection.prepareStatement(insertSql);

             PreparedStatement stockPs =
                connection.prepareStatement(stockSql)) {

            selectPs.setInt(1, cartId);

            try (ResultSet rs =
                    selectPs.executeQuery()) {

                while (rs.next()) {

                    int sizeId =
                        rs.getInt("size_id");

                    int productId =
                        rs.getInt("product_id");

                    int quantity =
                        rs.getInt("quantity");

                    String size =
                        rs.getString("size");

                    String productName =
                        rs.getString("product_name");

                    BigDecimal price =
                        rs.getBigDecimal("price");

                    stockPs.setInt(1, quantity);
                    stockPs.setInt(2, sizeId);
                    stockPs.setInt(3, quantity);

                    if (stockPs.executeUpdate() != 1) {
                        throw new SQLException(
                            "Insufficient stock for "
                            + productName
                        );
                    }

                    insertPs.setInt(1, orderId);
                    insertPs.setInt(2, productId);
                    insertPs.setString(3, productName);
                    insertPs.setString(4, size);
                    insertPs.setInt(5, quantity);
                    insertPs.setBigDecimal(6, price);

                    if (insertPs.executeUpdate() != 1) {
                        throw new SQLException(
                            "Unable to save order item."
                        );
                    }
                }
            }
        }
    }

    private void clearCartItems(
            Connection connection,
            int cartId) throws SQLException {

        String sql =
            "DELETE FROM cart_items "
            + "WHERE cart_id = ?";

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    private boolean isBlank(String value) {
        return value == null
            || value.trim().isEmpty();
    }
}
