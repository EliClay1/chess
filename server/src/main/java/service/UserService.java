package service;

import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
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

        UserData testUser = dataAccess.getUser(username);

        if (testUser != null) {
            throw new AlreadyTakenException("Username has already been taken.");
        }
        dataAccess.createUser(user);

        return new AuthData(username, UUID.randomUUID().toString());
    }
}
