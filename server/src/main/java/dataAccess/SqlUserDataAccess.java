package dataAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;

public class SqlUserDataAccess extends SqlDataAccess implements UserDataAccess
{
    public SqlUserDataAccess() throws DataAccessException
    {
        super();
    }
    @Override
    public void clear() throws DataAccessException
    {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashPass = encoder.encode(user.password());
        if(userExists(user))
            throw new DataAccessException("Error: already taken", 403);
        var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(new UserData(user.username(), hashPass, user.email()));
        executeUpdate(statement, user.username(), hashPass, user.email(), json);

        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        try(var conn = DatabaseManager.getConnection())
        {
            var statement = "SELECT json FROM auth WHERE username=?";
            try(var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, username);
                try(var rs = ps.executeQuery())
                {
                    if(rs.next())
                    {
                        String json = rs.getString(1);
                        return new Gson().fromJson(json, UserData.class);
                    }
                    else
                        throw new DataAccessException("Error: bad request", 400);
                }
            }
        } catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage(), 500);
        }
    }

    @Override
    public boolean verifyUser(String username, String password) throws DataAccessException
    {
        try(var conn = DatabaseManager.getConnection())
        {
            var statement = "SELECT json FROM user WHERE username=?";
            try(var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, username);
                try(var rs = ps.executeQuery())
                {
                    if(rs.next())
                    {
                        String json = rs.getString(1);
                        UserData user = new Gson().fromJson(json, UserData.class);
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        return encoder.matches(password, user.password());
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

    public boolean userExists(UserData user) throws DataAccessException
    {
        try(var conn = DatabaseManager.getConnection())
        {
            var statement = "SELECT COUNT(*) FROM user WHERE username = ? OR email = ?";
            try(var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, user.username());
                ps.setString(2, user.email());
                try(var rs = ps.executeQuery())
                {
                        rs.next();
                        int count = rs.getInt(1);
                        return (count > 0);
                }
            }
        } catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage(), 500);
        }

    }

    @Override
    public int getSize() throws DataAccessException
    {
        int size = 0;
        var statement = "SELECT COUNT(*) FROM user";
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
