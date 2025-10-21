package service;

import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.DoesntExistException;
import exceptions.InvalidException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public record UserService(DataAccess dataAccess) {

    public AuthData register(UserData user) throws Exception {
        String username = user.username();
        if (dataAccess.getUser(username) != null) {
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
        UserData userByName = dataAccess.getUser(username);
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
        AuthData userByAuth = dataAccess.getAuth(authToken);
        if (userByAuth == null) {
            throw new InvalidException();
        }
        dataAccess.deleteAuth(userByAuth);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
