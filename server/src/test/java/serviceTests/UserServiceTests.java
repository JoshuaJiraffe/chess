package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemAuthDataAccess;
import dataAccess.MemGameDataAccess;
import dataAccess.MemUserDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

class UserServiceTests
{
    UserService userService;
    MemUserDataAccess userDAO;
    MemAuthDataAccess authDAO;
    @BeforeEach
    public void setUp() throws DataAccessException
    {
        userDAO = new MemUserDataAccess();
        authDAO = new MemAuthDataAccess();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() throws DataAccessException
    {
        UserData user = new UserData("name", "pass", "abdf@gmail.com");
        AuthData token = userService.register(user);
//        UserData actual = userService.

    }
    @Test
    void registerFail() throws DataAccessException
    {
    }

    @Test
    void loginSuccess() throws DataAccessException
    {
    }
    @Test
    void loginFail() throws DataAccessException
    {
    }

    @Test
    void logoutSuccess() throws DataAccessException
    {
    }

    @Test
    void logoutFail() throws DataAccessException
    {
    }
}