package service;

import chess.ChessGame;
import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.GameDataAccess;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService
{
    private final GameDataAccess gameAccess;
    private final AuthDataAccess authAccess;

    public GameService(GameDataAccess gameAccess, AuthDataAccess authAccess)
    {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException
    {
        AuthData auth = authAccess.getAuth(authToken);
        return gameAccess.listGames();
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException
    {
        AuthData auth = authAccess.getAuth(authToken);
        return gameAccess.createGame(gameName);
    }

    public GameData joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException
    {
        AuthData auth = authAccess.getAuth(authToken);
        return gameAccess.joinGame(auth.username(), playerColor, gameID);
    }

    public GameData updateGame(int gameID, ChessGame updatedGame, ChessGame.TeamColor colorGone) throws DataAccessException
    {
        return gameAccess.updateGame(gameID, updatedGame, colorGone);
    }

    public GameData getGame(int gameID) throws DataAccessException
    {
        return gameAccess.getGame(gameID);
    }

    public AuthData getAuth(String authToken) throws DataAccessException
    {
        return authAccess.getAuth(authToken);
    }
}
