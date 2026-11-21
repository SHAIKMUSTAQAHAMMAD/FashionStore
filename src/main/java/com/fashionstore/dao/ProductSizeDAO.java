
package com.fashionstore.dao;

import java.sql.SQLException;
import java.util.List;

import com.fashionstore.model.ProductSize;

public interface ProductSizeDAO {

    // Add a new size for a product
    boolean addProductSize(ProductSize productSize) throws SQLException;

    // Retrieve size details by size ID
    ProductSize getProductSizeById(int sizeId) throws SQLException;

    // Retrieve all sizes of a particular product
    List<ProductSize> getSizesByProductId(int productId)
            throws SQLException;

    // Retrieve available sizes of a product
    List<ProductSize> getAvailableSizesByProductId(int productId)
            throws SQLException;

    // Update product size details
    boolean updateProductSize(ProductSize productSize) throws SQLException;

    // Update stock quantity
    boolean updateStockQuantity(int sizeId, int stockQuantity)
            throws SQLException;

    // Decrease stock quantity
    boolean decreaseStock(int sizeId, int quantity) throws SQLException;

    // Increase stock quantity
    boolean increaseStock(int sizeId, int quantity) throws SQLException;

    // Delete product size
    boolean deleteProductSize(int sizeId) throws SQLException;

}
