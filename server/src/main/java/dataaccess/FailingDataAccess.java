package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class FailingDataAccess implements DataAccess{
    @Override
    public void clear() throws Exception {
        throw new Exception("failure");
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
    public int createGame(GameData gameData) {
        return 0;
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
}
