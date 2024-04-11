package dataAccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MemGameDataAccess implements GameDataAccess
{
    private int nextID = 101;
    private Set<GameData> games = new HashSet<>();
    @Override
    public void clear() throws DataAccessException
    {
        games.clear();
        getSize();
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException
    {
        for (GameData existingGame: games)
        {
            if(existingGame.gameName().equals(gameName))
                throw new DataAccessException("Error: bad request", 400);
        }
        GameData game = new GameData(nextID, null, null, gameName, new ChessGame());
        nextID += ((int)(Math.random()*9) + 1);
        games.add(game);
        return game;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException
    {
        return games;
    }

    @Override
    public GameData joinGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException
    {
        for (GameData existingGame: games)
        {
            if(existingGame.gameID() == gameID)
            {
                if(playerColor == null)
                    return existingGame;
                else
                {
                    GameData newData = null;
                    if(playerColor == ChessGame.TeamColor.WHITE)
                    {
                        if(existingGame.whiteUsername() == null)
                        {
                            newData = new GameData(existingGame.gameID(), username, existingGame.blackUsername(), existingGame.gameName(), existingGame.game());
                        }
                        else
                            throw new DataAccessException("Error: already taken", 403);
                    }
                    else if(playerColor == ChessGame.TeamColor.BLACK)
                    {
                        if(existingGame.blackUsername() == null)
                        {
                            newData = new GameData(existingGame.gameID(), existingGame.whiteUsername(), username, existingGame.gameName(), existingGame.game());
                        }
                        else
                            throw new DataAccessException("Error: already taken", 403);
                    }
                    if(newData != null)
                    {
                        deleteGame(existingGame.gameID());
                        games.add(newData);
                        return newData;
                    }
                    else
                        throw new DataAccessException("Error: Something really wonky happened", 500);
                }

            }
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException
    {
        for (GameData existingGame: games)
        {
            if(existingGame.gameID() == gameID)
            {
                GameData deadGame = existingGame;
                return games.remove(deadGame);
            }
        }
        throw new DataAccessException("Error: bad request", 400);
    }

    @Override
    public int getSize() throws DataAccessException
    {
        return games.size();
    }

    @Override
    public GameData updateGame(int gameID, ChessGame updatedGame) throws DataAccessException
    {
        return null;
    }
}
