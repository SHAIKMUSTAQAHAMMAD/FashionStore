
package com.fashionstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = getSetting(
            "DB_URL",
            "jdbc:mysql://localhost:3306/fashion_store"
    );

    private static final String USERNAME = getSetting(
            "DB_USERNAME",
            "root"
    );

    private static final String PASSWORD = getSetting(
            "DB_PASSWORD",
            ""
    );

    private static String getSetting(String name, String defaultValue) {
        String value = System.getenv(name);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(
                    URL, USERNAME, PASSWORD
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException | SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

            return null;
        }
    }
}
