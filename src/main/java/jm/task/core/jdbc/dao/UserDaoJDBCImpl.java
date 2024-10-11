package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private Connection connection;

    public UserDaoJDBCImpl() throws SQLException {
        this.connection = Util.getConnection();
    }

    @Override
    public void dropUsersTable() {               //  Удаление таблицы User(ов) — не должно приводить к исключению, если таблицы не существует
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS User;");     //  DROP удаляет целую таблицу, включая ее структуру и все данные.
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении таблицы", e);
        }
    }

    @Override
    public void createUsersTable() {                                  //  Создание таблицы для User(ов) — не должно приводить к исключению, если такая таблица уже существует
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(15), " +
                    "lastName VARCHAR(15), " +
                    "age INT(2)" +
                    ");");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы", e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {                      //  Добавление User в таблицу
        String insert = "INSERT INTO user (name, lastName, age) VALUES (?, ?, ?)";      //  было так: String insert = "INSERT INTO my_new_table (name, lastName, age) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.execute();
            connection.commit();
            System.out.println("User " + name + " добавлен в таблицу");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Ошибка отката транзакции", rollbackException);
            }
            throw new RuntimeException("Ошибка добавления user'а в таблицу", e);
        }
    }

    @Override
    public void removeUserById(long id) {                             //  Удаление User из таблицы (по id)
        String deleteOfId = "DELETE FROM user WHERE id = ?";          //  было так: String deleteOfId = "DELETE FROM my_new_table WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteOfId)) {
            connection.setAutoCommit(false);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            connection.commit();
            System.out.println("User с данным ID " + id + " удалён из таблицы");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Ошибка отката транзакции", rollbackException);
            }
            throw new RuntimeException("Ошибка удаления user'а из таблицы", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";                        //  было так: String query = "SELECT * FROM my_new_table";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getByte("age"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех users'ов из таблицы", e);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {                                       //  Очистка содержания таблицы
        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.execute("DELETE FROM user;");                    //  было так: statement.execute("DELETE FROM my_new_table;");
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Ошибка отката транзакции", rollbackException);
            }
            throw new RuntimeException("Ошибка очистки таблицы", e);
        }
    }
}



/*
package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private Connection connection;  //  объяви поле типа Connection и присвой ему результат работы статического метода класса Util,
    // таким образом получишь одно соединение с БД и сможешь использовать его во всех методах.

    public UserDaoJDBCImpl() throws SQLException {
        this.connection = Util.getConnection();

    }

    @Override
    public void dropUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS User;");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении таблицы", e);
        }
    }

    @Override
    public void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(15), " +
                    "lastName VARCHAR(15), " +
                    "age INT(2)" +
                    ");");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы", e);
        }
    }

    @Override                     //  Эта аннотация указывает, что метод saveUser переопределяет метод из интерфейса UserService, который реализует данный класс.
    public void saveUser(String name, String lastName, byte age) {   //  Метод saveUser добавляет нового пользователя в таблицу user в базе данных с использованием транзакций для обеспечения атомарности операций.
        String insert = "INSERT INTO user (name, lastName, age) VALUES (?, ?, ?)";  //  Создается SQL-запрос INSERT для добавления нового пользователя в таблицу user.
        //  В SQL-запросе используются знаки вопроса ? как заполнители для значений имени, фамилии и возраста.

        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {  //  Создает объект PreparedStatement с использованием предоставленного SQL-запроса insert.
            //  PreparedStatement - это безопасный способ выполнения SQL-запросов, который защищает от SQL-инъекций.
            connection.setAutoCommit(false); // Отключаем авто-коммит
            preparedStatement.setString(1, name);       //  setString(1, name) устанавливает значение имени пользователя на первое место в запросе (индекс 1).
            preparedStatement.setString(2, lastName);   //  setString(2, lastName) и setByte(3, age) аналогично устанавливают значения для фамилии и возраста.
            preparedStatement.setByte(3, age);
            preparedStatement.execute();
            connection.commit();                        //  Фиксирует изменения, сделанные в транзакции. Если транзакция будет завершена успешно, все изменения будут сохранены в базе данных.
            System.out.println("User " + name + " добавлен в таблицу");
        } catch (SQLException e) {
            try {
                connection.rollback();    //  Если в блоке try возникла ошибка, выполняется откат транзакции. Это гарантирует, что в базе данных не будут сохранены неполные или некорректные данные.
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Ошибка отката транзакции", rollbackException);
            }
            throw new RuntimeException("Ошибка добавления user'а в таблицу", e);
        }
    }

    @Override
    public void removeUserById(long id) {
        String deleteOfId = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteOfId)) {
            connection.setAutoCommit(false); // Отключаем авто-коммит
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            connection.commit();
            System.out.println("User с данным ID " + id + " удалён из таблицы");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Ошибка отката транзакции", rollbackException);
            }
            throw new RuntimeException("Ошибка удаления user'а из таблицы", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getByte("age"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех users'ов из таблицы", e);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.execute("DELETE FROM user;");
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Ошибка отката транзакции", rollbackException);
            }
            throw new RuntimeException("Ошибка очистки таблицы", e);
        }
    }
}
 */