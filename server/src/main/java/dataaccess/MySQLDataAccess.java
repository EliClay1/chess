package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;


public class MySQLDataAccess implements DataAccess{

    public MySQLDataAccess() throws Exception {
        initializeDatabase();
    }

    @Override
    public void clear() throws Exception {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
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
                id INT,
                username VARCHAR(100),
                password VARCHAR(255),
                email VARCHAR(255)
                );
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
                id INT,
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
            throw new DataAccessException(String.format("Failed to create database, %s", e));
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String createCommand : createTables) {
                PreparedStatement preparedStatement = conn.prepareStatement(createCommand);
                preparedStatement.execute();
            }

        } catch (Exception e) {
            // TODO - actually give explanation to the error here.
            throw new DataAccessException(String.format("Failed to create tables, %s", e));
        }
    }

    private void sendDatabaseCommand(String sqlCommand, Object... additionalArguments) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            // not entirely sure what return generated keys is doing here.
            try (PreparedStatement prepState = conn.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS)) {

            } catch (Exception e) {
                throw new DataAccessException(String.format("Failed to send sql command to database, %s", e));
            }


        } catch (Exception e) {
            throw new DataAccessException(String.format("Failed to connect to database, %s", e));
        }
    }
}
