package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.rmi.ServerException;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerSuccess() throws ServerException
    {

    }

    @Test
    public void registerFail() throws ServerException
    {

    }

    @Test
    public void loginSuccess() throws ServerException
    {

    }
    @Test
    public void loginFail() throws ServerException
    {

    }

    @Test
    public void logoutSuccess() throws ServerException
    {

    }
    @Test
    public void logoutFail() throws ServerException
    {

    }

    @Test
    public void listGamesSuccess() throws ServerException
    {

    }
    @Test
    public void listGamesFail() throws ServerException
    {

    }

    @Test
    public void createGameSuccess() throws ServerException
    {

    }
    @Test
    public void createGameFail() throws ServerException
    {

    }

    @Test
    public void joinGameSuccess() throws ServerException
    {

    }
    @Test
    public void joinGameFail() throws ServerException
    {

    }

}
