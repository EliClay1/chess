package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    void clear() throws Exception;
    void createUser(UserData user) throws Exception;
    UserData getUser(String username) throws Exception;
    void addAuth(AuthData authData) throws Exception;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData authData) throws Exception;
    int createGame(GameData gameData) throws Exception;
    GameData getGame(Integer gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(GameData gameData);
    int createID();
    boolean isEmpty();

}
