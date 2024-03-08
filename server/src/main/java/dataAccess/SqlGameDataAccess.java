package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public class SqlGameDataAccess extends SqlDataAccess implements GameDataAccess
{
    public SqlGameDataAccess() throws DataAccessException
    {
        super();
    }
    @Override
    public void clear() throws DataAccessException
    {

    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException
    {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException
    {
        return null;
    }

    @Override
    public GameData joinGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException
    {
        return null;
    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException
    {
        return false;
    }

    @Override
    public int getSize() throws DataAccessException
    {
        return 0;
    }
}
