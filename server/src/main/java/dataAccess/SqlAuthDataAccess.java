package dataAccess;

import model.AuthData;
import model.UserData;

public class SqlAuthDataAccess implements AuthDataAccess
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

    @Override
    public int getSize() throws DataAccessException
    {
        return 0;
    }
}
