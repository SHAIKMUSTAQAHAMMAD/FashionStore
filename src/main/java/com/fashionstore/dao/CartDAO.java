
package com.fashionstore.dao;

import java.sql.SQLException;

import com.fashionstore.model.Cart;

public interface CartDAO {

    // Create a cart for a user
    boolean createCart(int userId) throws SQLException;

    // Retrieve cart by cart ID
    Cart getCartById(int cartId) throws SQLException;

    // Retrieve cart belonging to a user
    Cart getCartByUserId(int userId) throws SQLException;

    // Check whether user already has a cart
    boolean isCartExists(int userId) throws SQLException;

    // Clear all items from a user's cart
    boolean clearCart(int cartId) throws SQLException;

    // Delete cart by ID
    boolean deleteCart(int cartId) throws SQLException;

}
