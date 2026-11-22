
package com.fashionstore.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.dao.CartItemDAO;
import com.fashionstore.model.CartItem;
import com.fashionstore.util.DBConnection;

public class CartItemDAOImpl implements CartItemDAO {

    @Override
    public boolean addCartItem(CartItem cartItem) throws SQLException {

        if (cartItem.getQuantity() <= 0) {
            throw new SQLException("Quantity must be greater than zero.");
        }

        String query = "INSERT INTO cart_items (cart_id, size_id, quantity) "
                     + "VALUES (?, ?, ?) "
                     + "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartItem.getCartId());
                ps.setInt(2, cartItem.getSizeId());
                ps.setInt(3, cartItem.getQuantity());
                ps.setInt(4, cartItem.getQuantity());

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public CartItem getCartItemById(int cartItemId) throws SQLException {

        String query = "SELECT * FROM cart_items WHERE cart_item_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartItemId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return extractCartItem(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public List<CartItem> getCartItemsByCartId(int cartId) throws SQLException {

        List<CartItem> cartItems = new ArrayList<>();

        String query = "SELECT * FROM cart_items WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        cartItems.add(extractCartItem(rs));
                    }
                }
            }
        }

        return cartItems;
    }

    @Override
    public boolean updateCartItemQuantity(int cartItemId, int quantity)
            throws SQLException {

        if (quantity <= 0) {
            throw new SQLException("Quantity must be greater than zero.");
        }

        String query = "UPDATE cart_items SET quantity = ? "
                     + "WHERE cart_item_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, quantity);
                ps.setInt(2, cartItemId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean removeCartItem(int cartItemId) throws SQLException {

        String query = "DELETE FROM cart_items WHERE cart_item_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartItemId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean removeCartItemBySizeId(int cartId, int sizeId)
            throws SQLException {

        String query = "DELETE FROM cart_items "
                     + "WHERE cart_id = ? AND size_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);
                ps.setInt(2, sizeId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean clearCartItems(int cartId) throws SQLException {

        String query = "DELETE FROM cart_items WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);

                ps.executeUpdate();

                return true;
            }
        }
    }

    @Override
    public boolean isCartItemExists(int cartId, int sizeId)
            throws SQLException {

        String query = "SELECT 1 FROM cart_items "
                     + "WHERE cart_id = ? AND size_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);
                ps.setInt(2, sizeId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    @Override
    public int getCartItemCount(int cartId) throws SQLException {

        String query = "SELECT COUNT(*) FROM cart_items WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed.");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return 0;
    }

    private CartItem extractCartItem(ResultSet rs) throws SQLException {

        CartItem cartItem = new CartItem();

        cartItem.setCartItemId(rs.getInt("cart_item_id"));
        cartItem.setCartId(rs.getInt("cart_id"));
        cartItem.setSizeId(rs.getInt("size_id"));
        cartItem.setQuantity(rs.getInt("quantity"));

        return cartItem;
    }
}
