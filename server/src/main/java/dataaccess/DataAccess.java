package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {

    /*
    * clear: A method for clearing all data from the database. This is used during testing.
    * createGame: Create a new game.
    * getGame: Retrieve a specified game with the given game ID.
    * listGames: Retrieve all games.
    * updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
    * This is used when players join a game or when a move is made.
    */

    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void addAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
    void createGame(GameData gameData);
    GameData getGame(Integer gameID);
    ArrayList<GameData> getAllGames();
    void updateGame(GameData gameData);
    int createID();

}
