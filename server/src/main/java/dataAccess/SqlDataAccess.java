package dataAccess;

import chess.ChessGame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class SqlDataAccess
{
    public SqlDataAccess() throws DataAccessException
    {
        configureDatabase();
    }

    protected void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param)
                    {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default ->
                        {
                        }
                    }
                }
                ps.executeUpdate();
                return;

            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), 500);
        }
    }

//    protected ResultSet executeQuery(String statement, Object... params) throws DataAccessException
//    {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    switch (param)
//                    {
//                        case String p -> ps.setString(i + 1, p);
//                        case Integer p -> ps.setInt(i + 1, p);
//                        case ChessGame p -> ps.setString(i + 1, p.toString());
//                        case null -> ps.setNull(i + 1, NULL);
//                        default ->
//                        {
//                        }
//                    }
//                }
//                return ps.executeQuery();
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()), 500);
//        }
//    }

//    protected int getSize(String database) throws DataAccessException
//    {
//        int size = 0;
//        var statement = "SELECT COUNT(*) FROM ?";
//        try(var conn = DatabaseManager.getConnection())
//        {
//            try (var ps = conn.prepareStatement(statement))
//            {
//                ps.setString(1, database);
//                try (var rs = ps.executeQuery())
//                {
//                    if (rs.next())
//                        size = rs.getInt(1);
//                }
//            }
//        }catch (Exception e) {
//        throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
//    }
//        return size;
//    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`)
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`authToken`)
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `jsonGame` TEXT NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };
    private void configureDatabase() throws DataAccessException
    {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), 500);
        }
    }
}
