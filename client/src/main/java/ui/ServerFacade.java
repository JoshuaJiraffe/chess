package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.rmi.ServerException;
import java.util.Collection;

public class ServerFacade
{
    private final String serverURL;
    public ServerFacade(String url)
    {
        serverURL = url;
    }

    public AuthData register(UserData user)
    {
        return null;
    }

    public AuthData login(String username, String password)
    {
        return null;

    }

    public boolean logout(String authToken)
    {
        return false;
    }

    public Collection<GameData> listGames(String authToken)
    {
        return null;
    }

    public GameData createGame(String authToken, String gameName)
    {
        return null;
    }

    public GameData joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID)
    {
        return null;
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ServerException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ServerException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException
    {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServerException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ServerException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
