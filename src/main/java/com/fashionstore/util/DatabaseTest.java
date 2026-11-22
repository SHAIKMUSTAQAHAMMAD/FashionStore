
package com.fashionstore.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                System.out.println("Database connection failed!");
                return;
            }

            System.out.println("Database connected successfully!");

            System.out.println("\n========== USERS ==========");
            displayTable(connection, "users");

            System.out.println("\n========== CATEGORIES ==========");
            displayTable(connection, "categories");

            System.out.println("\n========== PRODUCTS ==========");
            displayTable(connection, "products");

            System.out.println("\n========== PRODUCT SIZES ==========");
            displayTable(connection, "product_sizes");

            System.out.println("\n========== CARTS ==========");
            displayTable(connection, "carts");

            System.out.println("\n========== CART ITEMS ==========");
            displayTable(connection, "cart_items");

            System.out.println("\n========== ORDERS ==========");
            displayTable(connection, "orders");

            System.out.println("\n========== ORDER ITEMS ==========");
            displayTable(connection, "order_items");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayTable(Connection connection, String tableName)
            throws SQLException {

        String query = "SELECT * FROM `" + tableName + "`";

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(query);

             ResultSet resultSet = preparedStatement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();

            // Display column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }

            System.out.println();

            // Display table records
            while (resultSet.next()) {

                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getObject(i) + "\t");
                }

                System.out.println();
            }
        }
    }
}
