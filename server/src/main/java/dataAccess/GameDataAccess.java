package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDataAccess
{
    void clear() throws DataAccessException;
    GameData createGame(String gameName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData joinGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;
    boolean deleteGame(int gameID) throws DataAccessException;
    int getSize() throws DataAccessException;
    public GameData updateGame(int gameID, ChessGame updatedGame) throws DataAccessException;
}
