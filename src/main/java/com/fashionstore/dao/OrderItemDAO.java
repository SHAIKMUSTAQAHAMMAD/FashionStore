
package com.fashionstore.dao;

import java.sql.SQLException;
import java.util.List;

import com.fashionstore.model.OrderItem;

public interface OrderItemDAO {

    // Add an item to an order
    boolean addOrderItem(OrderItem orderItem) throws SQLException;

    // Add multiple items to an order
    boolean addOrderItems(List<OrderItem> orderItems)
            throws SQLException;

    // Retrieve order item by ID
    OrderItem getOrderItemById(int orderItemId) throws SQLException;

    // Retrieve all items belonging to an order
    List<OrderItem> getOrderItemsByOrderId(int orderId)
            throws SQLException;

    // Delete an order item
    boolean deleteOrderItem(int orderItemId) throws SQLException;

    // Delete all items belonging to an order
    boolean deleteOrderItemsByOrderId(int orderId) throws SQLException;

}
