package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SqlAuthDataAccess;
import dataAccess.SqlUserDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthDAOTests
{
    SqlAuthDataAccess authDAO;
    UserData user1;
    AuthData token;
    @BeforeEach
    void setUp() throws DataAccessException
    {
        authDAO = new SqlAuthDataAccess();
        authDAO.clear();
        user1 = new UserData("name", "pass", "abdf@gmail.com");
        token = authDAO.createAuth(user1);
    }

    @Test
    public void testClear() throws DataAccessException
    {
        authDAO.clear();
        assertEquals(0, authDAO.getSize());

    }
    @Test
    public void createAuthSuccess() throws DataAccessException
    {
        assertEquals(token, authDAO.getAuth(token.authToken()));
        assertEquals("name", authDAO.getAuth(token.authToken()).username());
    }
    @Test
    public void createAuthFail() throws DataAccessException
    {
        assertThrows(Exception.class, () -> {
            authDAO.createAuth(null);
        });
    }
    @Test
    public void getAuthSuccess() throws DataAccessException
    {
        assertEquals(token, authDAO.getAuth((token.authToken())));
    }
    @Test
    public void getAuthFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth("flibertigibbitles");
        });
    }
    @Test
    public void deleteAuthSuccess() throws DataAccessException
    {
        authDAO.deleteAuth(token.authToken());
        assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(token.authToken());
        });
    }
    @Test
    public void deleteAuthFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("awoooooggggaaaa");
        });
    }

}
