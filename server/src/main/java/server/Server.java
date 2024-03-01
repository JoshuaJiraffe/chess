package server;

import com.google.gson.Gson;
import dataAccess.*;
import service.*;
import spark.*;

import java.util.Map;

public class Server {
    public static void main(String[] args)
    {
        var port = 8080;
        if (args.length >= 1)
            port = Integer.parseInt(args[0]);
        new Server().run(port);
    }
    private final GameDataAccess gameDAO;
    private final UserDataAccess userDAO;
    private final AuthDataAccess authDAO;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;
    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    public Server()
    {
        gameDAO = new MemGameDataAccess();
        userDAO = new MemUserDataAccess();
        authDAO = new MemAuthDataAccess();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        clearHandler = new ClearHandler(clearService);
        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");




        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> (clearHandler).clear(req, res));
        Spark.post("/user", (req, res) -> (userHandler).register(req, res));
        Spark.post("/session", (req, res) -> (userHandler).login(req, res));
        Spark.delete("/session", (req, res) -> (userHandler).logout(req, res));
        Spark.get("/game", (req, res) -> (gameHandler).listGames(req, res));
        Spark.post("/game", (req, res) -> (gameHandler).createGame(req, res));
        Spark.put("/game", (req, res) -> (gameHandler).joinGame(req, res));

        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }
    private void exceptionHandler(DataAccessException e, Request req, Response res )
    {
        res.status(e.getStatusCode());
        res.body(new Gson().toJson(Map.of("message", e.getMessage())));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}