package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests
{
    GameService gameService;
    GameDataAccess gameDAO;
    AuthDataAccess authDAO;
    UserData user;
    AuthData token;
    @BeforeEach
    void setUp() throws DataAccessException
    {
        gameDAO = new MemGameDataAccess();
        authDAO = new MemAuthDataAccess();
        gameService = new GameService(gameDAO, authDAO);
        user = new UserData("honey", "buns", "asdf@gmail.com");
        token = authDAO.createAuth(user);
    }

    @Test
    void listGamesSuccess() throws DataAccessException
    {
        Set<GameData> expected = new HashSet<>();
        expected.add(gameService.createGame(token.authToken(), "GothamChess"));
        expected.add(gameService.createGame(token.authToken(), "GothamChess2"));
        expected.add(gameService.createGame(token.authToken(), "GothamChess3"));
        expected.add(gameService.createGame(token.authToken(), "GothamChess4"));
        assertEquals(expected, gameService.listGames(token.authToken()));
    }
    @Test
    void listGamesFailUnAuthorized() throws DataAccessException
    {
        Set<GameData> expected = new HashSet<>();
        expected.add(gameService.createGame(token.authToken(), "GothamChess"));
        expected.add(gameService.createGame(token.authToken(), "GothamChess2"));
        expected.add(gameService.createGame(token.authToken(), "GothamChess3"));
        expected.add(gameService.createGame(token.authToken(), "GothamChess4"));
        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("letmeinnnnn");
        });

    }


    @Test
    void createGameSuccess() throws DataAccessException
    {
        GameData game = gameService.createGame(token.authToken(), "GothamChess");
        assertTrue(gameDAO.listGames().contains(game));
    }
    @Test
    void createGameFailSameName() throws DataAccessException
    {
        gameService.createGame(token.authToken(), "GothamChess");
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(token.authToken(), "GothamChess");
        });
    }
    @Test
    void createGameFailUnauthorized() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("imtotallyauthorized", "GothamChess");
        });
    }

    @Test
    void joinGameSuccess() throws DataAccessException
    {
        GameData game = gameService.createGame(token.authToken(), "GothamChess");
        game = gameService.joinGame(token.authToken(), null, game.gameID());
        game = gameService.joinGame(token.authToken(), ChessGame.TeamColor.WHITE, game.gameID());
        game = gameService.joinGame(token.authToken(), ChessGame.TeamColor.BLACK, game.gameID());
    }
    @Test
    void joinGameFailUnauthorized() throws DataAccessException
    {
        GameData game = gameService.createGame(token.authToken(), "GothamChess");
        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("trustme", null, game.gameID());
        });
    }
    @Test
    void joinGameFailBadColor() throws DataAccessException
    {
        GameData game = gameService.createGame(token.authToken(), "GothamChess");
        gameService.joinGame(token.authToken(), ChessGame.TeamColor.WHITE, game.gameID());
        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(token.authToken(), ChessGame.TeamColor.WHITE, game.gameID());
        });
    }

}