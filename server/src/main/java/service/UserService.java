package service;

import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.DoesntExistException;
import exceptions.InvalidPasswordException;
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
        if (dataAccess.getUserByName(username) == null) {
            throw new DoesntExistException();
        }
        if (!dataAccess.getUserByName(username).password().equals(user.password())) {
            throw new InvalidPasswordException();
        }

        return null;
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
