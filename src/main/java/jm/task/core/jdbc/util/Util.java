package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {

    private static final String URL = "jdbc:mysql://localhost:3306/my new db";
    private static final String USER = "root";
    ;
    private static final String PASSWORD = "PaToNg20ph24uket$";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}
