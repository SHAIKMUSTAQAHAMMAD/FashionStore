
package com.fashionstore.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.fashionstore.model.Product;

public interface ProductDAO {

    boolean addProduct(Product product) throws SQLException;

    Product getProductById(int productId) throws SQLException;

    List<Product> getAllProducts() throws SQLException;

    boolean updateProduct(Product product) throws SQLException;

    boolean deleteProduct(int productId) throws SQLException;

    List<Product> searchProducts(String keyword) throws SQLException;

    List<Product> getProductsByCategory(int categoryId) throws SQLException;

    List<Product> getProductsByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice) throws SQLException;

    List<Product> getProductsBySize(String size) throws SQLException;

    List<Product> sortProductsByPriceLowToHigh() throws SQLException;

    List<Product> sortProductsByPriceHighToLow() throws SQLException;

    List<Product> sortProductsByNewest() throws SQLException;

    List<Product> filterProducts(
            String keyword,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size,
            String sortBy,
            int limit,
            int offset) throws SQLException;

    List<Product> getProductsWithPagination(
            int limit, int offset) throws SQLException;

    int getTotalProductCount() throws SQLException;

    int getFilteredProductCount(
            String keyword,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size) throws SQLException;
}
