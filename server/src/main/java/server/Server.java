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

    GameDataAccess gameDAO = new MemGameDataAccess();
    UserDataAccess userDAO = new MemUserDataAccess();
    AuthDataAccess authDAO = new MemAuthDataAccess();
    ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    GameService gameService = new GameService(gameDAO, authDAO);
    UserService userService = new UserService(userDAO, authDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");


        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> (new ClearHandler(clearService)).clear(req, res));
        Spark.post("/user", (req, res) -> (new UserHandler(userService)).register(req, res));
        Spark.post("/session", (req, res) -> (new UserHandler(userService)).login(req, res));
        Spark.delete("/session", (req, res) -> (new UserHandler(userService)).logout(req, res));
        Spark.get("/game", (req, res) -> (new GameHandler(gameService)).listGames(req, res));
        Spark.post("/game", (req, res) -> (new GameHandler(gameService)).createGame(req, res));
        Spark.put("/game", (req, res) -> (new GameHandler(gameService)).joinGame(req, res));

        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }
    private void exceptionHandler(DataAccessException e, Request req, Response res )
    {
        res.status(e.getStatusCode());
        res.body(new Gson().toJson(e.getMessage()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}