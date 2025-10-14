package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }



    public AuthData register(UserData user) throws Exception {



        /*
        Does user already exist? call dao.getUser(request.username)
        If so, raise UserAlreadyExistsError
        Otherwise:
        Generate random token
        Create new UserData, AuthData
        dao.insertUser, dao.insertAuth
        return RegisterResponse(username, authToken)
         */

        String username = user.username();

        // TODO - Check if user already exits

        if (dataAccess.getUser(username) != null) {
            throw new Exception("Uh oh, that's an error");
        }
//        UserDAO userDAO = new UserDAO();
//        if (userDAO.getUser(username).equals(user)) {
//            // Big bad, that means it exists.
//        }

        return new AuthData(username, UUID.randomUUID().toString());
    }
}
