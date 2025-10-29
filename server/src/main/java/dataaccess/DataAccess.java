package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    void clear() throws Exception;
    void createUser(UserData user) throws Exception;
    UserData getUser(String username);
    void addAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
    void createGame(GameData gameData);
    GameData getGame(Integer gameID);
    ArrayList<GameData> listGames();
    void updateGame(GameData gameData);
    int createID();
    boolean isEmpty();

}
