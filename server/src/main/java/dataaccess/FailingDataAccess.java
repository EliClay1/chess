package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class FailingDataAccess implements DataAccess{

    private final HashMap<String, UserData> usersByName = new HashMap<>();
    private final HashMap<String, AuthData> auth = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private final TreeSet<Integer> usedIDs = new TreeSet<>();
    private final PriorityQueue<Integer> reusableIDs = new PriorityQueue<>();

    @Override
    public void clear() throws Exception {
        throw new Exception("database error");
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
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> listOfGames = new ArrayList<>();
        for (int id : usedIDs) {
            listOfGames.add(getGame(id));
        }
        return listOfGames;
    }

    @Override
    public void updateGame(GameData gameData) {
        int gameID = gameData.gameID();
        games.remove(gameID);
        games.put(gameID, gameData);
    }

    @Override
    public int createID() {
        int nextID;
        if (!reusableIDs.isEmpty()) {
            nextID = reusableIDs.remove();
        } else {
            nextID = (usedIDs.isEmpty() ? 1 : usedIDs.last() + 1);
        }
        usedIDs.add(nextID);
        return nextID;
    }

    @Override
    public boolean isEmpty() {
        return usedIDs.isEmpty() && auth.isEmpty() && games.isEmpty() && usersByName.isEmpty() && reusableIDs.isEmpty();
    }
}
