package dataAccess;

import model.UserData;

public class SqlUserDataAccess extends SqlDataAccess implements UserDataAccess
{
    public SqlUserDataAccess() throws DataAccessException
    {
        super();
    }
    @Override
    public void clear() throws DataAccessException
    {

    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException
    {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        return null;
    }

    @Override
    public boolean verifyUser(String username, String password) throws DataAccessException
    {
        return false;
    }

    @Override
    public int getSize() throws DataAccessException
    {
        return 0;
    }
}
