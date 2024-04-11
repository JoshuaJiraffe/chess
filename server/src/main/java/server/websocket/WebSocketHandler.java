package server.websocket;

import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;

@WebSocket
public class WebSocketHandler
{
    private final GameService gameService;
    private final WebSocketSessions sessions;
    public WebSocketHandler(GameService service)
    {
        gameService = service;
        sessions = new WebSocketSessions();
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {

    }

    @OnWebSocketClose
    public void onClose(Session session)
    {

    }

    @OnWebSocketError
    public void onError(Throwable throwable)
    {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String str)
    {

    }

    public void joinPlayer()
    {

    }

    public void joinObserver()
    {

    }

    public void makeMove()
    {

    }

    public void leaveGame()
    {

    }

    public void resignGame()
    {

    }

    public void sendMessage(int gameID, String message, AuthData authData)
    {

    }

    public void broadcastMessage(int gameID, String message, String exceptThisAuthToken)
    {

    }

}
