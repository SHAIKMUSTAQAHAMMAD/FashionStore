
package com.fashionstore.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fashionstore.dao.CartDAO;
import com.fashionstore.model.Cart;
import com.fashionstore.util.DBConnection;

public class CartDAOImpl implements CartDAO {

    @Override
    public boolean createCart(int userId) throws SQLException {

        String query = "INSERT INTO carts (user_id) VALUES (?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, userId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public Cart getCartById(int cartId) throws SQLException {

        String query = "SELECT * FROM carts WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return extractCart(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Cart getCartByUserId(int userId) throws SQLException {

        String query = "SELECT * FROM carts WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return extractCart(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public boolean isCartExists(int userId) throws SQLException {

        String query = "SELECT 1 FROM carts WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    @Override
    public boolean clearCart(int cartId) throws SQLException {

        String query = "DELETE FROM cart_items WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);

                ps.executeUpdate();
                return true;
            }
        }
    }

    @Override
    public boolean deleteCart(int cartId) throws SQLException {

        String query = "DELETE FROM carts WHERE cart_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, cartId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    private Cart extractCart(ResultSet rs) throws SQLException {

        Cart cart = new Cart();

        cart.setCartId(rs.getInt("cart_id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setCreatedAt(rs.getTimestamp("created_at"));
        cart.setUpdatedAt(rs.getTimestamp("updated_at"));

        return cart;
    }
}
