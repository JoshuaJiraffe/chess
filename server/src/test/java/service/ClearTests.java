package service;

import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClearTests
{
    ClearService clearService;
    @BeforeEach
    public void setUp() throws DataAccessException
    {
        clearService = new ClearService(new MemUserDataAccess(), new MemGameDataAccess(), new MemAuthDataAccess());

    }

    @Test
    public void testClear() throws DataAccessException
    {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("Users", 0);
        expected.put("Games", 0);
        expected.put("Auths", 0);
        clearService.clear();
        assertEquals(expected, clearService.getDataSizes());
    }
}