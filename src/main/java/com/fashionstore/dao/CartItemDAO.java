
package com.fashionstore.dao;

import java.sql.SQLException;
import java.util.List;

import com.fashionstore.model.CartItem;

public interface CartItemDAO {

    // Add an item to the cart
    boolean addCartItem(CartItem cartItem) throws SQLException;

    // Retrieve cart item by ID
    CartItem getCartItemById(int cartItemId) throws SQLException;

    // Retrieve all items belonging to a cart
    List<CartItem> getCartItemsByCartId(int cartId)
            throws SQLException;

    // Update item quantity
    boolean updateCartItemQuantity(int cartItemId, int quantity)
            throws SQLException;

    // Remove an item from the cart
    boolean removeCartItem(int cartItemId) throws SQLException;

    // Remove a particular product size from the cart
    boolean removeCartItemBySizeId(int cartId, int sizeId)
            throws SQLException;

    // Clear all items from the cart
    boolean clearCartItems(int cartId) throws SQLException;

    // Check whether an item already exists in the cart
    boolean isCartItemExists(int cartId, int sizeId)
            throws SQLException;

    // Get total number of items in the cart
    int getCartItemCount(int cartId) throws SQLException;

}
