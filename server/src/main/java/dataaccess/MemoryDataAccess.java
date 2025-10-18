package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> usersByName = new HashMap<>();
    private final HashMap<String, AuthData> auth = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        usersByName.clear();
    }

    @Override
    public void createUser(UserData user) {
        usersByName.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return usersByName.get(username);
    }

    @Override
    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auth.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        auth.remove(authData.authToken());
    }

    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameId(), gameData);
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

}
