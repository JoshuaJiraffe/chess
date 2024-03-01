package server;

import com.google.gson.Gson;
import dataAccess.MemAuthDataAccess;
import dataAccess.MemGameDataAccess;
import dataAccess.MemUserDataAccess;
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

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        MemGameDataAccess gameDAO = new MemGameDataAccess();
        MemUserDataAccess userDAO = new MemUserDataAccess();
        MemAuthDataAccess authDAO = new MemAuthDataAccess();
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> (new ClearHandler(clearService)).clear(req, res));
//        Spark.post("/user", (req, res) -> (new UserHandler(userService)).register(req, res));
//        Spark.post("/session", (req, res) -> (new UserHandler(userService)).login(req, res));
//        Spark.delete("/session", (req, res) -> (new UserHandler(userService)).logout(req, res));
//        Spark.get("/game", (req, res) -> (new GameHandler(gameService)).listGames(req, res));
//        Spark.post("/game", (req, res) -> (new GameHandler(gameService)).createGame(req, res));
//        Spark.put("/game", (req, res) -> (new GameHandler(gameService)).joinGame(req, res));

        Spark.awaitInitialization();
        return Spark.port();
    }
    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}