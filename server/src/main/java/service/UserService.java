package service;

public class UserService {
    private void request() {
        /*
        Does user already exist? call dao.getUser(request.username)
        If so, raise UserAlreadyExistsError
        Otherwise:
        Generate random token
        Create new UserData, AuthData
        dao.insertUser, dao.insertAuth
        return RegisterResponse(username, authToken)
         */
    }
}
