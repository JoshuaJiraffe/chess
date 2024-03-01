package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameData;
import spark.Request;
import spark.Response;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GameHandler
{
    private final GameService gameService;
    private Gson gson;

    public GameHandler(GameService gameService)
    {
        this.gameService = gameService;
        gson = new Gson();
    }

    public Object listGames(Request req, Response res) throws DataAccessException
    {
        String auth = req.headers("authorization");
        Collection<GameData> games = gameService.listGames(auth);
        res.status(200);
        return gson.toJson(games);
    }

    public Object createGame(Request req, Response res) throws DataAccessException
    {
        String auth = req.headers("authorization");
        GameData gameData = gson.fromJson(req.body(), GameData.class);
        GameData game = gameService.createGame(auth, gameData.gameName());
        res.status(200);
        return gson.toJson(Map.of("gameID", game.gameID()));
    }

    public Object joinGame(Request req, Response res) throws DataAccessException
    {
        String auth = req.headers("authorization");
        Map<String, Object> body = gson.fromJson(req.body(), Map.class);
        int gameID = (int)body.get("gameID");
        String colour = (String)body.get("playerColor");
        ChessGame.TeamColor color;
        if(colour.equals("WHITE"))
            color = ChessGame.TeamColor.WHITE;
        else
            color = ChessGame.TeamColor.BLACK;
        gameService.joinGame(auth, color, gameID);
        res.status(200);
        return "{}";
    }

}
