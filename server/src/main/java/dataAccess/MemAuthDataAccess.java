package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class MemAuthDataAccess implements AuthDataAccess
{
    private Set<AuthData> auths = new HashSet<>();
    @Override
    public void clear() throws DataAccessException
    {
        auths.clear();
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
        return auths.size();
    }
}
