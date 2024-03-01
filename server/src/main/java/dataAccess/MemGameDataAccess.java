package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MemGameDataAccess implements GameDataAccess
{
    private Set<GameData> games = new HashSet<>();
    @Override
    public void clear() throws DataAccessException
    {
        games.clear();
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

    @Override
    public int getSize() throws DataAccessException
    {
        return games.size();
    }
}
