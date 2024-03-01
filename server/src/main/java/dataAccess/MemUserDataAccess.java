package dataAccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MemUserDataAccess implements UserDataAccess
{
    private Set<UserData> users = new HashSet<>();
    @Override
    public void clear() throws DataAccessException
    {
        users.clear();
        getSize();
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException
    {
        for (UserData existingUser: users)
        {
            if(existingUser.email().equals(user.email()))
                throw new DataAccessException("Error: already taken", 403);
            if (existingUser.username().equals(user.username()))
                throw new DataAccessException("Error: already taken", 403);
        }
        users.add(user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        for(UserData existingUser: users)
            if(existingUser.username().equals(username))
                return existingUser;
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public boolean verifyUser(String username, String password) throws DataAccessException
    {
        for(UserData existingUser: users)
        {
            if(existingUser.username().equals(username))
            {
                if(existingUser.password().equals(password))
                    return true;
                return false;
            }
        }
        throw new DataAccessException("Error: bad request", 401);
    }

    @Override
    public int getSize() throws DataAccessException
    {
        return users.size();
    }
}
