package org.ptcc.internals.Database;

import java.sql.DriverManager;
import java.sql.SQLException;

public enum Connection {
    INSTANCE;
    private java.sql.Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/javaccr";
    private final String user = "root";
    private final String password = "admin";

    Connection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }
}
