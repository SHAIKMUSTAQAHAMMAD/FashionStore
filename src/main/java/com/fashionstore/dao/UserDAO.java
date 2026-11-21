
package com.fashionstore.dao;

import java.sql.SQLException;

import com.fashionstore.model.User;

public interface UserDAO {

    // Register a new user
    boolean registerUser(User user) throws SQLException;

    // Retrieve user by ID
    User getUserById(int userId) throws SQLException;

    // Retrieve user by email (Login)
    User getUserByEmail(String email) throws SQLException;

    // Check whether email already exists
    boolean isEmailExists(String email) throws SQLException;

    // Update user profile details
    boolean updateUser(User user) throws SQLException;

    // Update user password
    boolean updatePassword(int userId, String newPassword)
            throws SQLException;
}
