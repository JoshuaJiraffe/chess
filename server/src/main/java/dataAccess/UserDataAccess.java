package dataAccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public interface UserDataAccess
{
    void clear() throws DataAccessException;
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean verifyUser(String username, String password) throws DataAccessException;
    int getSize() throws DataAccessException;
}
