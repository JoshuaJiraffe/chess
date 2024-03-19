package ui;

import com.google.gson.Gson;
import requestClasses.LoginRequest;
import resultClasses.LoginResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.rmi.ServerException;

public class ServerFacade
{
    private final int port;
    private final String serverURL;
    public ServerFacade(int porty)
    {
        port = porty;
        serverURL = "http://localhost:" + port;
    }

    public Object register()
    {
        return null;
    }

    public LoginResult login(LoginRequest request)
    {
        LoginResult result = new LoginResult();
        return result;

    }

    public Object logout()
    {
        return null;
    }

    public Object listGames()
    {
        return null;
    }

    public Object createGame()
    {
        return null;
    }

    public Object joinGame()
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
