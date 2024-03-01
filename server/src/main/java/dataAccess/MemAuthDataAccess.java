package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MemAuthDataAccess implements AuthDataAccess
{
    private Set<AuthData> auths = new HashSet<>();
    @Override
    public void clear() throws DataAccessException
    {
        auths.clear();
        getSize();
    }

    @Override
    public AuthData createAuth(UserData user) throws DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        auths.add(auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        for(AuthData existingAuth : auths)
            if(existingAuth.authToken().equals(authToken))
                return existingAuth;
        throw new DataAccessException("Error: unauthorized", 401);
    }


    @Override
    public boolean deleteAuth(String authToken) throws DataAccessException
    {
        for(AuthData existingAuth : auths)
            if(existingAuth.authToken().equals(authToken))
            {
                AuthData deadAuth = existingAuth;
                auths.remove(deadAuth);
                return true;
            }
        throw new DataAccessException("Error: unauthorized", 401);
    }

    @Override
    public int getSize() throws DataAccessException
    {
        return auths.size();
    }
}
