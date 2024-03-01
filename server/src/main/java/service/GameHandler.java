package service;

import com.google.gson.Gson;

public class GameHandler
{
    private final GameService gameService;
    private Gson gson;

    public GameHandler(GameService gameService)
    {
        this.gameService = gameService;
        gson = new Gson();
    }

}
