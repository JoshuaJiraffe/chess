package serviceTests;

import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTests
{
    ClearService clearService;
    MemGameDataAccess gameDAO;
    MemUserDataAccess userDAO;
    MemAuthDataAccess authDAO;
    @BeforeEach
    void setUp() throws DataAccessException
    {
        gameDAO = new MemGameDataAccess();
        authDAO = new MemAuthDataAccess();
        userDAO = new MemUserDataAccess();
        clearService = new ClearService(userDAO, gameDAO, authDAO);

    }

    @Test
    public void testClear() throws DataAccessException
    {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("Users", 0);
        expected.put("Games", 0);
        expected.put("Auths", 0);
        Map<String, Integer> actual = new HashMap<>();
        actual.put("Users", userDAO.getSize());
        actual.put("Games", gameDAO.getSize());
        actual.put("Auths", authDAO.getSize());

        clearService.clear();
        assertEquals(expected, actual);
    }
}