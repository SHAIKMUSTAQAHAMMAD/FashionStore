
package com.fashionstore.dao;

import java.sql.SQLException;
import java.util.List;

import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;

public interface OrderDAO {

    // Place a new order with its items and cart ID
    int placeOrder(Order order, List<OrderItem> orderItems, int cartId)
            throws SQLException;

    // Retrieve an order belonging to a specific user
    Order getOrderByIdAndUserId(int orderId, int userId)
            throws SQLException;

    // Retrieve all orders belonging to a user
    List<Order> getOrdersByUserId(int userId)
            throws SQLException;

    // Cancel an order belonging to a specific user
    boolean cancelOrder(int orderId, int userId)
            throws SQLException;
}
