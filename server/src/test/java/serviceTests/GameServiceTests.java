package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemAuthDataAccess;
import dataAccess.MemGameDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

class GameServiceTests
{
    GameService gameService;
    MemGameDataAccess gameDAO;
    MemAuthDataAccess authDAO;
    @BeforeEach
    void setUp() throws DataAccessException
    {
        gameDAO = new MemGameDataAccess();
        authDAO = new MemAuthDataAccess();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void listGamesSuccess() throws DataAccessException
    {
    }
    @Test
    void listGamesFail() throws DataAccessException
    {
    }

    @Test
    void createGameSuccess() throws DataAccessException
    {
    }
    @Test
    void createGameFail() throws DataAccessException
    {
    }

    @Test
    void joinGameSuccess() throws DataAccessException
    {
    }
    @Test
    void joinGameFail() throws DataAccessException
    {
    }
}