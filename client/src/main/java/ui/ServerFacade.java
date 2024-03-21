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
import java.util.Map;
import java.util.Set;

public class ServerFacade
{
    private final String serverURL;
    public ServerFacade(String url)
    {
        serverURL = url;
    }

    public AuthData register(UserData user) throws ServerException
    {
        var path = "/user";
        return this.makeRequest("POST", path, null, user, AuthData.class);
    }

    public AuthData login(String username, String password) throws ServerException
    {
        var path = "/session";
        return this.makeRequest("POST", path, null, Map.of("username", username, "password", password), AuthData.class);

    }

    public void logout(String authToken) throws ServerException
    {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null, Object.class);
    }

    public Collection<GameData> listGames(String authToken) throws ServerException
    {
        var path = "/game";
        record listGameResponse(Set<GameData> games) {
        }
        var response = this.makeRequest("GET", path, authToken, null, listGameResponse.class);
        return response.games();
    }

    public GameData createGame(String authToken, String gameName) throws ServerException
    {
        var path = "/game";
        return this.makeRequest("POST", path, authToken, Map.of("gameName", gameName), GameData.class);
    }

    public GameData joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws ServerException
    {
        var path = "/game";
        return this.makeRequest("PUT", path, authToken, Map.of("playerColor", playerColor, "gameID", gameID), GameData.class);
    }



    private <T> T makeRequest(String method, String path, String header, Object request, Class<T> responseClass) throws ServerException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if(header != null)
                http.addRequestProperty("authorization", header);
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
