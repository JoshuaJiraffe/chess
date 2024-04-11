package server;

import com.google.gson.Gson;
import dataAccess.*;
import server.websocket.WebSocketHandler;
import service.*;
import spark.*;

import java.util.Map;

public class Server {
//    public static void main(String[] args)
//    {
//        var port = 8080;
//        if (args.length >= 1)
//            port = Integer.parseInt(args[0]);
//        new Server().run(port);
//    }

    public Server()
    {

    }

    public Server run(int desiredPort){
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        try
        {
            GameDataAccess gameDAO = new SqlGameDataAccess();
            UserDataAccess userDAO = new SqlUserDataAccess();
            AuthDataAccess authDAO = new SqlAuthDataAccess();
//            GameDataAccess gameDAO = new MemGameDataAccess();
//            UserDataAccess userDAO = new MemUserDataAccess();
//            AuthDataAccess authDAO = new MemAuthDataAccess();
            ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
            GameService gameService = new GameService(gameDAO, authDAO);
            UserService userService = new UserService(userDAO, authDAO);
            ClearHandler clearHandler = new ClearHandler(clearService);
            UserHandler userHandler = new UserHandler(userService);
            GameHandler gameHandler = new GameHandler(gameService);
            WebSocketHandler webSocketHandler = new WebSocketHandler(gameService);

            Spark.webSocket("/connect", webSocketHandler);

            // Register your endpoints and handle exceptions here.
            Spark.delete("/db", (req, res) -> (clearHandler).clear(req, res));
            Spark.post("/user", (req, res) -> (userHandler).register(req, res));
            Spark.post("/session", (req, res) -> (userHandler).login(req, res));
            Spark.delete("/session", (req, res) -> (userHandler).logout(req, res));
            Spark.get("/game", (req, res) -> (gameHandler).listGames(req, res));
            Spark.post("/game", (req, res) -> (gameHandler).createGame(req, res));
            Spark.put("/game", (req, res) -> (gameHandler).joinGame(req, res));
        }
        catch (DataAccessException e)
        {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }


        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return this;
    }
    private void exceptionHandler(DataAccessException e, Request req, Response res )
    {
        res.status(e.getStatusCode());
        res.body(new Gson().toJson(Map.of("message", e.getMessage())));
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}