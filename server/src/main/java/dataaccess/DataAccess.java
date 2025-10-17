package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {

    /*
    * clear: A method for clearing all data from the database. This is used during testing.
    * createUser: Create a new user.
    * getUser: Retrieve a user with the given username.
    * createGame: Create a new game.
    * getGame: Retrieve a specified game with the given game ID.
    * listGames: Retrieve all games.
    * updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
    */

    void clear();
    void createUser(UserData user);
    UserData getUserByName(String username);
    void addAuth(AuthData authData);
    AuthData getUserByAuth(String authToken);
    void deleteAuth(AuthData authData);

}
