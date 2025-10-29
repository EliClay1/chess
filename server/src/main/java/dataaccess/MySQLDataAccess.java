package dataaccess;

import chess.ChessGame;
import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class MySQLDataAccess implements DataAccess{

    public MySQLDataAccess() throws Exception {
        initializeDatabase();
    }

    @Override
    public void clear() throws Exception {
        String[] clearCommands = {
                "TRUNCATE TABLE authdata",
                "TRUNCATE TABLE userdata",
                "TRUNCATE TABLE gamedata"
        };

        for (String command : clearCommands) {
            sendDatabaseCommand(command);
        }
    }

    @Override
    public void createUser(UserData user) throws Exception {
        // the question marks are used to prevent SQL injection.
        String sqlCommand = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        // just because thed sendDatabaseCommand returns an int, doesn't mean I have to assingn it to.
        sendDatabaseCommand(sqlCommand, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlCommand = "SELECT * FROM userdata WHERE username=?";
            try (PreparedStatement prepState = conn.prepareStatement(sqlCommand)) {
                prepState.setString(1, username);
                prepState.executeUpdate();
                try (ResultSet resultSet = prepState.getResultSet()) {
                    System.out.println(resultSet.getString(2));
                    System.out.println(resultSet.getString(3));
                    System.out.println(resultSet.getString(4));
                }
            }
        }
        return null;
    }

    @Override
    public void addAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }

    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public int createID() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private final String[] createTables = {
            """
            CREATE TABLE IF NOT EXISTS userdata (
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(100),
                password VARCHAR(255),
                email VARCHAR(255)
                );
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(100),
                authToken VARCHAR(255)
                );
            """,
            """
            CREATE TABLE IF NOT EXISTS gamedata (
                gameID INT PRIMARY KEY AUTO_INCREMENT,
                gameName VARCHAR(255),
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                game LONGTEXT
                );
            """
    };

    private void initializeDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new DataAccessException(String.format("Failed to create database, %s", e.getMessage()));
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String createCommand : createTables) {
                PreparedStatement preparedStatement = conn.prepareStatement(createCommand);
                preparedStatement.execute();
            }

        } catch (Exception e) {
            throw new DataAccessException(String.format("Failed to create tables, %s", e.getMessage()));
        }
    }

    // TODO - figure how to get this to return data, like searching the database.
    private int sendDatabaseCommand(String sqlCommand, Object... additionalArguments) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {

            try {
                // not entirely sure what return generated keys is doing here.
                PreparedStatement prepState = conn.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS);

                for (int i = 0; i < additionalArguments.length; i++) {
                    var arg = additionalArguments[i];
                    // the + 1 is to fix out of range errors.
                    if (arg instanceof String a) {
                        prepState.setString(i + 1, a);
                    } else if (arg instanceof Integer a) {
                        prepState.setInt(i + 1, a);
                    } else if (arg instanceof ChessGame a) {
                        prepState.setObject(i + 1, a);
                    }
                }

                prepState.executeUpdate();

                // This code will return the auto-generated ID number attached to the database (if there is one).
                // This isn't entirely neccessary, but it could be useful later down the line.
                ResultSet resultSet = prepState.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }
                return ID;
            } catch (Exception e) {
                throw new DataAccessException(String.format("Failed to send sql command to database, %s", e.getMessage()));
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Failed to connect to database, %s", e.getMessage()));
        }
    }
}
