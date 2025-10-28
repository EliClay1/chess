package dataaccess;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.sql.*;


public class MySQLDataAccess implements DataAccess{

    public MySQLDataAccess() throws Exception {
        configureDatabase();
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

    private void configureDatabase() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1 + 1, SELECT 2 + 2")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.print(rs.getInt(1));
                System.out.print(rs.getInt(2));
            }
        }
    }
}
