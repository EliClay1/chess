package service;

import service.requests.RegisterRequest;
import service.responses.RegisterResponse;

public class UserService {
    public RegisterResponse request(RegisterRequest registerRequest) {
        /*
        Does user already exist? call dao.getUser(request.username)
        If so, raise UserAlreadyExistsError
        Otherwise:
        Generate random token
        Create new UserData, AuthData
        dao.insertUser, dao.insertAuth
        return RegisterResponse(username, authToken)
         */

        System.out.println("request sent!");
        return new RegisterResponse("user", "auth");
    }
}
