package dataAccess;

import model.AuthData;
import model.UserData;

public class MemAuthDataAccess implements AuthDataAccess
{
    @Override
    public void clear() throws DataAccessException
    {

    }

    @Override
    public AuthData createAuth(UserData user) throws DataAccessException
    {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        return null;
    }

    @Override
    public boolean deleteAuth(String authToken) throws DataAccessException
    {
        return false;
    }
}
