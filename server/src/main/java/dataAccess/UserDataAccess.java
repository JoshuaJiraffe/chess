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
    Collection<UserData> listUsers() throws DataAccessException;
    int getSize() throws DataAccessException;
}
