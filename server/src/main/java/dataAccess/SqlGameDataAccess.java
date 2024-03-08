package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class SqlGameDataAccess extends SqlDataAccess implements GameDataAccess
{
    private int nextID = 101;
    public SqlGameDataAccess() throws DataAccessException
    {
        super();
    }
    @Override
    public void clear() throws DataAccessException
    {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException
    {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM game WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if(rs.getInt(1) > 0)
                            throw new DataAccessException("Error: bad request", 400);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

        GameData game = new GameData(nextID, null, null, gameName, new ChessGame());
        nextID += ((int)(Math.random()*9) + 1);
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, jsonGame, json) VALUES (?, ?, ?, ?, ?, ?)";
        var json = new Gson().toJson(game);
        var jsonGame = new Gson().toJson(game.game());
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), jsonGame, json);

        return game;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException
    {
        Collection<GameData> games = new HashSet<>();
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT json FROM game";
            try(var ps = conn.prepareStatement(statement)){
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("json");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        games.add(game);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
        return games;
    }

    @Override
    public GameData joinGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException
    {
        var updateStatement = "UPDATE game SET blackUsername = ?, whiteUsername = ?";
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT json, whiteUsername, blackUsername FROM game WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try(var rs = ps.executeQuery())
                {
                    if(rs.next())
                    {
                        String json = rs.getString("json");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        if(playerColor == ChessGame.TeamColor.WHITE)
                        {
                            if(whiteUsername == null)
                            {
                                whiteUsername = username;
                            }
                            else
                                throw new DataAccessException("Error: already taken", 403);
                        }
                        else if(playerColor == ChessGame.TeamColor.BLACK)
                        {
                            if(blackUsername == null)
                            {
                                blackUsername = username;
                            }
                            else
                                throw new DataAccessException("Error: already taken", 403);
                        }
                        executeUpdate(updateStatement, whiteUsername, blackUsername);
                        return new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
                    }
                    else
                    {
                        throw new DataAccessException("Error: bad request", 400);
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException
    {
        var statement = "DELETE FROM game WHERE id=?";
        int rowsAffected = executeUpdate(statement, gameID);
        if(rowsAffected == 0)
            throw new DataAccessException("Error: bad request", 400);
        return true;
    }

    @Override
    public int getSize() throws DataAccessException
    {
        int size = 0;
        var statement = "SELECT COUNT(*) FROM game";
        try(var conn = DatabaseManager.getConnection())
        {
            try (var ps = conn.prepareStatement(statement))
            {
                try (var rs = ps.executeQuery())
                {
                    if (rs.next())
                        size = rs.getInt(1);
                }
            }
        }catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
        return size;
    }
}
