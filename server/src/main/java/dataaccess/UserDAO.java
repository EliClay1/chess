package dataaccess;

import model.UserData;

public class UserDAO {

    /*  C.R.U.D. Operations:
        Create objects in the data store
        Read objects from the data store
        Update objects already in the data store
        Delete objects from the data store
    */

     /*
        *   clear: A method for clearing all data from the database. This is used during testing.
            createUser: Create a new user.
            getUser: Retrieve a user with the given username.
            createGame: Create a new game.
            getGame: Retrieve a specified game with the given game ID.
            listGames: Retrieve all games.
            updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
            createAuth: Create a new authorization.
            getAuth: Retrieve an authorization given an authToken.
            deleteAuth: Delete an authorization so that it is no longer valid.
* */

//    void insertUser(UserData u) throws DataAccessException {
//    }

    void createUser(String username, String password, String email) {

    }

}
