package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemAuthDataAccess;
import dataAccess.MemUserDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests
{
    UserService userService;
    MemUserDataAccess userDAO;
    MemAuthDataAccess authDAO;
    UserData user1;
    AuthData token;
    @BeforeEach
    public void setUp() throws DataAccessException
    {
        userDAO = new MemUserDataAccess();
        authDAO = new MemAuthDataAccess();
        userService = new UserService(userDAO, authDAO);
        user1 = new UserData("name", "pass", "abdf@gmail.com");
        token = userService.register(user1);
    }

    @Test
    void registerSuccess() throws DataAccessException
    {
        assertEquals(token, authDAO.getAuth(token.authToken()));
        assertTrue(userDAO.verifyUser("name", "pass"));
    }
    @Test
    void registerFailSameEmail() throws DataAccessException
    {
        UserData user2 = new UserData("name2", "pass2", "abdf@gmail.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(user2);
        });
    }
    @Test
    void registerFailSameUser() throws DataAccessException
    {
        UserData user2 = new UserData("name", "pass2", "abdef@gmail.com");
        assertThrows(DataAccessException.class, () -> {
            userService.register(user2);
        });
    }

    @Test
    void loginSuccess() throws DataAccessException
    {
        AuthData realToken = userService.login("name", "pass");
        AuthData actual = authDAO.getAuth(realToken.authToken());
        assertEquals(realToken, actual);
    }
    @Test
    void loginFailWrongPass() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            userService.login("name", "password");
        });
    }
    @Test
    void loginFailWrongUser() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            userService.login("username", "password");
        });
    }

    @Test
    void logoutSuccess() throws DataAccessException
    {
        AuthData realToken = userService.login("name", "pass");
        userService.logout(realToken.authToken());
        assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth(realToken.authToken());
        });
    }

    @Test
    void logoutFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            userService.logout("thisissuchafakeauthtoken");
        });
    }
}