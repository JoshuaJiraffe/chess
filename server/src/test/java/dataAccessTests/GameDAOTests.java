package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.SqlGameDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests
{
    SqlGameDataAccess gameDAO;
    GameData game1;
    @BeforeEach
    void setUp() throws DataAccessException
    {
        gameDAO = new SqlGameDataAccess();
        gameDAO.clear();
        game1 = gameDAO.createGame("GothamChess");
    }

    @Test
    public void testClear() throws DataAccessException
    {
        gameDAO.clear();
        assertEquals(0, gameDAO.getSize());
    }
    @Test
    public void createGameSuccess() throws DataAccessException
    {
        assertEquals("GothamChess", game1.gameName());
    }
    @Test
    public void createGameFail() throws DataAccessException
    {
        assertThrows(Exception.class, () -> {
            gameDAO.createGame(null);
        });
    }
    @Test
    public void listGamesSuccess() throws DataAccessException
    {
        gameDAO.clear();
        Set<GameData> expected = new HashSet<>();
        expected.add(gameDAO.createGame("GothamChess"));
        expected.add(gameDAO.createGame("GothamChess2"));
        expected.add(gameDAO.createGame("GothamChess3"));
        expected.add(gameDAO.createGame("GothamChess4"));
        expected.add(gameDAO.createGame("GothamChess5"));
        assertEquals(expected, gameDAO.listGames());
    }
    @Test
    public void listGamesFail() throws DataAccessException
    {
        gameDAO.clear();
        Set<GameData> expected = new HashSet<>();
        expected.add(gameDAO.createGame("GothamChess"));
        expected.add(gameDAO.createGame("GothamChess2"));
        expected.add(gameDAO.createGame("GothamChess3"));
        expected.add(gameDAO.createGame("GothamChess4"));
        expected.add(gameDAO.createGame("GothamChess5"));
        expected.add(new GameData(1, null, null, "hello", new ChessGame()));
        assertNotEquals(expected, gameDAO.listGames());
    }
    @Test
    public void joinGameSuccess() throws DataAccessException
    {
        GameData expected = new GameData(game1.gameID(), "me", null, game1.gameName(), game1.game());
        assertEquals(expected, gameDAO.joinGame("me", ChessGame.TeamColor.WHITE, game1.gameID()));
    }
    @Test
    public void joinGameFail() throws DataAccessException
    {
        gameDAO.joinGame("me", ChessGame.TeamColor.WHITE, game1.gameID());
        assertThrows(DataAccessException.class, () -> {
            gameDAO.joinGame("meagain", ChessGame.TeamColor.WHITE, game1.gameID());
        });

    }
    @Test
    public void deleteGameSuccess() throws DataAccessException
    {
        assertTrue(gameDAO.deleteGame(game1.gameID()));
    }
    @Test
    public void deleteGameFail() throws DataAccessException
    {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.deleteGame(1);
        });
    }

}
