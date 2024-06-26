package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class SqlGameDataAccess extends SqlDataAccess implements GameDataAccess
{
    private int nextID = UUID.randomUUID().hashCode();
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
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

        GameData game = new GameData(nextID, null, null, gameName, new ChessGame());
//        nextID += ((int)(Math.random()*9) + 1);
        nextID = UUID.randomUUID().hashCode();
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
        var updateStatement = "UPDATE game SET whiteUsername=?, blackUsername=?, json=? WHERE gameID=?";
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
                        GameData newGame = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
                        executeUpdate(updateStatement, whiteUsername, blackUsername, new Gson().toJson(newGame), gameID);
                        return newGame;
                    }
                    else
                    {
                        throw new DataAccessException("Error: bad request", 400);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }

    }

    @Override
    public boolean deleteGame(int gameID) throws DataAccessException
    {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "DELETE FROM game WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                int rowsAffected = ps.executeUpdate();
                if(rowsAffected == 0)
                    throw new DataAccessException("Error: bad request", 400);
                return true;
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException
    {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT json, jsonGame, whiteUsername, blackUsername FROM game WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try(var rs = ps.executeQuery())
                {
                    if(rs.next())
                    {
                        String json = rs.getString("json");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        return game;
                    }
                    else
                    {
                        throw new DataAccessException("Error: bad request", 400);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
    }


    @Override
    public GameData updateGame(int gameID, ChessGame updatedGame, ChessGame.TeamColor colorGone) throws DataAccessException
    {
        var updateStatement = "UPDATE game SET jsonGame=?, json=?, whiteUsername=?, blackUsername=? WHERE gameID=?";
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
                        String whiteUsername = game.whiteUsername();
                        String blackUsername = game.blackUsername();
                        if(colorGone == ChessGame.TeamColor.WHITE)
                            whiteUsername = null;
                        if(colorGone == ChessGame.TeamColor.BLACK)
                            blackUsername = null;
                        GameData newGame = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), updatedGame);

                        executeUpdate(updateStatement, new Gson().toJson(updatedGame), new Gson().toJson(newGame), whiteUsername, blackUsername, gameID);
                        return newGame;
                    }
                    else
                    {
                        throw new DataAccessException("Error: bad request", 400);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
    }


    @Override
    public int getSize() throws DataAccessException
    {
        int size = 0;
        var statement = "SELECT COUNT(*) FROM game";
        return SqlDataAccess.getSize(size, statement);
    }


}
