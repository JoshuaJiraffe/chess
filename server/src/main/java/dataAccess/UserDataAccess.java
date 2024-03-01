package dataAccess;

import model.UserData;

public interface UserDataAccess
{
    void clear() throws DataAccessException;
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean verifyUser(String username, String password) throws DataAccessException;
    int getSize() throws DataAccessException;
}
