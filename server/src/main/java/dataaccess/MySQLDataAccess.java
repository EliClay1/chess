package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.DoesntExistException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;


public class MySQLDataAccess implements DataAccess{

    private final Gson serializer = new Gson();

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
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlCommand = "SELECT * FROM userdata WHERE username=?";
            try (PreparedStatement prepState = conn.prepareStatement(sqlCommand)) {
                prepState.setString(1, username);
                try (ResultSet resultSet = prepState.executeQuery()) {
                    if (resultSet.next()) {
                        var user = resultSet.getString("username");
                        var password = resultSet.getString("password");
                        var email = resultSet.getString("email");
                        return new UserData(user, password, email);
                    }

                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void addAuth(AuthData authData) throws Exception {
        if (getAuth(authData.authToken()) != null) {throw new AlreadyTakenException();}

        String sqlCommand = "INSERT INTO authdata (username, authtoken) VALUES (?, ?)";
        sendDatabaseCommand(sqlCommand, authData.username(), authData.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlCommand = "SELECT * FROM authdata WHERE authtoken=?";
            try (PreparedStatement prepState = conn.prepareStatement(sqlCommand)) {
                prepState.setString(1, authToken);
                try (ResultSet resultSet = prepState.executeQuery()) {
                    if (resultSet.next()) {
                        var authToken1 = resultSet.getString("authToken");
                        var user = resultSet.getString("username");
                        return new AuthData(user, authToken1);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws Exception {
        if (getAuth(authData.authToken()) == null) {throw new DoesntExistException();}
        String sqlCommand = "DELETE FROM authdata WHERE username=?";
        sendDatabaseCommand(sqlCommand, authData.username());
    }

    @Override
    public int createGame(GameData gameData) throws Exception {
        String serializedGame = serializeFromGameObject(gameData.game());
        int gameID;
        String sqlCommand = "INSERT INTO gamedata (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
        gameID = sendDatabaseCommand(sqlCommand, gameData.gameName(),
                gameData.whiteUsername(), gameData.blackUsername(), serializedGame);
        return gameID;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlCommand = "SELECT * FROM gamedata WHERE gameID=?";
            try (PreparedStatement prepState = conn.prepareStatement(sqlCommand)) {
                prepState.setInt(1, gameID);
                try (ResultSet resultSet = prepState.executeQuery()) {
                    if (resultSet.next()) {
                        var gameName = resultSet.getString("gameName");
                        var whiteName = resultSet.getString("whiteUsername");
                        var blackName = resultSet.getString("blackUsername");
                        var game = resultSet.getString("game");
                        // TODO - move game serialization
                        return new GameData(gameID, whiteName, blackName, gameName, serializeToGameObject(game));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<Integer> usedIDs = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlCommand = "SELECT gameID FROM gamedata";
            try (PreparedStatement prepState = conn.prepareStatement(sqlCommand)) {
                try (ResultSet resultSet = prepState.executeQuery()) {
                    while (resultSet.next()) {
                        usedIDs.add(resultSet.getInt("gameID"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        ArrayList<GameData> listOfGames = new ArrayList<>();
        for (int id : usedIDs) {
            listOfGames.add(getGame(id));
        }
        return listOfGames;
    }

    @Override
    public void updateGame(GameData gameData) {

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
                game JSON
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

    // TODO - We don't even have to have a catch statement, the handling in other functions will cover it as long as an exception is thrown.
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
                    } else if (arg == null) {
                        prepState.setObject(i + 1, null);
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

    private String serializeFromGameObject(ChessGame chessGame) {
        return serializer.toJson(chessGame);
    }

    private ChessGame serializeToGameObject(String chessGame) {
        // TODO - is this a violation of any principles?
        return serializer.fromJson(chessGame, ChessGame.class);
    }
}
