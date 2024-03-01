package service;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import spark.Request;
import spark.Response;

public class ClearHandler
{
    private final ClearService clearService;
    private Gson gson;

    public ClearHandler(ClearService clearService)
    {
        this.clearService = clearService;
        gson = new Gson();
    }

    public String clear(Request req, Response res) throws DataAccessException
    {
        clearService.clear();
        res.status(200);
        return gson.toJson("{}");
    }
}
