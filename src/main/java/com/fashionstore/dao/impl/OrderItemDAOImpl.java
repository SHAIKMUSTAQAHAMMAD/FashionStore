
package com.fashionstore.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.dao.OrderItemDAO;
import com.fashionstore.model.OrderItem;
import com.fashionstore.util.DBConnection;

public class OrderItemDAOImpl implements OrderItemDAO {

    @Override
    public boolean addOrderItem(OrderItem orderItem) throws SQLException {

        String query = "INSERT INTO order_items "
                + "(order_id, product_id, product_name, size, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, orderItem.getOrderId());
                ps.setInt(2, orderItem.getProductId());
                ps.setString(3, orderItem.getProductName());
                ps.setString(4, orderItem.getSize());
                ps.setInt(5, orderItem.getQuantity());
                ps.setBigDecimal(6, orderItem.getUnitPrice());

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean addOrderItems(List<OrderItem> orderItems)
            throws SQLException {

        if (orderItems == null || orderItems.isEmpty()) {
            return false;
        }

        String query = "INSERT INTO order_items "
                + "(order_id, product_id, product_name, size, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try {
                connection.setAutoCommit(false);

                try (PreparedStatement ps = connection.prepareStatement(query)) {

                    for (OrderItem item : orderItems) {

                        ps.setInt(1, item.getOrderId());
                        ps.setInt(2, item.getProductId());
                        ps.setString(3, item.getProductName());
                        ps.setString(4, item.getSize());
                        ps.setInt(5, item.getQuantity());
                        ps.setBigDecimal(6, item.getUnitPrice());

                        ps.addBatch();
                    }

                    int[] results = ps.executeBatch();

                    for (int result : results) {
                        if (result == 0) {
                            throw new SQLException(
                                    "Failed to insert an order item.");
                        }
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

    @Override
    public OrderItem getOrderItemById(int orderItemId)
            throws SQLException {

        String query = "SELECT * FROM order_items WHERE order_item_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, orderItemId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return extractOrderItem(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId)
            throws SQLException {

        List<OrderItem> orderItems = new ArrayList<>();

        String query = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        orderItems.add(extractOrderItem(rs));
                    }
                }
            }
        }

        return orderItems;
    }

    @Override
    public boolean deleteOrderItem(int orderItemId) throws SQLException {

        String query = "DELETE FROM order_items WHERE order_item_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, orderItemId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean deleteOrderItemsByOrderId(int orderId)
            throws SQLException {

        String query = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, orderId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    // Extract order item details from ResultSet.

    private OrderItem extractOrderItem(ResultSet rs) throws SQLException {

        OrderItem orderItem = new OrderItem();

        orderItem.setOrderItemId(rs.getInt("order_item_id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setProductId(rs.getInt("product_id"));
        orderItem.setProductName(rs.getString("product_name"));
        orderItem.setSize(rs.getString("size"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setUnitPrice(rs.getBigDecimal("unit_price"));

        return orderItem;
    }
}
