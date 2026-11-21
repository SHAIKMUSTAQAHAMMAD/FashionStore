
package com.fashionstore.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.dao.ProductSizeDAO;
import com.fashionstore.model.ProductSize;
import com.fashionstore.util.DBConnection;

public class ProductSizeDAOImpl implements ProductSizeDAO {

    @Override
    public boolean addProductSize(ProductSize productSize)
            throws SQLException {

        String sql = "INSERT INTO product_sizes "
                   + "(product_id, size, stock_quantity) "
                   + "VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productSize.getProductId());
            ps.setString(2, productSize.getSize());
            ps.setInt(3, productSize.getStockQuantity());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ProductSize getProductSizeById(int sizeId)
            throws SQLException {

        String sql = "SELECT size_id, product_id, size, stock_quantity "
                   + "FROM product_sizes WHERE size_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, sizeId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapProductSize(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<ProductSize> getSizesByProductId(int productId)
            throws SQLException {

        List<ProductSize> sizes = new ArrayList<>();

        String sql = "SELECT size_id, product_id, size, stock_quantity "
                   + "FROM product_sizes "
                   + "WHERE product_id = ? "
                   + "ORDER BY size_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    sizes.add(mapProductSize(rs));
                }
            }
        }

        return sizes;
    }

    @Override
    public List<ProductSize> getAvailableSizesByProductId(int productId)
            throws SQLException {

        List<ProductSize> sizes = new ArrayList<>();

        String sql = "SELECT size_id, product_id, size, stock_quantity "
                   + "FROM product_sizes "
                   + "WHERE product_id = ? AND stock_quantity > 0 "
                   + "ORDER BY size_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    sizes.add(mapProductSize(rs));
                }
            }
        }

        return sizes;
    }

    @Override
    public boolean updateProductSize(ProductSize productSize)
            throws SQLException {

        String sql = "UPDATE product_sizes "
                   + "SET product_id = ?, size = ?, stock_quantity = ? "
                   + "WHERE size_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productSize.getProductId());
            ps.setString(2, productSize.getSize());
            ps.setInt(3, productSize.getStockQuantity());
            ps.setInt(4, productSize.getSizeId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStockQuantity(int sizeId, int quantity)
            throws SQLException {

        String sql = "UPDATE product_sizes "
                   + "SET stock_quantity = ? WHERE size_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, sizeId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean decreaseStock(int sizeId, int quantity)
            throws SQLException {

        if (quantity <= 0) {
            return false;
        }

        String sql = "UPDATE product_sizes "
                   + "SET stock_quantity = stock_quantity - ? "
                   + "WHERE size_id = ? AND stock_quantity >= ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, sizeId);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean increaseStock(int sizeId, int quantity)
            throws SQLException {

        if (quantity <= 0) {
            return false;
        }

        String sql = "UPDATE product_sizes "
                   + "SET stock_quantity = stock_quantity + ? "
                   + "WHERE size_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, sizeId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteProductSize(int sizeId)
            throws SQLException {

        String sql = "DELETE FROM product_sizes WHERE size_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, sizeId);

            return ps.executeUpdate() > 0;
        }
    }

    private ProductSize mapProductSize(ResultSet rs)
            throws SQLException {

        ProductSize productSize = new ProductSize();

        productSize.setSizeId(rs.getInt("size_id"));
        productSize.setProductId(rs.getInt("product_id"));
        productSize.setSize(rs.getString("size"));
        productSize.setStockQuantity(rs.getInt("stock_quantity"));

        return productSize;
    }
}
