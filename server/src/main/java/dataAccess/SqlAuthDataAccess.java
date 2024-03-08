package dataAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDataAccess extends SqlDataAccess implements AuthDataAccess
{
    public SqlAuthDataAccess() throws DataAccessException
    {
        super();
    }

    @Override
    public void clear() throws DataAccessException
    {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth(UserData user) throws DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        var json = new Gson().toJson(auth);
        var statement = "INSERT INTO auth (authToken, username, json) VALUES (?, ? ?)";
        executeUpdate(statement, authToken, user.username(), json);

        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        try(var conn = DatabaseManager.getConnection())
        {
            var statement = "SELECT json FROM auth WHERE authToken=?";
            try(var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, authToken);
                try(var rs = ps.executeQuery())
                {
                    if(rs.next())
                    {
                        String json = rs.getString(1);
                        return new Gson().fromJson(json, AuthData.class);
                    }
                    else
                        throw new DataAccessException("Error: unauthorized", 401);
                }
            }
        } catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }

    @Override
    public boolean deleteAuth(String authToken) throws DataAccessException
    {
        var statement = "DELETE FROM auth WHERE authToken=?";
        int rowsAffected = executeUpdate(statement, authToken);
        if(rowsAffected == 0)
            throw new DataAccessException("Error: unauthorized", 401);
        return true;
    }

    @Override
    public int getSize() throws DataAccessException
    {
        var statement = "SELECT COUNT(*) FROM auth";
        return executeUpdate(statement);
    }
}
