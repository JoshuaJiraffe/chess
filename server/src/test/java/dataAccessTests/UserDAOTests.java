package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SqlUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests
{
    SqlUserDataAccess userDAO;
    UserData user1;
    UserData user1data;

    @BeforeEach
    void setUp() throws DataAccessException
    {
        userDAO = new SqlUserDataAccess();
        userDAO.clear();
        user1 = new UserData("name", "pass", "abdf@gmail.com");
        user1data = userDAO.createUser(user1);
    }
    @Test
    public void testClear() throws DataAccessException
    {
        userDAO.clear();
        assertEquals(0, userDAO.getSize());
    }
    @Test
    public void createUserSuccess() throws DataAccessException
    {
        assertEquals(user1, user1data);
    }
    @Test
    public void createUserFailSameCreds() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData("name", "who", "cares"));
        });
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData("username", "who", "abdf@gmail.com"));
        });
    }
    @Test
    public void getUserSuccess() throws DataAccessException
    {
        UserData actual = userDAO.getUser("name");
        assertEquals("name", actual.username());
        assertEquals("abdf@gmail.com", actual.email());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("pass", actual.password()));
    }
    @Test
    public void getUserFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("Idontexist");
        });
    }
    @Test
    public void verifyUserSuccess() throws DataAccessException
    {
        assertTrue(userDAO.verifyUser("name", "pass"));

    }
    @Test
    public void verifyUserFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            userDAO.verifyUser("Stillgone", "withthewind");
        });
        assertFalse(userDAO.verifyUser("name", "incorrect"));
    }
    @Test
    public void userExistsSuccess() throws DataAccessException
    {
        assertTrue(userDAO.userExists(new UserData("username", "fake", "abdf@gmail.com")));
        assertTrue(userDAO.userExists(new UserData("name", "fake", "bad")));
    }
    @Test
    public void userExistsFail() throws DataAccessException
    {
        assertFalse(userDAO.userExists(new UserData("fake", "news", "here")));
    }

}
