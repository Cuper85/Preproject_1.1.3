package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static jm.task.core.jdbc.util.Util.getConnection;

public class UserDaoJDBCImpl implements UserDao {

    public UserDaoJDBCImpl() {
    }

    @Override
    public void dropUsersTable() {
        try (Statement statement = getConnection().createStatement()) {
            statement.execute("DROP TABLE IF EXISTS User;");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении таблицы", e);
        }
    }

    @Override
    public void createUsersTable() throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
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
    public void saveUser(String name, String lastName, byte age) {
        String insert = "INSERT INTO user (name, lastName, age) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(insert)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.execute();
            System.out.println("User " + name + " добавлен в таблицу");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления user'а в таблицу", e);
        }
    }

    @Override
    public void removeUserById(long id) {
        String deleteOfId = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(deleteOfId)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            System.out.println("User с данным ID " + id + " удалён из таблицы");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления user'а из таблицы", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement statement = getConnection().createStatement();
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
        try (Statement statement = getConnection().createStatement()) {
            statement.execute("DELETE FROM user;");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки таблицы", e);
        }
    }
}


