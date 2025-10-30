package service;

import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.DoesntExistException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public record UserService(DataAccess dataAccess) {

    public AuthData register(UserData user) throws Exception {
        String username = user.username();
        if (dataAccess.getUser(username) != null) {
            throw new AlreadyTakenException();
        }
        dataAccess.createUser(user);

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
            throw new UnauthorizedException();
        }
        AuthData authData = new AuthData(username, generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(AuthData authData) throws Exception {
        String authToken = authData.authToken();
        AuthData userByAuth = dataAccess.getAuth(authToken);
        if (userByAuth == null) {
            throw new UnauthorizedException();
        }
        dataAccess.deleteAuth(userByAuth);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private String generateHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
