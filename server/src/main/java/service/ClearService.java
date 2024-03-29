package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import dataAccess.UserDataAccess;

import java.util.HashMap;
import java.util.Map;

public class ClearService
{
    private final UserDataAccess userAccess;
    private final GameDataAccess gameAccess;
    private final AuthDataAccess authAccess;

    public ClearService(UserDataAccess userAccess, GameDataAccess gameAccess, AuthDataAccess authAccess)
    {
        this.userAccess = userAccess;
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public void clear() throws DataAccessException
    {
        authAccess.clear();
        userAccess.clear();
        gameAccess.clear();
    }
}
