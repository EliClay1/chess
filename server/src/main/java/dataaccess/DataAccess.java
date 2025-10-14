package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {

    void clear();

    void createUser(UserData user);

    UserData getUserByName(String username);

    void addAuth(AuthData authData);

    AuthData getUserByAuth(String authToken);



}
