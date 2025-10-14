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
        String username = user.username();

        if (dataAccess.getUser(username) != null) {
            throw new AlreadyTakenException("Username has already been taken.");
        } else {
            dataAccess.createUser(user);
        }
        AuthData authData = new AuthData(username, generateAuthToken());


        // TODO - Add auth data to the data access

        return authData;
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
