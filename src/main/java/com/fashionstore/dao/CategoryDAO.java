
package com.fashionstore.dao;

import java.sql.SQLException;
import java.util.List;

import com.fashionstore.model.Category;

public interface CategoryDAO {

    // Add a new category
    boolean addCategory(Category category) throws SQLException;

    // Retrieve category by ID
    Category getCategoryById(int categoryId) throws SQLException;

    // Retrieve all categories
    List<Category> getAllCategories() throws SQLException;

    // Update category details
    boolean updateCategory(Category category) throws SQLException;

    // Delete category by ID
    boolean deleteCategory(int categoryId) throws SQLException;

    // Check whether category name already exists
    boolean isCategoryExists(String categoryName) throws SQLException;
}
