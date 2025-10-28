package dataaccess;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.sql.*;


public class MySQLDataAccess implements DataAccess{

    public MySQLDataAccess() throws ResponseException {
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

    private void configureDatabase() {
    }
}
