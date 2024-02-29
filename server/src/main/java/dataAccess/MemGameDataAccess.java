package dataAccess;

import model.GameData;

import java.util.Collection;

public class MemGameDataAccess implements GameDataAccess
{
    @Override
    public void clear() throws DataAccessException
    {

    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException
    {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException
    {
        return null;
    }

    @Override
    public boolean updateGame(int gameID, GameData game) throws DataAccessException
    {
        return false;
    }

    @Override
    public boolean joinGame(String username, String playerColor, int gameID) throws DataAccessException
    {
        return false;
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException
    {
        return false;
    }
}
