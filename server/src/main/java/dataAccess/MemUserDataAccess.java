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
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException
    {
        for (UserData existingUser: users)
        {
            if(existingUser.email() == user.email())
                throw new DataAccessException("Error: already taken", 403);
            if (existingUser.username() == user.username())
                throw new DataAccessException("Error: already taken", 403);
        }
        users.add(user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        for(UserData existingUser: users)
            if(existingUser.username() == username)
                return existingUser;
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public boolean verifyUser(String username, String password) throws DataAccessException
    {
        for(UserData existingUser: users)
        {
            if(existingUser.username() == username)
            {
                if(existingUser.password() == password)
                    return true;
                return false;
            }
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public Collection<UserData> listUsers() throws DataAccessException
    {
        return users;
    }

    @Override
    public int getSize() throws DataAccessException
    {
        return users.size();
    }
}
