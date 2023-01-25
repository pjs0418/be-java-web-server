package db;

import model.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static model.general.Database.*;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private Map<String, User> users = new HashMap<>();

    public void addUser(User user) {
        try {
            Connection connection =
                    DriverManager.getConnection(DB_URL.getDBInfo(), DB_USER_NAME.getDBInfo(), DB_PASSWORD.getDBInfo());

            PreparedStatement preparedStatement =
                    connection.prepareStatement("insert into user(userId, password, name, email) values(?, ?, ?, ?);");

            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getEmail());

            int changeRow = preparedStatement.executeUpdate();
            logger.debug("변경된 row 수: {}", changeRow);

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findUserById(String userId) {
        User user = null;

        try {
            Connection connection =
                    DriverManager.getConnection(DB_URL.getDBInfo(), DB_USER_NAME.getDBInfo(), DB_PASSWORD.getDBInfo());

            PreparedStatement preparedStatement =
                    connection.prepareStatement("select * from user where userId = ?;");

            preparedStatement.setString(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            user = User.of(resultSet.getString(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4));

            logger.debug("userId: {}, password: {}, name: {}, email: {}",
                    user.getUserId(), user.getPassword(), user.getName(), user.getEmail());

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public Collection<User> findAll() {
        return users.values();
    }
}
