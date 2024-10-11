package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {

    private static final String URL = "jdbc:mysql://localhost:3306/my new db";
    private static final String USER = "root";
    private static final String PASSWORD = "PaToNg20ph24uket$";

    private static Connection connection;  // Создаем статическое поле private static Connection connection - для хранения соединения с базой данных. Это поле будет общедоступным для всех методов класса Util.

    private Util() {
    }    //  Объявляем конструктор private Util() {}, чтобы предотвратить создание новых экземпляров класса Util вне самого класса. Это делает класс Util - singleton.

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);   //  Метод getConnection() проверяет, закрыто ли соединение, и создает новое, если это необходимо.
        }
        return connection;
    }

    public static void closeConnection() {   //  Метод closeConnection() закрывает соединение и освобождает ссылку на него.
        if (connection != null) {
            try {
                connection.close();
                connection = null;    //   Освобождаем ссылку после закрытия. Сбрасываем ссылку на соединение после закрытия
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}

