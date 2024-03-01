package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;

public class UserService
{
    private final UserDataAccess userAccess;
    private final AuthDataAccess authAccess;

    public UserService(UserDataAccess userAccess, AuthDataAccess authAccess)
    {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }
    public AuthData register(UserData user) throws DataAccessException
    {
        user = userAccess.createUser(user);
        return authAccess.createAuth(user);
    }
    public AuthData login(String username, String password) throws DataAccessException
    {
        if(userAccess.verifyUser(username, password))
            return authAccess.createAuth(userAccess.getUser(username));
        throw new DataAccessException("Error: Invalid Password");
    }

    public boolean logout(String authToken) throws DataAccessException
    {
        return authAccess.deleteAuth(authToken);
    }
}
