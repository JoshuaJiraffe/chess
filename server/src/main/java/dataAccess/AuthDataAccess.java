package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface AuthDataAccess
{
    void clear() throws DataAccessException;
    AuthData createAuth(UserData user) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    boolean deleteAuth(String authToken) throws DataAccessException;
    int getSize() throws DataAccessException;

}
