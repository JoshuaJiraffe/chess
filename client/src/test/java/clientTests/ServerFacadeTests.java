package clientTests;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    AuthData auth;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void prep() throws ServerException
    {
        facade.clear();
        auth = facade.register(new UserData("player1", "password", "p1@email.com"));
    }

    @AfterAll
    static void stopServer() throws ServerException
    {
        facade.clear();
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void registerSuccess() throws ServerException
    {
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    public void registerFail() throws ServerException
    {
        assertThrows(ServerException.class, () -> {
            facade.register(new UserData("player1", "newpass", "real@email.com"));
        });
        assertThrows(ServerException.class, () -> {
            facade.register(new UserData("player2", "newpass", "p1@email.com"));
        });
    }

    @Test
    public void loginSuccess() throws ServerException
    {
        AuthData auth2 = facade.login("player1", "password");
        assertTrue(auth2.authToken().length() > 10);
        assertEquals(auth.username(), auth2.username());
    }
    @Test
    public void loginFail() throws ServerException
    {
        assertThrows(ServerException.class, () -> {
            facade.login("player2", "pass");
        });
        assertThrows(ServerException.class, () -> {
            AuthData auth2 = facade.login("player1", "wrongword");
        });
    }

    @Test
    public void logoutSuccess() throws ServerException
    {
        facade.logout(auth.authToken());
        assertThrows(ServerException.class, () -> {
            facade.logout(auth.authToken());
        });
    }
    @Test
    public void logoutFail() throws ServerException
    {
        assertThrows(ServerException.class, () -> {
            facade.logout("thisisveryrealtoken");
        });
    }

    @Test
    public void createGameSuccess() throws ServerException
    {
        int id = facade.createGame(auth.authToken(), "Gotham");
        assertTrue(id != 0);
    }
    @Test
    public void createGameFail() throws ServerException
    {
        facade.createGame(auth.authToken(), "Gotham");
        assertThrows(ServerException.class, () -> {
            facade.createGame(auth.authToken(),"Gotham");
        });
    }

    @Test
    public void listGamesSuccess() throws ServerException
    {
        int [] expected = new int[5];
        expected[0] = (facade.createGame(auth.authToken(), "Gotham"));
        expected[1] = (facade.createGame(auth.authToken(), "Gotham2"));
        expected[2] = (facade.createGame(auth.authToken(), "Gotham3"));
        expected[3] = (facade.createGame(auth.authToken(), "Gotham4"));
        expected[4] = (facade.createGame(auth.authToken(), "Gotham5"));
        ArrayList<GameData> actual = (ArrayList<GameData>) facade.listGames(auth.authToken());
        assertEquals(expected.length, actual.size());
        HashSet<Integer> ids = new HashSet<>();
        for(GameData game: actual)
            ids.add(game.gameID());
        for(int id: expected)
            assertTrue(ids.contains(id));

    }

    @Test
    public void listGamesFail() throws ServerException
    {
        assertThrows(ServerException.class, () -> {
            facade.createGame("veryrealauth","Gotham");
        });
    }


    @Test
    public void joinGameSuccess() throws ServerException
    {
        int id = facade.createGame(auth.authToken(), "Gotham");
        facade.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, id);
        facade.joinGame(auth.authToken(), ChessGame.TeamColor.BLACK, id);
        facade.joinGame(auth.authToken(), null, id);
        ArrayList<GameData> games = (ArrayList<GameData>) facade.listGames(auth.authToken());
        assertTrue(games.contains(new GameData(id, "player1", "player1", "Gotham", new ChessGame())));
    }
    @Test
    public void joinGameFail() throws ServerException
    {
        int id = facade.createGame(auth.authToken(), "Gotham");
        facade.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, id);
        assertThrows(ServerException.class, () -> {
            facade.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, id);
        });
        assertThrows(ServerException.class, () -> {
            facade.joinGame("letmeinnn", ChessGame.TeamColor.BLACK, id);
        });
        assertThrows(ServerException.class, () -> {
            facade.joinGame(auth.authToken(), ChessGame.TeamColor.BLACK, UUID.randomUUID().hashCode());
        });
    }

}
