package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDataAccess
{
    void clear() throws DataAccessException;
    GameData createGame(GameData game) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    boolean updateGame(int gameID, GameData game) throws DataAccessException;
    boolean joinGame(String username, String playerColor, int gameID) throws DataAccessException;
    boolean deleteGame(int gameID) throws DataAccessException;
}
