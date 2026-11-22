
package com.fashionstore.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.model.Product;
import com.fashionstore.util.DBConnection;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public boolean addProduct(Product product) throws SQLException {

        String query = "INSERT INTO products "
                + "(category_id, product_name, description, price, image_url) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, product.getCategoryId());
                ps.setString(2, product.getProductName());
                ps.setString(3, product.getDescription());
                ps.setBigDecimal(4, product.getPrice());
                ps.setString(5, product.getImageUrl());

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public Product getProductById(int productId) throws SQLException {

        String query = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, productId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return extractProduct(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {

        String query = "SELECT * FROM products ORDER BY product_id DESC";

        return executeProductQuery(query);
    }

    @Override
    public boolean updateProduct(Product product) throws SQLException {

        String query = "UPDATE products SET category_id = ?, "
                + "product_name = ?, description = ?, price = ?, image_url = ? "
                + "WHERE product_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, product.getCategoryId());
                ps.setString(2, product.getProductName());
                ps.setString(3, product.getDescription());
                ps.setBigDecimal(4, product.getPrice());
                ps.setString(5, product.getImageUrl());
                ps.setInt(6, product.getProductId());

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean deleteProduct(int productId) throws SQLException {

        String query = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, productId);

                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public List<Product> searchProducts(String keyword) throws SQLException {

        String query = "SELECT * FROM products "
                + "WHERE product_name LIKE ? OR description LIKE ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                String searchValue = "%" + keyword + "%";

                ps.setString(1, searchValue);
                ps.setString(2, searchValue);

                try (ResultSet rs = ps.executeQuery()) {
                    return extractProductList(rs);
                }
            }
        }
    }

    @Override
    public List<Product> getProductsByCategory(int categoryId)
            throws SQLException {

        String query = "SELECT * FROM products WHERE category_id = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, categoryId);

                try (ResultSet rs = ps.executeQuery()) {
                    return extractProductList(rs);
                }
            }
        }
    }

    @Override
    public List<Product> getProductsByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {

        String query = "SELECT * FROM products WHERE price BETWEEN ? AND ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setBigDecimal(1, minPrice);
                ps.setBigDecimal(2, maxPrice);

                try (ResultSet rs = ps.executeQuery()) {
                    return extractProductList(rs);
                }
            }
        }
    }

    @Override
    public List<Product> getProductsBySize(String size) throws SQLException {

        String query = "SELECT DISTINCT p.* FROM products p "
                + "INNER JOIN product_sizes ps "
                + "ON p.product_id = ps.product_id "
                + "WHERE ps.size = ? AND ps.stock_quantity > 0";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, size);

                try (ResultSet rs = ps.executeQuery()) {
                    return extractProductList(rs);
                }
            }
        }
    }

    @Override
    public List<Product> sortProductsByPriceLowToHigh()
            throws SQLException {

        return executeProductQuery(
                "SELECT * FROM products ORDER BY price ASC");
    }

    @Override
    public List<Product> sortProductsByPriceHighToLow()
            throws SQLException {

        return executeProductQuery(
                "SELECT * FROM products ORDER BY price DESC");
    }

    @Override
    public List<Product> sortProductsByNewest() throws SQLException {

        return executeProductQuery(
                "SELECT * FROM products ORDER BY created_at DESC");
    }

    @Override
    public List<Product> filterProducts(
            String keyword,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size,
            String sortBy,
            int limit,
            int offset) throws SQLException {

        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT p.* FROM products p ");

        if (size != null && !size.trim().isEmpty()) {
            query.append("INNER JOIN product_sizes ps ")
                    .append("ON p.product_id = ps.product_id ");
        }

        query.append("WHERE 1=1 ");

        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {

            query.append("AND (p.product_name LIKE ? ")
                    .append("OR p.description LIKE ?) ");

            String searchValue = "%" + keyword.trim() + "%";

            parameters.add(searchValue);
            parameters.add(searchValue);
        }

        if (categoryId != null) {
            query.append("AND p.category_id = ? ");
            parameters.add(categoryId);
        }

        if (minPrice != null) {
            query.append("AND p.price >= ? ");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            query.append("AND p.price <= ? ");
            parameters.add(maxPrice);
        }

        if (size != null && !size.trim().isEmpty()) {
            query.append("AND ps.size = ? AND ps.stock_quantity > 0 ");
            parameters.add(size.trim());
        }

        if ("price_low".equalsIgnoreCase(sortBy)) {
            query.append("ORDER BY p.price ASC ");
        } else if ("price_high".equalsIgnoreCase(sortBy)) {
            query.append("ORDER BY p.price DESC ");
        } else if ("newest".equalsIgnoreCase(sortBy)) {
            query.append("ORDER BY p.created_at DESC ");
        } else {
            query.append("ORDER BY p.product_id DESC ");
        }

        query.append("LIMIT ? OFFSET ?");

        parameters.add(limit);
        parameters.add(offset);

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps =
                    connection.prepareStatement(query.toString())) {

                setParameters(ps, parameters);

                try (ResultSet rs = ps.executeQuery()) {
                    return extractProductList(rs);
                }
            }
        }
    }

    @Override
    public List<Product> getProductsWithPagination(int limit, int offset)
            throws SQLException {

        String query = "SELECT * FROM products "
                + "ORDER BY product_id DESC LIMIT ? OFFSET ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, limit);
                ps.setInt(2, offset);

                try (ResultSet rs = ps.executeQuery()) {
                    return extractProductList(rs);
                }
            }
        }
    }

    @Override
    public int getTotalProductCount() throws SQLException {

        String query = "SELECT COUNT(*) FROM products";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    @Override
    public int getFilteredProductCount(
            String keyword,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size) throws SQLException {

        StringBuilder query = new StringBuilder(
                "SELECT COUNT(DISTINCT p.product_id) FROM products p ");

        if (size != null && !size.trim().isEmpty()) {
            query.append("INNER JOIN product_sizes ps ")
                    .append("ON p.product_id = ps.product_id ");
        }

        query.append("WHERE 1=1 ");

        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {

            query.append("AND (p.product_name LIKE ? ")
                    .append("OR p.description LIKE ?) ");

            String searchValue = "%" + keyword.trim() + "%";

            parameters.add(searchValue);
            parameters.add(searchValue);
        }

        if (categoryId != null) {
            query.append("AND p.category_id = ? ");
            parameters.add(categoryId);
        }

        if (minPrice != null) {
            query.append("AND p.price >= ? ");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            query.append("AND p.price <= ? ");
            parameters.add(maxPrice);
        }

        if (size != null && !size.trim().isEmpty()) {
            query.append("AND ps.size = ? AND ps.stock_quantity > 0 ");
            parameters.add(size.trim());
        }

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps =
                    connection.prepareStatement(query.toString())) {

                setParameters(ps, parameters);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return 0;
    }

    private List<Product> executeProductQuery(String query)
            throws SQLException {

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                return extractProductList(rs);
            }
        }
    }

    private List<Product> extractProductList(ResultSet rs)
            throws SQLException {

        List<Product> products = new ArrayList<>();

        while (rs.next()) {
            products.add(extractProduct(rs));
        }

        return products;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {

        Product product = new Product();

        product.setProductId(rs.getInt("product_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setProductName(rs.getString("product_name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedAt(rs.getTimestamp("created_at"));

        return product;
    }

    private void setParameters(
            PreparedStatement ps, List<Object> parameters)
            throws SQLException {

        for (int i = 0; i < parameters.size(); i++) {

            Object parameter = parameters.get(i);

            if (parameter instanceof Integer) {
                ps.setInt(i + 1, (Integer) parameter);

            } else if (parameter instanceof BigDecimal) {
                ps.setBigDecimal(i + 1, (BigDecimal) parameter);

            } else if (parameter instanceof String) {
                ps.setString(i + 1, (String) parameter);
            }
        }
    }
}
