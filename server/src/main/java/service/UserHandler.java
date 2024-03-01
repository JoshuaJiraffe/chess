package service;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;

public class UserHandler
{
    private final UserService userService;
    private Gson gson;

    public UserHandler(UserService userService)
    {
        this.userService = userService;
        gson = new Gson();
    }

    public Object register(Request req, Response res) throws DataAccessException
    {
        UserData user = gson.fromJson(req.body(), UserData.class);
        AuthData authToken = userService.register(user);
        res.status(200);
        return gson.toJson(authToken);

    }

    public Object login(Request req, Response res) throws DataAccessException
    {
        UserData user = gson.fromJson(req.body(), UserData.class);
        AuthData authToken = userService.login(user.username(), user.password());
        res.status(200);
        return gson.toJson(authToken);
    }

    public Object logout(Request req, Response res) throws DataAccessException
    {
        String auth = req.headers("authorization");
        userService.logout(auth);
        res.status(200);
        return "{}";
    }
}
