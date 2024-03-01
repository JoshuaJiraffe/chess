package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemAuthDataAccess;
import dataAccess.MemGameDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        UserData user = new UserData("honey", "buns", "asdf@gmail.com");
        AuthData token = authDAO.createAuth(user);
        GameData game = gameService.createGame(token.authToken(), "GothamChess");
        assertTrue(gameDAO.listGames().contains(game));
    }
    @Test
    void createGameFailSameName() throws DataAccessException
    {
        UserData user = new UserData("honey", "buns", "asdf@gmail.com");
        AuthData token = authDAO.createAuth(user);
        gameService.createGame(token.authToken(), "GothamChess");
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(token.authToken(), "GothamChess");
        });
    }
    @Test
    void createGameFailUnauthorized() throws DataAccessException
    {
        UserData user = new UserData("honey", "buns", "asdf@gmail.com");
        AuthData token = authDAO.createAuth(user);
        gameService.createGame(token.authToken(), "GothamChess");
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(token.authToken(), "GothamChess");
        });
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