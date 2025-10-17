package service;

import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.DoesntExistException;
import exceptions.InvalidException;
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
        if (dataAccess.getUserByName(username) != null) {
            throw new AlreadyTakenException();
        } else {
            dataAccess.createUser(user);
        }
        AuthData authData = new AuthData(username, generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        String username = user.username();
        UserData userByName = dataAccess.getUserByName(username);
        if (userByName == null) {
            throw new DoesntExistException();
        }
        if (!userByName.password().equals(user.password())) {
            throw new InvalidException();
        }
        AuthData authData = new AuthData(username, generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(AuthData authData) throws Exception {
        String authToken = authData.authToken();
        AuthData userByAuth = dataAccess.getUserByAuth(authToken);
        if (userByAuth == null) {
            throw new InvalidException();
        }
        dataAccess.deleteAuth(userByAuth);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
