
package com.fashionstore.util;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input != null) {
                properties.load(input);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to read db.properties", e);
        }
    }

    private static String getConfig(String key, String defaultValue) {

        // Render environment variables take priority.
        String value = System.getenv(key);

        if (value != null && !value.isEmpty()) {
            return value;
        }

        // Local project configuration.
        return properties.getProperty(key, defaultValue);
    }

    public static Connection getConnection() {

        String url = getConfig(
                "DB_URL",
                "jdbc:mysql://localhost:3306/fashion_store");

        String username = getConfig("DB_USERNAME", "root");

        String password = getConfig("DB_PASSWORD", null);

        if (password == null || password.isEmpty()) {
            throw new IllegalStateException(
                    "DB_PASSWORD is missing.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(
                    url, username, password);

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException(
                    "Unable to connect to MySQL database", e);
        }
    }
}
